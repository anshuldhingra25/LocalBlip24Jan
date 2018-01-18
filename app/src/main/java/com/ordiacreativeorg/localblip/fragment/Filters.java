package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 */
public class Filters extends DialogFragment {
    static String searchText = "";

    public Filters() {
        // Required empty public constructor
    }

    public static Filters createInstance(ArrayList<String> categories) {
        Filters filters = new Filters();
        Bundle args = new Bundle();
        args.putStringArrayList("categories", categories);
        filters.setArguments(args);
        return filters;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    private TextView mSearchKeywordTextView;
    private Spinner mCategoriesSpinner;
    private Spinner mMilesSpinner;
    private EditText mZipCodeEditText;
    private RadioButton mOnlineVendorRadioButton;
    private RadioButton mLocalVendorRadioButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filters, container, false);
        mSearchKeywordTextView = (TextView) view.findViewById(R.id.et_keyword);
        mCategoriesSpinner = (Spinner) view.findViewById(R.id.sp_categories);
        mOnlineVendorRadioButton = (RadioButton) view.findViewById(R.id.rb_nationwide);
        mLocalVendorRadioButton = (RadioButton) view.findViewById(R.id.rb_local);
        mMilesSpinner = (Spinner) view.findViewById(R.id.sp_miles);
        mZipCodeEditText = (EditText) view.findViewById(R.id.et_zipcode);
        view.findViewById(R.id.btn_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TemporaryStorageSingleton.getInstance().setSearchKeyWord(mSearchKeywordTextView.getText().toString());
                searchText = mSearchKeywordTextView.getText().toString();
                TemporaryStorageSingleton.getInstance().setCategoryPosition(mCategoriesSpinner.getSelectedItemPosition());
                TemporaryStorageSingleton.getInstance().setShowLocalVendors(mLocalVendorRadioButton.isChecked());
                TemporaryStorageSingleton.getInstance().setMiles(mMilesSpinner.getSelectedItemPosition());
                int zipcode = -1;
                if (!mZipCodeEditText.getText().toString().isEmpty()) {
                    try {
                        zipcode = Integer.valueOf(mZipCodeEditText.getText().toString());
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
                TemporaryStorageSingleton.getInstance().setZipCode(zipcode);

                TemporaryStorageSingleton.getInstance().setValueZip(1);
                dismiss();
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        initFilter();
        return view;
    }

    private void initFilter() {
        if (!TemporaryStorageSingleton.getInstance().getSearchKeyWord().isEmpty()) {
            mSearchKeywordTextView.setText(TemporaryStorageSingleton.getInstance().getSearchKeyWord());
        }
        if (getArguments() != null && getArguments().containsKey("categories")) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getArguments().getStringArrayList("categories"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCategoriesSpinner.setAdapter(adapter);
            mCategoriesSpinner.setSelection(TemporaryStorageSingleton.getInstance().getCategoryPosition());
        }
        mLocalVendorRadioButton.setChecked(TemporaryStorageSingleton.getInstance().isShowLocalVendors());
        mOnlineVendorRadioButton.setChecked(!TemporaryStorageSingleton.getInstance().isShowLocalVendors());
        mMilesSpinner.setSelection(TemporaryStorageSingleton.getInstance().getMiles());
        if (TemporaryStorageSingleton.getInstance().getZipCode() < 0) {
            if (TemporaryStorageSingleton.getInstance().getMemberDetails().getZipCode().trim().isEmpty() || TemporaryStorageSingleton.getInstance().getMemberDetails().isZipCodeAsHome().equals("0")) {
                mZipCodeEditText.setText("33304");
            } else {
                mZipCodeEditText.setText(TemporaryStorageSingleton.getInstance().getMemberDetails().getZipCode().trim());
            }
        } else {
            mZipCodeEditText.setText(String.valueOf(TemporaryStorageSingleton.getInstance().getZipCode()));
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
        super.onCancel(dialog);
    }
}
