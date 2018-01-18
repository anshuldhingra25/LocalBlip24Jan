package com.ordiacreativeorg.localblip.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.Html;
import android.util.Log;

import com.ordiacreativeorg.localblip.BuildConfig;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.util.ArchiveManager;

import java.util.HashMap;


/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/27/2015
 */
public class LBNotificationsIntentReceiver extends WakefulBroadcastReceiver {

    private final String LOG_TAG = "BlipNotificationsIR";

    private static final String FIRE_NEXT_NOTIFICATION = "com.ordiacreative.localblip.FIRE_NEXT_NOTIFICATION";
    public static final String FIRE_NEXT_POPUP = "com.ordiacreative.localblip.FIRE_NEXT_POPUP";
    public static final String MANUAL_UPDATE_ALARMS = "com.ordiacreative.localblip.MANUAL_UPDATE_ALARMS";
    public static final String MANUAL_STOP_ALARMS = "com.ordiacreative.localblip.MANUAL_STOP_ALARMS";
    public int cou_id = 1;

//   String id = "";

    @Override
    public void onReceive(Context context, Intent intent) {


        HashMap<String, String> lastSession = new HashMap<>();


        ArchiveManager.getSession(context.getApplicationContext(), lastSession);

//       Log.e("coupnid",intent.getStringExtra( "content" ));
        if (lastSession.size() > 0) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                if (BuildConfig.DEBUG)
                    Log.d(LOG_TAG, "Setting alarms at boot...");

            } else if (MANUAL_UPDATE_ALARMS.equals(intent.getAction())) {
                if (BuildConfig.DEBUG)
                    Log.d(LOG_TAG, "Setting alarms by application stop...");

                nextNotify(context, lastSession, intent.getStringExtra("message"));

            } else if (MANUAL_STOP_ALARMS.equals(intent.getAction())) {
                if (BuildConfig.DEBUG)
                    Log.d(LOG_TAG, "Stopping alarms by application start...");

            } else if (FIRE_NEXT_NOTIFICATION.equals(intent.getAction())) {
                if (BuildConfig.DEBUG)
                    Log.i(LOG_TAG, "Setting upcoming alarm by event start...");

                nextNotify(context, lastSession, intent.getStringExtra("message"));

            } else if (FIRE_NEXT_POPUP.equals(intent.getAction())) {
                if (BuildConfig.DEBUG)
                    Log.i(LOG_TAG, "Setting upcoming foreground alarm...");

                nextPopup(context, lastSession, intent.getStringExtra("message"));
            }
        }
    }

    private void nextPopup(final Context context, final HashMap<String, String> lastSession, String message) {
        Log.e("Message1", "Message is " + message);
        String[] animalsArray = message.split("/");
        String message1 = animalsArray[0];

        if (message1.equals("A Blip in Your BlipBook has changed ")) {
            Intent resultIntent = new Intent(BaseActivity.SHOW_EDIT_BLIP_POPUP);
            resultIntent.putExtra("message", message);
            context.sendBroadcast(resultIntent);
        } else if (message1.startsWith("Your LocalBlip Coupon")) {
            Intent resultIntent = new Intent(BaseActivity.SHOW_EXPIRATION_POPUP_EX);
            resultIntent.putExtra("message", message);
            context.sendBroadcast(resultIntent);
        } else if (message1.startsWith("Your Coupon")) {
            Intent resultIntent = new Intent(BaseActivity.SHOW_EXPIRATION_POPUP_EX);
            resultIntent.putExtra("message", message);
            context.sendBroadcast(resultIntent);
        } else {
            Intent resultIntent = new Intent(BaseActivity.SHOW_NEW_BLIP_POPUP);
            resultIntent.putExtra("message", message);
            context.sendBroadcast(resultIntent);
        }


    }

    /*02-24 11:40:45.376 5120-5142/? I/Pushwoosh: [com.pushwoosh.GCMListenerService] Received message: Bundle[{google.sent_time=1487916644676, pw_msg=1, p=#_, u={"488":{"type":"blip_expires","content":"Your Coupon test share is about to expire in next few days deepa Bharti\/1910"},"976":{"type":"blip_expires","content":"Your Coupon q45q52 is about to expire in next few days deepa Bharti\/1911"},"1464":{"type":"blip_expires","content":"Your Coupon Adam test is about to expire in next few days deepa Bharti\/1926"},"1952":{"type":"blip_expires","content":"Your Coupon candy is about to expire in next few days deepa Bharti\/1879"},"2440":{"type":"blip_expires","content":"Your Coupon mac is about to expire in next few days deepa Bharti\/1880"},"2928":{"type":"blip_expires","content":"Your Coupon timu is about to expire in next few days deepa Bharti\/1881"},"3416":{"type":"blip_expires","content":"Your Coupon hello is about to expire in next few days deepa Bharti\/1877"},"3904":{"type":"blip_expires","content":"Your Coupon yy is about to expire in next few days deepa Bharti\/1878"},"4392":{"type":"blip_expires","content":"Your Coupon add is about to expire in next few days deepa Bharti\/1874"},"4880":{"type":"blip_expires","content":"Your Coupon uu is about to expire in next few days deepa Bharti\/1876"},"5368":{"type":"blip_expires","content":"Your Coupon omg is about to expire in next few days deepa Bharti\/1872"},"5856":{"type":"blip_expires","content":"Your Coupon fg is about to expire in next few days deepa Bharti\/1868"},"6344":{"type":"blip_expires","content":"Your Coupon op is about to expire in next few days deepa Bharti\/1521"},"6832":{"type":"blip_expires","content":"Your Coupon iuu is about to expire in next few days deepa Bharti\/1523"},"7320":{"type":"blip_expires","content":"Your Coupon jack is about to expire in next few days deepa Bharti\/1552"},"7324":{"type":"blip_expires","content":"Your Coupon fitness online  is about to expire in next few days shop test 1\/1982"}}, title=Your Coupon <strong>fitness online </strong> is about to expire in next few days shop test 1, google.message_id=0:1487916644688409%84a6fb69f9fd7ecd, collapse_key=do_not_collapse, pw_badges=0}] from: 61744457682
02-24 11:40:45.381 5120-5143/? I/Pushwoosh: [com.pushwoosh.GCMListenerService] Received message: Bundle[{google.sent_time=1487916644959, pw_msg=1, p=#q, u={"488":{"type":"blip_expires","content":"Your Coupon test share is about to expire in next few days deepa Bharti\/1910"},"976":{"type":"blip_expires","content":"Your Coupon q45q52 is about to expire in next few days deepa Bharti\/1911"},"1464":{"type":"blip_expires","content":"Your Coupon Adam test is about to expire in next few days deepa Bharti\/1926"},"1952":{"type":"blip_expires","content":"Your Coupon candy is about to expire in next few days deepa Bharti\/1879"},"2440":{"type":"blip_expires","content":"Your Coupon mac is about to expire in next few days deepa Bharti\/1880"},"2928":{"type":"blip_expires","content":"Your Coupon timu is about to expire in next few days deepa Bharti\/1881"},"3416":{"type":"blip_expires","content":"Your Coupon hello is about to expire in next few days deepa Bharti\/1877"},"3904":{"type":"blip_expires","content":"Your Coupon yy is about to expire in next few days deepa Bharti\/1878"},"4392":{"type":"blip_expires","content":"Your Coupon add is about to expire in next few days deepa Bharti\/1874"},"4880":{"type":"blip_expires","content":"Your Coupon uu is about to expire in next few days deepa Bharti\/1876"},"5368":{"type":"blip_expires","content":"Your Coupon omg is about to expire in next few days deepa Bharti\/1872"},"5856":{"type":"blip_expires","content":"Your Coupon fg is about to expire in next few days deepa Bharti\/1868"},"6344":{"type":"blip_expires","content":"Your Coupon op is about to expire in next few days deepa Bharti\/1521"},"6832":{"type":"blip_expires","content":"Your Coupon iuu is about to expire in next few days deepa Bharti\/1523"},"7320":{"type":"blip_expires","content":"Your Coupon jack is about to expire in next few days deepa Bharti\/1552"},"7324":{"type":"blip_expires","content":"Your Coupon fitness online  is about to expire in next few days shop test 1\/1982"}}, title=Your Coupon <strong>fitness online </strong> is about to expire in next few days shop test 1, google.message_id=0:1487916644966047%84a6fb69f9fd7ecd, collapse_key=do_not_collapse, pw_badges=0}] from: 61744457682
02-24 11:40:45.413 5120-5142/? I/Pushwoosh: [AbsNotificationFactory] nofify: Bundle[{google.sent_time=1487916644676, onStart=true, pw_msg=1, p=#_, u={"488":{"type":"blip_expires","content":"Your Coupon test share is about to expire in next few days deepa Bharti\/1910"},"976":{"type":"blip_expires","content":"Your Coupon q45q52 is about to expire in next few days deepa Bharti\/1911"},"1464":{"type":"blip_expires","content":"Your Coupon Adam test is about to expire in next few days deepa Bharti\/1926"},"1952":{"type":"blip_expires","content":"Your Coupon candy is about to expire in next few days deepa Bharti\/1879"},"2440":{"type":"blip_expires","content":"Your Coupon mac is about to expire in next few days deepa Bharti\/1880"},"2928":{"type":"blip_expires","content":"Your Coupon timu is about to expire in next few days deepa Bharti\/1881"},"3416":{"type":"blip_expires","content":"Your Coupon hello is about to expire in next few days deepa Bharti\/1877"},"3904":{"type":"blip_expires","content":"Your Coupon yy is about to expire in next few days deepa Bharti\/1878"},"4392":{"type":"blip_expires","content":"Your Coupon add is about to expire in next few days deepa Bharti\/1874"},"4880":{"type":"blip_expires","content":"Your Coupon uu is about to expire in next few days deepa Bharti\/1876"},"5368":{"type":"blip_expires","content":"Your Coupon omg is about to expire in next few days deepa Bharti\/1872"},"5856":{"type":"blip_expires","content":"Your Coupon fg is about to expire in next few days deepa Bharti\/1868"},"6344":{"type":"blip_expires","content":"Your Coupon op is about to expire in next few days deepa Bharti\/1521"},"6832":{"type":"blip_expires","content":"Your Coupon iuu is about to expire in next few days deepa Bharti\/1523"},"7320":{"type":"blip_expires","content":"Your Coupon jack is about to expire in next few days deepa Bharti\/1552"},"7324":{"type":"blip_expires","content":"Your Coupon fitness online  is about to expire in next few days shop test 1\/1982"}}, title=Your Coupon <strong>fitness online </strong> is about to expire in next few days shop test 1, google.message_id=0:1487916644688409%84a6fb69f9fd7ecd, collapse_key=do_not_collapse, pw_badges=0, foreground=false}]
02-24 11:40:45.413 5120-5143/? I/Pushwoosh: [AbsNotificationFactory] nofify: Bundle[{google.sent_time=1487916644959, onStart=true, pw_msg=1, p=#q, u={"488":{"type":"blip_expires","content":"Your Coupon test share is about to expire in next few days deepa Bharti\/1910"},"976":{"type":"blip_expires","content":"Your Coupon q45q52 is about to expire in next few days deepa Bharti\/1911"},"1464":{"type":"blip_expires","content":"Your Coupon Adam test is about to expire in next few days deepa Bharti\/1926"},"1952":{"type":"blip_expires","content":"Your Coupon candy is about to expire in next few days deepa Bharti\/1879"},"2440":{"type":"blip_expires","content":"Your Coupon mac is about to expire in next few days deepa Bharti\/1880"},"2928":{"type":"blip_expires","content":"Your Coupon timu is about to expire in next few days deepa Bharti\/1881"},"3416":{"type":"blip_expires","content":"Your Coupon hello is about to expire in next few days deepa Bharti\/1877"},"3904":{"type":"blip_expires","content":"Your Coupon yy is about to expire in next few days deepa Bharti\/1878"},"4392":{"type":"blip_expires","content":"Your Coupon add is about to expire in next few days deepa Bharti\/1874"},"4880":{"type":"blip_expires","content":"Your Coupon uu is about to expire in next few days deepa Bharti\/1876"},"5368":{"type":"blip_expires","content":"Your Coupon omg is about to expire in next few days deepa Bharti\/1872"},"5856":{"type":"blip_expires","content":"Your Coupon fg is about to expire in next few days deepa Bharti\/1868"},"6344":{"type":"blip_expires","content":"Your Coupon op is about to expire in next few days deepa Bharti\/1521"},"6832":{"type":"blip_expires","content":"Your Coupon iuu is about to expire in next few days deepa Bharti\/1523"},"7320":{"type":"blip_expires","content":"Your Coupon jack is about to expire in next few days deepa Bharti\/1552"},"7324":{"type":"blip_expires","content":"Your Coupon fitness online  is about to expire in next few days shop test 1\/1982"}}, title=Your Coupon <strong>fitness online </strong> is about to expire in next few days shop test 1, google.message_id=0:1487916644966047%84a6fb69f9fd7ecd, collapse_key=do_not_collapse, pw_badges=0, foreground=false}]
*/

    private void nextNotify(final Context context, final HashMap<String, String> lastSession, String message) {
        Log.e("Message2", "Message is " + message);
        Intent resultIntent = new Intent(context, MainActivity.class);
//        Your Coupon test share is about to expire in next few days deepa Bharti\/1910
        String[] animalsArray = message.split("/");
        String message1 = animalsArray[0];
        Log.e("messageLog", "" + message1);


        if (message1.equals("A Blip in Your BlipBook has changed ")) {
            Log.e("messageggg", "editBlips");
            resultIntent.putExtra("fragment", "editBlips");
        } else if (message1.startsWith("Your LocalBlip Coupon")) {
            Log.e("messageggg", "Your LocalBlip Coupon");
            resultIntent.putExtra("fragment", "blipbook");
        } else if (message1.startsWith("Your Coupon")) {
            Log.e("messageggg", "Your Coupon");
            resultIntent.putExtra("fragment", "coupan");
        } else {

            resultIntent.putExtra("fragment", "newBlips");
        }


        throwNotification(context, resultIntent, message);
    }

    private void throwNotification(Context context, Intent resultIntent, String message) {
        Log.e("Message3", "Message is " + message);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), resultIntent, 0);

        String[] animalsArray = message.split("/");
        message = animalsArray[0];


        cou_id = Integer.parseInt(animalsArray[1]);

        String contntTitlne = "New Blip arrived!";
        if (message.equals("A Blip in Your BlipBook has changed ")) {
            contntTitlne = "Your BlipBook update";
        } else if (message.startsWith("Your LocalBlip Coupon")) {
            contntTitlne = "LocalBlip BlipBook";
        } else if (message.startsWith("Your Coupon")) {
            contntTitlne = "LocalBlip Vendor";
        } else {
            contntTitlne = "New Blip arrived!";
        }

        Notification mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(contntTitlne)
                .setContentText(Html.fromHtml(message))
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_ALL)
                //  .setContentIntent(resultPendingIntent.getActivity(context, 0, resultIntent, 0)).build();
                .setContentIntent(resultPendingIntent).build();
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.flags |= Notification.FLAG_AUTO_CANCEL;
        int id = (int) System.currentTimeMillis();
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder);

    }
}