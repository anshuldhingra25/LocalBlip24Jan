package com.ordiacreativeorg.localblip.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;
import com.pushwoosh.PushManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dmytrobohachevskyy on 9/26/15.
 * Rewritten by Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 * <p>
 * Fragment for editing profile information
 */
public class Profile extends Fragment {
    // static data
    private View mRootView;

    private TextInputLayout mNameTextInputLayout;
    private EditText mNameEditText;
    private Spinner mAgeSpinner;
    private SwitchCompat mGenderSwitch;
    private TextInputLayout mZipCodeTextInputLayout;
    private EditText mZipCodeEditText;
    private SwitchCompat mZipAsHomeSwitch;
    private Spinner mDistanceSpinner;
    private LinearLayout mCategoriesLayout;
    private View mButtonsLayout;
    private String[] mDistance;
    ScrollView scrollProfile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setBarTitle(R.string.nd_profile, false);
        mRootView = inflater.inflate(R.layout.profile_fragment, container, false);
        mRootView.setFocusableInTouchMode(true);
        mRootView.requestFocus();
        mRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        Log.e("lkkj", "" + 1);
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

        mDistance = getActivity().getResources().getStringArray(R.array.miles_array);
        scrollProfile = (ScrollView) mRootView.findViewById(R.id.scrollProfile);
        mNameTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_first_name);
        mNameEditText = (EditText) mRootView.findViewById(R.id.et_first_name);
        mAgeSpinner = (Spinner) mRootView.findViewById(R.id.sp_age);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.age_value)));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAgeSpinner.setAdapter(dataAdapter);
        mAgeSpinner.setSelection(2);
        mGenderSwitch = (SwitchCompat) mRootView.findViewById(R.id.sw_gender);
        mZipCodeEditText = (EditText) mRootView.findViewById(R.id.et_zipcode);
        mZipAsHomeSwitch = (SwitchCompat) mRootView.findViewById(R.id.sw_is_home_zip);
        mDistanceSpinner = (Spinner) mRootView.findViewById(R.id.sp_miles);
        mCategoriesLayout = (LinearLayout) mRootView.findViewById(R.id.ll_categories);
        mButtonsLayout = mRootView.findViewById(R.id.cv_buttons);

        // init save button
        scrollProfile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mNameEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mZipCodeEditText.getWindowToken(), 0);
                return false;
            }
        });
        mRootView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountInfo();
            }
        });

        loadProfileInfo();
        loadNotificationsSettings();

        return mRootView;
    }

    private void loadProfileInfo() {
        MemberDetail memberDetailInfo = TemporaryStorageSingleton.getInstance().getMemberDetails();
        mNameEditText.setText(memberDetailInfo.getFirstName());
        if (!memberDetailInfo.getAge().trim().isEmpty()) {
            mAgeSpinner.setSelection(Integer.valueOf(memberDetailInfo.getAge()) - 1);
        }
       // mAgeSpinner.setSelection(Integer.valueOf(memberDetailInfo.getAge()) - 1);
        if (memberDetailInfo.getGender() != null) {
            mGenderSwitch.setChecked(memberDetailInfo.getGender().equals("1"));
        }
        mZipCodeEditText.setText(memberDetailInfo.getZipCode());
        if (memberDetailInfo.isZipCodeAsHome() != null) {
            mZipAsHomeSwitch.setChecked(!memberDetailInfo.isZipCodeAsHome().equals("0"));
        } else {
            mZipAsHomeSwitch.setChecked(true);
        }
        if (memberDetailInfo.getMemberType() == 4) {
            mNameTextInputLayout.setHint(getResources().getString(R.string.business_name));
            mNameEditText.setEnabled(false);
        } else {
            mNameTextInputLayout.setHint(getResources().getString(R.string.first_name));
            mNameEditText.setEnabled(true);
        }
        if (memberDetailInfo.getNotifyDistance() != null) {
            for (int i = 0; i < mDistance.length; i++) {
                if (mDistance[i].equals(memberDetailInfo.getNotifyDistance())) {
                    mDistanceSpinner.setSelection(i);
                }
            }
        } else {
            mDistanceSpinner.setSelection(2);
        }
    }

    private void loadNotificationsSettings() {
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        MemberDetail memberDetailInfo = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Api.getInstance().getMethods().getCategory(
                memberDetailInfo.getEmail(),
                memberDetailInfo.getApiKey()
        ).enqueue(new Callback<ArrayList<Integer>>() {
            @Override
            public void onResponse(Response<ArrayList<Integer>> response, Retrofit retrofit) {
                mSelectedIds = response.body();
                showCategories(mSelectedIds);
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                mProgressDialog.dismiss();
                Snackbar.make(mRootView, getResources().getString(R.string.failed_to_get_categories) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        });

    }

    private ArrayList<Integer> mSelectedIds;

    private void showCategories(final List<Integer> selectedIds) {
        final float dpi = getResources().getDisplayMetrics().density;
        ArrayList<Category> categories = new ArrayList<>();
        categories.addAll(TemporaryStorageSingleton.getInstance().getCategories());
        categories.remove(0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, Math.round(20 * dpi + 0.5f), 0, 0);
        for (final Category category : categories) {
            CheckBox checkBox = (CheckBox) getActivity().getLayoutInflater().inflate(R.layout.cb_element, mCategoriesLayout, false);
            checkBox.setText(category.getCategoryName());
            for (int id : selectedIds) {
                if (id == category.getCategoryId()) {
                    checkBox.setChecked(true);
                }
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedIds.add(category.getCategoryId());
                    } else {
                        selectedIds.remove(new Integer(category.getCategoryId()));
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

    private void saveAccountInfo() {
        final MemberDetail memberDetailInfo = TemporaryStorageSingleton.getInstance().getMemberDetails();
        final String name = mNameEditText.getText().toString();
        final String zipcode = mZipCodeEditText.getText().toString();

        mNameTextInputLayout.setErrorEnabled(false);
        if (name.isEmpty()) {
            mNameTextInputLayout.setError(getResources().getString(R.string.enter_name));
            return;
        }

        final HashMap<String, String> params = new HashMap<>();
        params.put("age", String.valueOf(mAgeSpinner.getSelectedItemPosition() + 1));
        params.put("gender", mGenderSwitch.isChecked() ? "1" : "0");

        if (!zipcode.isEmpty()) {
            params.put("zipcode", zipcode);
            params.put("ziphome", mZipAsHomeSwitch.isChecked() ? "1" : "0");
        }

        String apikey = memberDetailInfo.getApiKey();
        String email = memberDetailInfo.getEmail();
        //m.setAge(ageValue);
        //params.put("age", ageValue.toString());

        params.put("pushtoken", PushManager.getPushToken(getActivity()));
        params.put("hwid", PushManager.getPushwooshHWID(getActivity()));
        params.put("devicetype", "3");

        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        Api.getInstance().getMethods().updateMemberDetail(
                apikey,
                email,
                email,
                name,
                params
        ).enqueue(new Callback<MemberDetail>() {
            @Override
            public void onResponse(Response<MemberDetail> response, Retrofit retrofit) {
                MemberDetail memberDetail = response.body();
                if (memberDetail.getResponse().equals("success")) {
                    Snackbar.make(mRootView, R.string.profile_updated, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                    memberDetailInfo.setFirstName(name);
                    memberDetailInfo.setAge(params.get("age"));
                    memberDetailInfo.setGender(params.get("gender"));
                    memberDetailInfo.setNotifyDistance(mDistance[mDistanceSpinner.getSelectedItemPosition()]);
                    if (!zipcode.isEmpty()) {
                        memberDetailInfo.setZipCode(zipcode);
                        memberDetailInfo.setZipCodeAsHome(params.get("ziphome"));
                    }
                } else {
                    Snackbar.make(mRootView, memberDetail.getResponse(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
                saveNotificationList();
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(mRootView, getResources().getString(R.string.profile_update_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                mProgressDialog.dismiss();
            }
        });
    }

    /**
     * Save checked file
     */
    private void saveNotificationList() {
        String categoryString = "";
        MemberDetail memberDetailInfo = TemporaryStorageSingleton.getInstance().getMemberDetails();
        for (int id : mSelectedIds) {
            categoryString += "," + id;
        }
        if (!categoryString.isEmpty()) {
            categoryString = categoryString.substring(1);
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("category", categoryString);
        params.put("distance", mDistance[mDistanceSpinner.getSelectedItemPosition()]);
        Api.getInstance().getMethods().setCategory(
                memberDetailInfo.getEmail(),
                memberDetailInfo.getApiKey(),
                params
        ).enqueue(new Callback<ArrayList<Integer>>() {
            @Override
            public void onResponse(Response<ArrayList<Integer>> response, Retrofit retrofit) {
                mProgressDialog.dismiss();
                Snackbar.make(mRootView, R.string.notifications_updated, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onFailure(Throwable t) {
                mProgressDialog.dismiss();
                Snackbar.make(mRootView, getResources().getString(R.string.notifications_update_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                loadNotificationsSettings();
            }
        });

    }
}
