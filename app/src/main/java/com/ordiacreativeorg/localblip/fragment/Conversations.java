package com.ordiacreativeorg.localblip.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.adapter.ConversationsAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.ChatMessage;
import com.ordiacreativeorg.localblip.model.Conversation;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.SimpleResponse;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;
import com.ordiacreativeorg.localblip.view.FixedSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/26/2015
 */
public class Conversations extends Fragment {

    private RecyclerView mRecyclerView;
    private TextView mEmptyTextView;
    private FixedSwipeRefreshLayout mFixedSwipeRefreshLayout;
    private Context mAppContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messages_fragment, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        ConversationsAdapter conversationsAdapter = new ConversationsAdapter(new ArrayList<Conversation>());
        conversationsAdapter.setOnConversationActionListener(onConversationActionListener);
        mRecyclerView.setAdapter(conversationsAdapter);
        mFixedSwipeRefreshLayout = (FixedSwipeRefreshLayout) rootView.findViewById(R.id.fsrl_refresh);
        mFixedSwipeRefreshLayout.setSize(0);
        mFixedSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.accent_color));
        mFixedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadConversations(replaceConversationsInList);
            }
        });
        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.btn_main_action);
        floatingActionButton.setImageResource(R.drawable.ic_action_add_light);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageDialog sendMessageDialog = SendMessageDialog.newInstance(
                        TemporaryStorageSingleton.getInstance().getMemberDetails().getMemberType() == 4
                );
                sendMessageDialog.setTargetFragment(Conversations.this, 1727);
                sendMessageDialog.show(getActivity().getSupportFragmentManager(), "sendMessage");
            }
        });
        mEmptyTextView = (TextView) rootView.findViewById(R.id.tv_empty);
        loadConversations(addConversationsToList);
        return rootView;
    }

    private final DataLoadResultListener addConversationsToList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(ConversationsAdapter adapter, List<Conversation> conversations) {
            adapter.addAll(conversations);
        }
    };

    private final DataLoadResultListener replaceConversationsInList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(ConversationsAdapter adapter, List<Conversation> conversations) {
            adapter.replaceAll(conversations);
        }
    };

    interface DataLoadResultListener {
        void onDataReturned(ConversationsAdapter adapter, List<Conversation> locations);
    }

    private void loadConversations(final DataLoadResultListener dataLoadResultListener) {
        mFixedSwipeRefreshLayout.setRefreshing(true);
        MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Api.getInstance().getMethods().getMessages(
                memberDetail.getEmail(),
                memberDetail.getApiKey()
        ).enqueue(new Callback<ArrayList<Conversation>>() {
            @Override
            public void onResponse(Response<ArrayList<Conversation>> response, Retrofit retrofit) {
                final ArrayList<Conversation> conversations = response.body();
                final ConversationsAdapter adapter = (ConversationsAdapter) mRecyclerView.getAdapter();
                if (conversations != null) {
                    collapseMessagesToConversations(conversations);
                }
                if (conversations != null && !conversations.isEmpty()) {
                    dataLoadResultListener.onDataReturned(adapter, conversations);
                    mEmptyTextView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                mFixedSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable t) {
                mEmptyTextView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                mFixedSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(mFixedSwipeRefreshLayout, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        });
    }

    private void collapseMessagesToConversations(ArrayList<Conversation> conversations) {
        ArrayList<Conversation> collapsed = new ArrayList<>();
        Log.e("collapsed", String.valueOf(collapsed));
        for (Conversation conversation : conversations) {
            if (conversation.getSenderEmail() == null) {
                conversation.setSenderEmail(conversation.getReceiverEmail());
            }

            if (conversation.getSenderName() == null) {
                conversation.setSenderName(conversation.getReceiverName());
            }

            String email = conversation.getSenderEmail();

            int count = 0;
            for (Conversation conv : collapsed) {
                Log.e("email", email);
                Log.e("Convemail", conv.getSenderEmail());
                Log.e("ConvBlipAlert", "" + conv.isBlipAlert());
                Log.e("ConversationBlipAlert", "" + conversation.isBlipAlert());
                if (email != null && email.equals(conv.getSenderEmail()) && conversation.isBlipAlert() == conv.isBlipAlert())
                    count++;
            }
            Log.e("count", "" + count);
            if (count == 0) {
                collapsed.add(conversation);
                Log.e("collapsed2", String.valueOf(collapsed));
            }
        }
        conversations.clear();
        conversations.addAll(collapsed);
        collapsed.clear();
    }

    private final ConversationsAdapter.OnConversationActionListener onConversationActionListener = new ConversationsAdapter.OnConversationActionListener() {
        @Override
        public void onConversationChosen(Conversation conversation) {
            Intent intent = new Intent(getContext(), com.ordiacreativeorg.localblip.activity.Chat.class);
            intent.putExtra("senderName", conversation.getSenderName());
            intent.putExtra("senderEmail", conversation.getSenderEmail());
            intent.putExtra("blipAlert", conversation.isBlipAlert());
            startActivityForResult(intent, 1362);
        }

        @Override
        public void onConversationRemoved(final Conversation conversation) {
            ConfirmationDialog.newInstance(null, "Remove all messages for this thread?", "Remove", null, new ConfirmationDialog.OnActionConfirmationListener() {
                @Override
                public void onConfirmed() {
                    removeConversation(conversation);
                }

                @Override
                public void onCanceled() {

                }
            }).show(getActivity().getSupportFragmentManager(), null);
        }
    };

    private ProgressDialog mProgressDialog = new ProgressDialog();
    private boolean allDeleted;

    private void removeConversation(final List<ChatMessage> chatMessages, final Conversation conversation) {
        Api.getInstance().getMethods().deleteThread(
                TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail(),
                TemporaryStorageSingleton.getInstance().getMemberDetails().getApiKey(),
                conversation.getThreadId()
        ).enqueue(new Callback<SimpleResponse>() {
                      @Override
                      public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                          //((ConversationsAdapter) mRecyclerView.getAdapter()).removeConversation(conversation.getMessageId());
                          loadConversations(replaceConversationsInList);
                          mProgressDialog.dismiss();
                          Snackbar.make(mRecyclerView, "All Messages in conversation were removed", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                              }
                          }).show();
                      }

                      @Override
                      public void onFailure(Throwable t) {
                          mProgressDialog.dismiss();
                          Snackbar.make(mRecyclerView, "Some messages were not removed", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                              }
                          }).show();
                          loadConversations(replaceConversationsInList);
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

    private void removeConversation(Conversation conversation) {
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        //loadAllMessagesForRemoval(conversation);
        removeConversation(new LinkedList(), conversation);
    }

    private void loadAllMessagesForRemoval(final Conversation conversation) {
        Api.getInstance().getMethods().getMessageHistory(
                TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail(),
                TemporaryStorageSingleton.getInstance().getMemberDetails().getApiKey(),
                conversation.getSenderEmail()
        ).enqueue(new Callback<ArrayList<ChatMessage>>() {
            @Override
            public void onResponse(Response<ArrayList<ChatMessage>> response, Retrofit retrofit) {
                final List<ChatMessage> chatMessages = response.body();
                if (chatMessages != null && !chatMessages.isEmpty()) {
                    allDeleted = true;
                    /*for (int i = 0; i < chatMessages.size(); i++){
                        if (chatMessages.get(i).isBlipAlert() != conversation.isBlipAlert()
                                || chatMessages.get(i).getSenderEmail().equals(TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail())){
                            chatMessages.remove(i);
                            i--;
                        }
                    }*/
                    removeConversation(chatMessages, conversation);
                } else {
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.no_messages_found), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (isAdded()) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1362:
                if (resultCode == Activity.RESULT_OK && data.hasExtra("action")) {
                    if (data.getStringExtra("action").equals("reload")) {
                        //Snackbar.make(mRecyclerView, data.getStringExtra("message"), Snackbar.LENGTH_INDEFINITE).show();
                        loadConversations(replaceConversationsInList);
                        //} else if (data.getStringExtra("action").equals("remove")) {
                        //    removeConversation(data.getIntExtra("target", -1));
                    }
                }
                break;
            case 1727:
                if (resultCode == Activity.RESULT_OK && data.hasExtra("conversation")) {
                    loadConversations(replaceConversationsInList);
                }
                break;
        }
    }

    // Temporary
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppContext = getActivity().getApplicationContext();
        ((MainActivity) getActivity()).setBarTitle(R.string.nd_messages, false);
    }
}
