package com.ordiacreativeorg.localblip;

import android.app.Application;

import com.ordiacreativeorg.localblip.util.ForegroundUtil;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ForegroundUtil.init( this );
    }
}
