package com.ordiacreativeorg.localblip.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
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
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/4/2015
 * <p>
 * This adapter for showing blip in blip list
 */
public class ShopBlipsAdapter extends RecyclerView.Adapter<ShopBlipsAdapter.ViewHolder> {
    private final ArrayList<Blip> mDataSet;
    private Context context;
    MediaController mediaControls;
    private static android.app.ProgressDialog progressDialog;

    public ShopBlipsAdapter(ArrayList<Blip> data, Context context) {
        mDataSet = data;
        this.context = context;

    }

    public ShopBlipsAdapter(ArrayList<Blip> data) {
        mDataSet = data;
    }

    @Override
    public ShopBlipsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blip_card, parent, false);
//        if (blip.getContenttype() == 1) {
//            if (blip.getVideotype() == 3) {
//                holder.video_icon.setVisibility(View.GONE);
//            } else {
//                holder.video_icon.setVisibility(View.VISIBLE);
//
//            }
//        }
//
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Blip blip = mDataSet.get(position);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.b_character)
                .showImageForEmptyUri(R.drawable.b_character)
                .showImageOnLoading(R.drawable.b_character)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        // Stop running tasks on this image if there are unfinished some
        ImageLoader.getInstance().cancelDisplayTask(holder.blipImage);
        // Load the image
        ImageLoader.getInstance().displayImage(blip.getBlipImageSmall(), holder.blipImage, options);
        holder.blipImage.setVisibility(View.VISIBLE);
        holder.blip_video.setVisibility(View.GONE);
        holder.videoview.setVisibility(View.GONE);
        holder.progressbar.setVisibility(View.GONE);
        holder.video_icon.setVisibility(View.GONE);
        if (blip.getContenttype() == 1) {
            if (blip.getVideotype() == 3) {
                holder.blipImage.setVisibility(View.VISIBLE);
                holder.blip_video.setVisibility(View.GONE);
                holder.videoview.setVisibility(View.GONE);
                holder.progressbar.setVisibility(View.GONE);

            } else if (blip.getVideotype() == 2) {
                holder.blip_video.setVisibility(View.VISIBLE);
                holder.blipImage.setVisibility(View.GONE);
                holder.videoview.setVisibility(View.GONE);
                holder.progressbar.setVisibility(View.GONE);
                holder.blip_video.getSettings().setLoadsImagesAutomatically(true);
                holder.blip_video.getSettings().setJavaScriptEnabled(true);
                holder.blip_video.getSettings().setLoadWithOverviewMode(true);
                holder.blip_video.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                holder.blip_video.getSettings().setUseWideViewPort(true);
//        holder.blip_video.getSettings().setBuiltInZoomControls(true);
                holder.blip_video.getSettings().setPluginState(WebSettings.PluginState.ON);
                holder.blip_video.setWebViewClient(new MyBrowser());
                // holder.blip_video.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                // Call private class InsideWebViewClient
                String videourl = blip.getBlipvideo();
                if (blip.getVideotype() == 1) {
                    videourl = "https:" + videourl;
                }
                if (blip.getVideotype() == 2) {
                    //videourl = "https://www.youtube.com/watch?v=VubcWIQTbdg";
                    videourl = videourl.split("v=")[1];
                    videourl = "https://www.youtube.com/embed/" + videourl;
                    if (videourl.contains("&")) {
                        videourl = videourl.split("&")[0];
                    }
                }
                Log.e("VIDEOURL", videourl);
                // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
                holder.blip_video.loadUrl(videourl);
            } else if (blip.getVideotype() == 1) {
                holder.blip_video.setVisibility(View.GONE);
                holder.blipImage.setVisibility(View.GONE);
                holder.videoview.setVisibility(View.VISIBLE);
                holder.progressbar.setVisibility(View.VISIBLE);

                String videourl = blip.getBlipvideo();
                if (blip.getVideotype() == 1) {
                    videourl = "https:" + videourl;
                }
                if (blip.getVideotype() == 2) {
                    //videourl = "https://www.youtube.com/watch?v=VubcWIQTbdg";
                    videourl = videourl.split("v=")[1];
                    videourl = "https://www.youtube.com/embed/" + videourl;
                    if (videourl.contains("&")) {
                        videourl = videourl.split("&")[0];
                    }
                }
                Log.e("VIDEOURL", videourl);

                Uri uri = Uri.parse(videourl);


//                if (mediaControls == null) {
//                    // create an object of media controller class
//                    mediaControls = new MediaController(context);
//                    mediaControls.setAnchorView(holder.videoview);
//
//                }
                // set the media controller for video view
                //  holder.videoview.setMediaController(mediaControls);
                // set the uri for the video view
                holder.videoview.setVideoURI(uri);
                holder.videoview.seekTo(1);
                // start a video
                //simpleVideoView.start();

                // implement on completion listener on video view
                holder.videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        holder.progressbar.setVisibility(View.GONE);
                        holder.video_icon.setVisibility(View.VISIBLE);
                        //  holder.videoview.start();
                        //  mediaControls.setAnchorView(holder.videoview);
//                        mediaControls.hide();
//                        // mediaControls.show(50000000);
//                        mediaControls.setPadding(0, 0, 0, 0);
//                        FrameLayout f = (FrameLayout) mediaControls.getParent();
//                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                        lp.addRule(RelativeLayout.ALIGN_BOTTOM, holder.videoview.getId());
//
//                        ((LinearLayout) f.getParent()).removeView(f);
//                        ((RelativeLayout) holder.videoview.getParent()).addView(f, lp);
//
//                        mediaControls.setAnchorView(holder.videoview);
                    }
                });
                final String finalVideourl = videourl;

                holder.videoview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {

                            Log.e("TAG", "Down");
                            return true;
                        }

                        if (event.getAction() == MotionEvent.ACTION_MOVE) {

                            Log.e("TAG", "Move");
                            return true;

                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            Intent intent = new Intent(context, VideoDisplay.class);
                            intent.putExtra("blip", finalVideourl);
                            context.startActivity(intent);
                            Log.e("TAG", "Up");
                            return true;
                        }
                        if (event.getAction() == MotionEvent.ACTION_SCROLL) {

                            Log.e("TAG", "Scroll");
                            return true;
                        }
                        if (event.getAction() == MotionEvent.AXIS_Y) {

                            Log.e("TAG", "AXIS_Y");
                            return true;
                        }
                        if (event.getAction() == MotionEvent.AXIS_X) {

                            Log.e("TAG", "AXIS_X");
                            return true;
                        }

                        return false;
                    }
                });
            }
        }


        if (!blip.getCategories().isEmpty()) {
            String category = blip.getCategories().get(0).getCategoryName();
            /*if (blip.getCategories().get(0).getSubCategories() != null && !blip.getCategories().get(0).getSubCategories().isEmpty()){
                category += ", " + blip.getCategories().get(0).getSubCategories().get(0).getSubCategoryName();
            }*/
            holder.categoryTextView.setVisibility(View.VISIBLE);
            holder.categoryTextView.setText(category);
        } else {
            holder.categoryTextView.setVisibility(View.GONE);
        }
