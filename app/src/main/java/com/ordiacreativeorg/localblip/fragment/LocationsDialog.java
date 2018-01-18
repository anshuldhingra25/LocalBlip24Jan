package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.Location;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/18/2015
 */
public class LocationsDialog extends DialogFragment {


    public static LocationsDialog newInstance(ArrayList<Integer> selectedIds) {
        Bundle args = new Bundle();
        args.putIntegerArrayList("locations", selectedIds);
        LocationsDialog fragment = new LocationsDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public LocationsDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    private ArrayList<Integer> selectedIds;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.subcategory_dialog, container, false);
        ((TextView) rootView.findViewById(R.id.tv_title)).setText(R.string.select_location);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        rootView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("locations", selectedIds);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        });
        final LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.ll_subcategories);
        if (getArguments() != null ) {
            final float dpi = getResources().getDisplayMetrics().density;
            selectedIds = getArguments().getIntegerArrayList("locations");
            List<Location> allLocations = TemporaryStorageSingleton.getInstance().getMemberDetails().getLocations();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, Math.round(20 * dpi + 0.5f), 0, 0);
            if(allLocations!=null)
            {
            for (final Location location : allLocations) {
                CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.cb_element, container, false);
                for (int id : selectedIds) {
                    if (id == location.getLocationId()) {
                        checkBox.setChecked(true);
                    }
                }

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            selectedIds.add(location.getLocationId());
                        } else {
                            selectedIds.remove(Integer.valueOf(location.getLocationId()));
                        }
                    }
                });


                String loc = "";
                if (!location.getAddress1().isEmpty()) {
                    loc += " " + location.getAddress1();
                }
                if (!location.getAddress2().isEmpty()) {
                    loc += "\n" + "#" + location.getAddress2();
                }
                if (!location.getCity().isEmpty()) {
                    if (!loc.isEmpty()) loc += ",\n ";
                    loc += location.getCity();
                }
                if (!location.getState().isEmpty()) {
                    if (!loc.isEmpty()) loc += ", ";
                    loc += location.getState();
                }
                if (!location.getZipCode().isEmpty()) {
                    if (!loc.isEmpty()) loc += "  ";
                    loc += location.getZipCode();
                }
                if (!location.getPhone().isEmpty()) {
                    loc += "\n" + " " + location.getPhone();
                }
                if (!loc.isEmpty()) {
                    if(loc.contains("NATIONWIDE VENDOR"))loc="Online Location";
                    checkBox.setText(loc);
                    Log.e("LOC1234",""+loc);
                }
                linearLayout.addView(checkBox, layoutParams);
            }
        }
        }
        return rootView;
    }
}
