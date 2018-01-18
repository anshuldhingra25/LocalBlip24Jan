package com.ordiacreativeorg.localblip.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 * <p>
 * This adapter for showing blip in blip list
 */
public class VendorBlipsAdapter extends RecyclerView.Adapter<VendorBlipsAdapter.ViewHolder> {
    private final ArrayList<Blip> mDataSet;
    private Context context;
    private List<String> packages = new ArrayList<>();

    public VendorBlipsAdapter(ArrayList<Blip> data, Context context) {
        mDataSet = data;
        this.context = context;
    }

    @Override
    public VendorBlipsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blip_card_vendor, parent, false);
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
                .showImageOnFail(R.drawable.no_image)
                .showImageForEmptyUri(R.drawable.no_image)
                .showImageOnLoading(R.drawable.no_image)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        // Stop running tasks on this image if there are unfinished some
        ImageLoader.getInstance().cancelDisplayTask(holder.blipImage);
        // Load the image
        ImageLoader.getInstance().displayImage(blip.getBlipImage(), holder.blipImage, options);

        if (!blip.getCategories().isEmpty()) {
            String category = blip.getCategories().get(0).getCategoryName();
            /*if (!blip.getCategories().get(0).getSubCategories().isEmpty()){
                category += ", " + blip.getCategories().get(0).getSubCategories().get(0).getSubCategoryName();
            }*/
            holder.categoryTextView.setVisibility(View.VISIBLE);
            holder.categoryTextView.setText(category);
        } else {
            holder.categoryTextView.setVisibility(View.GONE);
        }

        //franchise Owner Tag
        if (!blip.getFranchise_owner().equals("0")) {

            holder.owner.setVisibility(View.VISIBLE);
            holder.owner.setText("FRANCHISE OWNER");
        } else {
            holder.owner.setVisibility(View.GONE);
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

        if (!blip.getLocations().isEmpty()) {
            Location location = blip.getLocations().get(0);
            String loc = "";
            if (!location.getAddress1().isEmpty()) {
                loc += " " + location.getAddress1();
            }
            if (!location.getAddress2().isEmpty()) {
                if (!loc.isEmpty()) loc += ",\n ";
                loc += "#" + location.getAddress2();
            }
            if (!location.getCity().isEmpty()) {
                if (!loc.isEmpty()) loc += ",\n ";
                loc += location.getCity();
            }
            if (!location.getState().isEmpty()) {
                if (!loc.isEmpty()) loc += ", ";
                loc += location.getState();
            }
            Log.e("LocationVendor", "" + loc);
            if(loc.trim().equalsIgnoreCase("ONLINE VENDOR")) loc="Online Merchant";
            holder.locationTextView.setVisibility(View.VISIBLE);
            holder.locationTextView.setText(loc);
        } else {
            holder.locationTextView.setVisibility(View.GONE);
        }

        holder.offerTextView.setText(blip.getCouponTitle());

        String expirationDate = blip.getExpireDate();
        String remainingBlips = blip.getRemaining();
        holder.rushLayout.setVisibility(View.GONE);
        holder.expirationDateTextView.setVisibility(View.GONE);
        holder.expirationDateTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        if (expirationDate != null && !expirationDate.equals("UNLIMITED") && !expirationDate.equals("0")) {
            holder.rushLayout.setVisibility(View.VISIBLE);
            holder.expirationDateTextView.setVisibility(View.VISIBLE);
            holder.expirationDateTextView.setText("Expires on " + expirationDate);
            String[] parsed = expirationDate.split("/");
            if (parsed.length == 3) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, Integer.valueOf(parsed[1]) - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parsed[0]));
                    calendar.set(Calendar.YEAR, Integer.valueOf(parsed[2]) + 2000);
                    if (Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis() < 2 * 24 * 60 * 60 * 1000) {
                        holder.expirationDateTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.warning, 0, 0, 0);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        holder.itemsLeftTextView.setVisibility(View.GONE);
        holder.itemsLeftTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        if (remainingBlips != null && !remainingBlips.equals("UNLIMITED")) {
            holder.rushLayout.setVisibility(View.VISIBLE);
            holder.itemsLeftTextView.setVisibility(View.VISIBLE);
            holder.itemsLeftTextView.setText(remainingBlips + " Blips Remaining");
            try {
                if (Integer.valueOf(remainingBlips) < 1) {
                    holder.itemsLeftTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.warning, 0, 0, 0);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        if (onBlipActionListener != null) {
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBlipActionListener.onBlipRemoved(blip);
                }
            });
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBlipActionListener.onBlipEditChosen(blip);
                }
            });
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String blipUrl = blip.getCouponUrl().replaceAll("\\s+", "");
                    Log.e("BLIPURL", blipUrl);

