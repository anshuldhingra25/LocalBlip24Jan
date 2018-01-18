package com.ordiacreativeorg.localblip.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
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
import com.ordiacreativeorg.localblip.adapter.BlipAnalyticsAdapter;
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
public class BlipAnalytics extends Fragment {

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
        BlipAnalyticsAdapter blipBookAdapter = new BlipAnalyticsAdapter(new ArrayList<Blip>());
        blipBookAdapter.setOnBlipActionListener(onBlipActionListener);
        mRecyclerView.setAdapter(blipBookAdapter);
        rootView.findViewById(R.id.btn_main_action).setVisibility(View.GONE);
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
        public void onDataReturned(BlipAnalyticsAdapter adapter, List<Blip> blips) {
            adapter.addAll(blips);
        }
    };

    private final DataLoadResultListener replaceBlipsInList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(BlipAnalyticsAdapter adapter, List<Blip> blips) {
            adapter.replaceAll(blips);
        }
    };

    interface DataLoadResultListener{
        void onDataReturned(BlipAnalyticsAdapter adapter, List<Blip> blips);
    }

    private void loadMoreBlips(final DataLoadResultListener dataLoadResultListener) {
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Api.getInstance().getMethods().getBlipAnalytics(
                memberDetail.getEmail(),
                memberDetail.getApiKey()
        ).enqueue(new Callback<ArrayList<Blip>>() {
            @Override
            public void onResponse(Response<ArrayList<Blip>> response, Retrofit retrofit) {
                final List<Blip> blips = response.body();
                final BlipAnalyticsAdapter adapter = (BlipAnalyticsAdapter) mRecyclerView.getAdapter();
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

    private final BlipAnalyticsAdapter.OnBlipActionListener onBlipActionListener = new BlipAnalyticsAdapter.OnBlipActionListener() {
        @Override
        public void onBlipChosen(Blip blip) {
            Intent intent = new Intent( getActivity(), com.ordiacreativeorg.localblip.activity.BlipAnalyticsDetails.class );
            intent.putExtra("blip", blip);
            startActivity(intent);
        }
    };

    // Temporary
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle(R.string.nd_analytics, false);
    }
}
