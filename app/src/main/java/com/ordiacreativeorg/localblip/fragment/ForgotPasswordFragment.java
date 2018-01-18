package com.ordiacreativeorg.localblip.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.LogInActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.SimpleResponse;
import com.ordiacreativeorg.localblip.util.CommonUtil;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dmytrobohachevskyy on 10/2/15.
 * Rewritten by Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 *
 * Fragment for restore password
 */
public class ForgotPasswordFragment extends Fragment {

    private View mRootView;

    // ui elements
    private TextInputLayout mUsernameTextInputLayout;
    private EditText mUsernameEditText;
    ScrollView scrollForgotPass;

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.forgot_password_fragment, container, false);
        mUsernameEditText = (EditText) mRootView.findViewById( R.id.et_email);
        mUsernameTextInputLayout = (TextInputLayout) mRootView.findViewById( R.id.til_email);
        scrollForgotPass= (ScrollView) mRootView.findViewById(R.id.scrollForgotPass);
        scrollForgotPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mUsernameEditText.getWindowToken(), 0);
                return false;
            }
        });
        mRootView.findViewById( R.id.btn_restore_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mUsernameEditText.getText().toString();
                mUsernameTextInputLayout.setErrorEnabled(false);

                if (email.isEmpty()) {
                    mUsernameTextInputLayout.setError(getResources().getString(R.string.enter_email));
                    return;
                }
                if (!CommonUtil.validateEmail(email)) {
                    mUsernameTextInputLayout.setError(getResources().getString(R.string.enter_valid_email));
                    return;
                }

                ((LogInActivity) getActivity()).setLoadingData(true);
                Api.getInstance().getMethods().resetMemberPassword(
                        email
                ).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                        mUsernameEditText.setText("");
                        SimpleResponse body = response.body();
                        if (body.getResponse().equals("success")) {
                            Snackbar.make(mRootView, R.string.instructions_sent, Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                            ((LogInActivity) getActivity()).setLoadingData(false, false);
                            autoReturn();
                        } else {
                            Snackbar.make(mRootView, body.getResponse(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                            ((LogInActivity) getActivity()).setLoadingData(false);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        mUsernameEditText.setText("");
                        ((LogInActivity) getActivity()).setLoadingData(false);
                        Snackbar.make(mRootView, getResources().getString(R.string.operation_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                    }
                });
            }
        });
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((LogInActivity) getActivity()).setBarTitle(R.string.forgot_password, true);

    }

    private void autoReturn(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = getActivity();
                if (getActivity() != null){
                    activity.onBackPressed();
                }
            }
        }, 2750);
    }
}