//
//        if(blip.getFranchise_owner().equals("1")){
//
//            holder.ownerType.setVisibility(View.VISIBLE);
//            holder.ownerType.setText("FRANCHISE OWNER");
//        }
        //franchise Owner Tag
        if (!blip.getFranchise_owner().equals("0")) {

            holder.ownerType.setVisibility(View.VISIBLE);
            holder.ownerType.setText("FRANCHISE OWNER");
        } else {
            holder.ownerType.setVisibility(View.GONE);
        }


        switch (blip.getValueType()) {
            case 1:
                holder.prefixTextView.setText("save  $");
                holder.valueTextView.setText(String.valueOf(blip.getValue()));
                holder.suffixTextView.setVisibility(View.GONE);
                break;
            case 2:
                holder.prefixTextView.setText("save  ");
                holder.valueTextView.setText(String.valueOf(blip.getValue()));
                holder.suffixTextView.setVisibility(View.VISIBLE);
                break;
        }

        holder.vendorTextView.setText(blip.getVendorName());
        Log.e("Vendor", blip.getVendorName());
        if (!blip.getLocations().isEmpty()) {
            String loc = "";
            if (blip.getLocations().size() > 1) {
                loc = "This offer available at " + blip.getLocations().size() + " different locations";
            } else {
                Location location = blip.getLocations().get(0);
                if (!location.getAddress1().isEmpty()) {
                    loc += " " + location.getAddress1();
                }
                if (!location.getAddress2().isEmpty()) {
                    if (!loc.isEmpty()) loc += ",\n ";
                    loc += "#" + location.getAddress2();
                }
                if (!location.getCity().isEmpty()) {
                    if (!loc.isEmpty()) loc += "\n";
                    loc += " " + location.getCity();
                    Log.e("lll", "" + location.getCity());
                }
                if (!location.getState().isEmpty()) {
                    if (!loc.isEmpty()) loc += ", ";
                    loc += location.getState();
                }
            }
            Log.e("Location", "" + loc);
            if (loc.trim().equalsIgnoreCase("NATIONWIDE VENDOR")) loc = "Online Merchant";
            holder.locationTextView.setVisibility(View.VISIBLE);
            holder.locationTextView.setText(loc);
        } else {
            holder.locationTextView.setVisibility(View.GONE);
        }

        holder.offerTextView.setText(blip.getCouponTitle());

        String expirationDate = blip.getExpireDate();
        String remainingBlips = blip.getRemaining();
        holder.rushLayout.setVisibility(View.GONE);
        if (expirationDate != null && !expirationDate.equals("UNLIMITED") && !expirationDate.equals("0")) {
            holder.rushLayout.setVisibility(View.VISIBLE);
            holder.expirationDateTextView.setVisibility(View.VISIBLE);
            holder.expirationDateTextView.setText("Expires on " + expirationDate);
        } else {
            holder.expirationDateTextView.setVisibility(View.GONE);
        }
        if (remainingBlips != null && !remainingBlips.equals("UNLIMITED")) {
            holder.rushLayout.setVisibility(View.VISIBLE);
            holder.itemsLeftTextView.setVisibility(View.VISIBLE);
            holder.itemsLeftTextView.setText(remainingBlips + " Blips Remaining");
        } else {
            holder.itemsLeftTextView.setVisibility(View.GONE);
        }

        if (blip.getAddedtobb()) {
            holder.purchaseButton.setImageResource(R.drawable.ic_action_minus);
        } else {
            holder.purchaseButton.setImageResource(R.drawable.ic_action_add);
        }

        switch (blip.getMyVote()) {
            case 1:
                holder.rateUpButton.setImageResource(R.drawable.ic_action_rated_up);
                break;
            default:
                holder.rateUpButton.setImageResource(R.drawable.ic_action_rate_up_dark);
                break;
        }

        switch (blip.getMyVote()) {
            case 2:
                holder.rateDownButton.setImageResource(R.drawable.ic_action_rated_down);
                break;
            default:
                holder.rateDownButton.setImageResource(R.drawable.ic_action_rate_down_dark);
                break;
        }

        holder.purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.purchaseButton.setClickable(false);
                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                if (onBlipActionListener != null) {
                    onBlipActionListener.showProgress();
                }
                Api.getInstance().getMethods().addRemoveBlipsBook(
                        memberDetail.getEmail(),
                        memberDetail.getApiKey(),
                        blip.getCouponId()
                ).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                        if (response.body().getResponse().equals("added")) {
                            blip.setAddedtobb(true);
                        } else {
                            blip.setAddedtobb(false);
                        }
                        if (blip.getAddedtobb()) {
                            Snackbar.make(holder.layoutView, R.string.blip_added_to_bb, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                            holder.purchaseButton.setImageResource(R.drawable.ic_action_minus);
                        } else {
                            Snackbar.make(holder.layoutView, R.string.blip_removed_from_bb, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                            holder.purchaseButton.setImageResource(R.drawable.ic_action_add);
                        }
                        holder.purchaseButton.setClickable(true);
                        if (onBlipActionListener != null) {
                            onBlipActionListener.dismissProgress();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(holder.layoutView, "Operation failed: " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                        holder.purchaseButton.setClickable(true);
                        if (onBlipActionListener != null) {
                            onBlipActionListener.dismissProgress();
                        }
                    }
                });
            }
        });

        holder.rateUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rateUpButton.setClickable(false);
                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                if (onBlipActionListener != null) {
                    onBlipActionListener.showProgress();
                }
                Api.getInstance().getMethods().rateBlip(
                        memberDetail.getEmail(),
                        memberDetail.getApiKey(),
                        blip.getCouponId(),
                        3
                ).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                        if (response.body().getResponse().equals("remove voted up")) {
                            blip.setMyVote(0);
                        } else if (response.body().getResponse().equals("voted up")) {
                            blip.setMyVote(1);
                        }
                        switch (blip.getMyVote()) {
                            case 1:
                                Snackbar.make(holder.layoutView, R.string.rated_up, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                                holder.rateUpButton.setImageResource(R.drawable.ic_action_rated_up);
                                holder.rateDownButton.setImageResource(R.drawable.ic_action_rate_down_dark);
                                blip.setMyVote(1);
                                break;
                            default:
                                Snackbar.make(holder.layoutView, R.string.unrated, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                                holder.rateUpButton.setImageResource(R.drawable.ic_action_rate_up_dark);
                                blip.setMyVote(0);
                                break;
                        }
                        holder.rateUpButton.setClickable(true);
                        if (onBlipActionListener != null) {
                            onBlipActionListener.dismissProgress();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(holder.layoutView, "Operation failed: " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                        holder.rateUpButton.setClickable(true);
                        if (onBlipActionListener != null) {
                            onBlipActionListener.dismissProgress();
                        }
                    }
                });
            }
        });
        holder.blip_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   videoplay(holder.blip_video);
            }
        });
        holder.shareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String blipUrl = blip.getCouponUrl().replaceAll("\\s+", "");
                Log.e("BLIPURL", blipUrl);

                holder.shareButton.setClickable(true);
                String value = holder.valueTextView.getText().toString();
                saveImageToBitmap(holder.blipImage);
                File imagePath = new File(context.getCacheDir(), "images");
                File newFile = new File(imagePath, "image.png");
                Uri contentUri = FileProvider.getUriForFile(context, "com.ordiacreativeorg.localblip.fileprovider", newFile);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
