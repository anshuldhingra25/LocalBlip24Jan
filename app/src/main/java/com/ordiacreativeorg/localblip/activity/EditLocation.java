package com.ordiacreativeorg.localblip.activity;

import android.os.Bundle;

import com.ordiacreativeorg.localblip.model.Location;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/25/2015
 *
 */
public class EditLocation extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Location location = (Location) getIntent().getSerializableExtra("location");
        Boolean edit = getIntent().getBooleanExtra("edit", false);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, com.ordiacreativeorg.localblip.fragment.EditLocation.newInstance(location, edit))
                    .commit();
        }
    }
}
