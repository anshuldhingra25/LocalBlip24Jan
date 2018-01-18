package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.VideoDisplay;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.Location;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.SimpleResponse;
import com.ordiacreativeorg.localblip.model.SocialMedia;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/9/2015
 */
public class BlipDetails extends Fragment {

    public static BlipDetails newInstance(Blip blip, boolean reset) {
        BlipDetails fragment = new BlipDetails();
        Bundle args = new Bundle();
        args.putSerializable("blip", blip);
        args.putBoolean("reset", reset);
        fragment.setArguments(args);
        return fragment;
    }

    public BlipDetails() {
        // Required empty public constructor
    }

    private View mRootView;
    private ImageButton mFlagButton;
    private ImageButton mBlockButton, shareButton;
    private ImageButton mRateDownButton;
    private ImageButton mRateUpButton;
    private Button mShowLocationsButton;
    private Button mShowBlipsPicksButton;
    private Button mPurchaseButton;
    private LinearLayout mLocationsLayout;
    private LinearLayout mBlipsPicksLayout;
    private Blip mBlip;
    private ProgressDialog mProgressDialog;
    private TextView owner_type;
    private WebView webView;
    private VideoView videoview;
    MediaController mediaControls;
    NestedScrollView scroll_view;
    RelativeLayout blip_videoview_layout;
    ProgressBar progressbar;
    ImageView video_icon;
//    private VideoEnabledWebChromeClient webChromeClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.blip_details, container, false);

        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFlagButton = (ImageButton) mRootView.findViewById(R.id.btn_flag);
        mBlockButton = (ImageButton) mRootView.findViewById(R.id.btn_block);
        mRateDownButton = (ImageButton) mRootView.findViewById(R.id.btn_rate_down);
        mRateUpButton = (ImageButton) mRootView.findViewById(R.id.btn_rate_up);
        shareButton = (ImageButton) mRootView.findViewById(R.id.btn_share);
        mShowLocationsButton = (Button) mRootView.findViewById(R.id.btn_locations);
        mShowBlipsPicksButton = (Button) mRootView.findViewById(R.id.btn_blipspicks);
        mPurchaseButton = (Button) mRootView.findViewById(R.id.btn_purchase);
        mLocationsLayout = (LinearLayout) mRootView.findViewById(R.id.locations_layout);
        mBlipsPicksLayout = (LinearLayout) mRootView.findViewById(R.id.blipspicks_layout);
        blip_videoview_layout = (RelativeLayout) mRootView.findViewById(R.id.blip_videoview_layout);
        videoview = (VideoView) mRootView.findViewById(R.id.blip_videoview);
        owner_type = (TextView) mRootView.findViewById(R.id.owner_type);
        progressbar = (ProgressBar) mRootView.findViewById(R.id.progressbar);
        scroll_view = (NestedScrollView) mRootView.findViewById(R.id.scroll_view);
        video_icon = (ImageView) mRootView.findViewById(R.id.video_icon);
        // blip_video = (RelativeLayout) mRootView.findViewById(R.id.blip_video);
        webView = (WebView) mRootView.findViewById(R.id.webView1);
        mProgressDialog = new ProgressDialog();