//                intent.putExtra("CONTENT_TYPE", "image/*;text/plain");
//                intent.setType("image/*,text/*");Check it out at LocalBlip
                intent.putExtra(Intent.EXTRA_SUBJECT, "LocalBlip");
                intent.putExtra(Intent.EXTRA_TEXT, "Check out this deal at LocalBlip" + "\n\n" + blipUrl + "\n\n");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);

                v.getContext().startActivity(Intent.createChooser(intent, "Post to social media"));


            }
        });


        holder.rateDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rateDownButton.setClickable(false);
                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                if (onBlipActionListener != null) {
                    onBlipActionListener.showProgress();
                }
                Api.getInstance().getMethods().rateBlip(
                        memberDetail.getEmail(),
                        memberDetail.getApiKey(),
                        blip.getCouponId(),
                        4
                ).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Response<SimpleResponse> response, Retrofit retrofit) {
                        if (response.body().getResponse().equals("remove voted down")) {
                            blip.setMyVote(0);
                        } else if (response.body().getResponse().equals("voted down")) {
                            blip.setMyVote(2);
                        }
                        switch (blip.getMyVote()) {
                            case 2:
                                Snackbar.make(holder.layoutView, R.string.rated_down, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                                holder.rateDownButton.setImageResource(R.drawable.ic_action_rated_down);
                                holder.rateUpButton.setImageResource(R.drawable.ic_action_rate_up_dark);
                                blip.setMyVote(2);
                                break;
                            default:
                                Snackbar.make(holder.layoutView, R.string.unrated, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                                holder.rateDownButton.setImageResource(R.drawable.ic_action_rate_down_dark);
                                blip.setMyVote(0);
                                break;
                        }
                        holder.rateDownButton.setClickable(true);
                        if (onBlipActionListener != null) {
                            onBlipActionListener.dismissProgress();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(holder.layoutView, "Operation failed: " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                        holder.rateDownButton.setClickable(true);
                        if (onBlipActionListener != null) {
                            onBlipActionListener.dismissProgress();
                        }
                    }
                });
            }
        });
