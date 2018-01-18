
/**
 * Created by jasyc on 30/12/16.
 */


package com.ordiacreativeorg.localblip.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 *
 * This adapter for showing blip in blip list
 */
public class SelectBlipPickAdapter extends RecyclerView.Adapter<SelectBlipPickAdapter.ViewHolder> {
    private final ArrayList<Blip> mDataSet;

    public SelectBlipPickAdapter(ArrayList<Blip> data) {
        mDataSet = data;
    }

    @Override
    public SelectBlipPickAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blip_pick_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Blip blip = mDataSet.get(position);

        holder.offerTextView.setText(mDataSet.get(position).getCouponTitle());
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onSelectionListener != null){
//                    onSelectionListener.onBlipSelected(blip);
//                }
//            }
//        });

        // Stop running tasks on this image if there are unfinished some
        // ImageLoader.getInstance().cancelDisplayTask(holder.blipImage);
        // Load the image
        //  ImageLoader.getInstance().displayImage(blip.getBlipImage(), holder.blipImage, options);

        if (!blip.getLocations().isEmpty()){
            Location location = blip.getLocations().get(0);
            String loc = "";
            if (!location.getAddress1().isEmpty()){
                loc += " "+location.getAddress1();
            }if (!location.getAddress2().isEmpty()) {
                if (!loc.isEmpty()) loc += ",\n ";
                loc += "Suit / Unit Number : "+location.getAddress2();
            }
            if (!location.getCity().isEmpty()){
                if (!loc.isEmpty()) loc += ",\n ";
                loc += location.getCity();
            }
            if (!location.getState().isEmpty()){
                if (!loc.isEmpty()) loc += ", ";
                loc += location.getState();
            }

        }else{
        }

//        holder.offerTextView.setText(blip.getCouponTitle());

//        holder.offerTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onSelectionListener != null){
//                    onSelectionListener.onBlipSelected(mDataSet.get(position));
//                }
//            }
//        });

        String expirationDate = blip.getExpireDate();
        String remainingBlips = blip.getRemaining();

        if (expirationDate != null && !expirationDate.equals("UNLIMITED") && !expirationDate.equals("0")){

            String[] parsed = expirationDate.split("/");
            if (parsed.length == 3){
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, Integer.valueOf(parsed[1]) - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parsed[0]));
                    calendar.set(Calendar.YEAR, Integer.valueOf(parsed[2]) + 2000);
                    if ( Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis() < 2 *24 * 60 * 60 * 1000){
                    }
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }

        if (remainingBlips != null && !remainingBlips.equals("UNLIMITED")){

            try {
                if (Integer.valueOf(remainingBlips) < 1){
                }
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        }


        holder.offerTextView.setText(mDataSet.get(position).getCouponTitle());
        if (onSelectionListener != null){

            holder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelectionListener.onBlipSelected(mDataSet.get(position));
                    Log.e("ttt",""+onSelectionListener);
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

        private final View layoutView;
        //  private final ImageView blipImage;

        private final TextView offerTextView;
        private final View view;



        public ViewHolder(View v) {
            super(v);
            layoutView = v;
            view = v;
            //blipImage = (ImageView) v.findViewById(R.id.blip_image);

            offerTextView = (TextView) v.findViewById(R.id.tv_offer);


        }
    }

    private OnSelectionListener onSelectionListener;

    public void setBlipSelectedListener(SelectBlipPickAdapter.OnSelectionListener onSelectListener){
        this.onSelectionListener = onSelectListener;
    }

    public interface OnSelectionListener {
        void onBlipSelected(Blip blip);
    }

//    private OnBlipActionListener onBlipActionListener;
//
//    public void setOnBlipActionListener( OnBlipActionListener onBlipActionListener){
//        this.onBlipActionListener = onBlipActionListener;
//    }
//
//    public interface OnBlipActionListener{
//        void onBlipChosen(Blip blip);
//        void onBlipEditChosen(Blip blip);
//        void onBlipRemoved(Blip blip);
//    }
}