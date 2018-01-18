package com.ordiacreativeorg.localblip.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.adapter.CategoryAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.constants.Constants;
import com.ordiacreativeorg.localblip.fragment.Account;
import com.ordiacreativeorg.localblip.fragment.BlipAnalytics;
import com.ordiacreativeorg.localblip.fragment.BlipBook;
import com.ordiacreativeorg.localblip.fragment.ConfirmationDialog;
import com.ordiacreativeorg.localblip.fragment.Conversations;
import com.ordiacreativeorg.localblip.fragment.IncreaseMyVisibility;
import com.ordiacreativeorg.localblip.fragment.MapFragment;
import com.ordiacreativeorg.localblip.fragment.NewBlips;
import com.ordiacreativeorg.localblip.fragment.Profile;
import com.ordiacreativeorg.localblip.fragment.Redeem;
import com.ordiacreativeorg.localblip.fragment.ShopBlips;
import com.ordiacreativeorg.localblip.fragment.SocialMediaFragment;
import com.ordiacreativeorg.localblip.fragment.StillDevelopment;
import com.ordiacreativeorg.localblip.fragment.VendorBlips;
import com.ordiacreativeorg.localblip.fragment.VendorLocations;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.PushWooshResponse;
import com.ordiacreativeorg.localblip.model.UnreadMessagesCount;
import com.ordiacreativeorg.localblip.util.ArchiveManager;
import com.ordiacreativeorg.localblip.util.ForegroundUtil;
import com.ordiacreativeorg.localblip.util.GPSTracker;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.ordiacreativeorg.localblip.R.id.et_search;

/**
 * Created by dmytrobohachevskyy on 9/26/15.
 * <p>
 * Rewritten by Sergey Mitrofanov (goretz.m@gmail.com) on 11/27/2015
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    GPSTracker gpsTracker;
    private String DLatitude = "", DLongitude = "";
    // drawer list
    private DrawerLayout mDrawerLayout;
    CategoryAdapter categoryAdapter;
    private TextView mTitleTextView;
    private ImageView mLogoImageView;
    public EditText mSearchEditText;
    public ImageButton back;
    private RelativeLayout rel_search;
    private ImageView img_search, img_cross;
    private ImageView map;
    public static TextView btn_shop;
    boolean isTYpe = false;
    // data
    private MemberDetail memberDetail;

    private static String lastMessage = "";
    private static long lastTime = 0;
    final int PERMISSION_REQUEST_CODE = 111;
    public static int textZipValue = 0;

    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver localReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.UPDATE_NEW_MESSAGES_COUNT:
                    updateNewMessagesCount();
                    break;
                case Constants.RESET_NEW_MESSAGES_COUNT:
                    View view = findViewById(R.id.messages_count_wrapper);
                    if (view != null) {
                        view.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    //Registration localReceiver
    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver() {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent) {
            checkMessage(intent);
        }
    };

    //Push message localReceiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
        @Override
        protected void onMessageReceive(Intent intent) {
            handleMessage(intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    //Registration of the receivers
    public void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
        registerReceiver(mReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);
        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        initLocalBroadcastManager();
        initPushNotifications();


        gpsTracker = new GPSTracker(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() ||
                    !checkWriteExternalStoragePermission() || !checkCameraStoragePermission()) {
                requestPermission();
            } else {
                setLocation();
            }
        } else {
            setLocation();
        }
        memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        if (memberDetail == null) {
            restartApp();
            return;
        }
        if (bundle != null) {
            hasFragment = bundle.getBoolean("hasFragment", false);
        }

        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_appbar);
        setSupportActionBar(toolbar);


        /* INIT DRAWER LIST  */
        ScrollView scrollView = (ScrollView) findViewById(R.id.sv_drawer);

        RelativeLayout child;
        if (4 == memberDetail.getMemberType()) {
            child = (RelativeLayout) getLayoutInflater().inflate(R.layout.drawer_vendor_layout, scrollView, false);
        } else {
            child = (RelativeLayout) getLayoutInflater().inflate(R.layout.drawer_consumer_layout, scrollView, false);
        }
        scrollView.addView(child);

        selectedMenuItem = (Button) child.findViewById(R.id.btn_shop);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView menuIconImage = (ImageView) toolbar.findViewById(R.id.iv_menu_navigation);
        menuIconImage.setVisibility(View.VISIBLE);
        menuIconImage.setOnClickListener(this);
        rel_search = (RelativeLayout) toolbar.findViewById(R.id.rel_search);
        img_search = (ImageView) toolbar.findViewById(R.id.img_search);
        img_search.setOnClickListener(this);

        img_cross = (ImageView) toolbar.findViewById(R.id.img_cross);
        img_cross.setOnClickListener(this);
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mLogoImageView = (ImageView) findViewById(R.id.logo);
        mSearchEditText = (EditText) findViewById(et_search);
        back = (ImageButton) findViewById(R.id.back);
        map = (ImageView) findViewById(R.id.map);
        map.setOnClickListener(this);
        back.setOnClickListener(this);
        mSearchEditText.addTextChangedListener(watch);

