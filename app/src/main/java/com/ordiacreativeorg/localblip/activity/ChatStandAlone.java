package com.ordiacreativeorg.localblip.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ordiacreativeorg.localblip.util.ArchiveManager;

import java.util.HashMap;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/25/2015
 *
 */
public class ChatStandAlone extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String senderName = getIntent().getStringExtra("senderName");
        String senderEmail = getIntent().getStringExtra("senderEmail");
        boolean blipAlert = getIntent().getBooleanExtra("blipAlert", false);
        HashMap<String, String> lastSession = new HashMap<>();
        ArchiveManager.getSession(getApplicationContext(), lastSession);
        if (lastSession.size() > 0) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, com.ordiacreativeorg.localblip.fragment.Chat.newInstance(senderName, senderEmail, blipAlert))
                        .commit();
            }
        } else {
            final Intent intent = new Intent( this, LogInActivity.class );
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("fragment", "messages");
        startActivity(resultIntent);
    }
}