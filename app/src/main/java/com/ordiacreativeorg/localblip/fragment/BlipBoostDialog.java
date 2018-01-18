package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ordiacreativeorg.localblip.R;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/18/2015
 */
public class BlipBoostDialog extends DialogFragment {


    public static BlipBoostDialog newInstance(int boostId) {
        Bundle args = new Bundle();
        args.putInt("boostId", boostId);
        BlipBoostDialog fragment = new BlipBoostDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public BlipBoostDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.blipboost_dialog, container, false);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        if (getArguments() != null ){
            final int boostId = getArguments().getInt("boostId");
            rootView.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("boostId", boostId);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                }
            });
        }

        return rootView;
    }
}