//        map.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {

//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fl_fragment_container, new SignUpFragment());
//
//            }
//        });

    }

    TextWatcher watch = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub
            if (arg0.length() > 0) {
                img_cross.setVisibility(View.VISIBLE);
            } else {
                img_cross.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {
            // TODO Auto-generated method stub


//            if(a >0){
//                img_search.setImageResource(R.drawable.ic_action_cancel);
//            }
//            else
//            {
//                img_search.setImageResource(R.drawable.search_icon2);
//            }
        }
    };

    private void initPushNotifications() {
        registerReceivers();
        PushManager pushManager = PushManager.getInstance(this);

        try {
            pushManager.onStartup(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pushManager.registerForPushNotifications();
        checkMessage(getIntent());
    }

    private void checkMessage(Intent intent) {
        if (null != intent) {
            resetIntentValues();
        }
    }

    /**
     * Will check main Activity intent and if it contains any PushWoosh data, will clear it
     */
    private void resetIntentValues() {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    private void handleMessage(String message) {
        Log.e("MESSAGEEE", "" + message);
        if (lastMessage.equals(message) && ((System.currentTimeMillis() - lastTime) < 3000)) {
            return;
        } else {
            lastMessage = message;
            lastTime = System.currentTimeMillis();
        }

        Gson gson = new Gson();

        PushWooshResponse response = gson.fromJson(message, PushWooshResponse.class);
        Log.e("rjkkyu", "" + response.isBlipAlert());
        Log.e("ExpireResponse", "" + response.isBlipExpires());
        Log.d(MainActivity.class.getName(), response.getMessage());

        if (response.getMessage() != null) {
            isTYpe = response.isBlipAlert();
            if (response.isMessage()) {
                if (ForegroundUtil.get().isBackground()) {
                    startBackgroundMessagesBR();
                } else {
                    startForegroundMessagesBR();
                }
                updateNewMessagesCount();
            } else if (response.isBlipAlert()) {

                if (ForegroundUtil.get().isBackground()) {
                    startBackgroundAlertBR(response.getMessage(), response.getSender());
                } else {
                    startForegroundAlertBR(response.getMessage(), response.getSender());
                }
            } else if (response.isNewBlip()) {
                if (ForegroundUtil.get().isBackground()) {
                    startBackgroundNotificationsBR(response.getMessage());
                } else {
                    startForegroundNotificationsBR(response.getMessage());
                }
            } else if (response.isBlipEdit()) {
                if (ForegroundUtil.get().isBackground()) {
                    startBackgroundNotificationsBR(response.getMessage());
                } else {
                    startForegroundNotificationsBR(response.getMessage());
                }
            } else if (response.isBlipExpires()) {
               Log.e("BlipExpire","true");
                if (ForegroundUtil.get().isBackground()) {
                    startBackgroundNotificationsBR(response.getMessage());
                } else {
                    startForegroundNotificationsBR(response.getMessage());
                }
            }
        }
    }

    private void initLocalBroadcastManager() {
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter intentFilter = new IntentFilter(Constants.UPDATE_NEW_MESSAGES_COUNT);
        intentFilter.addAction(Constants.RESET_NEW_MESSAGES_COUNT);

        this.localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private boolean hasFragment = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasFragment", hasFragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        checkMessage(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //   registerReceivers();
        updateNewMessagesCount();
        checkBlipAlert();
        //    checkBlipExpires();

        String pendingFragment = getIntent().getStringExtra("fragment");

        Log.e("testnew", "" + pendingFragment);
        Boolean firstTime = getIntent().getBooleanExtra("firstTime", false);
//        getIntent().removeExtra("fragment");
        if (firstTime) {
            Log.e("pendingFragmenta", "fragment" );
            findViewById(R.id.btn_profile).performClick();
        }
//        else if(pendingFragment != null && !pendingFragment.isEmpty()&&pendingFragment.equals("newBlips")){
//
//            findViewById(R.id.btn_new_blips).performClick();
//
//        }
//
        else if (pendingFragment != null && (!pendingFragment.isEmpty() || !pendingFragment.equals(""))) {
            getIntent().removeExtra("fragment");
            Log.e("pendingFragmenta", "" + pendingFragment);
            if (pendingFragment.equals("newBlips")) {
                LBNotificationsIntentReceiver abc = new LBNotificationsIntentReceiver();
                findViewById(R.id.btn_new_blips).performClick();
//                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.cancel(abc.cou_id);
            } else if (pendingFragment.equals("editBlips")) {
                LBNotificationsIntentReceiver abc = new LBNotificationsIntentReceiver();
                findViewById(R.id.btn_blipbook).performClick();
//                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.cancel(abc.cou_id);
            } else if (pendingFragment.equals("blipbook")) {
                findViewById(R.id.btn_blipbook).performClick();
            } else if (pendingFragment.equals("coupan")) {
                findViewById(R.id.btn_create_blip).performClick();
            } else if (pendingFragment.equals("messages")) {
                findViewById(R.id.btn_messages).performClick();
            } else if (pendingFragment.equals("createBlip")) {
                findViewById(R.id.btn_create_blip).performClick();
            }

        } else if (!hasFragment) {
            Log.e("pendingFragmenta", "not fragment" );
//            findViewById(R.id.btn_shop).performClick();
            findViewById(R.id.btn_shop).performClick();
        }
        Log.e("globalcount", "" + globalcount);
        if (globalcount == 1) {
            findViewById(R.id.btn_shop).performClick();

        }
    }


    private void checkBlipExpires() {
        String message = getIntent().getStringExtra("message");
        String sender = getIntent().getStringExtra("sender");
        Log.e("Mess", "" + message + " " + sender);

        getIntent().removeExtra("message");
        getIntent().removeExtra("sender");

        Gson gson = new Gson();

        PushWooshResponse response1 = gson.fromJson(lastMessage, PushWooshResponse.class);
        Log.w("ISTYPEE", "" + message);
        if (message != null && message.length() > 0 &&
                response1.isBlipExpires()) {
            ConfirmationDialog.newInstance("Vendor: " + sender, String.valueOf(Html.fromHtml(message)), "OK", null, new ConfirmationDialog.OnActionConfirmationListener() {
                @Override
                public void onConfirmed() {
                }

                @Override
                public void onCanceled() {
                }
            }, true).show(this.getSupportFragmentManager(), null);
        }
    }


    private void checkBlipAlert() {
        String message = getIntent().getStringExtra("message");
        String sender = getIntent().getStringExtra("sender");
        Log.e("message1111",""+message);
        Log.e("sender1111",""+sender);

        getIntent().removeExtra("message");
        getIntent().removeExtra("sender");

        Gson gson = new Gson();

        PushWooshResponse response1 = gson.fromJson(lastMessage, PushWooshResponse.class);
        Log.w("ISTYPEE", "" + message);
        if (message != null && message.length() > 0 &&
                response1.isBlipAlert()) {
            ConfirmationDialog.newInstance("" + sender, String.valueOf(Html.fromHtml(message)), "OK", null, new ConfirmationDialog.OnActionConfirmationListener() {
                @Override
                public void onConfirmed() {
                }

                @Override
                public void onCanceled() {
                }
            }, true).show(this.getSupportFragmentManager(), null);
        }
    }

    private void updateNewMessagesCount() {
        Api.getInstance().getMethods().getUnredMessageCount(
                TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail(),
                TemporaryStorageSingleton.getInstance().getMemberDetails().getApiKey()
        ).enqueue(new Callback<UnreadMessagesCount>() {
            @Override
            public void onResponse(Response<UnreadMessagesCount> response, Retrofit retrofit) {
                UnreadMessagesCount messageCount = response.body();
                if (messageCount.getUnreadMessageCount() == 0 && findViewById(R.id.messages_count_wrapper) != null) {
                    findViewById(R.id.messages_count_wrapper).setVisibility(View.GONE);
                } else if (findViewById(R.id.messages_count) != null) {
                    findViewById(R.id.messages_count_wrapper).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.messages_count)).
                            setText(String.valueOf(messageCount.getUnreadMessageCount()));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void askForLogOut() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        logOut();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.do_you_want_logout)
                .setPositiveButton(R.string.nd_logout, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void logOut() {
        LogInActivity.firstTime = "second";
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        TemporaryStorageSingleton.getInstance().setMemberDetails(null);
        TemporaryStorageSingleton.getInstance().setCategoryPosition(0);
        ArchiveManager.removeSession(getApplicationContext());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstDialog", true);
        editor.commit();
        startActivity(intent);
        finish();

    }

    private void restartApp() {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        intent.putExtras(getIntent());
        TemporaryStorageSingleton.getInstance().setMemberDetails(null);
        startActivity(intent);
        finish();
    }

    private Button selectedMenuItem;

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        boolean fragmentChanged = false;

        switch (v.getId()) {
            case R.id.iv_menu_navigation:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.map:
                globalcount = 0;
                mTitleTextView.setVisibility(View.VISIBLE);
                map.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                rel_search.setVisibility(View.GONE);
                fragmentTransaction.replace(R.id.fl_fragment_container, new MapFragment());
                fragmentChanged = true;

                break;
            case R.id.back:
                globalcount = 0;
                mTitleTextView.setVisibility(View.GONE);
                map.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                rel_search.setVisibility(View.VISIBLE);
                fragmentTransaction.replace(R.id.fl_fragment_container, new ShopBlips());
                fragmentChanged = true;

                break;
            case R.id.btn_account:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, new Account());
                fragmentChanged = true;
                break;
            case R.id.btn_smedia:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, SocialMediaFragment.newInstance());
                fragmentChanged = true;
                break;
            case R.id.btn_profile:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, new Profile());
                fragmentChanged = true;
                break;
            case R.id.btn_locations:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, new VendorLocations());
                fragmentChanged = true;
                break;
            case R.id.btn_create_blip:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, new VendorBlips());
                fragmentChanged = true;
                break;
            case R.id.btn_redeem:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, new Redeem());
                fragmentChanged = true;
                break;
            case R.id.btn_visibility:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, new IncreaseMyVisibility());
                fragmentChanged = true;
                break;
            case R.id.btn_analytics:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, new BlipAnalytics());
                fragmentChanged = true;
                break;
            case R.id.btn_messages:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, new Conversations());
                fragmentChanged = true;
                break;
            case R.id.btn_blipbook:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, new BlipBook());
                fragmentChanged = true;
                break;
            case R.id.btn_new_blips:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.GONE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.VISIBLE);
                map.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                mSearchEditText.setText("");
                TemporaryStorageSingleton.getInstance().setSearchKeyWord("");
                fragmentTransaction.replace(R.id.fl_fragment_container, new NewBlips());
                fragmentChanged = true;
                break;
            case R.id.btn_shop:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.GONE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.VISIBLE);
                map.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                mSearchEditText.setText("");
                TemporaryStorageSingleton.getInstance().setSearchKeyWord("");
                TemporaryStorageSingleton.getInstance().setCategoryPosition(0);
                fragmentTransaction.replace(R.id.fl_fragment_container, new ShopBlips());
                fragmentChanged = true;
                break;
            case R.id.btn_faq:
                globalcount = 0;
                back.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mLogoImageView.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;
                fragmentTransaction.replace(R.id.fl_fragment_container, StillDevelopment.newInstance());
                fragmentChanged = true;
                break;
            case R.id.btn_logout:
                askForLogOut();
                break;

            case R.id.img_search:

                back.setVisibility(View.GONE);
                //   Toast.makeText(getApplicationContext(), "toast", Toast.LENGTH_SHORT).show();
                mTitleTextView.setVisibility(View.GONE);
                mLogoImageView.setVisibility(View.GONE);
                rel_search.setVisibility(View.VISIBLE);
                map.setVisibility(View.VISIBLE);

               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color, null));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color, null));
                } else {
                    selectedMenuItem.setTextColor(getResources().getColor(R.color.primary_text_color));
                    ((Button) v).setTextColor(getResources().getColor(R.color.primary_color));
                }
                selectedMenuItem = (Button) v;*/
                String text = mSearchEditText.getText().toString();
                if (text.matches("[0-9]+") && text.length() == 5) {
                    TemporaryStorageSingleton.getInstance().setZipCode(Integer.parseInt(mSearchEditText.getText().toString()));
                    TemporaryStorageSingleton.getInstance().setSearchKeyWord("");
                    TemporaryStorageSingleton.getInstance().setValueZip(1);
                    textZipValue = 1;


                } else {
                    TemporaryStorageSingleton.getInstance().setSearchKeyWord(mSearchEditText.getText().toString());
                    TemporaryStorageSingleton.getInstance().setValueZip(1);
                    textZipValue = 0;
                }