        if (getArguments() != null) {
            mBlip = ((Blip) getArguments().getSerializable("blip"));
            if (mBlip != null) {
                loadBlipImage(mBlip.getBlipImage());
                final int myVote = mBlip.getMyVote();
                final boolean addedToBB = mBlip.getAddedtobb();
//                mBlip.getFranchise_owner();
//                Log.e("wtywqq",""+mBlip.getFranchise_owner());
                if (!mBlip.getFranchise_owner().equals("0")) {

                    owner_type.setVisibility(View.VISIBLE);
                    owner_type.setText("FRANCHISE OWNER");
                } else {
                    owner_type.setVisibility(View.GONE);
                }

                //fillDetails(mBlip);
                if (mBlip.getBlipvideo() != null && !mBlip.getBlipvideo().isEmpty()) {
                    String videourl = mBlip.getBlipvideo();
                    if (mBlip.getVideotype() == 1) {
                        videourl = "https:" + videourl;
                        progressbar.setVisibility(View.VISIBLE);

                    }
                    if (mBlip.getVideotype() == 2) {
                        //videourl = "https://www.youtube.com/watch?v=VubcWIQTbdg";
                        videourl = videourl.split("v=")[1];
                        videourl = "https://www.youtube.com/embed/" + videourl;
                        if (videourl.contains("&")) {
                            videourl = videourl.split("&")[0];
                        }
                    }
                    Log.e("VIDEOURL", videourl);
                    ImageView imageView = (ImageView) mRootView.findViewById(R.id.blip_image);
                    imageView.setVisibility(View.GONE);
                    // getActivity().findViewById(R.id.blip_image).setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    if (mBlip.getVideotype() == 3) {
                        imageView.setVisibility(View.VISIBLE);
                        // getActivity().findViewById(R.id.blip_image).setVisibility(View.GONE);
                        blip_videoview_layout.setVisibility(View.GONE);
                        videoview.setVisibility(View.GONE);
                        webView.setVisibility(View.GONE);
                    } else if (mBlip.getVideotype() == 2) {
                        webView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);
                        blip_videoview_layout.setVisibility(View.GONE);
                        videoview.setVisibility(View.GONE);
                        webView.getSettings().setLoadsImagesAutomatically(true);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                        webView.getSettings().setUseWideViewPort(true);
                        webView.getSettings().setDomStorageEnabled(true);
                        webView.getSettings().setSupportMultipleWindows(true);
                        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        holder.blip_video.getSettings().setBuiltInZoomControls(true);
                        webView.getSettings().setLoadsImagesAutomatically(true);
                        webView.getSettings().setPluginState(WebSettings.PluginState.ON);


                        // holder.blip_video.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                        // Call private class InsideWebViewClient

                        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site

                        webView.loadUrl(videourl);

                        webView.setWebViewClient(new MyBrowser());
                    } else if (mBlip.getVideotype() == 1) {

                        imageView.setVisibility(View.GONE);
                        blip_videoview_layout.setVisibility(View.VISIBLE);
                        videoview.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);
                        Uri uri = Uri.parse(videourl);

                        progressbar.setVisibility(View.VISIBLE);


//                        if (mediaControls == null) {
//                            // create an object of media controller class
//                            mediaControls = new MediaController(getActivity());
//                            mediaControls.setAnchorView(videoview);
//
//                        }

                        // set the media controller for video view
                      //  videoview.setMediaController(mediaControls);
                        // set the uri for the video view
                        videoview.setVideoURI(uri);
                        videoview.seekTo(1);
                        //  mediaControls.setVisibility(View.VISIBLE);

                        // start a video
                        //simpleVideoView.start();

                        // implement on completion listener on video view
                        final String finalVideourl = videourl;
                        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                progressbar.setVisibility(View.GONE);
                                video_icon.setVisibility(View.VISIBLE);
                                videoview.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {

                                        Intent intent = new Intent(getActivity(), VideoDisplay.class);
                                        intent.putExtra("blip", finalVideourl);
                                        startActivity(intent);
//                                        if (motionEvent.getAction()==MotionEvent.ACTION_UP){
//                                            Intent intent = new Intent(getActivity(), VideoDisplay.class);
//                                            intent.putExtra("blip", finalVideourl);
//                                            startActivity(intent);
//                                            Log.e("TAG","Up");
//                                            return true;
//                                        }
                                        return false;
                                    }
                                });
                                //mediaPlayer.start()
                                //videoview.start();
                                //  videoview.pause();
//                                mediaControls.show(50000000);
//                                CollapsingToolbarLayout.LayoutParams lp = new CollapsingToolbarLayout.LayoutParams((int)(width),
//                                        LinearLayout.LayoutParams.WRAP_CONTENT); // or set height to any fixed value you want
//
//                                blip_videoview_layout.setLayoutParams(lp);
// OR

