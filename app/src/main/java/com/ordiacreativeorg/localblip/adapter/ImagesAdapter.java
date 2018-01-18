package com.ordiacreativeorg.localblip.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.ImageFile;

import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/19/2015
 *
 * This adapter for showing blip in blip list
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private final List<ImageFile> mDataSet;

    public ImagesAdapter(List<ImageFile> data) {
        mDataSet = data;
    }

    @Override
    public ImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
        ImageLoader.getInstance().cancelDisplayTask(holder.imageView);
        // Load the image
        ImageLoader.getInstance().displayImage("http://localblip.com/" + mDataSet.get(position).getSource().substring(3).replace("\\", ""), holder.imageView, options);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectionListener != null) {
                    onSelectionListener.onImageSelected(mDataSet.get(position));
                }
            }
        });
    }

    public void addAll(List<ImageFile> elements) {
        int from = mDataSet.size();
        mDataSet.addAll(elements);
        int to = mDataSet.size();
        notifyItemRangeInserted(from, to - from);
    }

    public void replaceAll(List<ImageFile> elements) {
        mDataSet.clear();
        if (elements != null) {
            mDataSet.addAll(elements);
        }
        notifyDataSetChanged();
    }

    public void removeAll() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v;
        }
    }

    private OnSelectionListener onSelectionListener;

    public void setImageSelectedListener(OnSelectionListener onSelectListener){
        this.onSelectionListener = onSelectListener;
    }

    public interface OnSelectionListener {
        void onImageSelected(ImageFile imageFile);
    }
}
