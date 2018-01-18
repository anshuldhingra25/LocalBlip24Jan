package com.ordiacreativeorg.localblip.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/25/2015
 *
 */
public class Chat extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String senderName = getIntent().getStringExtra("senderName");
        String senderEmail = getIntent().getStringExtra("senderEmail");
        boolean blipAlert = getIntent().getBooleanExtra("blipAlert", false);

        if (TemporaryStorageSingleton.getInstance().getMemberDetails() == null){
            final Intent intent = new Intent( this, LogInActivity.class );
            startActivity(intent);
            finish();
        }else {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, com.ordiacreativeorg.localblip.fragment.Chat.newInstance(senderName, senderEmail, blipAlert))
                        .commit();
            }
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("action", "reload");
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }
}