//                                FrameLayout f = (FrameLayout) videoview.getParent();
//                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                lp.addRule(RelativeLayout.ALIGN_BOTTOM, videoview.getId());
//
//                                ((LinearLayout) f.getParent()).removeView(f);
//                                ((RelativeLayout) videoview.getParent()).addView(f, lp);
//
//                                mediaControls.setAnchorView(videoview);


                            }
                        });
                    }

                   /* blip_video.setWebViewClient(new MyBrowser());
                    blip_video.getSettings().setLoadsImagesAutomatically(true);
                    blip_video.getSettings().setJavaScriptEnabled(true);
                    // holder.blip_video.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    String videourl = mBlip.getBlipvideo();
                    if (mBlip.getVideotype() == 1) {
                        videourl = "http:" + videourl;
                    }
                    if(mBlip.getVideotype()==2)
                    {
                        //videourl = "https://www.youtube.com/watch?v=VubcWIQTbdg";
                        videourl=videourl.split("v=")[1];
                        videourl="https://www.youtube.com/embed/"+videourl;
                    }
                    Log.e("VIDEOURL",videourl);
                    blip_video.loadUrl(videourl);*/


//                    holder.blip_video.setWebViewClient(new MyBrowser());
////        holder.blip_video.getSettings().setLoadsImagesAutomatically(true);
////        holder.blip_video.getSettings().setJavaScriptEnabled(true);
////       // holder.blip_video.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
////
////        holder.blip_video.loadUrl
////                (blip.getVideotype()==1?"http:"+blip.getBlipvideo():blip.getBlipvideo());


//        holder.blip_video.getSettings().setLoadsImagesAutomatically(true);
//        holder.blip_video.getSettings().setJavaScriptEnabled(true);
//       // holder.blip_video.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//