//        holder.shareButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String answer = null;
//                shareAnswer(answer);
//            }
//        });

        if (onBlipActionListener != null) {
            holder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBlipActionListener.onBlipChosen(blip);
                }
            });
        }
    }

    private Uri createImageBitmapUrl(View view) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        File file = new File(myDir, "photo.jpg");
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
//            ImageView imageView = (ImageView) mRootView.findViewById(R.id.blip_image);
            ImageView blipImage = (ImageView) view.findViewById(R.id.blip_image);
            Bitmap photoBitmap = ((BitmapDrawable) blipImage.getDrawable()).getBitmap();
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.d("PHOTO_PATH", String.format("%s %d", file.getPath(), file.getTotalSpace()));
        return Uri.fromFile(file);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addAll(List<Blip> elements) {
        int from = mDataSet.size();
        mDataSet.addAll(elements);
        int to = mDataSet.size();
        notifyItemRangeInserted(from, to - from);
    }

    public void replaceAll(List<Blip> elements) {
        mDataSet.clear();
        mDataSet.addAll(elements);
        notifyDataSetChanged();
    }

    public void fixAll(List<Integer> ids, List<Integer> votes, List<Integer> abb) {
        for (int i = 0; i < mDataSet.size(); i++) {
            int index = ids.indexOf(mDataSet.get(i).getCouponId());
            if (index >= 0) {
                mDataSet.get(i).setMyVote(votes.get(index));
                mDataSet.get(i).setAddedtobb(abb.get(index) == 1);
            }
        }
        notifyDataSetChanged();
    }
