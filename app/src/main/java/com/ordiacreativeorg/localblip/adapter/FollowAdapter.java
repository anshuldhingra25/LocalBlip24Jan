package com.ordiacreativeorg.localblip.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.SocialItem;

import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/18/2015
 *
 * This adapter for showing blip in blip list
 */
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    private final List<SocialItem> mDataSet;
    private Context mContext;

    public FollowAdapter(Context context, List<SocialItem> data) {
        mDataSet = data;
        mContext = context;
    }

    @Override
    public FollowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.followImageView.setImageResource(mDataSet.get(position).getImageId());
        holder.followTextView.setText(mDataSet.get(position).getName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try{
                    mContext.getPackageManager().getPackageInfo(mDataSet.get(position).getAppPackage(), 0);
                    intent.setData(Uri.parse(mDataSet.get(position).getAppUrl() + mDataSet.get(position).getId()));
                    intent.setPackage(mDataSet.get(position).getAppPackage());
                } catch (Exception e) {
                    e.printStackTrace();
                    intent.setData(Uri.parse(mDataSet.get(position).getUrl() + mDataSet.get(position).getId()));
                }
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView followTextView;
        private ImageView followImageView;

        public ViewHolder(View v) {
            super(v);
            view = v;
            followTextView = (TextView) v.findViewById(R.id.tv_follow);
            followImageView = (ImageView) v.findViewById(R.id.iv_follow);
        }
    }
}
