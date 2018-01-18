package com.ordiacreativeorg.localblip.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 *
 * This adapter for showing blip in blip list
 */
public class VendorLocationsAdapter extends RecyclerView.Adapter<VendorLocationsAdapter.ViewHolder> {
    private final ArrayList<Location> mDataSet;

    public VendorLocationsAdapter(ArrayList<Location> data) {
        mDataSet = data;
    }

    @Override
    public VendorLocationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_card_vendor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Location location = mDataSet.get(position);

        if (location.isNationwide()){
            holder.address1TextView.setVisibility(View.GONE);
            holder.address2TextView.setVisibility(View.GONE);
            holder.stateTextView.setText(R.string.nationwide_location);
        }else {
            if (location.getAddress1() != null && !location.getAddress1().isEmpty()) {
                holder.address1TextView.setText(location.getAddress1());
                holder.address1TextView.setVisibility(View.VISIBLE);
            } else {
                holder.address1TextView.setVisibility(View.GONE);
            }
            if (location.getAddress2() != null && !location.getAddress2().isEmpty()) {
                holder.address2TextView.setText("#"+location.getAddress2());
                holder.address2TextView.setVisibility(View.VISIBLE);
            } else {
                holder.address2TextView.setVisibility(View.GONE);
            }
            String state = "";
            if (location.getCity() != null){
                state += location.getCity();
            }
            if (!state.isEmpty()) state += ", ";
            if (location.getState() != null){
                state += location.getState() + "  ";
            }
            if (location.getZipCode() != null){
                state += location.getZipCode();
            }
            if (state.isEmpty()){
                holder.stateTextView.setVisibility(View.GONE);
            }else{
                holder.stateTextView.setVisibility(View.VISIBLE);
                holder.stateTextView.setText(state);
            }
        }
        if (location.getWebsite() != null && !location.getWebsite().isEmpty()) {
            holder.websiteTextView.setText(location.getWebsite());
            holder.websiteTextView.setVisibility(View.VISIBLE);
        } else {
            holder.websiteTextView.setVisibility(View.GONE);
        }
        if (location.getPhone() != null && !location.getPhone().isEmpty()) {
            holder.phoneTextView.setText(location.getPhone());
            holder.phoneTextView.setVisibility(View.VISIBLE);
        } else {
            holder.phoneTextView.setVisibility(View.GONE);
        }


        if (onLocationActionListener != null){
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLocationActionListener.onLocationRemoved(location);
                }
            });
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLocationActionListener.onLocationEdit(location);
                }
            });
            holder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLocationActionListener.onLocationChosen(location);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addAll(List<Location> elements) {
        int from = mDataSet.size();
        mDataSet.addAll(elements);
        int to = mDataSet.size();
        notifyItemRangeInserted(from, to - from);
    }

    public void replaceAll(List<Location> elements) {
        mDataSet.clear();
        mDataSet.addAll(elements);
        notifyDataSetChanged();
    }

    public void removeLocation(Location location) {
        int index = mDataSet.indexOf(location);
        if (index < 0){
            for (int i = 0; i < mDataSet.size(); i++){
                if (location.getLocationId() == mDataSet.get(i).getLocationId()){
                    index = i;
                }
            }
        }
        if (index >= 0) {
            mDataSet.remove(index);
            notifyItemRemoved(index);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final View layoutView;
        private final TextView address1TextView;
        private final TextView address2TextView;
        private final TextView websiteTextView;
        private final TextView phoneTextView;
        private final TextView stateTextView;
        private final Button removeButton;
        private final Button editButton;

        public ViewHolder(View v) {
            super(v);
            layoutView = v;
            address1TextView = (TextView) v.findViewById(R.id.tv_address1);
            address2TextView = (TextView) v.findViewById(R.id.tv_address2);
            websiteTextView = (TextView) v.findViewById(R.id.tv_website);
            phoneTextView = (TextView) v.findViewById(R.id.tv_phone);
            stateTextView = (TextView) v.findViewById(R.id.tv_state_city_zip);
            removeButton = (Button) v.findViewById(R.id.btn_remove);
            editButton = (Button) v.findViewById(R.id.btn_edit);
        }
    }

    private OnLocationActionListener onLocationActionListener;

    public void setOnLocationActionListener(OnLocationActionListener onLocationActionListener){
        this.onLocationActionListener = onLocationActionListener;
    }

    public interface OnLocationActionListener {
        void onLocationEdit(Location location);
        void onLocationChosen(Location location);
        void onLocationRemoved(Location location);
    }
}