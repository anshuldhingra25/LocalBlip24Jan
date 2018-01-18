package com.ordiacreativeorg.localblip.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ordiacreativeorg.localblip.R;

/**
 * Created by dmytrobohachevskyy on 10/6/15.
 *
 * This fragment show that this part is not done yet
 */
public class StillDevelopment extends Fragment {

    public static StillDevelopment newInstance() {
        return new StillDevelopment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.developing_fragment, container, false);
    }


}
