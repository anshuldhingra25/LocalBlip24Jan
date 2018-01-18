package com.ordiacreativeorg.localblip.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.Category;

import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/18/2015
 *
 * This adapter for showing blip in blip list
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private final List<Category> mDataSet;

    public CategoriesAdapter(List<Category> data) {
        mDataSet = data;
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.categoryTextView.setText(mDataSet.get(position).getCategoryName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectionListener != null){
                    onSelectionListener.onCategorySelected(mDataSet.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final View view;
        private final TextView categoryTextView;

        public ViewHolder(View v) {
            super(v);

            view = v;
            categoryTextView = (TextView) v.findViewById(R.id.tv_category);
        }
    }

    private OnSelectionListener onSelectionListener;

    public void setCategorySelectedListener(OnSelectionListener onSelectListener){
        this.onSelectionListener = onSelectListener;
    }

    public interface OnSelectionListener {
        void onCategorySelected(Category category);
    }
}
