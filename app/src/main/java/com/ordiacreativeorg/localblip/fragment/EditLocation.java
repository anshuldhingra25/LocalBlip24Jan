package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Location;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/24/2015
 */
public class EditLocation extends DialogFragment {

    public static EditLocation newInstance(Location location, boolean edit) {
        EditLocation fragment = new EditLocation();
        Bundle args = new Bundle();
        args.putSerializable("location", location);
        args.putBoolean("edit", edit);
        fragment.setArguments(args);
        return fragment;
    }

    public EditLocation() {
        // Required empty public constructor
    }

    private View mRootView;
    private Location mLocation;
    private SwitchCompat mTypeSwitch;
    private ScrollView scrollEditLocation;
    private View mLocationView;
    private EditText mPhoneEditText;
    private EditText mFirstAddressEditText;
    private EditText mSecondAddressEditText;
    private TextInputLayout mCityTextInputLayout;
    private EditText mCityEditText;
    private Spinner mStateSpinner;
    private TextInputLayout mZipCodeTextInputLayout;
    private EditText mZipCodeEditText;
    private TextInputLayout mWebsiteTextInputLayout;
    private EditText mWebsiteEditText;
    private EditText mDescriptionEditText;
    private View mButtonsLayout;
    private Button mNegativeButton;
    private Button mPositiveButton;

    private ProgressDialog mProgressDialog;

