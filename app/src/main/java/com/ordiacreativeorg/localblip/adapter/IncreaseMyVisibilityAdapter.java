package com.ordiacreativeorg.localblip.adapter;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.fragment.Blip_Alert;
import com.ordiacreativeorg.localblip.fragment.Blip_Mail;
import com.ordiacreativeorg.localblip.fragment.Blip_Picks;
import com.ordiacreativeorg.localblip.model.BlipBoost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 *
 * This adapter for showing blip in blip list
 */
public class IncreaseMyVisibilityAdapter extends RecyclerView.Adapter<IncreaseMyVisibilityAdapter.ViewHolder> {
    private final ArrayList<BlipBoost> mDataSet;

    public IncreaseMyVisibilityAdapter(ArrayList<BlipBoost> data ) {
        mDataSet = data;
    }

    @Override
    public IncreaseMyVisibilityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blip_boost_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BlipBoost blipBoost = mDataSet.get(position);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.no_image)
                .showImageForEmptyUri(R.drawable.no_image)
                .showImageOnLoading(R.drawable.no_image)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        // Stop running tasks on this image if there are unfinished some
        ImageLoader.getInstance().cancelDisplayTask(holder.blipBoostImage);
        // Load the image
        ImageLoader.getInstance().displayImage(blipBoost.getImage(), holder.blipBoostImage, options);

        holder.titleTextView.setText(blipBoost.getTitle());
        holder.summaryTextView.setText(blipBoost.getSummary());

        if (onBlipBootsActionListener != null){
            holder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBlipBootsActionListener.onBlipBoostChosen(blipBoost);
                }
            });
        }

    }

    @Override
    public int getItemCount()  {
        return mDataSet.size();
    }

    public void addAll(List<BlipBoost> elements) {
        int from = mDataSet.size();
        mDataSet.addAll(elements);
        int to = mDataSet.size();
        notifyItemRangeInserted(from, to - from);
    }

    public void replaceAll(List<BlipBoost> elements) {
        mDataSet.clear();
        mDataSet.addAll(elements);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View layoutView;
        private final ImageView blipBoostImage;
        private final TextView titleTextView;
        private final TextView summaryTextView;
        private FragmentTransaction fragmentTransaction;
//        OnItemClickListener mItemClickListener;
        //private final Button moreButton;

        public ViewHolder(View v) {
            super(v);
            layoutView = v;
            blipBoostImage = (ImageView) v.findViewById(R.id.iv_icon);
            titleTextView = (TextView) v.findViewById(R.id.tv_title);
            summaryTextView = (TextView) v.findViewById(R.id.tv_summary);
            v.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 getAdapterPosition();
                 Log.e("posss",""+getAdapterPosition());
                 if (getAdapterPosition()==3){

                     AppCompatActivity activity = (AppCompatActivity) view.getContext();
                     Fragment alert = new Blip_Alert();

                     activity.getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, alert).addToBackStack(null).commit();

                 }
                 if (getAdapterPosition()==4){
                     Log.e("mailll",""+getAdapterPosition());

                     AppCompatActivity activity = (AppCompatActivity) view.getContext();
                     Fragment mail = new Blip_Mail();

                     activity.getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, mail).addToBackStack(null).commit();

                 }
                 if (getAdapterPosition()==0){

                     AppCompatActivity activity = (AppCompatActivity) view.getContext();
                     Fragment pick = new Blip_Picks();

                     activity.getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, pick).addToBackStack(null).commit();

                 }
             }
         });
        }
    }

    private OnBlipBootsActionListener onBlipBootsActionListener;

    public void setOnBlipBootsActionListener(OnBlipBootsActionListener onBlipBootsActionListener){
        this.onBlipBootsActionListener = onBlipBootsActionListener;
    }

    public interface OnBlipBootsActionListener {
        void onBlipBoostChosen(BlipBoost blip);
    }
}