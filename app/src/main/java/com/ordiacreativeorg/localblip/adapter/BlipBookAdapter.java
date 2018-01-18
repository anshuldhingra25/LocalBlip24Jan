package com.ordiacreativeorg.localblip.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/4/2015
 *
 * This adapter for showing blip in blip list
 */
public class BlipBookAdapter extends RecyclerView.Adapter<BlipBookAdapter.ViewHolder> {
    private final ArrayList<Blip> mDataSet;

    public BlipBookAdapter(ArrayList<Blip> data) {
        mDataSet = data;
    }

    @Override
    public BlipBookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blipbook_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Blip blip = mDataSet.get(position);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.b_character)
                .showImageForEmptyUri(R.drawable.b_character)
                .showImageOnLoading(R.drawable.b_character)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        // Stop running tasks on this image if there are unfinished some
        ImageLoader.getInstance().cancelDisplayTask(holder.blipImage);
        // Load the image
        ImageLoader.getInstance().displayImage(blip.getBlipImage(), holder.blipImage, options);

        switch (blip.getValueType()) {
            case 1:
                holder.prefixTextView.setVisibility(View.VISIBLE);
                holder.valueTextView.setText(String.valueOf(blip.getValue()));
                holder.suffixTextView.setVisibility(View.GONE);
                break;
            case 2:
                holder.prefixTextView.setVisibility(View.GONE);
                holder.valueTextView.setText(String.valueOf(blip.getValue()));
                holder.suffixTextView.setVisibility(View.VISIBLE);
                break;
        }

        holder.vendorTextView.setText(blip.getVendorName());
        if (blip.getLocations() != null && !blip.getLocations().isEmpty()){
            Location location = blip.getLocations().get(0);
            String loc = "";
            if (!location.getAddress1().isEmpty()){
                loc += location.getAddress1();

            }if (!location.getAddress2().isEmpty()) {
                loc += location.getAddress2();
            }
            if (!location.getCity().isEmpty()){
                if (!loc.isEmpty()) loc += "\n";
                loc += location.getCity();
            }
            if (!location.getState().isEmpty()){
                if (!loc.isEmpty()) loc += ", ";
                loc += location.getState();
            }
            if (!location.getPhone().isEmpty()){
                if (!loc.isEmpty()) loc += "\n";
                loc += location.getPhone();
            }
            Log.e("LocationBook", "" + loc);
            if(loc.contains("VENDOR")) loc=loc.replace("VENDOR","MERCHANT");
            holder.locationTextView.setVisibility(View.VISIBLE);
            holder.locationTextView.setText(loc);
        }else{
            holder.locationTextView.setVisibility(View.GONE);
        }

        holder.offerTextView.setText(blip.getCouponTitle());

        String expirationDate = blip.getExpireDate();
        if (!expirationDate.equals("UNLIMITED") && !expirationDate.equals("0")){
            holder.expirationDateTitleTextView.setVisibility(View.VISIBLE);
            holder.expirationDateTextView.setVisibility(View.VISIBLE);
            holder.expirationDateTextView.setText(expirationDate);
        }else{
            holder.expirationDateTitleTextView.setVisibility(View.GONE);
            holder.expirationDateTextView.setVisibility(View.GONE);
        }

        if (onBlipActionListener != null){
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBlipActionListener.onBlipRemoved(blip);
                }
            });
            holder.viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBlipActionListener.onBlipChosen(blip);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addAll(List<Blip> elements) {
        int from = mDataSet.size();
        mDataSet.addAll(elements);
        int to = mDataSet.size();
        notifyItemRangeInserted(from, to - from);
    }

    public void restore(Blip element) {
        mDataSet.add(0, element);
        notifyDataSetChanged();
    }

    public void replaceAll(List<Blip> elements) {
        mDataSet.clear();
        mDataSet.addAll(elements);
        notifyDataSetChanged();
    }

    public void removeBlip(Blip blip) {
        int index = mDataSet.indexOf(blip);
        mDataSet.remove(blip);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView blipImage;
        private final TextView prefixTextView;
        private final TextView valueTextView;
        private final TextView suffixTextView;
        private final TextView vendorTextView;
        private final TextView locationTextView;
        private final TextView offerTextView;
        private final TextView expirationDateTextView;
        private final TextView expirationDateTitleTextView;
        private final Button removeButton;
        private final Button viewButton;

        public ViewHolder(View v) {
            super(v);

            blipImage = (ImageView) v.findViewById(R.id.blip_image);
            prefixTextView = (TextView) v.findViewById(R.id.tv_prefix);
            valueTextView = (TextView) v.findViewById(R.id.tv_value);
            suffixTextView = (TextView) v.findViewById(R.id.tv_suffix);
            vendorTextView = (TextView) v.findViewById(R.id.tv_vendor);
            offerTextView = (TextView) v.findViewById(R.id.tv_offer);
            locationTextView = (TextView) v.findViewById(R.id.tv_location);
            expirationDateTextView = (TextView) v.findViewById(R.id.tv_expiration_date);
            expirationDateTitleTextView = (TextView) v.findViewById(R.id.tv_expiration_date_title);
            removeButton = (Button) v.findViewById(R.id.btn_remove);
            viewButton = (Button) v.findViewById(R.id.btn_view);
        }
    }

    private OnBlipActionListener onBlipActionListener;

    public void setOnBlipActionListener( OnBlipActionListener onBlipActionListener){
        this.onBlipActionListener = onBlipActionListener;
    }

    public interface OnBlipActionListener{
        void onBlipChosen(Blip blip);
        void onBlipRemoved(Blip blip);
    }
}
