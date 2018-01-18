package com.ordiacreativeorg.localblip.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.SocialMedia;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dmytrobohachevskyy on 10/6/15.
 * Rewritten by Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 * <p>
 * This fragment allow user update social information about him
 */
public class SocialMediaFragment extends Fragment {
    // static data
    static private final String TAG = "SocialMediaFragment";

    private View mRootView;
    private ScrollView scrollSocial;
    // ui elements
    private TextInputLayout mFacebookTextInputLayout;
    private EditText mFacebookEditText;
    private TextInputLayout mGoogleTextInputLayout;
    private EditText mGoogleEditText;
    private TextInputLayout mInstagramTextInputLayout;
    private EditText mInstagramEditText;
    private TextInputLayout mTwitterTextInputLayout;
    private EditText mTwitterEditText;
    private TextInputLayout mPinterestTextInputLayout;
    private EditText mPinterestEditText;
    private TextInputLayout mTumblrTextInputLayout;
    private EditText mTumblrEditText;
    private TextInputLayout mSnapchatTextInputLayout;
    private EditText mSnapchatEditText;
    private ProgressDialog mProgress;

    public static SocialMediaFragment newInstance() {
        return new SocialMediaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.social_fragment, container, false);

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

        // Initialize view elements and class members
        scrollSocial = (ScrollView) mRootView.findViewById(R.id.scrollSocial);
        mFacebookTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_facebook);
        mFacebookEditText = (EditText) mRootView.findViewById(R.id.et_facebook);
        mGoogleTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_google);
        mGoogleEditText = (EditText) mRootView.findViewById(R.id.et_google);
        mInstagramTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_instagram);
        mInstagramEditText = (EditText) mRootView.findViewById(R.id.et_instagram);
        mTwitterTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_twitter);
        mTwitterEditText = (EditText) mRootView.findViewById(R.id.et_twitter);
        mPinterestTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_pinterest);
        mPinterestEditText = (EditText) mRootView.findViewById(R.id.et_pinterest);
        mTumblrTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_tumblr);
        mTumblrEditText = (EditText) mRootView.findViewById(R.id.et_tumblr);
        mSnapchatTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_snapchat);
        mSnapchatEditText = (EditText) mRootView.findViewById(R.id.et_snapchat);
        mProgress = new ProgressDialog();

        // init save button
        scrollSocial.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mFacebookEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mGoogleEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mInstagramEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mTwitterEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPinterestEditText.getWindowToken(), 0);

                imm.hideSoftInputFromWindow(mTumblrEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mSnapchatEditText.getWindowToken(), 0);


                return false;
            }
        });
        mRootView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MemberDetail memberDetailInfo = TemporaryStorageSingleton.getInstance().getMemberDetails();

                HashMap<String, String> params = new HashMap<>();
                String tmp = mFacebookEditText.getText().toString();
                if (!tmp.isEmpty()) {
                    params.put("facebook", tmp);
                }

                tmp = mGoogleEditText.getText().toString();
                if (!tmp.isEmpty()) {
                    params.put("google", tmp);
                }

                tmp = mInstagramEditText.getText().toString();
                if (!tmp.isEmpty()) {
                    params.put("instagram", tmp);
                }

                tmp = mPinterestEditText.getText().toString();
                if (!tmp.isEmpty()) {
                    params.put("pinterest", tmp);
                }

                tmp = mTwitterEditText.getText().toString();
                if (!tmp.isEmpty()) {
                    params.put("twitter", tmp);
                }

                tmp = mTumblrEditText.getText().toString();
                if (!tmp.isEmpty()) {
                    params.put("tumblr", tmp);
                }

                tmp = mSnapchatEditText.getText().toString();
                if (!tmp.isEmpty()) {
                    params.put("snapchat", tmp);
                }

                mProgress.show(getActivity().getSupportFragmentManager(), "progress");
                Api.getInstance().getMethods().setSocialMedia(
                        memberDetailInfo.getEmail(),
                        memberDetailInfo.getApiKey(),
                        params
                ).enqueue(new Callback<SocialMedia>() {
                    @Override
                    public void onResponse(Response<SocialMedia> response, Retrofit retrofit) {
                        Snackbar.make(mRootView, R.string.saved, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                        mProgress.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(mRootView, getResources().getString(R.string.save_failed) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                        mProgress.dismiss();
                    }
                });
            }
        });

        mFacebookEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mFacebookEditText.getText().toString().isEmpty()) {
                    mFacebookTextInputLayout.setHint("https://www.facebook.com/...");
                } else {
                    mFacebookTextInputLayout.setHint("https://www.facebook.com/" + mFacebookEditText.getText().toString());
                }
            }
        });

        mTwitterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTwitterEditText.getText().toString().isEmpty()) {
                    mTwitterTextInputLayout.setHint("https://www.twitter.com/...");
                } else {
                    mTwitterTextInputLayout.setHint("https://www.twitter.com/" + mTwitterEditText.getText().toString());
                }
            }
        });

        mInstagramEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mInstagramEditText.getText().toString().isEmpty()) {
                    mInstagramTextInputLayout.setHint("https://www.instagram.com/...");
                } else {
                    mInstagramTextInputLayout.setHint("https://www.instagram.com/" + mInstagramEditText.getText().toString());
                }
            }
        });

        mPinterestEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mPinterestEditText.getText().toString().isEmpty()) {
                    mPinterestTextInputLayout.setHint("https://www.pinterest.com/...");
                } else {
                    mPinterestTextInputLayout.setHint("https://www.pinterest.com/" + mPinterestEditText.getText().toString());
                }
            }
        });

        mGoogleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mGoogleEditText.getText().toString().isEmpty()) {
                    mGoogleTextInputLayout.setHint("https://plus.google.com/...");
                } else {
                    mGoogleTextInputLayout.setHint("https://plus.google.com/" + mGoogleEditText.getText().toString());
                }
            }
        });

        mTumblrEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTumblrEditText.getText().toString().isEmpty()) {
                    mTumblrTextInputLayout.setHint("https://....tumblr.com");
                } else {
                    mTumblrTextInputLayout.setHint("https://" + mTumblrEditText.getText().toString() + ".tumblr.com");
                }
            }
        });
        mSnapchatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSnapchatEditText.getText().toString().isEmpty()) {
                    mSnapchatTextInputLayout.setHint(" https://www.snapchat.com/add/");
                } else {
                    mSnapchatTextInputLayout.setHint(" https://www.snapchat.com/add/" + mSnapchatEditText.getText().toString());
                }
            }
        });

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).setBarTitle(R.string.nd_smedia, false);

        showSocialMedia();
    }

    /**
     * Show social link when fragment opens
     */
    private void showSocialMedia() {
        final MemberDetail memberDetailInfo = TemporaryStorageSingleton.getInstance().getMemberDetails();
        mProgress.show(getActivity().getSupportFragmentManager(), "progress");
        Api.getInstance().getMethods().getSocialMedia(
                memberDetailInfo.getEmail(),
                memberDetailInfo.getApiKey()
        ).enqueue(new Callback<SocialMedia>() {
            @Override
            public void onResponse(Response<SocialMedia> response, Retrofit retrofit) {
                mProgress.dismiss();
                SocialMedia body = response.body();

                mFacebookEditText.setText("");
                if (body.getFacebook() != null && !body.getFacebook().isEmpty() && !body.getFacebook().equalsIgnoreCase("false"))
                    mFacebookEditText.setText(body.getFacebook());
                mGoogleEditText.setText("");
                if (body.getGoogle() != null && !body.getGoogle().isEmpty() && !body.getGoogle().equalsIgnoreCase("false"))
                    mGoogleEditText.setText(body.getGoogle());
                mInstagramEditText.setText("");
                if (body.getInstagram() != null && !body.getInstagram().isEmpty() && !body.getInstagram().equalsIgnoreCase("false"))
                    mInstagramEditText.setText(body.getInstagram());
                mTwitterEditText.setText("");
                if (body.getTwitter() != null && !body.getTwitter().isEmpty() && !body.getTwitter().equalsIgnoreCase("false"))
                    mTwitterEditText.setText(body.getTwitter());
                mPinterestEditText.setText("");
                if (body.getPinterest() != null && !body.getPinterest().isEmpty() && !body.getPinterest().equalsIgnoreCase("false"))
                    mPinterestEditText.setText(body.getPinterest());
                mTumblrEditText.setText("");
                if (body.getTumblr() != null && !body.getTumblr().isEmpty() && !body.getTumblr().equalsIgnoreCase("false"))
                    mTumblrEditText.setText(body.getTumblr());
                Log.e("tummm", "" + (body.getTumblr() != null && !body.getTumblr().isEmpty() && !body.getTumblr().equalsIgnoreCase("false")));
                mSnapchatEditText.setText("");
                if (body.getSnapchat() != null && !body.getSnapchat().isEmpty() && !body.getSnapchat().equalsIgnoreCase("false"))
                    mSnapchatEditText.setText(body.getSnapchat());
                Log.e("snpppp", "" + (body.getSnapchat() != null && !body.getSnapchat().isEmpty() && !body.getSnapchat().equalsIgnoreCase("false")));
            }

            @Override
            public void onFailure(Throwable t) {
                mProgress.dismiss();
                Snackbar.make(mRootView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        });
    }
}
