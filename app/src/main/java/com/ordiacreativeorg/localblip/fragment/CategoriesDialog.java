package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.adapter.CategoriesAdapter;
import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/18/2015
 */
public class CategoriesDialog extends DialogFragment {


    public CategoriesDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.category_dialog, container, false);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        List<Category> categories = new ArrayList<>();
        categories.addAll(TemporaryStorageSingleton.getInstance().getCategories());
        categories.remove(0);
        recyclerView.setAdapter(new CategoriesAdapter(categories));
        ((CategoriesAdapter) recyclerView.getAdapter()).setCategorySelectedListener(new CategoriesAdapter.OnSelectionListener() {
            @Override
            public void onCategorySelected(Category category) {
                Intent intent = new Intent();
                intent.putExtra("category", category.copyCategory());
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        });

        return rootView;
    }
}
