package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Conversation;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.SentMessage;
import com.ordiacreativeorg.localblip.model.Vendor;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/18/2015
 */
public class SendMessageDialog extends DialogFragment {

    private SwitchCompat mSwitchCompat;
    private TextInputLayout mRecipientTextInputLayout;
    private AutoCompleteTextView mRecipientEditText;
    private TextInputLayout mMessageTextInputLayout;
    private EditText mMessageEditText;
    private ProgressBar mSendingProgressBar;

    private ArrayList<String> mVendorsNames;
    private ArrayList<Vendor> mVendors;
    private boolean admin;

    public static SendMessageDialog newInstance(boolean adminsOnly) {
        Bundle args = new Bundle();
        args.putBoolean("admin", adminsOnly);
        SendMessageDialog fragment = new SendMessageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public SendMessageDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.send_message_dialog, container, false);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        admin = false;
        if (getArguments() != null){
            admin = getArguments().getBoolean("admin", false);
        }

        mSwitchCompat = (SwitchCompat) rootView.findViewById(R.id.sw_admin);
        mRecipientTextInputLayout = (TextInputLayout) rootView.findViewById(R.id.til_recipient);
        mRecipientEditText = (AutoCompleteTextView) rootView.findViewById(R.id.et_recipient);
        mMessageTextInputLayout = (TextInputLayout) rootView.findViewById(R.id.til_message);
        mMessageEditText = (EditText) rootView.findViewById(R.id.et_message);
        mSendingProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_sending);

        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRecipientEditText.setEnabled(false);
                    mRecipientTextInputLayout.setVisibility(View.GONE);
                } else {
                    mRecipientEditText.setEnabled(true);
                    mRecipientTextInputLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        mSwitchCompat.setChecked(admin);
        mSwitchCompat.setEnabled(!admin);

        mVendorsNames = new ArrayList<>();
        mVendors = TemporaryStorageSingleton.getInstance().getVendors();
        for (Vendor vendor : mVendors){
            mVendorsNames.add(vendor.getBusinessName());
        }
        if (admin) {
            mRecipientEditText.setVisibility(View.GONE);
        } else {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mVendorsNames);
            mRecipientEditText.setAdapter(arrayAdapter);
        }

        mMessageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        rootView.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return rootView;
    }

    private void hideSoftKeyboard(Activity activity) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder token = activity.getCurrentFocus().getWindowToken();
            if (token != null) {
                inputMethodManager.hideSoftInputFromWindow(token, 0);
            }
        } catch (final Exception ex){
            ex.printStackTrace();
        }

    }

    private void sendMessage(){
        final int index = mVendorsNames.indexOf(mRecipientEditText.getText().toString());
        mRecipientTextInputLayout.setErrorEnabled(false);
        mMessageTextInputLayout.setErrorEnabled(false);
        if ((mSwitchCompat.isChecked() || index >= 0) && !mMessageEditText.getText().toString().isEmpty()) {
            mSwitchCompat.setEnabled(false);
            mMessageEditText.setEnabled(false);
            mRecipientEditText.setEnabled(false);
            mSendingProgressBar.setVisibility(View.VISIBLE);
            MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
            HashMap<String, String> params = new HashMap<>();
            if (mSwitchCompat.isChecked()) {
                params.put("type", "0");
            }else{
                params.put("type", "1");
                params.put("recemail", mVendors.get(index).getEmail());
            }
            Api.getInstance().getMethods().sendMessage(
                    memberDetail.getEmail(),
                    memberDetail.getApiKey(),
                    mMessageEditText.getText().toString(),
                    params
            ).enqueue(new Callback<SentMessage>() {
                @Override
                public void onResponse(Response<SentMessage> response, Retrofit retrofit) {
                    SentMessage sentMessage = response.body();
                    Log.e("messageid",""+sentMessage.getMessageId());
                    Conversation conversation = new Conversation();
                    conversation.setMessageId(sentMessage.getMessageId());
                    if (index >= 0 ) {
                        conversation.setSenderName(mVendors.get(index).getBusinessName());
                        conversation.setSenderEmail(mVendors.get(index).getEmail());
                    }else{
                        conversation.setSenderName("LocalBlip");
                        conversation.setSenderEmail("support@localblip.com");
                    }
                    conversation.setContent(mMessageEditText.getText().toString());
                    conversation.setOpened(true);
                    conversation.setReply(false);

                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    conversation.setTime(simpleDateFormat.format(date));

                    Intent intent = new Intent();
                    intent.putExtra("conversation", conversation);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    Snackbar.make(mSwitchCompat, getResources().getString(R.string.failed_to_send_a_message) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                    mSendingProgressBar.setVisibility(View.GONE);
                    mSwitchCompat.setEnabled(!admin);
                    mMessageEditText.setEnabled(true);
                    mRecipientEditText.setEnabled(true);
                }
            });
        } else {
            if (index < 0 && !mSwitchCompat.isChecked()){
                mRecipientTextInputLayout.setError(getResources().getString(R.string.select_valid_vendor));
            }
            if (mMessageEditText.getText().toString().isEmpty()){
                mMessageTextInputLayout.setError(getResources().getString(R.string.enter_message));
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onViewCreated(view, savedInstanceState);
    }
}
