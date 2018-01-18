package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.SubCategory;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/18/2015
 */
public class SubCategoriesDialog extends DialogFragment {


    public static SubCategoriesDialog newInstance(int categoryId, int selectedId1, int selectedId2) {
        Bundle args = new Bundle();
        args.putInt("category", categoryId);
        args.putInt("subcatid1", selectedId1);
        args.putInt("subcatid2", selectedId2);
        SubCategoriesDialog fragment = new SubCategoriesDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public SubCategoriesDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    private int check1 = -1;
    private int check2 = -1;
    private List<SubCategory> subCategories;
    private boolean firstSelected = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.subcategory_dialog, container, false);
        ((TextView) rootView.findViewById(R.id.tv_title)).setText(R.string.select_subcategories);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        rootView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (check1 >= 0){
                    intent.putExtra("subcat1", subCategories.get(check1).copySubCategory());
                }
                if (check2 >= 0){
                    intent.putExtra("subcat2", subCategories.get(check2).copySubCategory());
                }
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        });
        final float dpi = getResources().getDisplayMetrics().density;
        final LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.ll_subcategories);
        if (getArguments() != null ){
            subCategories = new ArrayList<>();
            int catId = getArguments().getInt("category");
            int id1 = getArguments().getInt("subcatid1");
            int id2 = getArguments().getInt("subcatid2");
            for (Category category : TemporaryStorageSingleton.getInstance().getCategories()){
                if (category.getCategoryId() == catId){
                    subCategories.addAll(category.getSubCategories());
                }
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, Math.round(20 * dpi + 0.5f), 0, 0);
            for (int i=0; i< subCategories.size(); i++){
                CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.cb_element, container, false);
                checkBox.setText(subCategories.get(i).getSubCategoryName());
                final int finalI = i;
                if (subCategories.get(i).getSubCategoryId() == id1){
                    check1 = i;
                    checkBox.setChecked(true);
                }else if (subCategories.get(i).getSubCategoryId() == id2){
                    check2 = i;
                    checkBox.setChecked(true);
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (check1 < 0) {
                                check1 = finalI;
                                firstSelected = true;
                            } else if (check2 < 0) {
                                check2 = finalI;
                                firstSelected = false;
                            } else if (!firstSelected) {
                                ((CheckBox) linearLayout.getChildAt(check1)).setChecked(false);
                                check1 = finalI;
                                firstSelected = true;
                            } else {
                                ((CheckBox) linearLayout.getChildAt(check2)).setChecked(false);
                                check2 = finalI;
                                firstSelected = false;
                            }
                        } else {
                            if (check1 == finalI) {
                                check1 = -1;
                            } else {
                                check2 = -1;
                            }
                        }
                    }
                });
                linearLayout.addView(checkBox, layoutParams);
            }
        }

        return rootView;
    }
}