//                        blip_video.loadUrl
//                                (mBlip.getVideotype() == 1 ? "http:" + mBlip.getBlipvideo() : mBlip.getBlipvideo());
                }

                int blipId = mBlip.getCouponId();
                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
                Api.getInstance().getMethods().getBlipDetails(
                        blipId,
                        memberDetail.getEmail(),
                        memberDetail.getApiKey()
                ).enqueue(new Callback<Blip>() {
                    @Override
                    public void onResponse(Response<Blip> response, Retrofit retrofit) {
                        mProgressDialog.dismiss();
                        mBlip = response.body();
                        mBlip.setMyVote(myVote);
                        mBlip.setAddedtobb(addedToBB);
                        initView(mBlip);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        mProgressDialog.dismiss();
                        Snackbar.make(mRootView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
                    }
                });
            }
        }

        if (mBlip.getVideotype() == 1) {
            scroll_view.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {
                 //   mediaControls.hide();
                }
            });
        }

        mFlagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlagButton.setClickable(false);
                mFlagButton.setImageResource(R.drawable.ic_action_flagged);
                ConfirmationDialog.newInstance(null,
                        getResources().getString(R.string.flag_as_inappropriate),
                        getResources().getString(R.string.flag), null, new ConfirmationDialog.OnActionConfirmationListener() {
                            @Override
                            public void onConfirmed() {
                                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                                mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
                                Api.getInstance().getMethods().rateBlip(
                                        memberDetail.getEmail(),
                                        memberDetail.getApiKey(),
                                        mBlip.getCouponId(),
                                        1
                                ).enqueue(new Callback<SimpleResponse>() {
                                    @Override
                                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                                        //if(!response.body().getResponse().contains("remove")) {
                                        Intent intent = new Intent();
                                        intent.putExtra("action", "reload");
                                        intent.putExtra("message", getResources().getString(R.string.flagged));
                                        getActivity().setResult(Activity.RESULT_OK, intent);
                                        getActivity().finish();
                                        //}else {
                                        //Snackbar.make(mRootView, "The blip is unflagged as inappropriate", Snackbar.LENGTH_INDEFINITE).show();
                                        //}
                                        mProgressDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Snackbar.make(mRootView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
                                        mFlagButton.setClickable(true);
                                        mProgressDialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onCanceled() {
                                mFlagButton.setImageResource(R.drawable.ic_action_flag);
                                mFlagButton.setClickable(true);
                            }
                        }).show(getActivity().getSupportFragmentManager(), null);
            }
        });
        mBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlockButton.setClickable(false);
                mBlockButton.setImageResource(R.drawable.ic_action_blocked);
                ConfirmationDialog.newInstance(null,
                        getResources().getString(R.string.block_this_vendor),
                        getResources().getString(R.string.block), null, new ConfirmationDialog.OnActionConfirmationListener() {
                            @Override
                            public void onConfirmed() {
                                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                                mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
                                Api.getInstance().getMethods().rateBlip(
                                        memberDetail.getEmail(),
                                        memberDetail.getApiKey(),
                                        mBlip.getCouponId(),
                                        2
                                ).enqueue(new Callback<SimpleResponse>() {
                                    @Override
                                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                                        //if(!response.body().getResponse().contains("remove")) {
                                        Intent intent = new Intent();
                                        intent.putExtra("action", "reload");
                                        intent.putExtra("message", getResources().getString(R.string.blips_from_vendor_blocked));
                                        getActivity().setResult(Activity.RESULT_OK, intent);
                                        getActivity().finish();
                                        //}else {
                                        //Snackbar.make(mRootView, "All blips from this vendor are unblocked", Snackbar.LENGTH_INDEFINITE).show();
                                        //}
                                        mProgressDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Snackbar.make(mRootView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
                                        mBlockButton.setClickable(true);
                                        mProgressDialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onCanceled() {
                                mBlockButton.setImageResource(R.drawable.ic_action_block);
                                mBlockButton.setClickable(true);
                            }
                        }).show(getActivity().getSupportFragmentManager(), null);
            }
        });
        mRateDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRateDownButton.setClickable(false);
                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
                Api.getInstance().getMethods().rateBlip(
                        memberDetail.getEmail(),
                        memberDetail.getApiKey(),
                        mBlip.getCouponId(),
                        4
                ).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                        if (response.body().getResponse().equals("remove voted down")) {
                            mBlip.setMyVote(0);
                        } else if (response.body().getResponse().equals("voted down")) {
                            mBlip.setMyVote(2);
                        }
                        switch (mBlip.getMyVote()) {
                            case 2:
                                Snackbar.make(mRootView, R.string.rated_down, Snackbar.LENGTH_INDEFINITE).show();
                                mRateDownButton.setImageResource(R.drawable.ic_action_rated_down);
                                mRateUpButton.setImageResource(R.drawable.ic_action_rate_up);
                                mBlip.setMyVote(2);
                                break;
                            default:
                                Snackbar.make(mRootView, R.string.unrated, Snackbar.LENGTH_INDEFINITE).show();
                                mRateDownButton.setImageResource(R.drawable.ic_action_rate_down);
                                mBlip.setMyVote(0);
                                break;
                        }
                        mRateDownButton.setClickable(true);
                        mProgressDialog.dismiss();
                        ((com.ordiacreativeorg.localblip.activity.BlipDetails) getActivity()).blipChanged(mBlip);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(mRootView, getResources().getString(R.string.operation_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
                        mRateDownButton.setClickable(true);
                        mProgressDialog.dismiss();
                    }
                });
            }
        });

        mRateUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRateUpButton.setClickable(false);
                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
                Api.getInstance().getMethods().rateBlip(
                        memberDetail.getEmail(),
                        memberDetail.getApiKey(),
                        mBlip.getCouponId(),
                        3
                ).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                        if (response.body().getResponse().equals("remove voted up")) {
                            mBlip.setMyVote(0);
                        } else if (response.body().getResponse().equals("voted up")) {
                            mBlip.setMyVote(1);
                        }
                        switch (mBlip.getMyVote()) {
                            case 1:
                                Snackbar.make(mRootView, R.string.rated_up, Snackbar.LENGTH_INDEFINITE).show();
                                mRateUpButton.setImageResource(R.drawable.ic_action_rated_up);
                                mRateDownButton.setImageResource(R.drawable.ic_action_rate_down);
                                mBlip.setMyVote(1);
                                break;
                            default:
                                Snackbar.make(mRootView, R.string.unrated, Snackbar.LENGTH_INDEFINITE).show();
                                mRateUpButton.setImageResource(R.drawable.ic_action_rate_up);
                                mBlip.setMyVote(0);
                                break;
                        }
                        mRateUpButton.setClickable(true);
                        mProgressDialog.dismiss();
                        ((com.ordiacreativeorg.localblip.activity.BlipDetails) getActivity()).blipChanged(mBlip);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(mRootView, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
                        mRateUpButton.setClickable(true);
                        mProgressDialog.dismiss();
                    }
                });
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSocialMediaMenu(v);
            }
        });

        Button follorMerchant = (Button) mRootView.findViewById(R.id.follow_merchant);
        follorMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowDialog followDialog = FollowDialog.newInstance(mBlip);
                followDialog.show(getActivity().getSupportFragmentManager(), null);
            }
        });

        mPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPurchaseButton.setClickable(false);
                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
                Api.getInstance().getMethods().addRemoveBlipsBook(
                        memberDetail.getEmail(),
                        memberDetail.getApiKey(),
                        mBlip.getCouponId()
                ).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                        if (response.body().getResponse().equals("added")) {
                            mBlip.setAddedtobb(true);
                        } else {
                            mBlip.setAddedtobb(false);
                        }
                        if (mBlip.getAddedtobb()) {
                            Snackbar.make(mRootView, R.string.blip_added_to_bb, Snackbar.LENGTH_INDEFINITE).show();
                            mPurchaseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_remove, 0, 0, 0);
                            mPurchaseButton.setText(R.string.remove_from_blipbook);
                            mBlip.setAddedtobb(true);
                        } else {
                            Snackbar.make(mRootView, R.string.blip_removed_from_bb, Snackbar.LENGTH_INDEFINITE).show();
                            mPurchaseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_add_accent, 0, 0, 0);
                            mPurchaseButton.setText(R.string.add_to_blipbook);
                            mBlip.setAddedtobb(false);
                        }
                        mPurchaseButton.setClickable(true);
                        mProgressDialog.dismiss();
                        ((com.ordiacreativeorg.localblip.activity.BlipDetails) getActivity()).blipChanged(mBlip);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(mRootView, getResources().getString(R.string.operation_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
                        mPurchaseButton.setClickable(true);
                        mProgressDialog.dismiss();
                    }
                });
            }
        });

        if (mShowLocationsButton.isClickable()) {
            mShowLocationsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLocationsLayout.getVisibility() == View.GONE) {
                        mLocationsLayout.setVisibility(View.VISIBLE);
                        mShowLocationsButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_collapse, 0, 0, 0);

                    } else {
                        mLocationsLayout.setVisibility(View.GONE);
                        mShowLocationsButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_expand, 0, 0, 0);
                    }
                }
            });
        }

        if (mShowBlipsPicksButton.isClickable()) {
            mShowBlipsPicksButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBlipsPicksLayout.getVisibility() == View.GONE) {
                        mBlipsPicksLayout.setVisibility(View.VISIBLE);
                        mShowBlipsPicksButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_collapse, 0, 0, 0);
                    } else {
                        mBlipsPicksLayout.setVisibility(View.GONE);
                        mShowBlipsPicksButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_expand, 0, 0, 0);
                    }
                }
            });
        }

        return mRootView;
    }

    private Uri createImageBitmapUrl() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        File file = new File(myDir, "photo.jpg");
        if (file.exists()) file.delete();
        try {

            FileOutputStream out = new FileOutputStream(file);
            ImageView imageView = (ImageView) mRootView.findViewById(R.id.blip_image);
            Bitmap photoBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Log.d("PHOTO_PATH", String.format("%s %d", file.getPath(), file.getTotalSpace()));
        return Uri.fromFile(file);
    }

    private void loadBlipImage(String url) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.no_image)
                .showImageForEmptyUri(R.drawable.no_image)
                .showImageOnLoading(R.drawable.no_image)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageView imageView = (ImageView) mRootView.findViewById(R.id.blip_image);
        // Stop running tasks on this image if there are unfinished some
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        // Load the image
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    private void initView(final Blip blip) {
        fillDetails(blip);
        fillLocations(blip, blip.getLocations(), mLocationsLayout, blip.isOnline());
        fillBlips(blip.getBlipsPicks(), mBlipsPicksLayout);
    }

    private void fillDetails(Blip blip) {
        final int myVote = blip.getMyVote();
        ((TextView) mRootView.findViewById(R.id.tv_description)).setText(Html.fromHtml(blip.getDescription()));
        ((TextView) mRootView.findViewById(R.id.tv_fine_print)).setText(Html.fromHtml(blip.getFinePrint()));
        switch (myVote) {
            case 1:
                mRateUpButton.setImageResource(R.drawable.ic_action_rated_up);
                break;
            case 2:
                mRateDownButton.setImageResource(R.drawable.ic_action_rated_down);
                break;
        }
        switch (blip.getValueType()) {
            case 1:
                mRootView.findViewById(R.id.tv_prefix).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.tv_suffix).setVisibility(View.GONE);
                break;
            case 2:
                mRootView.findViewById(R.id.tv_prefix).setVisibility(View.GONE);
                mRootView.findViewById(R.id.tv_suffix).setVisibility(View.VISIBLE);
                break;
        }
        ((TextView) mRootView.findViewById(R.id.tv_value)).setText(String.valueOf(blip.getValue()));

        ((TextView) mRootView.findViewById(R.id.tv_vendor)).setText(mBlip.getVendorName());
        ((TextView) mRootView.findViewById(R.id.tv_offer)).setText(mBlip.getCouponTitle());

        String expirationDate = mBlip.getExpireDate();
        String remainingBlips = mBlip.getRemaining();
        if (expirationDate != null && !expirationDate.equals("UNLIMITED") && !expirationDate.equals("0")) {
            ((TextView) mRootView.findViewById(R.id.tv_expiration_date)).setText(expirationDate);
        } else {
            ((TextView) mRootView.findViewById(R.id.tv_expiration_date)).setText(R.string.never_expires);
        }
        if (remainingBlips != null) {
            ((TextView) mRootView.findViewById(R.id.tv_coupons_left)).setText(remainingBlips);
        }

        if (mBlip.getAddedtobb()) {
            mPurchaseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_remove, 0, 0, 0);
            mPurchaseButton.setText(R.string.remove_from_blipbook);
        }
    }

    private void fillLocations(Blip blip, List<Location> locations, final LinearLayout linearLayout, boolean online) {
        int count = 0;
        for (final Location location : locations) {

            View view = getActivity().getLayoutInflater().inflate(R.layout.location_description, linearLayout, false);

            if (count == 0) {

                if (!blip.getBusinessdesc().trim().isEmpty()) {
                    ((TextView) view).setText(blip.getBusinessdesc());
                    linearLayout.addView(view);
                } else if (linearLayout.getChildCount() > 0) {
                    ((TextView) view).setText(" ");
                    linearLayout.addView(view);
                }
            }
            String loc = "";
            if (!location.getAddress1().isEmpty()) {
                loc += location.getAddress1() + ", ";

            }
            if (!location.getAddress2().isEmpty()) {
                loc += location.getAddress2();
            }
            if (!location.getCity().isEmpty()) {
                if (!loc.isEmpty()) loc += ", ";
                loc += location.getCity();
            }
            if (!location.getState().isEmpty()) {
                if (!loc.isEmpty()) loc += ", ";
                loc += location.getState();
            }
            if (!location.getZipCode().isEmpty()) {
                if (!loc.isEmpty()) loc += " ";
                loc += location.getZipCode();
            }
            if (!loc.isEmpty() && !online) {
                view = getActivity().getLayoutInflater().inflate(R.layout.location_item, linearLayout, false);
                ((TextView) view.findViewById(R.id.tv_text)).setText(loc);
                ((ImageButton) view.findViewById(R.id.btn_action)).setImageResource(R.drawable.ic_action_show_on_map);

                final String loc1 = loc;
                view.findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("geo:0,0?q=" + loc1));
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Snackbar.make(linearLayout, R.string.install_map_application, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                        }
                    }
                });
                linearLayout.addView(view);
            }
            if (location.getWebsite() != null && !location.getWebsite().isEmpty()) {
                view = getActivity().getLayoutInflater().inflate(R.layout.location_item, linearLayout, false);
                ((TextView) view.findViewById(R.id.tv_text)).setText(location.getWebsite());
                ((ImageButton) view.findViewById(R.id.btn_action)).setImageResource(R.drawable.ic_action_link);
                view.findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(location.getWebsite()));
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Snackbar.make(linearLayout, R.string.install_web_browser, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                        }
                    }
                });
                linearLayout.addView(view);
            }
            if (!location.getPhone().isEmpty()) {
                view = getActivity().getLayoutInflater().inflate(R.layout.location_item, linearLayout, false);
                ((TextView) view.findViewById(R.id.tv_text)).setText(location.getPhone());
                ((ImageButton) view.findViewById(R.id.btn_action)).setImageResource(R.drawable.ic_action_call);
                view.findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + location.getPhone()));
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Snackbar.make(linearLayout, R.string.install_caller, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                        }
                    }
                });
                linearLayout.addView(view);
            }
            count++;
        }
    }

    private void fillBlips(List<Blip> blips, LinearLayout linearLayout) {
        for (final Blip blip : blips) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.blip_card_small, linearLayout, false);
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

            // Stop running tasks on this image if there are unfinished some
            ImageLoader.getInstance().cancelDisplayTask((ImageView) view.findViewById(R.id.blip_image));
            // Load the image
            ImageLoader.getInstance().displayImage(blip.getBlipImageSmall(), (ImageView) view.findViewById(R.id.blip_image), options);

            if (!blip.getCategories().isEmpty()) {
                String category = blip.getCategories().get(0).getCategoryName();
                if (blip.getCategories().get(0).getSubCategories() != null && !blip.getCategories().get(0).getSubCategories().isEmpty()) {
                    category += ", " + blip.getCategories().get(0).getSubCategories().get(0).getSubCategoryName();
                }
                view.findViewById(R.id.tv_category).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tv_category)).setText(category);
            } else {
                view.findViewById(R.id.tv_category).setVisibility(View.GONE);
            }

            switch (blip.getValueType()) {
                case 1:
                    ((TextView) view.findViewById(R.id.tv_prefix)).setText("save  $");
                    ((TextView) view.findViewById(R.id.tv_value)).setText(String.valueOf(blip.getValue()));
                    view.findViewById(R.id.tv_suffix).setVisibility(View.GONE);
                    break;
                case 2:
                    ((TextView) view.findViewById(R.id.tv_prefix)).setText("save  ");
                    ((TextView) view.findViewById(R.id.tv_value)).setText(String.valueOf(blip.getValue()));
                    view.findViewById(R.id.tv_suffix).setVisibility(View.VISIBLE);
                    break;
            }

            ((TextView) view.findViewById(R.id.tv_vendor)).setText(blip.getVendorName());
            if (!blip.getLocations().isEmpty()) {
                Location location = blip.getLocations().get(0);
                String loc = "";
                if (!location.getCity().isEmpty()) {
                    loc += location.getCity();
                }
                if (!location.getState().isEmpty()) {
                    if (!loc.isEmpty()) loc += ", ";
                    loc += location.getState();
                }
                view.findViewById(R.id.tv_location).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tv_location)).setText(loc);
            } else {
                view.findViewById(R.id.tv_location).setVisibility(View.GONE);
            }

            ((TextView) view.findViewById(R.id.tv_offer)).setText(blip.getCouponTitle());

            String expirationDate = blip.getExpireDate();
            String remainingBlips = blip.getRemaining();
            view.findViewById(R.id.rush_layout).setVisibility(View.GONE);
            if (expirationDate != null && !expirationDate.equals("UNLIMITED") && !expirationDate.equals("0")) {
                view.findViewById(R.id.rush_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.tv_expiration_date).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tv_expiration_date)).setText("Expires on " + expirationDate);
            } else {
                view.findViewById(R.id.tv_expiration_date).setVisibility(View.GONE);
            }
            if (remainingBlips != null && !remainingBlips.equals("UNLIMITED")) {
                view.findViewById(R.id.rush_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.tv_quantity).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tv_quantity)).setText(remainingBlips + " Blips left");
            } else {
                view.findViewById(R.id.tv_quantity).setVisibility(View.GONE);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((com.ordiacreativeorg.localblip.activity.BlipDetails) getActivity()).changeBlip(blip);
                }
            });
            linearLayout.addView(view);
        }
    }

    public void share() {
        ImageView imageView = (ImageView) mRootView.findViewById(R.id.blip_image);
        saveImageToBitmap(imageView);
        File imagePath = new File(getActivity().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.ordiacreativeorg.localblip.fileprovider", newFile);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setType("*/*");
        String[] mimetypes = {"image/*", "text/plain"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.putExtra(Intent.EXTRA_SUBJECT, "LocalBlip");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this deal at LocalBlip" + "\n\n" + mBlip.getCouponUrl().replaceAll("\\s+", "") + "\n\n");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);

        // Share Via Facebook
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.name).contains("www.facebook.com")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.setComponent(name);
                getActivity().startActivity(intent);
                break;
            }
        }


        getActivity().startActivity(Intent.createChooser(intent, "Post to social media"));
    }

    protected void showSocialMediaMenu(final View v) {
        if (mBlip.getSocial().size() > 0) {
            boolean hasSocial = false;
            for (SocialMedia socialMedia : mBlip.getSocial()) {
                hasSocial = !(socialMedia.getFacebook().trim().isEmpty()
                        && socialMedia.getGoogle().trim().isEmpty()
                        && socialMedia.getInstagram().trim().isEmpty()
                        && socialMedia.getTwitter().trim().isEmpty()
                        && socialMedia.getPinterest().trim().isEmpty()
//                        && socialMedia.getSnapchat().trim().isEmpty()
                        && socialMedia.getTumblr().trim().isEmpty());
            }
            if (hasSocial) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenu().add(R.string.share);
                popup.getMenu().add(R.string.follow);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (String.valueOf(item).equals(getResources().getString(R.string.share))) {
                            share();
                        }
                        if (String.valueOf(item).equals(getResources().getString(R.string.follow))) {
                            FollowDialog followDialog = FollowDialog.newInstance(mBlip);
                            followDialog.show(getActivity().getSupportFragmentManager(), null);
                        }
                        return true;
                    }
                });
            } else {
                share();
            }
        } else {
            share();
        }
    }

    private void saveImageToBitmap(ImageView view) {
        try {


            Bitmap photoBitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
            File cachePath = new File(getActivity().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(WebView view, String url) {

            super.onLoadResource(view, url);

        }
    }

    private class InsideWebViewClient extends WebViewClient {
        @Override
        // Force links to be opened inside WebView and not in Default Browser
        // Thanks http://stackoverflow.com/a/33681975/1815624
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


//    public void onBackPressed() {
//        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
//        if (!webChromeClient.onBackPressed()) {
//            if (webView.canGoBack()) {
//                webView.goBack();
//            } else {
//                // Standard back button implementation (for example this could close the app)
//                super.getActivity().onBackPressed();
//            }
//        }
//    }

}


