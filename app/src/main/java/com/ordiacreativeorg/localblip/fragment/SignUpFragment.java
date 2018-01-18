package com.ordiacreativeorg.localblip.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.LogInActivity;
import com.ordiacreativeorg.localblip.constants.Constants;
import com.ordiacreativeorg.localblip.util.CommonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by dmytrobohachevskyy on 9/25/15.
 * Rewritten by Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 * <p>
 * Allow user to sign up us vendor or customer
 */
public class SignUpFragment extends InitialFragment implements LocationListener {
    // static info
    static private final String USER_TYPE_KEY = "USER_TYPE_FOR_SIGN_UP";
    public static String type = "", FEMAIL = "", FPASSWORD = "", FNAME = "", FZIP = "0";
    private View mRootView;
    private LogInActivity delegate;
    private ScrollView scrollSignUp;
    protected LocationManager locationManager;

    final int PERMISSION_REQUEST_CODE = 111;
    // data fields
    private Constants.UserType userType;
    private int userTypeNum;

    // ui elements
    private TextInputLayout mEmailTextInputLayout;
    private EditText mEmailEditText;
    private TextInputLayout mFirstNameTextInputLayout;
    private EditText mFirstNameEditText;
    private TextInputLayout mPasswordTextInputLayout;
    private EditText mPasswordEditText;
    private TextInputLayout mConfirmPasswordTextInputLayout;
    private EditText mConfirmPasswordEditText;
    private EditText mZipCodeEditText;
    private EditText mPhoneEditText;
    private double latitude = 0; // current lat
    private double longitude = 0; // current long


    public static SignUpFragment newInstance(Constants.UserType userType) {
        Bundle bundle = new Bundle();
        switch (userType) {
            case VENDOR:

                bundle.putInt(USER_TYPE_KEY, 4);
                type = "4";
                break;

            case CONSUMER:
                bundle.putInt(USER_TYPE_KEY, 3);
                type = "3";
                break;
        }

        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sign_up_fragment, container, false);