    private ArrayAdapter<String> mStatesAdapter;
    private boolean mEdit = false;
    private int addressStateIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getShowsDialog()) {
            mRootView = inflater.inflate(R.layout.edit_location_dialog, container, false);
            ((TextView) mRootView.findViewById(R.id.tv_title)).setText(R.string.edit_location);
        } else {
            mRootView = inflater.inflate(R.layout.edit_location, container, false);
            Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getActivity().setTitle(R.string.edit_location);

        }
        scrollEditLocation = (ScrollView) mRootView.findViewById(R.id.scrollEditLocation);
        mTypeSwitch = (SwitchCompat) mRootView.findViewById(R.id.sw_ltype);
        mLocationView = mRootView.findViewById(R.id.rl_location);
        mPhoneEditText = (EditText) mRootView.findViewById(R.id.et_phone);
        mFirstAddressEditText = (EditText) mRootView.findViewById(R.id.et_address1);
        mSecondAddressEditText = (EditText) mRootView.findViewById(R.id.et_address2);
        mCityTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_city);
        mCityEditText = (EditText) mRootView.findViewById(R.id.et_city);
        mStateSpinner = (Spinner) mRootView.findViewById(R.id.sp_state);
        mZipCodeTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_zipcode);
        mZipCodeEditText = (EditText) mRootView.findViewById(R.id.et_zipcode);
        mWebsiteTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_website);
        mWebsiteEditText = (EditText) mRootView.findViewById(R.id.et_website);
        mDescriptionEditText = (EditText) mRootView.findViewById(R.id.et_description);
        mButtonsLayout = mRootView.findViewById(R.id.cv_buttons);

        mNegativeButton = (Button) mRootView.findViewById(R.id.btn_remove);
        mPositiveButton = (Button) mRootView.findViewById(R.id.btn_edit);
        mProgressDialog = new ProgressDialog();
        scrollEditLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPhoneEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mFirstAddressEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mSecondAddressEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mCityEditText.getWindowToken(), 0);

                imm.hideSoftInputFromWindow(mZipCodeEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mWebsiteEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mDescriptionEditText.getWindowToken(), 0);


                return false;
            }
        });

        mPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mLocationView.setVisibility(View.VISIBLE);
                } else {
                    mLocationView.setVisibility(View.GONE);
                }
            }
        });

        mStatesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, TemporaryStorageSingleton.getInstance().getStatesNames());
        mStatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mStateSpinner.setAdapter(mStatesAdapter);
        mStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addressStateIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (getArguments() != null) {
            mLocation = ((Location) getArguments().getSerializable("location"));
            mEdit = getArguments().getBoolean("edit", false);
            if (mLocation != null && mLocation.getLocationId() > 0) {
                loadLocation(mLocation);
                if (mEdit) initEditMode();
                else initViewMode();
            } else {
                mEdit = true;
                mLocation = new Location();
                mLocationView.setVisibility(View.VISIBLE);
                initNewLocationView();
                initEditMode();
            }
        } else {
            mEdit = true;
            mLocation = new Location();
            mLocationView.setVisibility(View.VISIBLE);
            initNewLocationView();
            initEditMode();
        }

        return mRootView;
    }

    private void loadLocation(Location location) {
        mTypeSwitch.setChecked(location.isNationwide());
        String stateName = TemporaryStorageSingleton.getInstance().getStateNameByID(location.getState());
        if (stateName != null && !stateName.isEmpty()) {
            addressStateIndex = mStatesAdapter.getPosition(stateName);
            mStateSpinner.setSelection(addressStateIndex);
        }

        mPhoneEditText.setText(location.getPhone());
        mFirstAddressEditText.setText(location.getAddress1());
        mSecondAddressEditText.setText("#" + location.getAddress2());
        mCityEditText.setText(location.getCity());
        mZipCodeEditText.setText(location.getZipCode());
        mDescriptionEditText.setText(location.getDescription());
        mWebsiteEditText.setText(location.getWebsite());
    }

    private void applyLocation() {
        mCityTextInputLayout.setErrorEnabled(false);
        mZipCodeTextInputLayout.setErrorEnabled(false);
        mWebsiteTextInputLayout.setErrorEnabled(false);
        if (mTypeSwitch.isChecked()) {
            if (mWebsiteEditText.getText().toString().isEmpty()) {
                mWebsiteTextInputLayout.setError("Enter your web site");
                return;
            }
        } else {
            if (mCityEditText.getText().toString().isEmpty()) {
                mCityTextInputLayout.setError("Enter your city");
                return;
            }
            if (mZipCodeEditText.getText().toString().isEmpty()) {
                mZipCodeTextInputLayout.setError("Enter your zipcode");
                return;
            }
        }
        initViewMode();
        mLocation.setNationwide(mTypeSwitch.isChecked());
        mLocation.setState(null);
        if (mLocation.isNationwide()) {
            mLocation.setState("online");
        } else {
            mLocation.setState(
                    TemporaryStorageSingleton
                            .getInstance()
                            .getStatesIds()
                            .get(addressStateIndex)

            );
        }
        mLocation.setAddress1(null);
        mLocation.setAddress1(mFirstAddressEditText.getText().toString());
        mLocation.setAddress2(null);
        mLocation.setAddress2(mSecondAddressEditText.getText().toString().replace("#",""));
        mLocation.setCity(null);
        mLocation.setCity(mCityEditText.getText().toString());
        mLocation.setPhone(null);
        mLocation.setPhone(mPhoneEditText.getText().toString());
        mLocation.setDescription(null);
        mLocation.setDescription(mDescriptionEditText.getText().toString());
        mLocation.setZipCode(null);
        mLocation.setZipCode(mZipCodeEditText.getText().toString());
        mLocation.setWebsite(null);
        mLocation.setWebsite(mWebsiteEditText.getText().toString());
        saveLocation();
    }

    private void initNewLocationView() {
        mTypeSwitch.setChecked(false);
        mLocation.setNationwide(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getShowsDialog()) {
            dismiss();
        } else {
            getActivity().finish();
        }
    }

    private void initViewMode() {
        getActivity().setTitle(getResources().getString(R.string.view_location));
        mTypeSwitch.setEnabled(false);
        mPhoneEditText.setEnabled(false);
        mFirstAddressEditText.setEnabled(false);
        mSecondAddressEditText.setEnabled(false);
        mCityEditText.setEnabled(false);
        mStateSpinner.setEnabled(false);
        mZipCodeEditText.setEnabled(false);
        mWebsiteEditText.setEnabled(false);
        mDescriptionEditText.setEnabled(false);

        mPositiveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_edit, 0, 0, 0);
        mPositiveButton.setText(R.string.edit);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initEditMode();
            }
        });
        mNegativeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_remove, 0, 0, 0);
        mNegativeButton.setText(R.string.remove);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("action", "remove");
                intent.putExtra("target", mLocation);
                if (getShowsDialog()) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                } else {
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            }
        });
    }

    private void initEditMode() {
        getActivity().setTitle(R.string.edit_location);
        mTypeSwitch.setEnabled(true);
        mPhoneEditText.setEnabled(true);
        mFirstAddressEditText.setEnabled(true);
        mSecondAddressEditText.setEnabled(true);
        mCityEditText.setEnabled(true);
        mStateSpinner.setEnabled(true);
        mZipCodeEditText.setEnabled(true);
        mWebsiteEditText.setEnabled(true);
        mDescriptionEditText.setEnabled(true);

        mPositiveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_apply, 0, 0, 0);
        mPositiveButton.setText(R.string.save);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyLocation();
            }
        });
        mNegativeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_cancel, 0, 0, 0);
        mNegativeButton.setText(android.R.string.cancel);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEdit) {
                    getActivity().onBackPressed();
                } else {
                    loadLocation(mLocation);
                    initViewMode();
                }
            }
        });
    }

    private void saveLocation() {
        final MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        HashMap<String, String> params = new HashMap<>();
        boolean newLocation = mLocation.getLocationId() < 0;
        if (mLocation.isNationwide()) {
            params.put("website", mLocation.getWebsite());
        } else {
            params.put("city", String.valueOf(mLocation.getCity()));
            params.put("zipcode", mLocation.getZipCode());
            if (!mLocation.getAddress1().isEmpty()) params.put("address1", mLocation.getAddress1());
            if (!mLocation.getAddress2().isEmpty()) params.put("address2", mLocation.getAddress2());
            if (!mLocation.getWebsite().isEmpty()) params.put("website", mLocation.getWebsite());
        }
        params.put("state", mLocation.getState());
        if (!mLocation.getPhone().isEmpty()) params.put("phone", mLocation.getPhone());
        if (!mLocation.getDescription().isEmpty())
            params.put("description", mLocation.getDescription());

        Callback<Location> callback = new Callback<Location>() {
            @Override
            public void onResponse(Response<Location> response, Retrofit retrofit) {
                Api.getInstance().getMethods().getMembersDetails(
                        memberDetail.getEmail(),
                        memberDetail.getApiKey(),
                        new HashMap()
                ).enqueue(new Callback<MemberDetail>() {
                    @Override
                    public void onResponse(Response<MemberDetail> response, Retrofit retrofit) {
                        TemporaryStorageSingleton.getInstance().setMemberDetails(response.body());
                        mProgressDialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("action", "reload");
                        intent.putExtra("message", (mLocation.getLocationId() < 0 ? getResources().getString(R.string.location_created) : getResources().getString(R.string.location_updated)));
                        if (getShowsDialog()) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            dismiss();
                        } else {
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(mRootView, getResources().getString(R.string.operation_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(mRootView, t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                mProgressDialog.dismiss();
                if (getShowsDialog()) {
                    dismiss();
                } else {
                    getActivity().finish();
                }
            }
        };

        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        if (newLocation) {
            Call<Location> call = Api.getInstance().getMethods().createLocation(
                    memberDetail.getEmail(),
                    memberDetail.getApiKey(),
                    params
            );
            call.enqueue(callback);
        } else {
            Api.getInstance().getMethods().updateLocation(
                    memberDetail.getEmail(),
                    memberDetail.getApiKey(),
                    String.valueOf(mLocation.getLocationId()),
                    params
            ).enqueue(callback);
        }
    }
}
