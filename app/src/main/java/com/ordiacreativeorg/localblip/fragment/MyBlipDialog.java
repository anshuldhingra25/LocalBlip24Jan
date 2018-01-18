package com.ordiacreativeorg.localblip.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.adapter.SelectBlipPickAdapter;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jasyc on 29/12/16.
 */

public class MyBlipDialog extends DialogFragment{
    private RecyclerView recyclerView;
    private Blip mSelectedBlip;
    public MyBlipDialog() {
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
        View rootView = inflater.inflate(R.layout.myblip_dialog, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_categories);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        List<Blip> blips = new ArrayList<>();
        final SelectBlipPickAdapter blipBookAdapter = new SelectBlipPickAdapter(new ArrayList<Blip>());
//        blipBookAdapter.setOnBlipActionListener(onBlipActionListener);
        recyclerView.setAdapter(blipBookAdapter);
        loadMoreBlips();
//
        ((SelectBlipPickAdapter) recyclerView.getAdapter()).setBlipSelectedListener(new SelectBlipPickAdapter.OnSelectionListener() {
            @Override
            public void onBlipSelected(Blip blip) {
                Intent in = new Intent();
                in.putExtra("blip", blip.copyBlip());
                Log.e("blll",""+blip.getCouponId());
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, in);
                dismiss();
            }

        });

        return rootView;
    }

    private void loadMoreBlips() {

        MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Api.getInstance().getMethods().getVendorsBlips(
                memberDetail.getEmail(),
                memberDetail.getApiKey()
        ).enqueue(new Callback<ArrayList<Blip>>() {
            @Override
            public void onResponse(Response<ArrayList<Blip>> response, Retrofit retrofit) {
                Log.e("ress",""+response.body());
                final List<Blip> blips = response.body();
                final SelectBlipPickAdapter adapter = (SelectBlipPickAdapter) recyclerView.getAdapter();
                if (blips != null && !blips.isEmpty()) {
                    adapter.addAll(blips);
                    //dataLoadResultListener.onDataReturned(adapter, blips);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                recyclerView.setVisibility(View.GONE);

                Snackbar.make(recyclerView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
            }
        });
    }

}
