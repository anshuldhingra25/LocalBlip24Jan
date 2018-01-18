package com.ordiacreativeorg.localblip.activity;

import android.os.Bundle;

import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.Location;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/12/2015
 *
 */
public class EditBlipDetails extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Blip blip = (Blip) getIntent().getSerializableExtra("blip");
        Location location = (Location) getIntent().getSerializableExtra("location");
        boolean edit = getIntent().getBooleanExtra("edit", false);
        boolean first =getIntent().getBooleanExtra("first", false);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, com.ordiacreativeorg.localblip.fragment.EditBlipDetails.newInstance(blip, location, edit,first))
                    .commit();
        }
    }
}
