package com.ordiacreativeorg.localblip.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.util.ArchiveManager;

import java.util.HashMap;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/12/2015
 *
 */
public class BlipDetailsStandAlone extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Blip blip = (Blip) getIntent().getSerializableExtra("blip");
        HashMap<String, String> lastSession = new HashMap<>();
        ArchiveManager.getSession(getApplicationContext(), lastSession);
        if (lastSession.size() > 0) {
            if (savedInstanceState == null) {
                changeBlip(blip);
            }
        } else {
            final Intent intent = new Intent(this, LogInActivity.class );
            startActivity(intent);
            finish();
        }
    }

    public void changeBlip(Blip blip){
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, com.ordiacreativeorg.localblip.fragment.BlipDetails.newInstance(blip, true))
                //.addToBackStack(null);
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void blipChanged(Blip blip){
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("fragment", "newBlips");
        startActivity(resultIntent);
    }
}