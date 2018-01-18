package com.ordiacreativeorg.localblip.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.adapter.CategoryAdapter;
import com.ordiacreativeorg.localblip.adapter.ShopBlipsAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.ListPojo;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.GPSTracker;
import com.ordiacreativeorg.localblip.util.HorizontalListView;
import com.ordiacreativeorg.localblip.util.PauseOnRecyclerScrollListener;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 */
public class ShopBlips extends Fragment {
    ListView listb;
    private int dis = 0;
    static public List<Blip> mapBlips;
    public static int mselectedItems;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private float mDPI;
    private int mItemsLoadingStep;
    private TextView mEmptyTextView;
    private ProgressDialog mProgressDialog;
    private Blip mBlip;
    private FloatingActionButton mFloatingActionButton;
    ImageView shareButton;
    private View rootView;
    private HorizontalListView listview;
    private ArrayList<String> categorylist;
    private RelativeLayout rellist;
    CategoryAdapter categoryAdapter;
    TextView txt_category;
    GPSTracker gpsTracker;
    ArrayList<ListPojo> models = new ArrayList<>();
    private int val;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDPI = getActivity().getResources().getDisplayMetrics().density;

        rootView = inflater.inflate(R.layout.itemlist_fragment, container, false);
        mProgressDialog = new ProgressDialog();
        gpsTracker = new GPSTracker(getActivity());
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false);
                        builder.setMessage("Do you want to Exit this App?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", then he is allowed to exit from application
                                getActivity().finish();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user select "No", just cancel this dialog and continue with app
                                dialog.cancel();
                            }
                        });

                        AlertDialog alert = builder.create();
                        Log.e("lkkjjkhk", "" + alert);
                        alert.show();

//                        Fragment blip = new BlipAnalytics();
//                        getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, blip);

                        return true;
                    }
                }
                return false;

            }
        });
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
        listview = (HorizontalListView) rootView.findViewById(R.id.book_my_ride_listview);
        categoryAdapter = new CategoryAdapter(getActivity(), categorylist, models);
        final TextView txt_category = (TextView) rootView.findViewById(R.id.categoryName);
//        txt_category.setTextColor(WHITE);
        listview.setAdapter(categoryAdapter);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_content);
        ShopBlipsAdapter shopBlipsAdapter = new ShopBlipsAdapter(new ArrayList<Blip>(), getActivity());
        shopBlipsAdapter.setOnBlipActionListener(onBlipActionListener);
        mRecyclerView.setAdapter(shopBlipsAdapter);
        mLayoutManager = new StaggeredGridLayoutManager(resolveSpanCount(), GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.btn_main_action);
        mFloatingActionButton.setImageResource(R.drawable.ic_action_search);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.tv_empty);
