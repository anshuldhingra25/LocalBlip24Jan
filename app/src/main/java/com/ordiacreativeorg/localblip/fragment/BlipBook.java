package com.ordiacreativeorg.localblip.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.adapter.BlipBookAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.SimpleResponse;
import com.ordiacreativeorg.localblip.util.PauseOnRecyclerScrollListener;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 */
public class BlipBook extends Fragment {

    private RecyclerView mRecyclerView;
    private float mDPI;
    private TextView mEmptyTextView;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDPI = getActivity().getResources().getDisplayMetrics().density;
        View rootView = inflater.inflate(R.layout.frag_billbook, container, false);

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
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(resolveSpanCount(), GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        BlipBookAdapter blipBookAdapter = new BlipBookAdapter(new ArrayList<Blip>());
        blipBookAdapter.setOnBlipActionListener(onBlipActionListener);
        mRecyclerView.setAdapter(blipBookAdapter);
        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.btn_main_action);
        floatingActionButton.setImageResource(R.drawable.ic_action_shop);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_fragment_container, new ShopBlips()).commit();

            }
        });
        mEmptyTextView = (TextView) rootView.findViewById(R.id.tv_empty);
        mEmptyTextView.setText(R.string.no_blips_added);
        loadMoreBlips(addBlipsToList);
        return rootView;
    }

    private int screenSize;

    private int resolveSpanCount() {
        int spanCount;
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        screenSize = Math.round(Math.min(height, width) / mDPI);

        // Calculating number of columns, optimal for the screen size - some magic =)
        spanCount = (int) Math.floor((width / mDPI) / 300);
        //Log.d("LOG_TAG", "width=" + width + "  spanCount=" + spanCount);
        if (spanCount < 1) spanCount = 1;

        return spanCount;
    }

    // To pause all ImageLoader working threads while scrolling
    private final PauseOnRecyclerScrollListener pauseOnRecyclerScrollListener = new PauseOnRecyclerScrollListener(ImageLoader.getInstance(), false, true);

    private final DataLoadResultListener addBlipsToList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(BlipBookAdapter adapter, List<Blip> blips) {
            adapter.addAll(blips);
        }
    };

    DataLoadResultListener replaceBlipsInList = new DataLoadResultListener() {
        @Override
        public void onDataReturned(BlipBookAdapter adapter, List<Blip> blips) {
            adapter.replaceAll(blips);
        }
    };

    interface DataLoadResultListener {
        void onDataReturned(BlipBookAdapter adapter, List<Blip> blips);
    }

    private void loadMoreBlips(final DataLoadResultListener dataLoadResultListener) {
        //mProgressBar.setVisibility(View.VISIBLE);
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Api.getInstance().getMethods().getBlipBookItems(
                memberDetail.getEmail(),
                memberDetail.getApiKey()
        ).enqueue(new Callback<ArrayList<Blip>>() {
            @Override
            public void onResponse(Response<ArrayList<Blip>> response, Retrofit retrofit) {
                final List<Blip> blips = response.body();
                final BlipBookAdapter adapter = (BlipBookAdapter) mRecyclerView.getAdapter();
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
                Snackbar.make(mRecyclerView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        });
    }

    private final BlipBookAdapter.OnBlipActionListener onBlipActionListener = new BlipBookAdapter.OnBlipActionListener() {
        @Override
        public void onBlipChosen(Blip blip) {
            if (screenSize > 480) {
                BlipCoupon.newInstance(blip).show(getActivity().getSupportFragmentManager(), "coupon");
            } else {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_fragment_container, BlipCoupon.newInstance(blip));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

        @Override
        public void onBlipRemoved(final Blip blip) {
            final MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
            mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
            final long millis = System.currentTimeMillis();
            Api.getInstance().getMethods().addRemoveBlipsBook(
                    memberDetail.getEmail(),
                    memberDetail.getApiKey(),
                    blip.getCouponId()
            ).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                    mProgressDialog.dismiss();
                    ((BlipBookAdapter) mRecyclerView.getAdapter()).removeBlip(blip);
                    if (mRecyclerView.getAdapter().getItemCount() < 1) {
                        mRecyclerView.removeOnScrollListener(pauseOnRecyclerScrollListener);
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    Snackbar.make(mRecyclerView, R.string.blip_removed, Snackbar.LENGTH_INDEFINITE).setDuration(3000)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
                                    final long millis = System.currentTimeMillis();
                                    Api.getInstance().getMethods().addRemoveBlipsBook(
                                            memberDetail.getEmail(),
                                            memberDetail.getApiKey(),
                                            blip.getCouponId()
                                    ).enqueue(new Callback<SimpleResponse>() {
                                        @Override
                                        public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                                            if (mRecyclerView.getAdapter().getItemCount() < 1) {
                                                mRecyclerView.addOnScrollListener(pauseOnRecyclerScrollListener);
                                                mEmptyTextView.setVisibility(View.GONE);
                                                mRecyclerView.setVisibility(View.VISIBLE);
                                            }
                                            ((BlipBookAdapter) mRecyclerView.getAdapter()).restore(blip);
                                            mProgressDialog.dismiss();
                                            Snackbar.make(mRecyclerView, R.string.blip_restored, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                }
                                            }).show();
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            mProgressDialog.dismiss();
                                            Snackbar.make(mRecyclerView, getResources().getString(R.string.restore_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                }
                                            }).show();
                                        }
                                    });

                                }
                            })
                            .show();
                }

                @Override
                public void onFailure(Throwable t) {
                    mProgressDialog.dismiss();
                    Snackbar.make(mRecyclerView, getResources().getString(R.string.remove_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
            });
        }
    };

    // Temporary
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle(R.string.nd_blipbook, false);
    }
}