        // init edit text fields
        mEmailTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_email);
        mEmailEditText = (EditText) mRootView.findViewById(R.id.et_email);
        mFirstNameTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_first_name);
        mFirstNameEditText = (EditText) mRootView.findViewById(R.id.et_first_name);
        mPasswordTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_password);
        mPasswordEditText = (EditText) mRootView.findViewById(R.id.et_password);
        mConfirmPasswordTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_confirm_password);
        mConfirmPasswordEditText = (EditText) mRootView.findViewById(R.id.et_confirm_password);
        mZipCodeEditText = (EditText) mRootView.findViewById(R.id.et_zipcode);
        mPhoneEditText = (EditText) mRootView.findViewById(R.id.et_phone);
        scrollSignUp= (ScrollView) mRootView.findViewById(R.id.scrollSignUp);
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (!checkAccessFineLocationPermission() ||
                    !checkAccessCoarseLocationPermission()
                    ) {
                requestPermission();
            } else {
                address();
            }
        } else {
            address();
        }
        scrollSignUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEmailEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mFirstNameEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mConfirmPasswordEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mZipCodeEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPhoneEditText.getWindowToken(), 0);




                return false;
            }
        });
        // init sign up button
        mRootView.findViewById(R.id.btn_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check correct user input
                if (validateInputInfo()) {
                    String email = mEmailEditText.getText().toString().trim();
                    String password = mPasswordEditText.getText().toString();
                    String firstName = mFirstNameEditText.getText().toString().trim();
                    String zipCode = mZipCodeEditText.getText().toString().trim();
                    String phone = mPhoneEditText.getText().toString().trim();
                    FEMAIL = email;
                    FPASSWORD = password;
                    FNAME = firstName;
                   /* SignUpFragment ldf = new SignUpFragment();
                    Bundle args = new Bundle();
                    args.putString("email", email);
                    args.putString("password", password);
                    args.putString("firstName", firstName);
                    args.putString("type", type);
                    ldf.setArguments(args);*/
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_fragment_container, SIgnUpSecond.newInstance())
                            .addToBackStack("signUp")
                            .commit();

/*

                    HashMap<String,String> params = new HashMap<>();
                    if (!zipCode.isEmpty()){
                        params.put("zipcode",zipCode);
                    }
                    if (!phone.isEmpty()){
                        params.put("phone",zipCode);
                    }

                    final String allCategories = getString(R.string.all_categories);
                    final List<String> states = Arrays.asList(getResources().getStringArray(R.array.states_list));
                    delegate.setLoadingData(true);
                    Api.getInstance().getMethods().createMember(
                            email,
                            password,
                            userTypeNum,
                            firstName,
                            params
                    ).enqueue(new Callback<MemberDetail>() {
                        @Override
                        public void onResponse(Response<MemberDetail> response, Retrofit retrofit) {
                            MemberDetail m = response.body();
                            if (null == m.getResponse()) {
                                m.setAge("");
                                TemporaryStorageSingleton.getInstance().setMemberDetails(m);
                                ArchiveManager.saveSession(getActivity().getApplicationContext(), m.getEmail(), m.getApiKey(), m.getMemberId());
                                loadCategories(allCategories, states);
                            } else {
                                delegate.setLoadingData(false);
                                Snackbar.make(mRootView, m.getResponse(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            delegate.setLoadingData(false);
                            Snackbar.make(mRootView, getResources().getString(R.string.operation_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                        }
                    });
                }
*/
                }
            }
        });

        return this.mRootView;
    }

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

        // get type of sign up: vendor or customer
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userTypeNum = bundle.getInt(USER_TYPE_KEY, -1);

            switch (userTypeNum) {
                case 4:
                    this.userType = Constants.UserType.VENDOR;
                    mFirstNameTextInputLayout.setHint(getResources().getString(R.string.business_name));
                    break;

                case 3:
                    this.userType = Constants.UserType.CONSUMER;
                    mFirstNameTextInputLayout.setHint(getResources().getString(R.string.first_name));
                    break;
            }
        }
    }

    /**
     * Check correcting user input
     *
     * @return true if all ok
     */
    private boolean validateInputInfo() {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();
        String firstName = mFirstNameEditText.getText().toString().trim();

        mEmailTextInputLayout.setErrorEnabled(false);
        mFirstNameTextInputLayout.setErrorEnabled(false);
        mPasswordTextInputLayout.setErrorEnabled(false);
        mConfirmPasswordTextInputLayout.setErrorEnabled(false);

        boolean valid = true;

        // check if not empty
        if (password.isEmpty()) {
            mPasswordTextInputLayout.setError(getResources().getString(R.string.enter_password));
            valid = false;
        }

        if (confirmPassword.isEmpty()) {
            mConfirmPasswordTextInputLayout.setError(getResources().getString(R.string.repeat_password));
            valid = false;
        }

        // check equals password
        if (valid && !password.equals(confirmPassword)) {
            mPasswordTextInputLayout.setError(getResources().getString(R.string.passwords_should_be_equal));
            mConfirmPasswordTextInputLayout.setError(getResources().getString(R.string.passwords_should_be_equal));
            valid = false;
        }

        if (email.isEmpty()) {
            mEmailTextInputLayout.setError(getResources().getString(R.string.enter_email));
            valid = false;
        } else if (!CommonUtil.validateEmail(email)) {
            mEmailTextInputLayout.setError(getResources().getString(R.string.enter_valid_email));
            valid = false;
        }

        if (firstName.isEmpty()) {
            mFirstNameTextInputLayout.setError(getResources().getString(R.string.enter_name));
            valid = false;
        }

        return valid;
    }

    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                address();
                break;
        }
    }

    private void address() {

        // getting Lattitude and Longtitude (Start)

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationManager lManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean netEnabled = lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (netEnabled) {
            lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            Location location = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                Log.e("lat", "lat" + latitude);
                longitude = location.getLongitude();
                Log.e("lon", "" + longitude);
            }
        }

        //End Lat / Long
        Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                FZIP = returnedAddress.getPostalCode();
                Log.e("ZIP", "" + FZIP);

            } else {
                FZIP = "0";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