//        shareButton = (ImageView) rootView.findViewById(R.id.share);
        loadMoreBlips(addBlipsToList);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //  Toast.makeText(getActivity(), ""+mapBlips.get(0).getSkyClosest(), Toast.LENGTH_SHORT).show();
                listview.setSelection(i);

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

                //      Toast.makeText(getActivity(), categorylist.get(i), Toast.LENGTH_SHORT).show();
                TemporaryStorageSingleton.getInstance().setCategoryPosition(i + 1);
                TemporaryStorageSingleton.getInstance().setSearchKeyWord("");
                ((MainActivity) getActivity()).mSearchEditText.setText("");
                dis = 0;
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
        mFloatingActionButton.setOnClickListener(null);

        final HashMap<String, String> params = new HashMap<>();
        params.put("startfrom", String.valueOf(position));
        params.put("first", String.valueOf(position));
        params.put("count", String.valueOf(limit));
        System.out.println("startfrom----" + String.valueOf(position));
        System.out.println("first----" + String.valueOf(position));
        System.out.println("count----" + String.valueOf(limit));
        if (TemporaryStorageSingleton.getInstance().getZipCode() >= 0) {
            params.put("zipcode", String.valueOf(TemporaryStorageSingleton.getInstance().getZipCode()));
        } else if (!TemporaryStorageSingleton.getInstance().getMemberDetails().getZipCode().trim().isEmpty() &&
                TemporaryStorageSingleton.getInstance().getMemberDetails().isZipCodeAsHome().equals("1")) {
            params.put("zipcode", TemporaryStorageSingleton.getInstance().getMemberDetails().getZipCode().trim());
        }
        String[] distance = getActivity().getResources().getStringArray(R.array.miles_array);
        Log.e("GETMILES", "" + distance[TemporaryStorageSingleton.getInstance().getMiles()]);
        if (dis == 0) {
            params.put("distance", distance[TemporaryStorageSingleton.getInstance().getMiles()]);
        } else {
            params.put("distance", "1000");
        }
        dis = 0;
        short vendors = (TemporaryStorageSingleton.getInstance().isShowLocalVendors() ? (short) 2 : 1);
        params.put("online", String.valueOf(vendors));
        if (!TemporaryStorageSingleton.getInstance().getSearchKeyWord().isEmpty()) {
            params.put("keyword", TemporaryStorageSingleton.getInstance().getSearchKeyWord());
        }
        if (TemporaryStorageSingleton.getInstance().getCategoryPosition() > 0) {
            params.put("catid", String.valueOf(TemporaryStorageSingleton.getInstance().getSelectedCategoryId()));
        }
        params.put("valueZip", String.valueOf(TemporaryStorageSingleton.getInstance().getValueZip()));
        //params.put("valueZip", "0");
        MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        Api.getInstance().getMethods().searchBlips(
                memberDetail.getEmail(),
                memberDetail.getApiKey(),
                params
        ).enqueue(new Callback<ArrayList<Blip>>() {
            @Override
            public void onResponse(Response<ArrayList<Blip>> response, Retrofit retrofit) {
                final List<Blip> blips = response.body();
//                Log.e("Blipvideo",""+blips.get(0));
                final ShopBlipsAdapter adapter = (ShopBlipsAdapter) mRecyclerView.getAdapter();
                mRecyclerView.removeOnScrollListener(pauseOnRecyclerScrollListener);
                if (blips != null && !blips.isEmpty()) {
                    mapBlips = blips;
                    for (int i = 0; i < blips.size(); i++) {
                        for (int j = 0; j < blips.get(i).getLocations().size(); j++) {
                            System.out.println("Blip Zip of ----" + blips.get(i).getCouponTitle()
                                    + " is---" + blips.get(i).getLocations().get(j).getZipCode());
                        }


                    }
//                    if (!params.containsKey("zipcode") || !params.get("zipcode").equals(blips.get(0).getLocations().get(0).getZipCode())){
//                        Snackbar.make(mRecyclerView, getResources().
//                                getString(R.string.blips_from_other_place), Snackbar.LENGTH_INDEFINITE).
//                        setAction( R.string.ok, new View.OnClickListener() {
//                            @Override
//                            public void onClick( View v ) {
//
//                            }
//                        } ).show();
//                    }
                    dataLoadResultListener.onDataReturned(adapter, blips);
                    if (blips.size() == mItemsLoadingStep) {
                        mRecyclerView.addOnScrollListener(onScrollListener);
                    }
                    mRecyclerView.addOnScrollListener(pauseOnRecyclerScrollListener);
                    mEmptyTextView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (blips.get(0).getSkyClosest() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("No Blips match your city or zip code. Showing closest Blips")
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })

                                .show();
                    }
                } else {
                    String zipcodeCurrent = "";
                    String valueNew = "";

                    Double CLatitude = gpsTracker.getLatitude();
                    Double CLongitude = gpsTracker.getLongitude();
                    try {
                        zipcodeCurrent = getPostal(CLatitude, CLongitude);
                        //       Toast.makeText(getActivity(), "ZipCode is" + zipcodeCurrent, Toast.LENGTH_SHORT).show();
//                        TemporaryStorageSingleton.getInstance().setZipCode(Integer.parseInt(zipcodeCurrent));
//                        loadMoreBlips(replaceBlipsInList);
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "Unable to find current location zipcode", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();

                    }
                    if (TemporaryStorageSingleton.getInstance().getValueZip() == 1
                            && TemporaryStorageSingleton.getInstance().getSearchKeyWord().equals("")) {
                        valueNew = "No Blips match your city or zip code. Showing closest Blips";
                        val = 1;

                    } else {
                        //  valueNew = getActivity().getResources().getString(R.string.different_location);
                        valueNew = "No Blips match your search request";
                        val = 2;
                    }
                    final String finalValueNew = valueNew;
                    final String finalZipcodeCurrent = zipcodeCurrent;
                    if (val == 2) {

                        Snackbar.make(mRecyclerView, valueNew, Snackbar.LENGTH_INDEFINITE).
                                setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                    } else {

                    }
                   /* Snackbar.make(mRecyclerView, valueNew, Snackbar.LENGTH_INDEFINITE).
                            setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (val == 2) {


                                    } else {
                                        TemporaryStorageSingleton.getInstance().setValueZip(0);
                                        loadMoreBlips(replaceBlipsInList);
//                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                        builder.setMessage(finalValueNew)
//                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                        TemporaryStorageSingleton.getInstance().setValueZip(0);
//                                                        loadMoreBlips(replaceBlipsInList);
//                                                       *//* if (val == 1) {
//                                                            dis = 1;
//                                                            // TemporaryStorageSingleton.getInstance().setMiles(4);
//                                                            loadMoreBlips(replaceBlipsInList);
//                                                        } else {
//                                                            TemporaryStorageSingleton.getInstance().setZipCode(Integer.parseInt(finalZipcodeCurrent));
//                                                            loadMoreBlips(replaceBlipsInList);
//                                                        }*//*
//                                                    }
//                                                })
//                                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                        dialogInterface.dismiss();
//                                                    }
//                                                })
//                                                .show();

                                    }
                                }
                            }).show();*/
                    if (position == 0) {
                        if (val == 1) {

                            mEmptyTextView.setText("No Blips match your city or zip code.");
                            showAlert();
                        } else {
                            mEmptyTextView.setText("No Blips match your search request");
                        }

                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mRecyclerView.addOnScrollListener(pauseOnRecyclerScrollListener);
                    }
                }
                mProgressDialog.dismiss();
                mFloatingActionButton.setOnClickListener(onClickListener);
            }

            @Override
            public void onFailure(Throwable t) {
                if (position == 0) {
                    if (val == 1) {
                        mEmptyTextView.setText("No Blips match your city or zip code.");
                    } else {
                        mEmptyTextView.setText("No Blips match your search request");
                    }
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                mProgressDialog.dismiss();
                Snackbar.make(mRecyclerView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                mFloatingActionButton.setOnClickListener(onClickListener);
            }
        });
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("No Blips match your city or zip code. Showing closest Blips")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TemporaryStorageSingleton.getInstance().isShowLocalVendors()) {
                            dis = 1;
                            TemporaryStorageSingleton.getInstance().setMiles(4);
                            loadMoreBlips(replaceBlipsInList);
                        } else {
                            TemporaryStorageSingleton.getInstance().setCategoryPosition(0);
                            loadMoreBlips(replaceBlipsInList);
                            listview = (HorizontalListView) rootView.findViewById(R.id.book_my_ride_listview);

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
                            categoryAdapter = new CategoryAdapter(getActivity(), categorylist, models);
                            listview.setAdapter(categoryAdapter);
                        }
                    }
                })

                .show();
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
            Log.e("BLIPSELECT", blip.getCouponTitle());
            Intent intent = new Intent(getActivity(), com.ordiacreativeorg.localblip.activity.BlipDetails.class);
            intent.putExtra("blip", blip);
            startActivityForResult(intent, 1020);
        }
    };

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFloatingActionButton.setOnClickListener(null);
            ArrayList<String> categoriesNames = new ArrayList<>();
            for (Category category : TemporaryStorageSingleton.getInstance().getCategories()) {
                categoriesNames.add(category.getCategoryName());
            }
            Filters filters = Filters.createInstance(categoriesNames);
            filters.setTargetFragment(ShopBlips.this, 1022);
            filters.show(getActivity().getSupportFragmentManager(), "filter");
        }
    };

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
                        dis = 0;
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
            case 1022:
                if (resultCode == Activity.RESULT_OK) {
//                    Snackbar.make(mRecyclerView, R.string.filter_applied, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                        }
//                    }).show();
                    dis = 0;
                    ((MainActivity) getActivity()).mSearchEditText.setText(Filters.searchText);
                    loadMoreBlips(replaceBlipsInList);
                }
                mFloatingActionButton.setOnClickListener(onClickListener);
                break;
        }
    }

    // Temporary
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle(R.string.nd_shop_blips, false);
    }

    private String getPostal(Double cLatitude, Double cLongitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        addresses = geocoder.getFromLocation(cLatitude, cLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        Log.d("postalcode", postalCode);
        return postalCode;
    }
}