//    private void shareAnswer(String answer) {
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType("text/plain");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, answer);
//        startActivity(Intent.createChooser(shareIntent, getString("title")));
//    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View layoutView;
        private final ImageView blipImage;
        private final TextView categoryTextView;
        private final TextView prefixTextView;
        private final TextView valueTextView;
        private final TextView suffixTextView;
        private final TextView vendorTextView;
        private final TextView locationTextView;
        private final TextView offerTextView;
        private final View rushLayout;
        private final TextView expirationDateTextView;
        private final TextView itemsLeftTextView;
        private final ImageButton purchaseButton;
        private final ImageButton rateUpButton;
        private final ImageButton rateDownButton;
        private final WebView blip_video;
        private final ProgressBar progressbar;
        private final VideoView videoview;
        private final ImageView video_icon;
        //        private final ImageButton shareButton;
        private ImageButton shareButton;
        TextView ownerType;

        public ViewHolder(View v) {
            super(v);
            layoutView = v;
            video_icon = (ImageView) v.findViewById(R.id.video_icon);
            ownerType = (TextView) v.findViewById(R.id.owner_type);
            blipImage = (ImageView) v.findViewById(R.id.blip_image);
            blip_video = (WebView) v.findViewById(R.id.blip_video);
            videoview = (VideoView) v.findViewById(R.id.videoview);
            progressbar = (ProgressBar) v.findViewById(R.id.progressbar);
            categoryTextView = (TextView) v.findViewById(R.id.tv_category);
            prefixTextView = (TextView) v.findViewById(R.id.tv_prefix);
            valueTextView = (TextView) v.findViewById(R.id.tv_value);
            suffixTextView = (TextView) v.findViewById(R.id.tv_suffix);
            vendorTextView = (TextView) v.findViewById(R.id.tv_vendor);
            locationTextView = (TextView) v.findViewById(R.id.tv_location);
            offerTextView = (TextView) v.findViewById(R.id.tv_offer);
            rushLayout = v.findViewById(R.id.rush_layout);
            expirationDateTextView = (TextView) v.findViewById(R.id.tv_expiration_date);
            itemsLeftTextView = (TextView) v.findViewById(R.id.tv_quantity);
            purchaseButton = (ImageButton) v.findViewById(R.id.btn_purchase);
            rateUpButton = (ImageButton) v.findViewById(R.id.btn_rate_up);
            rateDownButton = (ImageButton) v.findViewById(R.id.btn_rate_down);
            shareButton = (ImageButton) v.findViewById(R.id.btn_share);


        }
    }

    private OnBlipActionListener onBlipActionListener;

    public void setOnBlipActionListener(OnBlipActionListener onBlipActionListener) {
        this.onBlipActionListener = onBlipActionListener;
    }

    public interface OnBlipActionListener {
        void showProgress();

        void dismissProgress();

        void onBlipChosen(Blip blip);
    }

    private void saveImageToBitmap(ImageView view) {
        try {


            Bitmap photoBitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
            File cachePath = new File(context.getCacheDir(), "images");
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

    public void videoplay(final VideoView blip_video) {
        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    context);
            mediacontroller.setAnchorView(blip_video);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(mDataSet.get(0).getBlipvideo());
            blip_video.setMediaController(mediacontroller);
            blip_video.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        blip_video.requestFocus();
        blip_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                blip_video.start();
            }
        });

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
}