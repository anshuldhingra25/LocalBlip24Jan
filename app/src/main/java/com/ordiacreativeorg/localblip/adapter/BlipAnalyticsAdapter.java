package com.ordiacreativeorg.localblip.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static com.ordiacreativeorg.localblip.R.id.tv_location;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 * <p>
 * This adapter for showing blip in blip list
 */
public class BlipAnalyticsAdapter extends RecyclerView.Adapter<BlipAnalyticsAdapter.ViewHolder> {
    private final ArrayList<Blip> mDataSet;

    public BlipAnalyticsAdapter(ArrayList<Blip> data) {
        mDataSet = data;
    }

    @Override
    public BlipAnalyticsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blip_card_analytics, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Blip blip = mDataSet.get(position);
        Log.e("BlipViews", String.valueOf(blip.getViews()));

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
        ImageLoader.getInstance().cancelDisplayTask(holder.blipImage);
        // Load the image
        ImageLoader.getInstance().displayImage(blip.getBlipImage(), holder.blipImage, options);

        if (!blip.getCategories().isEmpty()) {
            String category = blip.getCategories().get(0).getCategoryName();
            /*if (!blip.getCategories().get(0).getSubCategories().isEmpty()){
                category += ", " + blip.getCategories().get(0).getSubCategories().get(0).getSubCategoryName();
            }*/
            holder.categoryTextView.setVisibility(View.VISIBLE);
            holder.categoryTextView.setText(category);
        } else {
            holder.categoryTextView.setVisibility(View.GONE);
        }

        //franchise Owner Tag
        if (!blip.getFranchise_owner().equals("0")) {

            holder.owner.setVisibility(View.VISIBLE);
            holder.owner.setText("FRANCHISE OWNER");
        } else {
            holder.owner.setVisibility(View.GONE);
        }

        switch (blip.getValueType()) {
            case 1:
                holder.prefixTextView.setText("save  $");
                holder.valueTextView.setText(String.valueOf(blip.getValue()));
                holder.suffixTextView.setVisibility(View.GONE);
                break;
            case 2:
                holder.prefixTextView.setText("save  ");
                holder.valueTextView.setText(String.valueOf(blip.getValue()));
                holder.suffixTextView.setVisibility(View.VISIBLE);
                break;
        }

        if (blip.isStatus()) {
            holder.inactiveTextView.setVisibility(View.GONE);
        } else {
            holder.inactiveTextView.setVisibility(View.VISIBLE);
        }

        holder.offerTextView.setText(blip.getCouponTitle());

        if (!blip.getLocations().isEmpty()) {
            holder.locationLayout.removeAllViews();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (Location location : blip.getLocations()) {
                String loc = "";
                TextView textView = new TextView(holder.locationLayout.getContext());
//                textView.setSingleLine();
                textView.setMaxLines(3);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                if (!location.getAddress1().isEmpty()) {
                    loc += location.getAddress1();

                }
                if (!location.getAddress2().isEmpty()) {
                    if (!loc.isEmpty()) loc += ",\n ";
                    loc += "#" + location.getAddress2();
                }
                if (location.isNationwide()) {
                    if (location.getWebsite() != null && !location.getWebsite().isEmpty()) {
                        if (!loc.isEmpty()) loc += "\n ";
                        loc += location.getWebsite();
                    }
                } else {
                    if (location.getCity() != null && !location.getCity().isEmpty()) {
                        if (!loc.isEmpty()) loc += "\n ";
                        loc += location.getCity();
                    }
                    if (location.getState() != null && !location.getState().isEmpty()) {
                        if (!loc.isEmpty()) loc += ", ";
                        loc += location.getState();
                    }
                    if (location.getZipCode() != null && !location.getZipCode().isEmpty()) {
                        if (!loc.isEmpty()) loc += " ";
                        loc += location.getZipCode();
                    }
                }
                if (!loc.isEmpty()) {
                    Log.e("LocationAnaltycs", "" + loc);
                    if(loc.contains("VENDOR")) loc=loc.replace("VENDOR","MERCHANT");
                    textView.setText(loc);
                    holder.locationLayout.addView(textView, layoutParams);
                }
                //loc += "\n";
            }
            //loc = loc.replaceAll("\n{2,}", "");
            //loc = loc.replaceAll("\n$", "");
            //holder.locationTextView.setVisibility(View.VISIBLE);
            holder.locationLayout.setVisibility(View.VISIBLE);
            //holder.locationTextView.setText(loc);
        } else {
            //holder.locationTextView.setVisibility(View.GONE);
            holder.locationLayout.setVisibility(View.GONE);
        }

        holder.viewsTextView.setText("Views: " + String.valueOf(blip.getViews()));
        holder.blipbooksTextView.setText("Added to BlipBooks: " + String.valueOf(blip.getCouponBookCount()));
        holder.mobileAppsTextView.setText("Redeemed with app: " + String.valueOf(blip.getMobileCount()));

        if (onBlipActionListener != null) {
            holder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBlipActionListener.onBlipChosen(blip);
                }
            });

            holder.detailsButton.setOnClickListener(new View.OnClickListener() {
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

    public void replaceAll(List<Blip> elements) {
        mDataSet.clear();
        mDataSet.addAll(elements);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View layoutView;
        private final ImageView blipImage;
        private final TextView categoryTextView;
        private final TextView prefixTextView;
        private final TextView valueTextView;
        private final TextView suffixTextView;
        private final TextView inactiveTextView;
        private final TextView offerTextView;
        private final TextView locationTextView;
        private final LinearLayout locationLayout;
        private final TextView viewsTextView;
        private final TextView blipbooksTextView;
        private final TextView mobileAppsTextView;
        private final Button detailsButton;
        private TextView owner;

        public ViewHolder(View v) {
            super(v);
            layoutView = v;
            owner = (TextView) v.findViewById(R.id.owner_type);
            blipImage = (ImageView) v.findViewById(R.id.blip_image);
            categoryTextView = (TextView) v.findViewById(R.id.tv_category);
            prefixTextView = (TextView) v.findViewById(R.id.tv_prefix);
            valueTextView = (TextView) v.findViewById(R.id.tv_value);
            suffixTextView = (TextView) v.findViewById(R.id.tv_suffix);
            inactiveTextView = (TextView) v.findViewById(R.id.tv_inactive);
            offerTextView = (TextView) v.findViewById(R.id.tv_offer);
            locationTextView = (TextView) v.findViewById(tv_location);
            locationLayout = (LinearLayout) v.findViewById(R.id.ll_location);
            viewsTextView = (TextView) v.findViewById(R.id.tv_views);
            blipbooksTextView = (TextView) v.findViewById(R.id.tv_blipbooks);
            mobileAppsTextView = (TextView) v.findViewById(R.id.tv_mobile_app);
            detailsButton = (Button) v.findViewById(R.id.btn_details);
        }
    }

    private OnBlipActionListener onBlipActionListener;

    public void setOnBlipActionListener(OnBlipActionListener onBlipActionListener) {
        this.onBlipActionListener = onBlipActionListener;
    }

    public interface OnBlipActionListener {
        void onBlipChosen(Blip blip);
    }
}