package com.ordiacreativeorg.localblip.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.ordiacreativeorg.localblip.BuildConfig;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.constants.Constants;
import com.ordiacreativeorg.localblip.model.UnreadMessagesCount;
import com.ordiacreativeorg.localblip.util.ArchiveManager;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/27/2015
 */
public class LBMessagesIntentReceiver extends WakefulBroadcastReceiver{
    private final String LOG_TAG = "BlipMessagesIR";

    private static final String FIRE_NEXT_NOTIFICATION = "com.ordiacreative.localblip.FIRE_NEXT_NOTIFICATION";
    public static final String FIRE_NEXT_POPUP = "com.ordiacreative.localblip.FIRE_NEXT_POPUP";
    public static final String MANUAL_UPDATE_ALARMS = "com.ordiacreative.localblip.MANUAL_UPDATE_ALARMS";
    public static final String MANUAL_STOP_ALARMS = "com.ordiacreative.localblip.MANUAL_STOP_ALARMS";

    @Override
    public void onReceive(Context context, Intent intent) {
        LocalBroadcastManager.getInstance( context ).sendBroadcast( new Intent( Constants.UPDATE_NEW_MESSAGES_COUNT ) );

        HashMap<String, String> lastSession = new HashMap<>();
        ArchiveManager.getSession(context.getApplicationContext(), lastSession);
        if (lastSession.size() > 0){
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting alarms at boot...");
            } else if (MANUAL_UPDATE_ALARMS.equals(intent.getAction())) {
                if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Setting alarms by application stop...");
                nextNotify( context, lastSession );
            } else if (MANUAL_STOP_ALARMS.equals(intent.getAction())) {
                if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Stopping alarms by application start...");
            } else if (FIRE_NEXT_NOTIFICATION.equals(intent.getAction())) {
                if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Setting upcoming alarm by event start...");
                nextNotify(context, lastSession);
            } else if (FIRE_NEXT_POPUP.equals(intent.getAction())) {
                if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Setting upcoming foreground alarm...");
                nextPopup(context, lastSession);
            }
        }
    }

    private void stopAlarms(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LBMessagesIntentReceiver.class);
        intent.setAction(LBMessagesIntentReceiver.FIRE_NEXT_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pendingIntent);
        intent = new Intent(context, LBMessagesIntentReceiver.class);
        intent.setAction(LBMessagesIntentReceiver.FIRE_NEXT_POPUP);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pendingIntent);
    }

    private void nextPopup(final Context context,  final HashMap<String, String> lastSession){
        Api.getInstance().getMethods().getUnredMessageCount(
                lastSession.get("email"),
                lastSession.get("apikey")
        ).enqueue(new Callback<UnreadMessagesCount>() {
            @Override
            public void onResponse(Response<UnreadMessagesCount> response, Retrofit retrofit) {
                if ( response.body().getUnreadMessageCount() == 0 ) {
                    return;
                }

                Intent resultIntent = new Intent(BaseActivity.SHOW_NEW_MESSAGE_POPUP);
                resultIntent.putExtra("conversations", response.body().getUnreadMessageCount());
                context.sendBroadcast(resultIntent);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("REFRESH_MESSAGE", String.format("fail: %s", t.getMessage()));
            }
        });
    }

    private void nextNotify(final Context context, final HashMap<String, String> lastSession){
        Api.getInstance().getMethods().getUnredMessageCount(
                lastSession.get("email"),
                lastSession.get("apikey")
        ).enqueue(new Callback< UnreadMessagesCount >() {
            @Override
            public void onResponse(Response< UnreadMessagesCount > response, Retrofit retrofit) {
                if ( response.body().getUnreadMessageCount() == 0 ) {
                    return;
                }

                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.putExtra("fragment", "messages");
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder mBuilder = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        //.setContentTitle(context.getResources().getString(R.string.notify_title))
                        //.setContentText(context.getResources().getString(R.string.notify_subtitle))
                        .setContentTitle( "LocalBlip" )
                        .setContentText( "You have " + response.body().getUnreadMessageCount() + " new Messages" )
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                int  id =(int) System.currentTimeMillis();
                mNotificationManager.notify( id, mBuilder.build());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("REFRESH_MESSAGE", String.format("fail: %s", t.getMessage()));
            }
        });
    }
}