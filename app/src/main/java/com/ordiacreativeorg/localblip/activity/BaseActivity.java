package com.ordiacreativeorg.localblip.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.ordiacreativeorg.localblip.BuildConfig;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.constants.Constants;
import com.ordiacreativeorg.localblip.model.Blip;

import java.io.IOException;

/**
 * Created by dmytrobohachevskyy on 9/26/15.
 *
 * Rewritten by Sergey Mitrofanov (goretz.m@gmail.com) on 11/27/2015
 */
public class BaseActivity extends AppCompatActivity {
    public static int globalcount = 0;
    public static final String SHOW_NEW_MESSAGE_POPUP = "com.ordiacreative.localblip.SHOW_NEW_MESSAGE_POPUP";
    public static final String SHOW_NEW_BLIP_POPUP = "com.ordiacreative.localblip.SHOW_NEW_BLIP_POPUP";
    public static final String SHOW_EDIT_BLIP_POPUP = "com.ordiacreative.localblip.SHOW_EDIT_BLIP_POPUP";
    public static final String SHOW_EXPIRATION_POPUP = "com.ordiacreative.localblip.SHOW_EXPIRATION_POPUP";
    public static final String SHOW_BLIP_ALERT_POPUP = "com.ordiacreative.localblip.SHOW_BLIP_ALERT_POPUP";
    public static final String SHOW_EXPIRATION_POPUP_EX = "com.ordiacreative.localblip.SHOW_EXPIRATION_POPUP_EX";
    private static final String LOG_TAG = "BaseActivity";

    public void setBarTitle( String title, boolean visible ) {
        setTitle(title);
        if (getSupportActionBar() != null) {
            if (visible) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    public void setBarTitle( int id, boolean visible ) {
        setTitle(id);
        if (getSupportActionBar() != null) {
            if (visible) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create global configuration and initialize ImageLoader with this config
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .imageDownloader(new BaseImageDownloader(getApplicationContext(), 20 * 1000, 25 * 1000))
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    .memoryCacheSize(2 * 1024 * 1024)
                    .memoryCacheSizePercentage(13)
                    .build();
            ImageLoader.getInstance().init(config);
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SHOW_NEW_MESSAGE_POPUP.equals(intent.getAction())) {
                    if ( BuildConfig.DEBUG ) Log.d( LOG_TAG, "Popup new message..." );
                    int newMessages = intent.getIntExtra( "conversations", 0 );

                    Uri notificationUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
                    //Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationUri);
                    //if (ringtone != null) ringtone.play();
                    try {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource( context, notificationUri );
                        mediaPlayer.setAudioStreamType( AudioManager.STREAM_NOTIFICATION );
                        mediaPlayer.prepare();
                        mediaPlayer.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion( MediaPlayer mp ) {
                                mp.stop();
                                mp.release();
                            }
                        } );
                        mediaPlayer.start();
                    } catch ( IllegalArgumentException | IOException | IllegalStateException | SecurityException e ) {
                        e.printStackTrace();
                    }

                    LocalBroadcastManager.getInstance( BaseActivity.this ).sendBroadcast( new Intent( Constants.UPDATE_NEW_MESSAGES_COUNT ) );

                    String message = "You have " + newMessages + " new Messages";
                    Snackbar.make( BaseActivity.this.findViewById( android.R.id.content ), Html.fromHtml(message), Snackbar.LENGTH_INDEFINITE )
                            .setAction( R.string.read, new View.OnClickListener() {
                                @Override
                                public void onClick( View v ) {
                                    Intent resultIntent = new Intent( BaseActivity.this, MainActivity.class );
                                    resultIntent.putExtra( "fragment", "messages" );
                                    startActivity( resultIntent );
                                    finish();
                                }
                            } )
                            .show();
                } else if ( SHOW_BLIP_ALERT_POPUP.equals( intent.getAction() ) ) {
                    if ( BuildConfig.DEBUG ) Log.d( LOG_TAG, "Popup new message..." );

                    Uri notificationUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
                    //Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationUri);
                    //if (ringtone != null) ringtone.play();
                    try {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource( context, notificationUri );
                        mediaPlayer.setAudioStreamType( AudioManager.STREAM_NOTIFICATION );
                        mediaPlayer.prepare();
                        mediaPlayer.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion( MediaPlayer mp ) {
                                mp.stop();
                                mp.release();
                            }
                        } );
                        mediaPlayer.start();
                    } catch ( IllegalArgumentException | IOException | IllegalStateException | SecurityException e ) {
                        e.printStackTrace();
                    }

