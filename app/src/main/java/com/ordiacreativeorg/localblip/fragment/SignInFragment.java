package com.ordiacreativeorg.localblip.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.LogInActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.ArchiveManager;
import com.ordiacreativeorg.localblip.util.CommonUtil;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;
import com.pushwoosh.PushManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class SignInFragment extends InitialFragment {


    private LogInActivity delegate;
    ScrollView scrollSignIn;
    // ui elements
    private TextInputLayout emailTextInputLayout;
    private EditText emailEditText;
    private TextInputLayout passwordTextInputLayout;
    private EditText passwordEditText;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TemporaryStorageSingleton.getInstance().getMemberDetails() != null) {
            ((LogInActivity) getActivity()).openMainActivity();
        }
    }

    private View mRootView;
    private Button signInButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sign_in_fragment, container, false);

        scrollSignIn = (ScrollView) mRootView.findViewById(R.id.scrollSignIn);
        emailTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_email);
        emailEditText = (EditText) mRootView.findViewById(R.id.et_email);
        passwordTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_password);
        passwordEditText = (EditText) mRootView.findViewById(R.id.et_password);

        signInButton = (Button) mRootView.findViewById(R.id.btn_sign_in);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    signInButton.performClick();
                    return true;
                }
                return false;
            }
        });
        scrollSignIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
                return false;
            }
        });
        HashMap<String, String> lastSession = new HashMap<>();
        ArchiveManager.getSession(getActivity(), lastSession);
        Log.e("Size", "" + lastSession.size());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (!prefs.getBoolean("firstDialog", false)) {
            Log.e("Size1", "" + prefs.getBoolean("firstDialog", false));


            Log.e("Size2", "" + lastSession.size());
            new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.app_name))
                    .setCancelable(false)
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage("I accept terms of" +
                            " service")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            getActivity().finish();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("firstDialog", true);
                            editor.commit();
                            dialog.dismiss();


                        }
                    })

                    .show();


        }

        return mRootView;
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private void login(String username, String password, final String allCategories, final List<String> states) {
        Map<String, String> params = new HashMap();
        params.put("pushtoken", PushManager.getPushToken(getActivity()));
        params.put("hwid", PushManager.getPushwooshHWID(getActivity()));
        Log.e("fffdd", "" + PushManager.getPushwooshHWID(getActivity()));
        params.put("devicetype", "3");

        delegate.setLoadingData(true);
        Api.getInstance().getMethods().signIn(
                username,
                password,
                params
        ).enqueue(new Callback<MemberDetail>() {
            @Override
            public void onResponse(Response<MemberDetail> response, Retrofit retrofit) {
                MemberDetail body = response.body();
                if (null == body.getResponse()) {
                    TemporaryStorageSingleton.getInstance().setMemberDetails(body);
                    loadCategories(allCategories, states);
                    ArchiveManager.saveSession(getActivity().getApplicationContext(), body.getEmail(), body.getApiKey(), body.getMemberId());
                } else {
                    delegate.setLoadingData(false);
                    Snackbar.make(mRootView, body.getResponse(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(mRootView, getResources().getString(R.string.operation_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                delegate.setLoadingData(false);
            }
        });
    }

    private void getLastSession(String username, String apikey, final String allCategories, final List<String> states) {
        Map<String, String> params = new HashMap();
        params.put("pushtoken", PushManager.getPushToken(getActivity()));
        params.put("hwid", PushManager.getPushwooshHWID(getActivity()));
        params.put("devicetype", "3");

        Api.getInstance().getMethods().getMembersDetails(
                username,
                apikey,
                params
        ).enqueue(new Callback<MemberDetail>() {
            @Override
            public void onResponse(Response<MemberDetail> response, Retrofit retrofit) {
                MemberDetail body = response.body();
                if (null == body.getResponse()) {
                    TemporaryStorageSingleton.getInstance().setMemberDetails(body);
                    loadCategories(allCategories, states);
                } else {
                    delegate.setLoadingData(false);
                    Snackbar.make(mRootView, body.getResponse(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (isAdded() && delegate != null) {
                    Snackbar.make(mRootView, getResources().getString(R.string.operation_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                    delegate.setLoadingData(false);
                }
            }
        });
    }

    @Override
    protected void loadFinished() {
        super.loadFinished();
        delegate.setLoadingData(false, false);
        delegate.openMainActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        delegate = (LogInActivity) getActivity();
        delegate.setBarTitle("", false);
        final String allCategories = getString(R.string.all_categories);
        final List<String> states = Arrays.asList(getResources().getStringArray(R.array.states_list));

        HashMap<String, String> lastSession = new HashMap<>();
        ArchiveManager.getSession(getActivity().getApplicationContext(), lastSession);
        if (lastSession.size() > 0 && lastSession.containsKey("id") && !((LogInActivity) getActivity()).isLoadingData()) {
            String email = lastSession.get("email");
            String apikey = lastSession.get("apikey");
            getLastSession(email, apikey, allCategories, states);
        } else {
            delegate.setLoadingData(false);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = emailEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString();
                emailTextInputLayout.setErrorEnabled(false);
                passwordTextInputLayout.setErrorEnabled(false);
                boolean errors = false;

                // check if fields is not empty
                if (username.isEmpty()) {
                    emailTextInputLayout.setError(getResources().getString(R.string.enter_email));
                    errors = true;
                } else if (!CommonUtil.validateEmail(username)) {
                    emailTextInputLayout.setError(getResources().getString(R.string.enter_valid_email));
                    errors = true;
                }

                if (password.isEmpty()) {
                    passwordTextInputLayout.setError(getResources().getString(R.string.enter_password));
                    errors = true;
                }


                if (errors) return;

                login(username, password, allCategories, states);

            }
        });
        mRootView.findViewById(R.id.btn_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_fragment_container, ChooseUserTypeFragment.newInstance())
                        .addToBackStack("signUp")
                        .commit();
            }
        });
        mRootView.findViewById(R.id.btn_new_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_fragment_container, ForgotPasswordFragment.newInstance())
                        .addToBackStack("signUp")
                        .commit();
            }
        });

    }
}
