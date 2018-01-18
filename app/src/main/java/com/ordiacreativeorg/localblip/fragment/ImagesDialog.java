package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.adapter.ImagesAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.CategoryWithImages;
import com.ordiacreativeorg.localblip.model.ImageFile;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.PauseOnRecyclerScrollListener;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/19/2015
 */
public class ImagesDialog extends DialogFragment {

    private float mDPI;
    private List<CategoryWithImages> mCategoriesWithImages = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private Spinner mSpinner;

    public ImagesDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDPI = getActivity().getResources().getDisplayMetrics().density;
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.images_dialog, container, false);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_images);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(resolveSpanCount(), StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(new ImagesAdapter(new ArrayList<ImageFile>()));
        mRecyclerView.addOnScrollListener(new PauseOnRecyclerScrollListener(ImageLoader.getInstance(), false, true));
        ((ImagesAdapter) mRecyclerView.getAdapter()).setImageSelectedListener(new ImagesAdapter.OnSelectionListener() {
            @Override
            public void onImageSelected(ImageFile imageFile) {
                Intent intent = new Intent();
                intent.putExtra("imageFile", imageFile);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        });
        mSpinner = (Spinner) rootView.findViewById(R.id.sp_categories);
        mProgressDialog = new ProgressDialog();

        loadCategoriesWithImages();

        return rootView;
    }

    private final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((ImagesAdapter) mRecyclerView.getAdapter()).replaceAll(mCategoriesWithImages.get(position).getImageFiles());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            ((ImagesAdapter) mRecyclerView.getAdapter()).removeAll();
        }
    };

    private void loadCategoriesWithImages(){
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        MemberDetail memberDetailInfo = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Api.getInstance().getMethods().getLibraryImages(
                memberDetailInfo.getEmail(),
                memberDetailInfo.getApiKey()
        ).enqueue(new Callback<List<CategoryWithImages>>() {

            @Override
            public void onResponse(Response<List<CategoryWithImages>> response, Retrofit retrofit) {
                mCategoriesWithImages = response.body();
                mProgressDialog.dismiss();
                ArrayList<String> categories = new ArrayList<>();
                for (CategoryWithImages categoryWithImages : mCategoriesWithImages) {
                    categories.add(categoryWithImages.getCategoryName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(onItemSelectedListener);
                mSpinner.setSelection(0);
                ((ImagesAdapter) mRecyclerView.getAdapter()).replaceAll(mCategoriesWithImages.get(0).getImageFiles());
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Snackbar.make(mRecyclerView, t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private int resolveSpanCount(){
        int spanCount;
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        // Calculating number of columns, optimal for the screen size - some magic =)
        spanCount = (int) Math.floor( (width / mDPI) / 160);
        //Log.d("LOG_TAG", "width=" + width + "  spanCount=" + spanCount);
        if (spanCount < 1) spanCount = 1;

        return spanCount;
    }

}