                    final String message = intent.getStringExtra( "message" );
                    final String sender = intent.getStringExtra( "sender" );
                    Snackbar.make( BaseActivity.this.findViewById( android.R.id.content ), "New blip alert from " + sender, Snackbar.LENGTH_INDEFINITE ).
                            setAction( "VIEW", new View.OnClickListener() { @Override public void onClick( View v ) {
//                                ConfirmationDialog.newInstance( "Vendor: " + sender, message, "OK", null, new ConfirmationDialog.OnActionConfirmationListener() {
//                                    @Override
//                                    public void onConfirmed() {}
//
//                                    @Override
//                                    public void onCanceled() {}
//                                }, true ).show( BaseActivity.this.getSupportFragmentManager(), null);

                                Intent resultIntent = new Intent( BaseActivity.this, MainActivity.class );
                                resultIntent.putExtra( "fragment", "messages" );
                                startActivity( resultIntent );
                                finish();

                            }
                            } ).show();
                } else if (SHOW_NEW_BLIP_POPUP.equals(intent.getAction())) {
                    if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Popup new blip...");

                    try {
                        Log.e("intent.getAction1()", "Popup new blip..."+intent.getAction());
                        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationUri);
                        ringtone.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final Intent nIntent = new Intent(BaseActivity.this, MainActivity.class);
                    nIntent.putExtra("fragment", "newBlips");

                    Snackbar.make(BaseActivity.this.findViewById(android.R.id.content), Html.fromHtml(intent.getStringExtra( "message" )), Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(nIntent);
                                }
                            })
                            .show();

                }
                else if (SHOW_EDIT_BLIP_POPUP.equals(intent.getAction())) {
                    if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Popup new blip...");

                    try {
                        Log.e("intent.getAction()", "Popup new blip..."+intent.getAction());
                        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationUri);
                        ringtone.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final Intent nIntent = new Intent(BaseActivity.this, MainActivity.class);
                    nIntent.putExtra("fragment", "editBlips");

                    Snackbar.make(BaseActivity.this.findViewById(android.R.id.content), Html.fromHtml(intent.getStringExtra( "message" )), Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(nIntent);
                                }
                            })
                            .show();

                }
                else if (SHOW_EXPIRATION_POPUP.equals(intent.getAction())) {
                    if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Popup expirations blip...");
                    final Blip blip = (Blip) intent.getSerializableExtra("blip");
                    final int count = intent.getIntExtra("count", 0);

                    try {
                        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationUri);
                        ringtone.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final Intent nIntent;
                    if (blip != null){
                        nIntent = new Intent(BaseActivity.this, BlipDetails.class);
                        nIntent.putExtra("blip", blip);
                    } else {
                        nIntent = new Intent(BaseActivity.this, MainActivity.class);
                        nIntent.putExtra("fragment", "createBlip");
                    }
                    Snackbar.make(BaseActivity.this.findViewById(android.R.id.content), "Your " + (count > 0 ? count + " blips" : "blip" ) + " expires soon!", Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } )
                            .setAction(R.string.view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(nIntent);
                                }
                            })
                            .show();

                }


                else if (SHOW_EXPIRATION_POPUP_EX.equals(intent.getAction())) {{
                    if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Popup new blip...");

                    try {
                        Log.e("intent.getAction()", "Popup new blip..."+intent.getAction());
                        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationUri);
                        ringtone.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final Intent nIntent = new Intent(BaseActivity.this, MainActivity.class);
                    if (intent.getStringExtra("message" ).startsWith("Your LocalBlip Coupon")) {
                        nIntent.putExtra("fragment", "blipbook");
                    } else if (intent.getStringExtra("message" ).startsWith("Your Coupon is about to expire")) {

                        nIntent.putExtra("fragment", "coupan");
                    }
                    else {

                        nIntent.putExtra("fragment", "blipbook");
                    }



                    Snackbar.make(BaseActivity.this.findViewById(android.R.id.content), Html.fromHtml(intent.getStringExtra( "message" )), Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(nIntent);
                                }
                            })
                            .show();

                }

                }



            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SHOW_NEW_BLIP_POPUP);
        intentFilter.addAction(SHOW_EDIT_BLIP_POPUP);
        intentFilter.addAction(SHOW_NEW_MESSAGE_POPUP);
        intentFilter.addAction(SHOW_EXPIRATION_POPUP);
        intentFilter.addAction(SHOW_BLIP_ALERT_POPUP);
        intentFilter.addAction(SHOW_EXPIRATION_POPUP_EX);


        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    protected void startBackgroundMessagesBR(){
        Intent intent = new Intent(getApplicationContext(), LBMessagesIntentReceiver.class);
        intent.setAction(LBMessagesIntentReceiver.MANUAL_UPDATE_ALARMS);
        sendBroadcast(intent);
    }

    protected void startForegroundMessagesBR(){
        Intent intent = new Intent(getApplicationContext(), LBMessagesIntentReceiver.class);
        intent.setAction(LBMessagesIntentReceiver.FIRE_NEXT_POPUP);
        sendBroadcast(intent);
    }

    protected void startBackgroundNotificationsBR( String message ){
        Intent intent = new Intent(getApplicationContext(), LBNotificationsIntentReceiver.class);
        intent.setAction(LBNotificationsIntentReceiver.MANUAL_UPDATE_ALARMS);
        intent.putExtra( "message", message );
        sendBroadcast(intent);
    }

    protected void startForegroundNotificationsBR( String message ){
        Intent intent = new Intent(getApplicationContext(), LBNotificationsIntentReceiver.class);
        intent.setAction(LBNotificationsIntentReceiver.FIRE_NEXT_POPUP);
        intent.putExtra( "message", message );
        sendBroadcast(intent);
    }

    protected void startBackgroundAlertBR( String message, String sender ) {
        Log.e("mesaaj",message);
        Log.e("sendaaj",sender);
        Intent intent = new Intent(getApplicationContext(), LBAlertIntentReceiver.class);
        intent.setAction( LBAlertIntentReceiver.MANUAL_UPDATE_ALARMS);
        intent.putExtra( "message", message );
        intent.putExtra( "sender", sender );
        sendBroadcast(intent);
    }

    protected void startForegroundAlertBR( String message, String sender ) {
        Intent intent = new Intent(getApplicationContext(), LBAlertIntentReceiver.class);
        intent.setAction( LBAlertIntentReceiver.FIRE_NEXT_POPUP);
        intent.putExtra( "message", message );
        intent.putExtra( "sender", sender );
        sendBroadcast(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fragment", true);
    }
}
