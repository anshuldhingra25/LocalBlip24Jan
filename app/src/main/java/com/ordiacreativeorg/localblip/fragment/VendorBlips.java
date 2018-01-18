package com.ordiacreativeorg.localblip.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.adapter.VendorBlipsAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.HorizontalListView;
import com.ordiacreativeorg.localblip.util.PauseOnRecyclerScrollListener;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 *
 */
public class VendorBlips extends Fragment {

    private RecyclerView mRecyclerView;
    private float mDPI;
    private TextView mEmptyTextView;
    private ProgressDialog mProgressDialog;
    private RelativeLayout rellist;
    private HorizontalListView book_my_ride_listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDPI = getActivity().getResources().getDisplayMetrics().density;
        View rootView = inflater.inflate(R.layout.itemlist_fragment, container, false);
        mProgressDialog = new ProgressDialog();
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        Log.e("lkkj",""+1);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
//                        getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, new ShopBlips()).addToBackStack(null).commit();
//                        ((MainActivity) getActivity()).changeFragment();
//                        acc.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;

            }
        });
        rellist = (RelativeLayout) rootView.findViewById(R.id.rellist);
        rellist.setVisibility(View.GONE);
        book_my_ride_listview = (HorizontalListView)rootView.findViewById(R.id.book_my_ride_listview);
        book_my_ride_listview.setVisibility(View.GONE);
//        RecyclerView list = new RecyclerView(getActivity());
//        list= (RecyclerView) rootView.findViewById(R.id.book_my_ride_listview);
//        ViewGroup.MarginLayoutParams listmarginLayoutParams =
//                (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
//        listmarginLayoutParams.setMargins(0, 10, 0, 10);
//        list.setLayoutParams(listmarginLayoutParams);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_content);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(resolveSpanCount(), GridLayoutManager.VERTICAL);
        ViewGroup.MarginLayoutParams listmarginLayoutParams = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
        listmarginLayoutParams.setMargins(0, 0, 0, 0);

        mRecyclerView.setLayoutManager(layoutManager);
        VendorBlipsAdapter blipBookAdapter = new VendorBlipsAdapter(new ArrayList<Blip>(),getActivity());
        blipBookAdapter.setOnBlipActionListener(onBlipActionListener);
        mRecyclerView.setAdapter(blipBookAdapter);
        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.btn_main_action);
        floatingActionButton.setImageResource(R.drawable.ic_action_add_light);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.ordiacreativeorg.localblip.activity.EditBlipDetails.class);
                startActivityForResult(intent, 2501);
            }
        });
        mEmptyTextView = (TextView) rootView.findViewById(R.id.tv_empty);
        mEmptyTextView.setText(R.string.no_blips_created);
        loadMoreBlips(addBlipsToList);
        return rootView;
    }

    private int resolveSpanCount(){
        int spanCount;
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        // Calculating number of columns, optimal for the screen size - some magic =)
        spanCount = (int) Math.floor( (width / mDPI) / 248);
        //Log.d("LOG_TAG", "width=" + width + "  spanCount=" + spanCount);
        if (spanCount < 1) spanCount = 1;

        return spanCount;
    }

    // To pause all ImageLoader working threads while scrolling
    private final PauseOnRecyclerScrollListener pauseOnRecyclerScrollListener = new PauseOnRecyclerScrollListener(ImageLoader.getInstance(), false, true);

    private final DataLoadResultListener addBlipsToList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(VendorBlipsAdapter adapter, List<Blip> blips) {
            adapter.addAll(blips);
        }
    };

    private final DataLoadResultListener replaceBlipsInList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(VendorBlipsAdapter adapter, List<Blip> blips) {
            adapter.replaceAll(blips);
        }
    };

    interface DataLoadResultListener{
        void onDataReturned(VendorBlipsAdapter adapter, List<Blip> blips);
    }

    private void loadMoreBlips(final DataLoadResultListener dataLoadResultListener) {
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Api.getInstance().getMethods().getVendorsBlips(
                memberDetail.getEmail(),
                memberDetail.getApiKey()
        ).enqueue(new Callback<ArrayList<Blip>>() {
            @Override
            public void onResponse(Response<ArrayList<Blip>> response, Retrofit retrofit) {
                final List<Blip> blips = response.body();
                final VendorBlipsAdapter adapter = (VendorBlipsAdapter) mRecyclerView.getAdapter();
                mRecyclerView.removeOnScrollListener(pauseOnRecyclerScrollListener);
                if (blips != null && !blips.isEmpty()) {
                    dataLoadResultListener.onDataReturned(adapter, blips);
                    mRecyclerView.addOnScrollListener(pauseOnRecyclerScrollListener);
                    mEmptyTextView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                mEmptyTextView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                mProgressDialog.dismiss();
                Snackbar.make(mRecyclerView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
            }
        });
    }

    private final VendorBlipsAdapter.OnBlipActionListener onBlipActionListener = new VendorBlipsAdapter.OnBlipActionListener() {
        @Override
        public void onBlipChosen(Blip blip) {
            Intent intent = new Intent( getActivity(), com.ordiacreativeorg.localblip.activity.EditBlipDetails.class );
            intent.putExtra("blip", blip);
            startActivityForResult(intent, 2501);
        }

        @Override
        public void onBlipEditChosen(Blip blip) {
            Intent intent = new Intent( getActivity(), com.ordiacreativeorg.localblip.activity.EditBlipDetails.class );
            intent.putExtra("blip", blip);
            intent.putExtra("edit", true);
            startActivityForResult(intent, 2501);
        }

        @Override
        public void onBlipRemoved(final Blip blip) {
            removeBlip(blip);
        }
    };

    private void removeBlip(final Blip blip){
        if (blip == null) return;
        final MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        Api.getInstance().getMethods().deleteBlip(
                memberDetail.getEmail(),
                memberDetail.getApiKey(),
                blip.getCouponId()
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                mProgressDialog.dismiss();
                if (response.body().equals("SUCCESS")) {
                    ((VendorBlipsAdapter) mRecyclerView.getAdapter()).removeBlip(blip);
                    if (mRecyclerView.getAdapter().getItemCount() < 1) {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    Snackbar.make(mRecyclerView, R.string.blip_removed, Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                }else{
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.blip_not_removed) + " " + response.body(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mProgressDialog.dismiss();
                Snackbar.make(mRecyclerView, getResources().getString(R.string.remove_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                if (mRecyclerView.getAdapter().getItemCount() < 1) {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 2501:
                if (resultCode == Activity.RESULT_OK && data.hasExtra("action")){
                    if (data.getStringExtra("action").equals("reload")) {
                        Snackbar.make(mRecyclerView, data.getStringExtra("message"), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                        loadMoreBlips(replaceBlipsInList);
                    } else if (data.getStringExtra("action").equals("remove")) {
                        removeBlip((Blip) data.getSerializableExtra("target"));
                    }
                }
                break;
        }
    }

    // Temporary
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle(R.string.your_blips, false);

    }
}
