package com.ordiacreativeorg.localblip.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.ListPojo;

import java.util.ArrayList;

/**
 * Created by sky on 9/12/16.
 */

public class CategoryAdapter extends BaseAdapter {
    private ArrayList<String> categoryList;
    private LayoutInflater mInflater;
    private Activity context;
    public static boolean val = false;
    ArrayList<ListPojo> models;

    public CategoryAdapter(Activity c, ArrayList<String> categoryList, ArrayList<ListPojo> models) {
        context = c;
        mInflater = LayoutInflater.from(context);
        this.categoryList = categoryList;
        this.models = models;

    }

    public CategoryAdapter(Activity c, ArrayList<String> categoryList) {
        context = c;
        mInflater = LayoutInflater.from(context);
        this.categoryList = categoryList;


    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }


    public class ViewHolder {

        private TextView category;
        private RelativeLayout categoryMainlayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.categorylayout, parent, false);
            holder = new ViewHolder();
            holder.category = (TextView) view.findViewById(R.id.categoryName);
            holder.categoryMainlayout = (RelativeLayout) view.findViewById(R.id.categoryMainlayout);
            holder.categoryMainlayout.setTag(position);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.category.setText(categoryList.get(position));
        holder.categoryMainlayout.setBackgroundColor(models.get(position).getSelected());
        // holder.time.setText(data.get(position).getCat_time());
//        if (position == ShopBlips.mselectedItems && val) {
//            view.findViewById(R.id.categoryMainlayout).setBackgroundColor(context.getResources().getColor(R.color.accent_color));
//            // set your color
//        }


        return view;
    }
}