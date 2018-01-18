package com.ordiacreativeorg.localblip.fragment;


import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.Location;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/9/2015
 *
 */
public class BlipCoupon extends DialogFragment {

    public static BlipCoupon newInstance(Blip blip) {
        BlipCoupon fragment = new BlipCoupon();
        Bundle args = new Bundle();
        args.putSerializable("blip", blip);
        fragment.setArguments(args);
        return fragment;
    }

    public BlipCoupon() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    private int[] getDialogSize(){
        int res[] = new int[2];
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        float dpi = getActivity().getResources().getDisplayMetrics().density;
        if (size.x > size.y){
            res[0] = Math.round(800 * dpi);
            res[1] = Math.round(480 * dpi);
        }else{
            res[0] = Math.round(480 * dpi);
            res[1] = Math.round(800 * dpi);
        }

        return res;
    }

    private View mRootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.blip_coupon, container, false);
        if (getArguments() != null) {
            Blip blip = ((Blip) getArguments().getSerializable("blip"));
            if (blip != null) {
                int blipId = blip.getCouponId();
                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                loadQRimage(blip.getBlipImage());
                Api.getInstance().getMethods().getBlipDetails(
                        blipId,
                        memberDetail.getEmail(),
                        memberDetail.getApiKey()
                ).enqueue(new Callback<Blip>() {
                    @Override
                    public void onResponse(Response<Blip> response, Retrofit retrofit) {
                        Blip blip = response.body();
                        initView(blip);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(mRootView, "Failed to get data: " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                    }
                });
            }
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getShowsDialog()){
            int size[] = getDialogSize();
            getDialog().getWindow().setLayout(size[0], size[1]);
        }
    }

    private void loadQRimage (String url){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.b_character)
                .showImageForEmptyUri(R.drawable.b_character)
                .showImageOnLoading(R.drawable.b_character)
                .build();

        ImageView imageView = (ImageView) mRootView.findViewById(R.id.blip_image);
        // Stop running tasks on this image if there are unfinished some
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        // Load the image
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    private void initView(Blip blip){
        switch (blip.getValueType()) {
            case 1:
                mRootView.findViewById(R.id.tv_prefix).setVisibility(View.VISIBLE);
                ((TextView) mRootView.findViewById(R.id.tv_value)).setText(String.valueOf(blip.getValue()));
                mRootView.findViewById(R.id.tv_suffix).setVisibility(View.GONE);
                break;
            case 2:
                mRootView.findViewById(R.id.tv_prefix).setVisibility(View.GONE);
                ((TextView) mRootView.findViewById(R.id.tv_value)).setText(String.valueOf(blip.getValue()));
                mRootView.findViewById(R.id.tv_suffix).setVisibility(View.VISIBLE);
                break;
        }

        ((TextView) mRootView.findViewById(R.id.tv_vendor)).setText(blip.getVendorName());
        ((TextView) mRootView.findViewById(R.id.tv_offer)).setText(blip.getCouponTitle());

        Location location = blip.getLocations().get(0);
        String loc = "";
        if (!location.getAddress1().isEmpty()){
            loc += location.getAddress1();

        }if (!location.getAddress2().isEmpty()) {
            loc += location.getAddress2();
        }
        if (!location.getCity().isEmpty()){
            if (!loc.isEmpty()) loc += ", ";
            loc += location.getCity();
        }
        if (!location.getState().isEmpty()){
            if (!loc.isEmpty()) loc += ", ";
            loc += location.getState();
        }
        if (!location.getPhone().isEmpty()){
            if (!loc.isEmpty()) loc += "\n";
            loc += location.getPhone();
        }

        String expirationDate = blip.getExpireDate();
        if (expirationDate != null && !expirationDate.equals("UNLIMITED") && !expirationDate.equals("0")){
            ((TextView) mRootView.findViewById(R.id.tv_expiration_date)).setText(blip.getExpireDate());
        }else{
            mRootView.findViewById(R.id.tv_expiration_date).setVisibility(View.GONE);
            mRootView.findViewById(R.id.tv_expiration_date_title).setVisibility(View.GONE);
            mRootView.findViewById(R.id.left_divider).setVisibility(View.GONE);
        }

        ((TextView) mRootView.findViewById(R.id.tv_location)).setText(loc);
        ((TextView) mRootView.findViewById(R.id.tv_description)).setText(Html.fromHtml(blip.getDescription()));
        ((TextView) mRootView.findViewById(R.id.tv_fine_print)).setText(Html.fromHtml(blip.getFinePrint()));
    }
}
