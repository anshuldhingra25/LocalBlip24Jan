package com.ordiacreativeorg.localblip.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.Html;
import android.util.Log;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.util.ArchiveManager;

import java.util.HashMap;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/27/2015
 */
public class LBAlertIntentReceiver extends WakefulBroadcastReceiver {
    HashMap<String, Long> messag = new HashMap<>();
    private final String LOG_TAG = "BlipAlertIR";

    private static final String FIRE_NEXT_NOTIFICATION = "com.ordiacreative.localblip.FIRE_NEXT_NOTIFICATION";
    public static final String FIRE_NEXT_POPUP = "com.ordiacreative.localblip.FIRE_NEXT_POPUP";
    public static final String MANUAL_UPDATE_ALARMS = "com.ordiacreative.localblip.MANUAL_UPDATE_ALARMS";
    public static final String MANUAL_STOP_ALARMS = "com.ordiacreative.localblip.MANUAL_STOP_ALARMS";

    @Override
    public void onReceive(Context context, Intent intent) {
        HashMap<String, String> lastSession = new HashMap<>();
        ArchiveManager.getSession(context.getApplicationContext(), lastSession);

        if (lastSession.size() > 0) {
            if (MANUAL_UPDATE_ALARMS.equals(intent.getAction())) {
                Log.e("MANUAL", "1");
                nextNotify(context, intent.getStringExtra("message"), intent.getStringExtra("sender"));
            } else if (FIRE_NEXT_NOTIFICATION.equals(intent.getAction())) {
                Log.e("MANUAL", "2");
                nextNotify(context, intent.getStringExtra("message"), intent.getStringExtra("sender"));
            } else if (FIRE_NEXT_POPUP.equals(intent.getAction())) {
                Log.e("MANUAL", "3");
                nextPopup(context, intent.getStringExtra("message"), intent.getStringExtra("sender"));
            }
        }
    }

    private void nextPopup(final Context context, String message, String sender) {
        Intent resultIntent = new Intent(BaseActivity.SHOW_BLIP_ALERT_POPUP);
        resultIntent.putExtra("message", message);
        resultIntent.putExtra("sender", sender);
        Log.e("nextPopup", "" + message);
        context.sendBroadcast(resultIntent);
    }

    private void nextNotify(final Context context, String message, String sender) {
        Intent resultIntent = new Intent(context, MainActivity.class);
//        resultIntent.putExtra("message", message);
//        resultIntent.putExtra("sender", sender);
        resultIntent.putExtra("fragment", "messages");
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("nextNotify", "" + message);
        Log.e("nextsender", "" + sender);
        throwNotification(context, resultIntent, message, sender);
    }

    private void throwNotification(Context context, Intent resultIntent, String message, String sender) {
        int id = (int) System.currentTimeMillis();
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
//                .setContentTitle( "New blip alert from " + sender )
                .setContentTitle(" " + sender)
                .setContentText(Html.fromHtml(message))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id, mBuilder.build());
    }
}
/*

    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),notificationIndex,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    notification.setLatestEventInfo(getApplicationContext(), notificationTitle, notificationMessage, pendingNotificationIntent);*/