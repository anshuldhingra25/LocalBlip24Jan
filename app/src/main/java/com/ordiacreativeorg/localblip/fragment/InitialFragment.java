package com.ordiacreativeorg.localblip.fragment;

import android.support.v4.app.Fragment;

import com.ordiacreativeorg.localblip.activity.LogInActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.MarketArea;
import com.ordiacreativeorg.localblip.model.Vendor;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by dmytrobohachevskyy on 9/25/15.
 * Rewritten by Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 *
 * First page that show when app is running
 * Allow user sign in or sign up
 */
public class InitialFragment extends Fragment {

    public InitialFragment() {
    }

    protected void loadCategories(final String allCategoriesName, final List<String> states) {
        Api.getInstance().getMethods().getCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Response<ArrayList<Category>> response, Retrofit retrofit) {
                ArrayList<Category> categories = new ArrayList<>();
                Category allCategories = new Category();
                //allCategories.setCategoryName(getString(R.string.all_categories));
                allCategories.setCategoryName(allCategoriesName);
                allCategories.setCategoryId(0);
                categories.add(allCategories);
                categories.addAll(response.body());
                TemporaryStorageSingleton.getInstance().setCategories(categories);
                loadMarketAreas(states);
            }

            @Override
            public void onFailure(Throwable t) {
                TemporaryStorageSingleton.getInstance().setCategories(new ArrayList<Category>());
                if (getActivity() != null) {
                    ((LogInActivity) getActivity()).setLoadingData(false, t.getMessage());
                }
            }
        });
    }

    private void loadMarketAreas(final List<String> states){
        Api.getInstance().getMethods().getMarketAreas(
                TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail(),
                TemporaryStorageSingleton.getInstance().getMemberDetails().getApiKey(),
                new HashMap<String, String>()
        ).enqueue(new Callback<ArrayList<MarketArea>>() {
            @Override
            public void onResponse(Response<ArrayList<MarketArea>> response, Retrofit retrofit) {
                TemporaryStorageSingleton.getInstance().setMarketAreas(response.body());
                //TemporaryStorageSingleton.getInstance().parseMarketAreas(Arrays.asList(getResources().getStringArray(R.array.states_list)));
                TemporaryStorageSingleton.getInstance().parseMarketAreas(states);
                loadVendors();
            }

            @Override
            public void onFailure(Throwable t) {
                TemporaryStorageSingleton.getInstance().setCategories(new ArrayList<Category>());
                if (getActivity() != null) {
                    ((LogInActivity) getActivity()).setLoadingData(false, t.getMessage());
                }
            }
        });
    }

    private void loadVendors(){
        Api.getInstance().getMethods().getVendorsList(
                TemporaryStorageSingleton.getInstance().getMemberDetails().getEmail(),
                TemporaryStorageSingleton.getInstance().getMemberDetails().getApiKey()
        ).enqueue(new Callback<ArrayList<Vendor>>() {
            @Override
            public void onResponse(Response<ArrayList<Vendor>> response, Retrofit retrofit) {
                TemporaryStorageSingleton.getInstance().setVendors(response.body());
                loadFinished();
            }

            @Override
            public void onFailure(Throwable t) {
                TemporaryStorageSingleton.getInstance().setCategories(new ArrayList<Category>());
                if (getActivity() != null) {
                    ((LogInActivity) getActivity()).setLoadingData(false, t.getMessage());
                }
            }
        });
    }

    protected void loadFinished(){

    }
}
