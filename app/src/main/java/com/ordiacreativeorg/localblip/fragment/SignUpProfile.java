package com.ordiacreativeorg.localblip.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.LogInActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.ArchiveManager;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.R.id.list;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpProfile extends InitialFragment {
    private View mRootView;

    private TextInputLayout mNameTextInputLayout;

    private Spinner sp_miles;

    private LinearLayout mCategoriesLayout;
    private View mButtonsLayout;
    private String[] mDistance;
    private List<Integer> ids;
    private LogInActivity delegate;


    public static SignUpProfile newInstance() {
        return new SignUpProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // ((LogInActivity) getActivity()).setBarTitle(R.string.sign_up, true);
        mDistance = getActivity().getResources().getStringArray(R.array.miles_array);
        mRootView = inflater.inflate(R.layout.sign_up_profile, container, false);
        mCategoriesLayout = (LinearLayout) mRootView.findViewById(R.id.ll_categories);
        mButtonsLayout = mRootView.findViewById(R.id.cv_buttons);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_miles = (Spinner) mRootView.findViewById(R.id.sp_miles);
        ids = new ArrayList<>();
        ids.add(0);
        ids.add(1);
        ids.add(3);
        showCategories(ids);
        sp_miles.setSelection(2);


        // init save button
        mRootView.findViewById(R.id.btn_save_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String allCategories = getString(R.string.all_categories);
                final List<String> states = Arrays.asList(getResources().getStringArray(R.array.states_list));
                delegate.setLoadingData(true);
                Api.getInstance().getMethods().createMemberNew(
                        SignUpFragment.FEMAIL,
                        SignUpFragment.FPASSWORD,
                        SignUpFragment.type,
                        SIgnUpSecond.FAGE,
                        SIgnUpSecond.FGENDER,
                        SignUpFragment.FZIP,
                        SignUpFragment.FNAME
                ).enqueue(new Callback<MemberDetail>() {
                    @Override
                    public void onResponse(Response<MemberDetail> response, Retrofit retrofit) {
                        MemberDetail m = response.body();
                        if (null == m.getResponse()) {
                            m.setAge(String.valueOf(SIgnUpSecond.agevalue));
                            m.setGender(String.valueOf(SIgnUpSecond.gendervalue));
                            m.setGender(String.valueOf(SIgnUpSecond.gendervalue));
                            m.setNotifyDistance(mDistance[sp_miles.getSelectedItemPosition()]);
                            Log.e("ZIPValue", SignUpFragment.FZIP);
                            m.setZipCode(SignUpFragment.FZIP);
                            m.setZipCodeAsHome("1");
                            System.out.println("Age----" + m.getAge());
                            TemporaryStorageSingleton.getInstance().setMemberDetails(m);
                            ArchiveManager.saveSession(getActivity().getApplicationContext(), m.getEmail(), m.getApiKey(), m.getMemberId());
                            loadCategories(allCategories, states);
                        } else {
                            delegate.setLoadingData(false);
                            Snackbar.make(mRootView, m.getResponse(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        delegate.setLoadingData(false);
                        Snackbar.make(mRootView, getResources().getString(R.string.operation_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                    }
                });

            }
        });

        return this.mRootView;
    }

    private ArrayList<Integer> mSelectedIds;

    private void showCategories(final List<Integer> selectedIds) {
        final float dpi = getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, Math.round(20 * dpi + 0.5f), 0, 0);
        final ArrayList<String> strings = new ArrayList<>();
        strings.add("Auto / Marine / Industrial");
        strings.add("Clothing / Accessories / Jewelry");
        strings.add("Electronics & Computers");
        strings.add("Entertainment & Recreation");
        strings.add("Food & Drink");
        strings.add("Handmade Items / Crafts");
        strings.add("Health & Beauty");
        strings.add("Home / Garden / Tools");
        strings.add("Lodgings");
        strings.add("Pet Care");
        strings.add("Professional Services");

        for (int i = 0; i < strings.size(); i++) {
            CheckBox checkBox = (CheckBox) getActivity().getLayoutInflater().inflate(R.layout.cb_element, mCategoriesLayout, false);

            checkBox.setText(strings.get(i));
            checkBox.setChecked(true);
/*
            for (int id : selectedIds) {
                if (id == i) {
                    checkBox.setChecked(true);
                }
            }*/

            final int finalI = i;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedIds.add(strings.indexOf(finalI));
                    } else {
                        selectedIds.remove(Integer.valueOf(strings.indexOf(finalI)));
                    }
                }
            });
            mCategoriesLayout.addView(checkBox, layoutParams);
        }
    }

    /**
     * Send request for saving account info
     */
    private final ProgressDialog mProgressDialog = new ProgressDialog();

    @Override
    protected void loadFinished() {
        super.loadFinished();
        delegate.setLoadingData(false, false);
        delegate.openMainActivityFirstTime();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        delegate = (LogInActivity) getActivity();
        delegate.setBarTitle(R.string.sign_up, true);


    }
}