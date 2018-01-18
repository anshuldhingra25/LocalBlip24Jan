package com.ordiacreativeorg.localblip.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.CommonUtil;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;
import com.pushwoosh.PushManager;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 12/1/2015
 * <p>
 * This fragment allow vedor to update him profile
 */
public class Account extends Fragment {
    private View mRootView;

    private LinearLayout mDescriptionTextInputLayout;
    private EditText mDescriptionEditText;
    private TextInputLayout mNameTextInputLayout;
    private EditText mNameEditText;
    private TextInputLayout mEmailTextInputLayout;
    private EditText mEmailEditText;
    private TextInputLayout mPhoneTextInputLayout;
    private EditText mPhoneEditText;
    private TextInputLayout mPasswordTextInputLayout;
    private EditText mPasswordEditText;
    private TextInputLayout mConfirmPasswordTextInputLayout;
    private EditText mConfirmPasswordEditText;
    private ScrollView scroll_Account;
    private RelativeLayout rel_Account;
    private TextView txt_limit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.account_fragment, container, false);
        ((MainActivity) getActivity()).setBarTitle(R.string.nd_account, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
        rel_Account = (RelativeLayout) mRootView.findViewById(R.id.rel_Account);

        mDescriptionTextInputLayout = (LinearLayout) mRootView.findViewById(R.id.til_description);
        mDescriptionEditText = (EditText) mRootView.findViewById(R.id.et_description);
        txt_limit = (TextView) mRootView.findViewById(R.id.txt_limit);
        mNameTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_first_name);
        mNameEditText = (EditText) mRootView.findViewById(R.id.et_first_name);
        mEmailTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_email);
        mEmailEditText = (EditText) mRootView.findViewById(R.id.et_email);
        mPhoneTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_phone);
        mPhoneEditText = (EditText) mRootView.findViewById(R.id.et_phone);
        mPasswordTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_new_password);
        mPasswordEditText = (EditText) mRootView.findViewById(R.id.et_new_password);
        mConfirmPasswordTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_confirm_password);
        mConfirmPasswordEditText = (EditText) mRootView.findViewById(R.id.et_confirm_password);
        scroll_Account = (ScrollView) mRootView.findViewById(R.id.scroll_Account);
        mPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mDescriptionEditText.addTextChangedListener(mTextEditorWatcher);
        scroll_Account.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mNameEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mEmailEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPhoneEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mConfirmPasswordEditText.getWindowToken(), 0);

                return false;
            }
        });
        rel_Account.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mNameEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mEmailEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPhoneEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mConfirmPasswordEditText.getWindowToken(), 0);

                return false;
            }
        });
        mRootView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ok = true;
                mEmailTextInputLayout.setErrorEnabled(false);
                mPasswordTextInputLayout.setErrorEnabled(false);
                mConfirmPasswordTextInputLayout.setErrorEnabled(false);
                if (!mPasswordEditText.getText().toString().isEmpty()) {
                    // user want to update password
                    String password = mPasswordEditText.getText().toString();
                    String confirmPassword = mConfirmPasswordEditText.getText().toString();

                    // check equals password
                    if (!password.equals(confirmPassword) || mPasswordEditText.getText().toString().isEmpty() ||
                            mConfirmPasswordEditText.getText().toString().isEmpty()) {
                        ok = false;
                        mPasswordTextInputLayout.setError(getResources().getString(R.string.passwords_should_be_equal));
                        mConfirmPasswordTextInputLayout.setError(getResources().getString(R.string.passwords_should_be_equal));
                    }
                }
                // check email
                if (mEmailEditText.getText().toString().trim().isEmpty()) {
                    ok = false;
                    mEmailTextInputLayout.setError(getResources().getString(R.string.enter_email));
                } else if (!CommonUtil.validateEmail(mEmailEditText.getText().toString())) {
                    ok = false;
                    mEmailTextInputLayout.setError(getResources().getString(R.string.enter_valid_email));
                }
                if (ok) {
                    saveAccountInfo();
                }
            }
        });

        loadAccountInfo();
        return mRootView;
    }

    private void loadAccountInfo() {
        MemberDetail memberDetails = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Log.e("Desc1", "" + memberDetails.getBusinessdesc());
        if (memberDetails.getMemberType() == 4) {
            mPhoneEditText.setText(memberDetails.getPhone());
            mNameTextInputLayout.setHint(getResources().getString(R.string.business_name));
        } else {
            mPhoneTextInputLayout.setVisibility(View.GONE);
            mNameTextInputLayout.setHint(getResources().getString(R.string.first_name));
        }
        mEmailEditText.setText(memberDetails.getEmail());
        mNameEditText.setText(memberDetails.getFirstName());
        mDescriptionEditText.setText(memberDetails.getBusinessdesc());
    }

    /**
     * Send request for saving account info
     */
    private void saveAccountInfo() {
        final MemberDetail memberDetailInfo = TemporaryStorageSingleton.getInstance().getMemberDetails();
        String email = memberDetailInfo.getEmail();
        String apiKey = memberDetailInfo.getApiKey();

        final String name = mNameEditText.getText().toString().trim().isEmpty() ? memberDetailInfo.getFirstName() : mNameEditText.getText().toString().trim();
        final String newEmail = mEmailEditText.getText().toString().trim().isEmpty() ? email : mEmailEditText.getText().toString().trim();
        final String phone = mPhoneEditText.getText().toString().trim();
        final String description = mDescriptionEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        if (!phone.isEmpty() && memberDetailInfo.getMemberType() == 4) {
            params.put("phone", phone);
        }

        if (!password.isEmpty()) {
            params.put("newpassword", password);
        }

        params.put("pushtoken", PushManager.getPushToken(getActivity()));
        params.put("hwid", PushManager.getPushwooshHWID(getActivity()));
        params.put("devicetype", "3");
        Log.e("description", description);
        final ProgressDialog progress = new ProgressDialog();
        progress.show(getActivity().getSupportFragmentManager(), "progress");
        Api.getInstance().getMethods().updateMemberDetail(
                apiKey,
                email,
                newEmail,
                name,
                description,
                params
        ).enqueue(new Callback<MemberDetail>() {
            @Override
            public void onResponse(Response<MemberDetail> response, Retrofit retrofit) {
                MemberDetail memberDetail = response.body();
                Log.e("Desc", "" + memberDetail.getBusinessdesc());
                Log.e("Email", "" + memberDetail.getEmail());

                if (memberDetail.getResponse().equals("success")) {
                    Snackbar.make(mRootView, R.string.saved, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                } else {
                    Snackbar.make(mRootView, memberDetail.getResponse(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }

                memberDetailInfo.setFirstName(name);
                memberDetailInfo.setEmail(newEmail);
                memberDetailInfo.setBusinessdesc(description);
                if (!phone.isEmpty()) memberDetailInfo.setPhone(phone);
                progress.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(mRootView, getResources().getString(R.string.save_failed) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                progress.dismiss();
            }
        });
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            txt_limit.setText(600 - s.toString().length() + "/600");
        }

        public void afterTextChanged(Editable s) {
        }
    };
}
