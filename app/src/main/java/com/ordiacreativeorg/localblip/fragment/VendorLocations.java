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
import com.ordiacreativeorg.localblip.adapter.VendorLocationsAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Location;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.SimpleResponse;
import com.ordiacreativeorg.localblip.util.HorizontalListView;
import com.ordiacreativeorg.localblip.util.PauseOnRecyclerScrollListener;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 *
 */
public class VendorLocations extends Fragment {

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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_content);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(resolveSpanCount(), GridLayoutManager.VERTICAL);
        ViewGroup.MarginLayoutParams listmarginLayoutParams = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
        listmarginLayoutParams.setMargins(0, 0, 0, 0);

        mRecyclerView.setLayoutManager(layoutManager);
        VendorLocationsAdapter blipBookAdapter = new VendorLocationsAdapter(new ArrayList<Location>());
        blipBookAdapter.setOnLocationActionListener(onBlipActionListener);
        mRecyclerView.setAdapter(blipBookAdapter);
        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.btn_main_action);
        floatingActionButton.setImageResource(R.drawable.ic_action_add_light);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (screenSize >= 480) {
                //    EditLocation editLocation = EditLocation.newInstance(new Location(), false);
                //    editLocation.setTargetFragment(VendorLocations.this, 1362);
                //    editLocation.show(getActivity().getSupportFragmentManager(), "location");
                //} else {
                    Intent intent = new Intent(getActivity(), com.ordiacreativeorg.localblip.activity.EditLocation.class);
                    intent.putExtra("location", new Location());
                    intent.putExtra("edit", true);
                    startActivityForResult(intent, 1362);
                //}
            }
        });
        mEmptyTextView = (TextView) rootView.findViewById(R.id.tv_empty);
        mEmptyTextView.setText(R.string.no_locations);
        loadMoreLocations(addBlipsToList);
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
        public void onDataReturned(VendorLocationsAdapter adapter, List<Location> locations) {
            adapter.addAll(locations);
        }
    };

    private final DataLoadResultListener replaceBlipsInList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(VendorLocationsAdapter adapter, List<Location> locations) {
            adapter.replaceAll(locations);
        }
    };

    interface DataLoadResultListener{
        void onDataReturned(VendorLocationsAdapter adapter, List<Location> locations);
    }

    private void loadMoreLocations(final DataLoadResultListener dataLoadResultListener) {
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        final MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Api.getInstance().getMethods().getMembersDetails(
                memberDetail.getEmail(),
                memberDetail.getApiKey(),
                new HashMap()
        ).enqueue(new Callback<MemberDetail>() {
            @Override
            public void onResponse(Response<MemberDetail> response, Retrofit retrofit) {
                final List<Location> locations = response.body().getLocations();
                final VendorLocationsAdapter adapter = (VendorLocationsAdapter) mRecyclerView.getAdapter();
                memberDetail.setLocations(locations);
                mRecyclerView.removeOnScrollListener(pauseOnRecyclerScrollListener);
                if (locations != null && !locations.isEmpty()) {
                    dataLoadResultListener.onDataReturned(adapter, locations);
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

    private final VendorLocationsAdapter.OnLocationActionListener onBlipActionListener = new VendorLocationsAdapter.OnLocationActionListener() {
        @Override
        public void onLocationEdit(Location location) {
            //Intent intent = new Intent( getActivity(), com.ordiacreative.localblip.activity.EditBlipDetails.class );
            //intent.putExtra("location", location);
            //startActivityForResult(intent, 2501);
            Intent intent = new Intent( getActivity(), com.ordiacreativeorg.localblip.activity.EditLocation.class );
            intent.putExtra("location", location);
            intent.putExtra("edit", true);
            startActivityForResult(intent, 1362);
        }

        @Override
        public void onLocationChosen(Location location) {
            //if (screenSize >= 480){
            //    EditLocation editLocation = EditLocation.newInstance(location);
            //    editLocation.setTargetFragment(VendorLocations.this, 1362);
            //    editLocation.show(getActivity().getSupportFragmentManager(), "location");
            //}else{
                Intent intent = new Intent( getActivity(), com.ordiacreativeorg.localblip.activity.EditLocation.class );
                intent.putExtra("location", location);
                intent.putExtra("edit", false);
                startActivityForResult(intent, 1362);
            //}
        }

        @Override
        public void onLocationRemoved(Location location) {
            removeLocation(location);
        }
    };

    private void removeLocation(final Location location){
        if (location == null) return;
        final MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        Api.getInstance().getMethods().deleteLocation(
                memberDetail.getEmail(),
                memberDetail.getApiKey(),
                location.getLocationId()
        ).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                mProgressDialog.dismiss();
                if (response.body().getResponse().equals("success")) {
                    ((VendorLocationsAdapter) mRecyclerView.getAdapter()).removeLocation(location);
                    if (mRecyclerView.getAdapter().getItemCount() < 1) {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    memberDetail.getLocations().remove(location);
                    Snackbar.make(mRecyclerView, R.string.location_removed, Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                }else{
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.location_not_removed) + response.body().getResponse(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
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
            case 1362:
                if (resultCode == Activity.RESULT_OK && data.hasExtra("action")){
                    if (data.getStringExtra("action").equals("reload")) {
                        Snackbar.make(mRecyclerView, data.getStringExtra("message"), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                        loadMoreLocations(replaceBlipsInList);
                    } else if (data.getStringExtra("action").equals("remove")) {
                        removeLocation((Location) data.getSerializableExtra("target"));
                    }
                }
                break;
        }
    }

    // Temporary
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle(R.string.nd_locations, false);

    }
}
