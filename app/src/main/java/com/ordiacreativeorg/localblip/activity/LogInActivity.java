package com.ordiacreativeorg.localblip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.fragment.SignInFragment;

/**
 * Created by dmytrobohachevskyy on 9/25/15.
 * <p>
 * Rewritten by Sergey Mitrofanov (goretz.m@gmail.com) on 11/27/2015
 */
public class LogInActivity extends BaseActivity {

    private boolean loadingData = false;
    public static String firstTime = "first";


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.log_in_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_appbar);
        setSupportActionBar(toolbar);
        if (bundle != null) {

            loadingData = bundle.getBoolean("loadingData", false);
        } else {
            resetScreen(false, null);
        }
    }

    private boolean loadingStopped = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (loadingData) {
            setLoadingData(true);
        } else if (loadingStopped) {
            openMainActivity();
        }
        loadingStopped = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loadingData)
            loadingStopped = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loadingData", loadingData);
    }

    public void resetScreen(boolean byDataLoader, String message) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, SignInFragment.newInstance())
                .commit();
        if (byDataLoader) {
            Snackbar.make(findViewById(R.id.tb_appbar), getResources().getString(R.string.failed_to_get_data) + message, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }

    public void openMainActivity() {
        if (!loadingStopped) {
            final Intent mainActivityIntent = new Intent(LogInActivity.this, MainActivity.class);
            mainActivityIntent.putExtras(getIntent());
            startActivity(mainActivityIntent);
            finish();
        }
    }

    public void openMainActivityFirstTime() {
        if (!loadingStopped) {
            final Intent mainActivityIntent = new Intent(LogInActivity.this, MainActivity.class);
            mainActivityIntent.putExtras(getIntent());
            mainActivityIntent.putExtra("firstTime", true);
            startActivity(mainActivityIntent);
            finish();
        }
    }

    public boolean isLoadingData() {
        return loadingData;
    }

    public void setLoadingData(boolean loadingData) {
        setLoadingData(loadingData, true, null);
    }

    public void setLoadingData(boolean loadingData, String message) {
        setLoadingData(loadingData, true, message);
    }

    public void setLoadingData(boolean loadingData, boolean restoreContent) {
        setLoadingData(loadingData, restoreContent, null);
    }

    public void setLoadingData(boolean loadingData, boolean restoreContent, String message) {
        this.loadingData = loadingData;
        View loginLinearLayout = findViewById(R.id.ll_content);
//        View popupLayout = findViewById(R.id.popup);
//        View logo = findViewById(R.id.iv_logo);
        View restoreProgressBar = findViewById(R.id.pb_working);
        if (message != null) {
            Snackbar.make(findViewById(R.id.tb_appbar), getResources().getString(R.string.failed_to_get_data) + message, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
       /* if (loginLinearLayout != null && restoreProgressBar != null) {
            if (loadingData) {
                loginLinearLayout.setVisibility(View.GONE);
                popupLayout.setVisibility(View.GONE);
                restoreProgressBar.setVisibility(View.VISIBLE);
            } else {
                if (restoreContent) popupLayout.setVisibility(View.VISIBLE);
                loginLinearLayout.setVisibility(View.GONE);
                restoreProgressBar.setVisibility(View.GONE);
                logo.setVisibility(View.GONE);
            }
        }*/
        if (loginLinearLayout != null && restoreProgressBar != null) {
            if (loadingData) {
                loginLinearLayout.setVisibility(View.GONE);
                restoreProgressBar.setVisibility(View.VISIBLE);
            } else {
                if (restoreContent) loginLinearLayout.setVisibility(View.VISIBLE);
                restoreProgressBar.setVisibility(View.GONE);
            }
        }
    }
}