//            holder.shareButton.setOnClickListener(new View.OnClickListener() {


//                BlipDetails share = new BlipDetails();

                    holder.share.setClickable(true);
                    String value =holder.valueTextView.getText().toString();
                    saveImageToBitmap(holder.blipImage);
                    File imagePath = new File(context.getCacheDir(), "images");
                    File newFile = new File(imagePath, "image.png");
                    Uri contentUri = FileProvider.getUriForFile(context, "com.ordiacreativeorg.localblip.fileprovider", newFile);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "LocalBlip");
                    intent.putExtra(Intent.EXTRA_TEXT,"Check out this deal at LocalBlip" + "\n\n" + blipUrl + "\n\n");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    intent.putExtra(Intent.EXTRA_STREAM, contentUri);

                    // Share Via Facebook
                    PackageManager pm = view.getContext().getPackageManager();
                    List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
                    for (final ResolveInfo app : activityList)
                    {
                        if ((app.activityInfo.name).contains("www.facebook.com"))
                        {
                            final ActivityInfo activity = app.activityInfo;
                            final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            intent.setComponent(name);
                            view.getContext().startActivity(intent);
                            break;
                        }
                    }


                    view.getContext().startActivity(Intent.createChooser(intent, "Post to social media"));
                }
            });
            holder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBlipActionListener.onBlipChosen(blip);
                }
            });
        }
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

    public void replaceAll(List<Blip> elements) {
        mDataSet.clear();
        mDataSet.addAll(elements);
        notifyDataSetChanged();
    }

    public void removeBlip(Blip blip) {
        int index = mDataSet.indexOf(blip);
        mDataSet.remove(blip);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View layoutView;
        private final ImageView blipImage;
        private final TextView categoryTextView;
        private final TextView prefixTextView;
        private final TextView valueTextView;
        private final TextView suffixTextView;
        private final TextView locationTextView;
        private final TextView offerTextView;
        private final View rushLayout;
        private final TextView expirationDateTextView;
        private final TextView itemsLeftTextView;
        private final Button removeButton;
        private final Button editButton;
        private TextView owner;
        private Button share;

        public ViewHolder(View v) {
            super(v);
            layoutView = v;
            share = (Button) v.findViewById(R.id.share);
            owner = (TextView) v.findViewById(R.id.owner_type);
            blipImage = (ImageView) v.findViewById(R.id.blip_image);
            categoryTextView = (TextView) v.findViewById(R.id.tv_category);
            prefixTextView = (TextView) v.findViewById(R.id.tv_prefix);
            valueTextView = (TextView) v.findViewById(R.id.tv_value);
            suffixTextView = (TextView) v.findViewById(R.id.tv_suffix);
            locationTextView = (TextView) v.findViewById(R.id.tv_location);
            offerTextView = (TextView) v.findViewById(R.id.tv_offer);
            rushLayout = v.findViewById(R.id.rush_layout);
            expirationDateTextView = (TextView) v.findViewById(R.id.tv_expiration_date);
            itemsLeftTextView = (TextView) v.findViewById(R.id.tv_quantity);
            removeButton = (Button) v.findViewById(R.id.btn_remove);
            editButton = (Button) v.findViewById(R.id.btn_edit);
        }
    }

    private OnBlipActionListener onBlipActionListener;

    public void setOnBlipActionListener(OnBlipActionListener onBlipActionListener) {
        this.onBlipActionListener = onBlipActionListener;
    }

    public interface OnBlipActionListener {
        void onBlipChosen(Blip blip);

        void onBlipEditChosen(Blip blip);

        void onBlipRemoved(Blip blip);
//        void onBlipShare(Blip blip);
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
}