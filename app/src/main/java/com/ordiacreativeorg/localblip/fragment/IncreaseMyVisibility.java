package com.ordiacreativeorg.localblip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 */
public class IncreaseMyVisibility extends Fragment {

    private ProgressDialog mProgressDialog;
    private RelativeLayout alert, mail, picks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.visibility, container, false);
        mProgressDialog = new ProgressDialog();
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        Log.e("lkkj",""+1);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
//                        getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, new ShopBlips()).addToBackStack(null).commit();
//                        ((MainActivity) getActivity()).changeFragment();
//                        acc.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;

            }
        });
        alert = (RelativeLayout) rootView.findViewById(R.id.alery_layout);
        mail = (RelativeLayout) rootView.findViewById(R.id.mail_layout);
        picks = (RelativeLayout) rootView.findViewById(R.id.pick_layout);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fl_fragment_container, new Blip_Alert());
                ft.addToBackStack(null);
                ft.commit();
//                Fragment alert = new Blip_Alert();
//                getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, alert).commit();

            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fl_fragment_container, new Blip_Mail());
                ft.addToBackStack(null);
                ft.commit();
//                Fragment mail = new Blip_Mail();
////
//                getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, mail).commit();
            }
        });
        picks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fl_fragment_container, new Blip_Picks());
                ft.addToBackStack(null);
                ft.commit();

            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle(R.string.nd_visibility, false);
    }
}
