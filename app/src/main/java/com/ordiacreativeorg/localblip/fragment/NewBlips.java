package com.ordiacreativeorg.localblip.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.adapter.CategoryAdapter;
import com.ordiacreativeorg.localblip.adapter.ShopBlipsAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.ListPojo;
import com.ordiacreativeorg.localblip.model.MemberDetail;
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
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 */
public class NewBlips extends Fragment {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private float mDPI;
    private int mItemsLoadingStep;
    private TextView mEmptyTextView;
    private ProgressDialog mProgressDialog;
    //private FloatingActionButton mFloatingActionButton;
    private HorizontalListView listview;
    private ArrayList<String> categorylist;
    private RelativeLayout rellist;
    CategoryAdapter categoryAdapter;
    ArrayList<ListPojo> models = new ArrayList<>();


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

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
//                         globalcount = 1;
//                        Log.e("lkkj",""+1);
//                        getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, new ShopBlips()).addToBackStack(null).commit();
//                        ((MainActivity) getActivity()).changeFragment();
//                        acc.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;

            }
        });
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_content);


        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(mRecyclerView.getLayoutParams());
        marginParams.setMargins(4, 1, 4, 0);
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(marginParams);
        mRecyclerView.setLayoutParams(layoutParams);



        ShopBlipsAdapter shopBlipsAdapter = new ShopBlipsAdapter(new ArrayList<Blip>(), getActivity());
        shopBlipsAdapter.setOnBlipActionListener(onBlipActionListener);
        mRecyclerView.setAdapter(shopBlipsAdapter);
        mLayoutManager = new StaggeredGridLayoutManager(resolveSpanCount(), GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        rootView.findViewById(R.id.btn_main_action).setVisibility(View.GONE);
        categorylist = new ArrayList<>();
        categorylist.add("Auto & Marine");
        ListPojo listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Clothing & Acc.");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Electronics");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Entertainment");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Food & Drink");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Handmade Items / Crafts");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Health & Beauty");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Home & Garden");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Lodging");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Pet Care");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        categorylist.add("Prof. Services");
        listPojo = new ListPojo(Color.TRANSPARENT);
        models.add(listPojo);
        rellist = (RelativeLayout) rootView.findViewById(R.id.rellist);
        rellist.setVisibility(View.GONE);
        listview = (HorizontalListView) rootView.findViewById(R.id.book_my_ride_listview);
        categoryAdapter = new CategoryAdapter(getActivity(), categorylist, models);
        listview.setAdapter(categoryAdapter);
        //mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.btn_main_action);
        //mFloatingActionButton.setImageResource(R.drawable.ic_action_search);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.tv_empty);
        mEmptyTextView.setText(R.string.no_new_blips);
        loadMoreBlips(addBlipsToList);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                models.set(0, new ListPojo(Color.TRANSPARENT));
                models.set(1, new ListPojo(Color.TRANSPARENT));
                models.set(2, new ListPojo(Color.TRANSPARENT));
                models.set(3, new ListPojo(Color.TRANSPARENT));
                models.set(4, new ListPojo(Color.TRANSPARENT));
                models.set(5, new ListPojo(Color.TRANSPARENT));
                models.set(6, new ListPojo(Color.TRANSPARENT));
                models.set(7, new ListPojo(Color.TRANSPARENT));
                models.set(8, new ListPojo(Color.TRANSPARENT));
                models.set(9, new ListPojo(Color.TRANSPARENT));
                models.set(10, new ListPojo(Color.TRANSPARENT));
                models.set(i, new ListPojo(getResources().getColor(R.color.accent_color)));
                categoryAdapter.notifyDataSetChanged();
                TemporaryStorageSingleton.getInstance().setCategoryPosition(i + 1);
                loadMoreBlips(replaceBlipsInList);
            }
        });
        return rootView;
    }

    private int mOnScreenItemsCount;

    private int resolveSpanCount() {
        int spanCount;
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        // Calculating number of columns, optimal for the screen size - some magic =)
        spanCount = (short) Math.floor((width / mDPI) / 248);
        //Log.d("LOG_TAG", "width=" + width + "  spanCount=" + spanCount);
        if (spanCount < 1) spanCount = 1;

        // Average card height
        float itemHeight = 360 * mDPI;
        // Average count of visible cards in a column
        int itemsInAColumn = (int) Math.ceil(height / itemHeight);
        // Average count of cards to load, to make the list look without borders
        mOnScreenItemsCount = mItemsLoadingStep = (int) Math.ceil(spanCount * itemsInAColumn * 2.5f);
        // Minimal count of loading elements
        if (mItemsLoadingStep < 20) mItemsLoadingStep = 20;
        if (mOnScreenItemsCount < 5) mOnScreenItemsCount = 5;

        return spanCount;
    }

    // To pause all ImageLoader working threads while scrolling
    private final PauseOnRecyclerScrollListener pauseOnRecyclerScrollListener = new PauseOnRecyclerScrollListener(ImageLoader.getInstance(), false, true);

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView view, int newstate) {
            int totalItemCount = mLayoutManager.getItemCount();
            int[] lastVisibleItemPositions;
            lastVisibleItemPositions = mLayoutManager.findLastVisibleItemPositions(null);
            if (lastVisibleItemPositions != null) {
                int totalLoadedItemCount = 0;
                for (int lastVisibleItemPosition : lastVisibleItemPositions) {
                    if (lastVisibleItemPosition >= totalLoadedItemCount)
                        totalLoadedItemCount = lastVisibleItemPosition;
                }

                final ShopBlipsAdapter adapter = (ShopBlipsAdapter) mRecyclerView.getAdapter();
                if (totalLoadedItemCount >= totalItemCount - mOnScreenItemsCount) {
                    loadMoreBlips(adapter.getItemCount(), addBlipsToList);
                }
            }
        }
    };

    private final DataLoadResultListener addBlipsToList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(ShopBlipsAdapter adapter, List<Blip> blips) {
            adapter.addAll(blips);
        }
    };

    private final DataLoadResultListener replaceBlipsInList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(ShopBlipsAdapter adapter, List<Blip> blips) {
            adapter.replaceAll(blips);
        }
    };

    interface DataLoadResultListener {
        void onDataReturned(ShopBlipsAdapter adapter, List<Blip> blips);
    }

    private void loadMoreBlips(DataLoadResultListener dataLoadResultListener) {
        loadMoreBlips(0, mItemsLoadingStep, dataLoadResultListener);
    }

    private void loadMoreBlips(int position, DataLoadResultListener dataLoadResultListener) {
        loadMoreBlips(position, mItemsLoadingStep, dataLoadResultListener);
    }

    private void loadMoreBlips(final int position, int limit, final DataLoadResultListener dataLoadResultListener) {
        mRecyclerView.removeOnScrollListener(onScrollListener);
        //mProgressBar.setVisibility(View.VISIBLE);
        //mFloatingActionButton.setOnClickListener(null);

        HashMap<String, String> params = new HashMap<>();
        params.put("startfrom", String.valueOf(position));
        params.put("first", String.valueOf(position));
        params.put("count", String.valueOf(limit));
        if (TemporaryStorageSingleton.getInstance().getCategoryPosition() > 0) {
            params.put("catid", String.valueOf(TemporaryStorageSingleton.getInstance().getSelectedCategoryId()));
        }

        MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        Api.getInstance().getMethods().getNewBlips(
                memberDetail.getEmail(),
                memberDetail.getApiKey(),
                params
        ).enqueue(new Callback<ArrayList<Blip>>() {
            @Override
            public void onResponse(Response<ArrayList<Blip>> response, Retrofit retrofit) {
                final List<Blip> blips = response.body();
                List<Blip> blipList = new ArrayList<Blip>();
                for (Blip blip : blips) {
                    if (!blip.isOnline()) {
                        blipList.add(blip);
                    }

                }
                final ShopBlipsAdapter adapter = (ShopBlipsAdapter) mRecyclerView.getAdapter();
                mRecyclerView.removeOnScrollListener(pauseOnRecyclerScrollListener);
                if (blipList != null && !blipList.isEmpty()) {
                    dataLoadResultListener.onDataReturned(adapter, blipList);
                    if (blipList.size() == mItemsLoadingStep) {
                        mRecyclerView.addOnScrollListener(onScrollListener);
                    }
                    mRecyclerView.addOnScrollListener(pauseOnRecyclerScrollListener);
                    mEmptyTextView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    if (position == 0) {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mRecyclerView.addOnScrollListener(pauseOnRecyclerScrollListener);
                    }
                }
                mProgressDialog.dismiss();
                //mFloatingActionButton.setOnClickListener(onClickListener);
            }

            @Override
            public void onFailure(Throwable t) {
                if (position == 0) {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                mProgressDialog.dismiss();
                Snackbar.make(mRecyclerView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                //mFloatingActionButton.setOnClickListener(onClickListener);
            }
        });
    }

    private final ShopBlipsAdapter.OnBlipActionListener onBlipActionListener = new ShopBlipsAdapter.OnBlipActionListener() {
        @Override
        public void showProgress() {
            mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        }

        @Override
        public void dismissProgress() {
            mProgressDialog.dismiss();
        }

        @Override
        public void onBlipChosen(Blip blip) {
            Intent intent = new Intent(getActivity(), com.ordiacreativeorg.localblip.activity.BlipDetails.class);
            intent.putExtra("blip", blip);
            startActivityForResult(intent, 1020);
        }
    };

    /*
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //mFloatingActionButton.setOnClickListener(null);
            ArrayList<String> categoriesNames = new ArrayList<>();
            for (Category category : TemporaryStorageSingleton.getInstance().getCategories()){
                categoriesNames.add(category.getCategoryName());
            }
            Filters filters = Filters.createInstance(categoriesNames);
            filters.setTargetFragment(NewBlips.this, 1022);
            filters.show(getActivity().getSupportFragmentManager(), "filter");
        }
    };*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1020:
                if (resultCode == Activity.RESULT_OK && data.hasExtra("action")) {
                    if (data.getStringExtra("action").equals("reload")) {
                        Snackbar.make(mRecyclerView, data.getStringExtra("message"), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                        loadMoreBlips(replaceBlipsInList);
                    } else if (data.getStringExtra("action").equals("fix")) {
                        ((ShopBlipsAdapter) mRecyclerView.getAdapter()).fixAll(
                                data.getIntegerArrayListExtra("ids"),
                                data.getIntegerArrayListExtra("votes"),
                                data.getIntegerArrayListExtra("abbs")
                        );
                    }
                }
                break;
            /*case 1022:
                if (resultCode == Activity.RESULT_OK) {
                    Snackbar.make(mRecyclerView, R.string.filter_applied, Snackbar.LENGTH_INDEFINITE).show();
                    loadMoreBlips(replaceBlipsInList);
                }
                mFloatingActionButton.setOnClickListener(onClickListener);
                break;*/
        }
    }

    // Temporary
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle(R.string.nd_new_blips, false);
    }
}
