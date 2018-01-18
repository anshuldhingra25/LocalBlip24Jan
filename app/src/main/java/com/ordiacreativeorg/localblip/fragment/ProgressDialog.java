package com.ordiacreativeorg.localblip.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/13/2015
 */
public class ProgressDialog extends DialogFragment {


    public ProgressDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog_Transparent);
    }

    private int id = -1;
    private View mRootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_progress_dialog, container, false);
        if (id < 0 ){
            mRootView.findViewById(R.id.progress_message).setVisibility(View.GONE);
        }else {
            mRootView.findViewById(R.id.progress_message).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.progress_message)).setText(id);
        }
        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);
        return mRootView;
    }

    public void setMessage(int id){
        this.id = id;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }
}
