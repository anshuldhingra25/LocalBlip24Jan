package com.ordiacreativeorg.localblip.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.ChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 * <p>
 * This adapter for showing blip in blip list
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final ArrayList<ChatMessage> mDataSet;
    private String mMyEmail;

    public ChatAdapter(ArrayList<ChatMessage> data, String email) {
        mDataSet = data;
        Collections.reverse(mDataSet);
        mMyEmail = email;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getSenderEmail().equals(mMyEmail) ? 1 : 0;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_income_item, parent, false);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_outcome_item, parent, false);
                break;
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatMessage chatMessage = mDataSet.get(position);

        holder.dateTextView.setText(chatMessage.getAdded().replaceAll("(\\d{4}) (\\d{2})", "$1\n$2"));
        holder.contentTextView.setText(Html.fromHtml(chatMessage.getContent()));
        holder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        if (chatMessage.isOpened()) {
            holder.statusTextView.setText(R.string.read);
        } else {
            if (chatMessage.getReceivereMail().equals(mMyEmail)) {
                holder.statusTextView.setText(R.string.recived);
            } else {
                holder.statusTextView.setText(R.string.delivered);
            }
        }

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChatMessageActionListener.onChatMessageRemoved(chatMessage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /*public void addAll(List<ChatMessage> elements) {
        int from = mDataSet.size();
        mDataSet.addAll(elements);
        int to = mDataSet.size();
        notifyItemRangeInserted(from, to - from);
    }*/

    public void add(ChatMessage chatMessage) {
        mDataSet.add(0, chatMessage);
        notifyItemInserted(0);
    }

    public void replaceAll(List<ChatMessage> elements) {
        mDataSet.clear();
        mDataSet.addAll(elements);
        Collections.reverse(mDataSet);
        notifyDataSetChanged();
    }

    public List<ChatMessage> getDataSet() {
        return mDataSet;
    }

    public void remove(ChatMessage chatMessage) {
        int index = this.mDataSet.indexOf(chatMessage);
        if (index > -1) {
            this.mDataSet.remove(index);
            this.notifyItemRemoved(index);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView dateTextView;
        private final TextView contentTextView;
        private final TextView statusTextView;
        private final ImageView removeButton;

        public ViewHolder(View v) {
            super(v);
            dateTextView = (TextView) v.findViewById(R.id.tv_date);
            contentTextView = (TextView) v.findViewById(R.id.tv_message);
            statusTextView = (TextView) v.findViewById(R.id.tv_status);
            removeButton = (ImageView) v.findViewById(R.id.btn_remove);
        }
    }

    private OnChatMessageActionListener onChatMessageActionListener;

    public void setOnChatMessageActionListener(OnChatMessageActionListener onChatMessageActionListener) {
        this.onChatMessageActionListener = onChatMessageActionListener;
    }

    public interface OnChatMessageActionListener {
        void onChatMessageRemoved(ChatMessage chatMessage);
    }
}