package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.adapter.ChatAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.constants.Constants;
import com.ordiacreativeorg.localblip.model.ChatMessage;
import com.ordiacreativeorg.localblip.model.SentMessage;
import com.ordiacreativeorg.localblip.model.SimpleResponse;
import com.ordiacreativeorg.localblip.util.ArchiveManager;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/26/2015
 */
public class Chat extends Fragment {

    public static Chat newInstance(String senderName, String senderEmail, boolean blipAlert) {
        Chat fragment = new Chat();
        Bundle args = new Bundle();
        args.putString("senderName", senderName);
        args.putString("senderEmail", senderEmail);
        args.putBoolean("blipAler", blipAlert);
        fragment.setArguments(args);
        return fragment;
    }

    public Chat() {
        // Required empty public constructor
    }

    private String mSenderEmail;
    private String mSenderName;
    private boolean mBlipAlert;
    private String mMyEmail;
    private String mMyApikey;

    private TextView mEmptyTextView;
    private RecyclerView mRecyclerView;
    private EditText mMessageEditText;
    private ImageButton mSendImageButton;

    private ProgressDialog mProgressDialog;
    private ProgressBar mSendingProgressBar;

    private ChatAdapter chatAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_layout, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getArguments() != null) {
            mSenderName = getArguments().getString("senderName");
            mSenderEmail = getArguments().getString("senderEmail");
            mBlipAlert = getArguments().getBoolean("blipAler", false);
            getActivity().setTitle(mSenderName);
            HashMap<String, String> lastSession = new HashMap<>();
            ArchiveManager.getSession(getActivity().getApplicationContext(), lastSession);
            mMyEmail = lastSession.get("email");
            mMyApikey = lastSession.get("apikey");
        }

        mEmptyTextView = (TextView) rootView.findViewById(R.id.tv_empty);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_messages);
        mSendImageButton = (ImageButton) rootView.findViewById(R.id.btn_send);
        mSendingProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_sending);
        mMessageEditText = (EditText) rootView.findViewById(R.id.et_message);
        mProgressDialog = new ProgressDialog();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        chatAdapter = new ChatAdapter(new ArrayList<ChatMessage>(), mMyEmail);
        chatAdapter.setOnChatMessageActionListener(new ChatAdapter.OnChatMessageActionListener() {
            @Override
            public void onChatMessageRemoved(final ChatMessage chatMessage) {
                ConfirmationDialog.newInstance(null, "Remove this message?", "Remove", null, new ConfirmationDialog.OnActionConfirmationListener() {
                    @Override
                    public void onConfirmed() {
                        removeMessage(chatMessage);
                    }

                    @Override
                    public void onCanceled() {
                    }
                }).show(getActivity().getSupportFragmentManager(), null);
            }
        });
        mRecyclerView.setAdapter(chatAdapter);

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

        mSendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        reloadMessages();
        return rootView;
    }

    private void removeMessage(final ChatMessage chatMessage) {
        Api.getInstance().getMethods().deleteMessage(
                TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail(),
                TemporaryStorageSingleton.getInstance().getMemberDetails().getApiKey(),
                String.valueOf(chatMessage.getMessageId())
        ).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                chatAdapter.remove(chatMessage);
                if (chatAdapter.getItemCount() > 0) {
                    Snackbar.make(mRecyclerView, "Message was removed", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                } else {
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mProgressDialog.dismiss();
                Snackbar.make(mRecyclerView, "Message wasn't removed", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        });
    }

    private void hideSoftKeyboard(Activity activity) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder token = activity.getCurrentFocus().getWindowToken();
            if (token != null) {
                inputMethodManager.hideSoftInputFromWindow(token, 0);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

    }

    private void sendMessage() {
        if (mMessageEditText.getText().toString().trim().length() > 0) {
            Log.e("httmmll", mMessageEditText.getText().toString().replaceAll("\\n", "<br />"));
            mMessageEditText.setEnabled(false);
            mSendImageButton.setEnabled(false);
            mSendingProgressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "1");
            params.put("recemail", mSenderEmail);
            Api.getInstance().getMethods().sendMessage(
                    mMyEmail,
                    mMyApikey,
                    mMessageEditText.getText().toString().replaceAll("\\n", "<br />"),
                    params
            ).enqueue(new Callback<SentMessage>() {
                @Override
                public void onResponse(Response<SentMessage> response, Retrofit retrofit) {
                    final ChatAdapter adapter = (ChatAdapter) mRecyclerView.getAdapter();
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setContent(mMessageEditText.getText().toString());
                    chatMessage.setSenderEmail(mMyEmail);
                    chatMessage.setAdded(getResources().getString(R.string.moments_ago));
                    chatMessage.setOpened(false);
                    chatMessage.setReceivereMail(mSenderEmail);
                    chatMessage.setReceiverName(mSenderName);
                    adapter.add(chatMessage);
                    mMessageEditText.setText("");
                    mMessageEditText.setEnabled(true);
                    mSendImageButton.setEnabled(true);
                    mSendingProgressBar.setVisibility(View.GONE);
                    mRecyclerView.scrollToPosition(0);
                }

                @Override
                public void onFailure(Throwable t) {
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.failed_to_send_a_message) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                    mMessageEditText.setEnabled(true);
                    mSendImageButton.setEnabled(true);
                    mSendingProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void reloadMessages() {
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        Api.getInstance().getMethods().getMessageHistory(
                mMyEmail,
                mMyApikey,
                mSenderEmail
        ).enqueue(new Callback<ArrayList<ChatMessage>>() {
            @Override
            public void onResponse(Response<ArrayList<ChatMessage>> response, Retrofit retrofit) {
                final List<ChatMessage> chatMessages = response.body();
                final ChatAdapter adapter = (ChatAdapter) mRecyclerView.getAdapter();
                if (chatMessages != null && !chatMessages.isEmpty()) {
                    for (int i = 0; i < chatMessages.size(); i++) {
                        if (chatMessages.get(i).isBlipAlert() != mBlipAlert) {
                            chatMessages.remove(i);
                            i--;
                        }
                    }
                    adapter.replaceAll(chatMessages);
                    mEmptyTextView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(Constants.UPDATE_NEW_MESSAGES_COUNT));
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                if (isAdded()) {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    mProgressDialog.dismiss();
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, R.string.refresh).setIcon(R.drawable.ic_action_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 1, "Remove all messages").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            reloadMessages();
        } else if (item.getItemId() == 1) {
            ConfirmationDialog.newInstance(null, "Remove all messages for this thread?", "Remove", null, new ConfirmationDialog.OnActionConfirmationListener() {
                @Override
                public void onConfirmed() {
                    allDeleted = true;
                    List<ChatMessage> chatMessages = ((ChatAdapter) mRecyclerView.getAdapter()).getDataSet();
                    /*for (int i = 0; i < chatMessages.size(); i++){
                        if (chatMessages.get(i).getSenderEmail().equals(TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail())){
                            chatMessages.remove(i);
                            i--;
                        }
                    }*/
                    removeMessages(chatMessages);
                }

                @Override
                public void onCanceled() {

                }
            }).show(getActivity().getSupportFragmentManager(), null);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean allDeleted;

    private void removeMessages(final List<ChatMessage> chatMessages) {
        Api.getInstance().getMethods().deleteMessage(
                TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail(),
                TemporaryStorageSingleton.getInstance().getMemberDetails().getApiKey(),
                String.valueOf(chatMessages.get(0).getMessageId())
        ).enqueue(
                new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                        if (chatMessages.get(0).isBlipAlert()) {
                            removeBlipAlert(chatMessages.get(0));
                        }
                        chatMessages.remove(0);
                        if (chatMessages.size() < 1) {
                            mProgressDialog.dismiss();
                            if (allDeleted) {
                                Snackbar.make(mRecyclerView, "All Messages in conversation were removed", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                            } else {
                                Snackbar.make(mRecyclerView, "Some messages were not removed", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                            }
                            getActivity().onBackPressed();
                        } else {
                            removeMessages(chatMessages);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        allDeleted = false;
                        if (chatMessages.get(0).isBlipAlert()) {
                            removeBlipAlert(chatMessages.get(0));
                        }
                        chatMessages.remove(0);
                        if (chatMessages.size() < 1) {
                            mProgressDialog.dismiss();
                            Snackbar.make(mRecyclerView, "Some messages were not removed", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                            reloadMessages();
                        } else {
                            removeMessages(chatMessages);
                        }
                    }
                }
        );
    }

    private void removeBlipAlert(ChatMessage chatMessage) {
        Api.getInstance().getMethods().deleteBlipAlert(
                TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail(),
                TemporaryStorageSingleton.getInstance().getMemberDetails().getApiKey(),
                String.valueOf(chatMessage.getMessageId())
        ).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {
                allDeleted = false;
            }
        });
    }
}
