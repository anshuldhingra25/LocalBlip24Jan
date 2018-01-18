package com.ordiacreativeorg.localblip.fragment;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.LogInActivity;
import com.ordiacreativeorg.localblip.constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class SIgnUpSecond extends Fragment {


    private LogInActivity delegate;
    private Spinner mAgeSpinner;
    private Spinner mGenderSpinner;
    private TextInputLayout mBusinessTextInputLayout, mNameTextInputLayout;
    private EditText mBusinessEditText, mNameEditText;
    public static String FAGE = "", FGENDER = "";
    public static int agevalue = 0, gendervalue = 0;


    private int userTypeNum;
    private Constants.UserType userType;

    public static SIgnUpSecond newInstance() {
        return new SIgnUpSecond();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sign_up_second, container, false);

        ((LogInActivity) getActivity()).setBarTitle(R.string.sign_up, true);


        mNameTextInputLayout = (TextInputLayout) rootView.findViewById(R.id.til_name);
        mNameEditText = (EditText) rootView.findViewById(R.id.et_name);

        mAgeSpinner = (Spinner) rootView.findViewById(R.id.sp_age);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.age_value)));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, list);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAgeSpinner.setAdapter(dataAdapter);
        mAgeSpinner.setSelection(2);

        mGenderSpinner = (Spinner) rootView.findViewById(R.id.sp_gender);


        ArrayList<String> genderList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.gender)));
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, genderList);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(genderAdapter);
        mGenderSpinner.setSelection(0);

        rootView.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check correct user input

                FAGE = String.valueOf(mAgeSpinner.getSelectedItemPosition() + 1);
                FGENDER = String.valueOf(mGenderSpinner.getSelectedItemPosition());
                agevalue = mAgeSpinner.getSelectedItemPosition() + 1;
                gendervalue = (mGenderSpinner.getSelectedItemPosition());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_fragment_container, new SignUpProfile().newInstance())
                        .addToBackStack("signUp")
                        .commit();


            }
        });


        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        delegate = (LogInActivity) getActivity();
        delegate.setBarTitle(R.string.sign_up, true);

        /*Bundle bundle = this.getArguments();
        if (bundle != null) {
            userTypeNum = bundle.getInt(USER_TYPE_KEY, -1);

            switch (userTypeNum) {
                case 4:
                    this.userType = Constants.UserType.VENDOR;
                    mBusinessTextInputLayout.setHint(getResources().getString(R.string.business_name));
                    break;

                case 3:
                    this.userType = Constants.UserType.CONSUMER;
                    mBusinessTextInputLayout.setHint(getResources().getString(R.string.first_name));
                    break;
            }*/
    }
    // get type of sign up: vendor or customer

}


