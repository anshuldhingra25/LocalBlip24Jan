package com.ordiacreativeorg.localblip.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ordiacreativeorg.localblip.model.Blip;

import java.util.ArrayList;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/12/2015
 *
 */
public class BlipDetails extends BaseActivity {

    private ArrayList<Blip> mBlips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Blip blip = (Blip) getIntent().getSerializableExtra("blip");
        if (savedInstanceState == null) {
            changeBlip(blip);
        }
    }

    public void changeBlip(Blip blip){
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, com.ordiacreativeorg.localblip.fragment.BlipDetails.newInstance(blip, false))
                //.addToBackStack(null);
                .commit();
    }

    public void blipChanged(Blip blip){
        if (!mBlips.contains(blip)){
            mBlips.add(blip);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("action", "fix");
        ArrayList<Integer> mChangedBlipsIds = new ArrayList<>();
        ArrayList<Integer> mChangedBlipsVotes = new ArrayList<>();
        ArrayList<Integer> mChangedBlipsBBs = new ArrayList<>();
        for (Blip blip : mBlips){
            mChangedBlipsIds.add(blip.getCouponId());
            mChangedBlipsVotes.add(blip.getMyVote());
            mChangedBlipsBBs.add(blip.getAddedtobb() ? 1 : 0);
        }
        intent.putIntegerArrayListExtra("ids", mChangedBlipsIds);
        intent.putIntegerArrayListExtra("votes", mChangedBlipsVotes);
        intent.putIntegerArrayListExtra("abbs", mChangedBlipsBBs);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
