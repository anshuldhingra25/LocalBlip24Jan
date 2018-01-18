package com.ordiacreativeorg.localblip.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.Conversation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/26/2015
 */
public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    private final ArrayList<Conversation> mDataSet;

    public ConversationsAdapter(ArrayList<Conversation> data) {
        mDataSet = data;
    }

    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Conversation conversation = mDataSet.get(position);

        if ( conversation.getRead() == 0 ) {
            holder.newTextView.setVisibility( View.VISIBLE );
        } else {
            holder.newTextView.setVisibility( View.GONE );
        }

        holder.senderTextView.setText(conversation.getSenderName());
        holder.dateTextView.setText(conversation.getTime());
        holder.contentTextView.setText(Html.fromHtml(conversation.getContent()));
       // holder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());

        if (onConversationActionListener != null){
            /*holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onConversationActionListener.onConversationRemoved(conversation);
                }
            });*/
            holder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onConversationActionListener.onConversationChosen(conversation);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addAll(List<Conversation> elements) {
        int from = mDataSet.size();
        mDataSet.addAll(elements);
        int to = mDataSet.size();
        notifyItemRangeInserted(from, to - from);
    }

    public void replaceAll(List<Conversation> elements) {
        mDataSet.clear();
        mDataSet.addAll(elements);
        notifyDataSetChanged();
    }

    private void removeConversation(Conversation conversation) {
        int index = mDataSet.indexOf(conversation);
        if (index >= 0) {
            mDataSet.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void removeConversation(String senderEmail) {
        int index = -1;
        for (int i=0; i < mDataSet.size(); i++){
            if (mDataSet.get(i).getSenderEmail().equals(senderEmail)){
                index = i;
            }
        }
        if (index >= 0) {
            mDataSet.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void removeConversation(int messageId) {
        int index = -1;
        for (int i=0; i < mDataSet.size(); i++){
            if (mDataSet.get(i).getMessageId() == messageId){
                index = i;
            }
        }
        if (index >= 0) {
            mDataSet.remove(index);
            notifyItemRemoved(index);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final View layoutView;
        private final TextView newTextView;
        private final TextView senderTextView;
        private final TextView dateTextView;
        private final TextView contentTextView;
        //private final ImageButton removeButton;

        public ViewHolder(View v) {
            super(v);
            layoutView = v;
            newTextView = (TextView) v.findViewById(R.id.tv_new);
            senderTextView = (TextView) v.findViewById(R.id.tv_sender);
            dateTextView = (TextView) v.findViewById(R.id.tv_date);
            contentTextView = (TextView) v.findViewById(R.id.tv_content);
            //removeButton = (ImageButton) v.findViewById(R.id.btn_remove);
        }
    }

    private OnConversationActionListener onConversationActionListener;

    public void setOnConversationActionListener(OnConversationActionListener onConversationActionListener){
        this.onConversationActionListener = onConversationActionListener;
    }

    public interface OnConversationActionListener {
        void onConversationChosen(Conversation conversation);
        void onConversationRemoved(Conversation conversation);
    }
}