//                TemporaryStorageSingleton.getInstance().setSearchKeyWord(mSearchEditText.getText().toString());
//                TemporaryStorageSingleton.getInstance().setSearchKeyWord("");
//                TemporaryStorageSingleton.getInstance().setValueZip(1);
//                textZipValue = 0;
                fragmentTransaction.replace(R.id.fl_fragment_container, new ShopBlips());
                fragmentChanged = true;
                break;
            case R.id.img_cross:
                TemporaryStorageSingleton.getInstance().setSearchKeyWord("");
                TemporaryStorageSingleton.getInstance().setValueZip(1);
                textZipValue = 0;
                mSearchEditText.setText("");
                fragmentTransaction.replace(R.id.fl_fragment_container, new ShopBlips());
                fragmentChanged = true;
                break;

        }
        if (fragmentChanged) {
            hasFragment = true;
            fragmentTransaction.commit();
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTitleTextView.setText(titleId);
    }

    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkCameraStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation();
                } else {
                    finish();
                }
                break;
        }
    }

    private void setLocation() {

        if (gpsTracker.isgpsenabled() && gpsTracker.canGetLocation()) {

            DLatitude = String.valueOf(gpsTracker.getLatitude());
            DLongitude = String.valueOf(gpsTracker.getLongitude());
            //   Toast.makeText(getApplicationContext(), DLatitude + DLongitude, Toast.LENGTH_SHORT).show();
            //Enabling Gps Service

        }


    }

    public void changeFragment() {
        mTitleTextView.setVisibility(View.GONE);
        mLogoImageView.setVisibility(View.GONE);
        rel_search.setVisibility(View.VISIBLE);
        map.setVisibility(View.VISIBLE);


        mSearchEditText.setText("");
        TemporaryStorageSingleton.getInstance().setSearchKeyWord("");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        boolean fragmentChanged = false;
        fragmentTransaction.replace(R.id.fl_fragment_container, new ShopBlips());
        fragmentChanged = true;
    }

}
