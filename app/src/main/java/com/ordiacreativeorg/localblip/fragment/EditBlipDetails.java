package com.ordiacreativeorg.localblip.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.ImageFile;
import com.ordiacreativeorg.localblip.model.Location;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.SubCategory;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.ordiacreativeorg.localblip.R.id.iv_blip;
import static com.ordiacreativeorg.localblip.R.id.ll_get_image;
import static com.ordiacreativeorg.localblip.R.id.owner_type;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 */
public class EditBlipDetails extends Fragment {


    public static EditBlipDetails newInstance(Blip blip, Location location,
                                              boolean edit, boolean first) {
        EditBlipDetails fragment = new EditBlipDetails();
        Bundle args = new Bundle();
        args.putSerializable("blip", blip);
        args.putSerializable("location", location);
        args.putBoolean("edit", edit);
        args.putBoolean("first", first);
        fragment.setArguments(args);
        return fragment;
    }

    public EditBlipDetails() {
        // Required empty public constructor
    }


    private RelativeLayout view_video;
    private WebView webView_editblip;
    private String videotype = "0", contenttype = "0", videoUrl = "";
    private String mediaPath = "";
    byte[] bytesvideo = null;
    private String exPiryDay = "";
    private View mRootView;
    private Blip mBlip;
    private ImageView mBlipImageView, videouplaod;
    private View mFromButtonsView;
    private TextInputLayout mBlipTitleTextInputLayout;
    private EditText mBlipTitleEditText;
    private Button mCategoryButton;
    private Button mSubCategoryButton;
    private SwitchCompat mActiveSwitch;
    private SwitchCompat mTypeSwitch;
    private SwitchCompat sw_media_type;
    private View mValueTypeLayout;
    private View mOfferPercentView;
    private View mOfferDollarView;
    private RadioButton mOfferDollarRadioButton;
    private TextInputLayout mOfferValueTextInputLayout;
    private EditText mOfferValueEditText;
    private RadioButton mOfferPercentRadioButton;
    private EditText mQuantityEditText;
    private Button mExpirationButton;
    private EditText mDescriptionEditText;
    private EditText mFinePrintEditText;
    private Button mLocationsButton;
    private LinearLayout mLocationsLinearLayout;
    private View mButtonsLayout;
    private CheckBox owner_check;
    private ScrollView scrollEdit_Details;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private ProgressDialog mProgressDialog;
    private boolean mEdit = false;
    private boolean mFirst = false;
    private String frann = "1";
    public static int mYear, mMonth, mDay;
    private int counter = 0;
    private int dialog_status = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.edit_blip_details, container, false);

        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBlipImageView = (ImageView) mRootView.findViewById(R.id.iv_blip);
//        mBlipImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
//        mBlipImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);
//        mBlipImageView.setGuideShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);
        ImageButton removeImageButton = (ImageButton) mRootView.findViewById(R.id.btn_remove_image);
        ImageButton fromImagesButton = (ImageButton) mRootView.findViewById(R.id.btn_images);
        ImageButton fromWebButton = (ImageButton) mRootView.findViewById(R.id.btn_web);
        mFromButtonsView = mRootView.findViewById(ll_get_image);
        mCategoryButton = (Button) mRootView.findViewById(R.id.btn_categories);
        mSubCategoryButton = (Button) mRootView.findViewById(R.id.btn_subcategory);
        mActiveSwitch = (SwitchCompat) mRootView.findViewById(R.id.sw_is_active);
        sw_media_type = (SwitchCompat) mRootView.findViewById(R.id.sw_media_type);
        mTypeSwitch = (SwitchCompat) mRootView.findViewById(R.id.sw_type);
        mBlipTitleTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_blip_title);
        mBlipTitleEditText = (EditText) mRootView.findViewById(R.id.et_blip_title);
        mOfferDollarRadioButton = (RadioButton) mRootView.findViewById(R.id.rb_dollar);
        mOfferValueTextInputLayout = (TextInputLayout) mRootView.findViewById(R.id.til_value);
        mOfferValueEditText = (EditText) mRootView.findViewById(R.id.et_value);
        mValueTypeLayout = mRootView.findViewById(R.id.rg_offer);
        mOfferDollarView = mRootView.findViewById(R.id.tv_prefix);
        mOfferPercentView = mRootView.findViewById(R.id.tv_suffix);
        mOfferPercentRadioButton = (RadioButton) mRootView.findViewById(R.id.rb_percent);
        mQuantityEditText = (EditText) mRootView.findViewById(R.id.et_quantity);
        mExpirationButton = (Button) mRootView.findViewById(R.id.btn_expiration_date);
        mDescriptionEditText = (EditText) mRootView.findViewById(R.id.et_description);
        mFinePrintEditText = (EditText) mRootView.findViewById(R.id.et_fine_print);
        mLocationsButton = (Button) mRootView.findViewById(R.id.btn_add_locations);
        mLocationsLinearLayout = (LinearLayout) mRootView.findViewById(R.id.ll_locations);
        mNegativeButton = (Button) mRootView.findViewById(R.id.btn_remove);
        mPositiveButton = (Button) mRootView.findViewById(R.id.btn_edit);
        mButtonsLayout = mRootView.findViewById(R.id.cv_buttons);
        owner_check = (CheckBox) mRootView.findViewById(owner_type);
        scrollEdit_Details = (ScrollView) mRootView.findViewById(R.id.scrollEdit_Details);


        webView_editblip = (WebView) mRootView.findViewById(R.id.webView_editblip);
        view_video = (RelativeLayout) mRootView.findViewById(R.id.view_video);
        view_video.setVisibility(View.GONE);

        // this tv is for showing videos layout
        sw_media_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Log.e("imageChange", "video");
                    contenttype = "1";
                    mFromButtonsView.setVisibility(View.GONE);
                    if (dialog_status == 0) {
                        MyVideodialog videoDialog = new MyVideodialog();
                        videoDialog.setTargetFragment(EditBlipDetails.this, 2501);
                        videoDialog.show(getActivity().getSupportFragmentManager(), "blip");
                    }

                } else {
                    Log.e("imageChange", "image");
                    mFromButtonsView.setVisibility(View.VISIBLE);
                    mBlipImageView.setVisibility(View.VISIBLE);
                    removeBlipImage();
                    contenttype = "0";
                    dialog_status = 0;
                    view_video.setVisibility(View.GONE);
                }
            }
        });

        mProgressDialog = new ProgressDialog();


        owner_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (owner_check.isChecked()) {
                    frann = "1";
                } else {
                    frann = "0";
                }
            }
        });
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBlipImage();
            }
        });

        fromImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pick image from gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1222);
            }
        });

        fromWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagesDialog imagesDialog = new ImagesDialog();
                imagesDialog.setTargetFragment(EditBlipDetails.this, 1423);
                imagesDialog.show(getActivity().getSupportFragmentManager(), "images");
            }
        });

        mOfferDollarRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mOfferPercentView.setVisibility(View.GONE);
                    mOfferDollarView.setVisibility(View.VISIBLE);
                    //mOfferValueTextInputLayout.setHint(getResources().getString(R.string.dollar_value));
                }
            }
        });

        mOfferPercentRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mOfferPercentView.setVisibility(View.VISIBLE);
                    mOfferDollarView.setVisibility(View.GONE);
                    //mOfferValueTextInputLayout.setHint(getResources().getString(R.string.percent_value));
                }
            }
        });

        if (getArguments() != null) {
            mBlip = ((Blip) getArguments().getSerializable("blip"));
            mEdit = getArguments().getBoolean("edit", false);
            mFirst = getArguments().getBoolean("first", false);

            if (mBlip != null && mBlip.getCouponId() > 0) {
                loadBlipDetails(mBlip);
                dialog_status = 1;
                view_video.setVisibility(View.GONE);
                // txt_videos.setText("Update Video");

                //   videotype = String.valueOf(mBlip.getVideotype());
//                txt_videos.setVisibility(View.GONE);
                if (mEdit) initEditMode();
                else initViewMode();
            } else {
                view_video.setVisibility(View.GONE);
//                txt_videos.setVisibility(View.VISIBLE);
                mEdit = true;
                mBlip = new Blip();
                Location location = (Location) getArguments().getSerializable("location");
                if (location != null) {
                    mBlip.setLocations(new ArrayList<Location>());
                    mBlip.getLocations().add(location);
                }
                createBlipDetails(mBlip);
                initEditMode();
            }
        } else {
            mEdit = true;
            mBlip = new Blip();
            createBlipDetails(mBlip);
            initEditMode();
//            view_video.setVisibility(View.GONE);
            // txt_videos.setVisibility(View.VISIBLE);
        }

        mCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoriesDialog categoriesDialog = new CategoriesDialog();
                categoriesDialog.setTargetFragment(EditBlipDetails.this, 1234);
                categoriesDialog.show(getActivity().getSupportFragmentManager(), "categories");
            }
        });

        mSubCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] selectedIds = new int[]{-1, -1};
                for (int i = 0; i < mSelectedSubCategories.size() && i < 2; i++) {
                    selectedIds[i] = mSelectedSubCategories.get(i).getSubCategoryId();
                }
                SubCategoriesDialog subCategoriesDialog = SubCategoriesDialog.newInstance(mSelectedCategory.getCategoryId(), selectedIds[0], selectedIds[1]);
                subCategoriesDialog.setTargetFragment(EditBlipDetails.this, 1245);
                subCategoriesDialog.show(getActivity().getSupportFragmentManager(), "categories");
            }
        });


        scrollEdit_Details.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mBlipTitleEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mOfferValueEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mQuantityEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mDescriptionEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mFinePrintEditText.getWindowToken(), 0);
                return false;
            }
        });


        mQuantityEditText.setHint(R.string.unlimited);

        mExpirationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 1;
                long millis = -1;
                if (!mExpirationButton.getText().toString().isEmpty() && !mExpirationButton.getText().toString().equalsIgnoreCase("Never") && !mExpirationButton.getText().toString().equalsIgnoreCase("0")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
                    //  simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    millis = simpleDateFormat.parse(mExpirationButton.getText().toString(), new ParsePosition(0)).getTime();

                    mYear = Integer.parseInt(mExpirationButton.getText().toString().split("/")[2]);
                    mMonth = Integer.parseInt(mExpirationButton.getText().toString().split("/")[0]);
                    mDay = Integer.parseInt(mExpirationButton.getText().toString().split("/")[1]);

                }
                final long finalMillis = millis;
                DateDialog dateDialog = DateDialog.newInstance(millis, new DateDialog.OnDateSelectedListener() {
                    @Override
                    public void onDateChosen(int year, int month, int day) {
                        if (year < 1 || month < 1 || day < 1) {


                            mExpirationButton.setText(R.string.never);
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month - 1);
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
                            //   simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            mExpirationButton.setText(simpleDateFormat.format(calendar.getTime()));
                            mBlip.setExpireDate(simpleDateFormat.format(calendar.getTime()));
                            mBlip.setExpDay(day);
                            exPiryDay = String.valueOf(day);
                            Log.e("ExpiryDay", exPiryDay);
                            Log.e("ExpiryDate", "" + mBlip.getExpireDate());
                            mBlip.setExpMonth(month);
                            mBlip.setExpYear(year);
                        }
                    }
                });
                dateDialog.show(getActivity().getSupportFragmentManager(), "date");
            }
        });

        mLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> selectedIds = new ArrayList<>();
                for (Location location : mSelectedLocations) {
                    selectedIds.add(location.getLocationId());
                }
                LocationsDialog locationsDialog = LocationsDialog.newInstance(selectedIds);
                locationsDialog.setTargetFragment(EditBlipDetails.this, 1937);
                locationsDialog.show(getActivity().getSupportFragmentManager(), "locations");
            }
        });

        return mRootView;
    }

    private void loadBlipImage(String imageUrl) {
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
        ImageLoader.getInstance().cancelDisplayTask(mBlipImageView);
        // Load the image
        ImageLoader.getInstance().displayImage(imageUrl, mBlipImageView, options);
    }

    private void removeBlipImage() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBlipImageView, "alpha", 1f, 0f);
        objectAnimator.setDuration(150);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBlipImageView.setImageResource(R.drawable.no_image);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBlipImageView, "alpha", 0f, 1f);
                objectAnimator.setDuration(300);
                objectAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
        if (mPhotoBitmap != null) {
            mPhotoBitmap.recycle();
        }
        mPhotoBitmap = null;
        smallImageUrl = null;
        imageUrl = null;
    }

    private void setBlipImage(final Bitmap bitmap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBlipImageView, "alpha", 1f, 0f);
        objectAnimator.setDuration(150);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBlipImageView.setImageBitmap(bitmap);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBlipImageView, "alpha", 0f, 1f);
                objectAnimator.setDuration(300);
                objectAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    //    private boolean isValidPassword(String pass) {
//        if (pass != null && pass.length() > 4) {
//            return true;
//        }
//        return false;
//    }
    private void loadBlipSummary(Blip blip) {
        mCategoryButton.setText(String.valueOf(blip.getCategories().get(0).getCategoryName()));
        mSelectedCategory = blip.getCategories().get(0);
        mSelectedSubCategories = mSelectedCategory.getSubCategories();
        while (mSelectedSubCategories.size() > 2) {
            mSelectedSubCategories.remove(2);
        }
        switch (mSelectedSubCategories.size()) {
            case 0:
                mSubCategoryButton.setText(R.string.add_subcategory);
                break;
            case 1:
                mSubCategoryButton.setText(mSelectedSubCategories.get(0).getSubCategoryName());
                break;
            case 2:
                mSubCategoryButton.setText(mSelectedSubCategories.get(0).getSubCategoryName() + "\n" + mSelectedSubCategories.get(1).getSubCategoryName());
                break;
        }
        owner_check.setChecked(Boolean.parseBoolean(blip.getFranchise_owner()));
        Log.e("uiiii", "" + blip.getFranchise_owner());
        mActiveSwitch.setChecked(blip.isStatus());
        mTypeSwitch.setChecked(blip.isOnline());
        mBlipTitleEditText.setText(String.valueOf(blip.getCouponTitle()));
        mOfferValueEditText.setText(String.valueOf(blip.getValue()));
        switch (blip.getValueType()) {
            case 1:
                mOfferDollarRadioButton.setChecked(true);
                break;
            case 2:
                mOfferPercentRadioButton.setChecked(true);
                break;
        }
        if (blip.getQuantity() == null || blip.getQuantity().equalsIgnoreCase("Unlimited")) {
            mQuantityEditText.setText("");
            mBlip.setQuantity(null);
        } else {
            mQuantityEditText.setText(String.valueOf(blip.getQuantity()));
        }
        if (blip.getExpireDate() == null || mBlip.getExpireDate().equals("0")) {
            mExpirationButton.setText(R.string.never);
            mBlip.setExpireDate(null);
        } else {
            mExpirationButton.setText(String.valueOf(blip.getExpireDate()));
        }
        //mExpirationEditText.setText(String.valueOf(blip.getExpireDate()));
    }

    private void applyBlipSummary() {


        mBlip.setCouponTitle(mBlipTitleEditText.getText().toString());
        mBlip.getCategories().clear();
        mBlip.getCategories().add(mSelectedCategory);
        mBlip.setStatus(mActiveSwitch.isChecked());
        mBlip.setOnline(mTypeSwitch.isChecked());
        if (mOfferDollarRadioButton.isChecked()) {
            mBlip.setValueType(1);
        } else {
            mBlip.setValueType(2);
        }
        // Frenchise
        if (owner_check.isChecked()) {
            mBlip.setFranchise_owner(String.valueOf(1));
            Log.e("freeeenn", "" + owner_check.isChecked());
            frann = "1";
        } else {
            frann = "0";
        }

        mBlip.setFranchise_owner(frann);
        mBlip.setValue(Integer.valueOf(mOfferValueEditText.getText().toString()));
        if (mQuantityEditText.getText().toString().isEmpty()) {
            mBlip.setQuantity(null);
        } else {
            mBlip.setQuantity(mQuantityEditText.getText().toString().toUpperCase());
        }
        if (smallImageUrl != null && imageUrl != null) {
            mBlip.setBlipImage(imageUrl);
        }
        if (!mExpirationButton.getText().toString().isEmpty() && !mExpirationButton.getText().toString().equalsIgnoreCase("Never") && !mExpirationButton.getText().toString().equalsIgnoreCase("Never")) {
            mBlip.setExpireDate(mExpirationButton.getText().toString());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(mExpirationButton.getText().toString(), new ParsePosition(0)));
            mBlip.setExpYear(calendar.get(Calendar.YEAR));
            mBlip.setExpMonth(calendar.get(Calendar.MONTH) + 1);
            mBlip.setExpDay(calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            mBlip.setExpireDate(null);
        }
        mBlip.setDescription(mDescriptionEditText.getText().toString());
        mBlip.setFinePrint(mFinePrintEditText.getText().toString());
        mBlip.setLocations(mSelectedLocations);
        saveBlip();
    }

    private void createBlipDetails(Blip blip) {
        mSelectedCategory = TemporaryStorageSingleton.getInstance().getCategories().get(1).copyCategory();
        mSelectedCategory.setSubCategories(mSelectedSubCategories);
        mCategoryButton.setText(String.valueOf(mSelectedCategory.getCategoryName()));
        mActiveSwitch.setChecked(true);
        blip.setStatus(true);
        mTypeSwitch.setChecked(false);
        blip.setOnline(false);
        blip.setCouponTitle(null);
        owner_check.setChecked(false);
        blip.setValue(-1);
        mOfferDollarRadioButton.setChecked(true);
        blip.setValueType(1);
        blip.setFranchise_owner(String.valueOf(1));
        blip.setExpireDate(null);
        mExpirationButton.setText(R.string.never);
        loadBlipLocations(blip.getLocations(), mLocationsLinearLayout);
    }

    private void loadBlipDetails(Blip blip) {
        Log.e("1111111", String.valueOf(2222222));
        if (smallImageUrl == null) {
            smallImageUrl = blip.getBlipImage().replace(".jpg", "-small.jpg");
            imageUrl = blip.getBlipImage();
        }
        Log.e("blip.getBlipvideo()", blip.getBlipvideo());
        Log.e("blip.getVideotype()", String.valueOf(blip.getVideotype()));
        setwebviewVideo(webView_editblip, blip.getBlipvideo(), blip.getVideotype());
        loadBlipImage(mBlip.getBlipImage());
        loadBlipSummary(blip);
        mDescriptionEditText.setText(String.valueOf(blip.getDescription()));
        mFinePrintEditText.setText(String.valueOf(blip.getFinePrint()));
        mSelectedLocations.clear();
        mSelectedLocations.addAll(blip.getLocations());
        getActivity().setTitle(getResources().getString(R.string.view_blip_details));
        loadBlipLocations(mSelectedLocations, mLocationsLinearLayout);
    }

    private void initViewMode() {

        Log.e("Franchise", mBlip.getFranchise_owner());
        if (mBlip.getFranchise_owner().equals("1")) {
            owner_check.setChecked(true);
        }
        if (mBlip.getContenttype() == 1) {
            sw_media_type.setChecked(true);
            sw_media_type.setEnabled(false);
            contenttype = "1";
            videotype = String.valueOf(mBlip.getVideotype());
            mBlipImageView.setVisibility(View.GONE);
            mFromButtonsView.setVisibility(View.GONE);
            view_video.setVisibility(View.VISIBLE);
        }
        sw_media_type.setEnabled(false);
        //  txt_videos.setEnabled(false);
        mFromButtonsView.setVisibility(View.GONE);
        mCategoryButton.setEnabled(false);
        mSubCategoryButton.setEnabled(false);
        mActiveSwitch.setEnabled(false);
        mTypeSwitch.setEnabled(false);
        owner_check.setEnabled(false);
        mOfferValueEditText.setEnabled(false);
        mBlipTitleEditText.setEnabled(false);
        mQuantityEditText.setEnabled(false);

        mDescriptionEditText.setEnabled(false);
        mFinePrintEditText.setEnabled(false);
        mExpirationButton.setEnabled(false);
        mPositiveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_edit, 0, 0, 0);
        mPositiveButton.setText(R.string.edit);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initEditMode();
            }
        });
        mNegativeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_remove, 0, 0, 0);
        mNegativeButton.setText(R.string.remove);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("action", "remove");
                intent.putExtra("target", mBlip);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        //mOfferDollarRadioButton.setVisibility(View.GONE);
        //mOfferPercentRadioButton.setVisibility(View.GONE);
        mValueTypeLayout.setVisibility(View.GONE);
        mRootView.findViewById(R.id.tv_blip_value).setVisibility(View.GONE);
        mRootView.findViewById(R.id.tv_category_title).setVisibility(View.GONE);
        /*if (mOfferDollarRadioButton.isChecked()){
            mOfferPercentView.setVisibility(View.GONE);
            mOfferDollarView.setVisibility(View.VISIBLE);
        }else if (mOfferPercentRadioButton.isChecked()){
            mOfferPercentView.setVisibility(View.VISIBLE);
            mOfferDollarView.setVisibility(View.GONE);
        }*/
        mLocationsButton.setEnabled(false);
        mLocationsButton.setText(R.string.location_info);
    }

    private void initEditMode() {
        getActivity().setTitle(getResources().getString(R.string.edit_blip_details));

        if (mBlip.getFranchise_owner().equals("1")) {
            owner_check.setChecked(true);
        }
        if (mBlipTitleEditText.getText().toString().equals("")) {
            owner_check.setChecked(false);
        }

        mFromButtonsView.setVisibility(View.VISIBLE);
        if (mBlip.getContenttype() == 1) {
            sw_media_type.setChecked(true);
            mFromButtonsView.setVisibility(View.GONE);
            contenttype = "1";

            videotype = String.valueOf(mBlip.getVideotype());
            mBlipImageView.setVisibility(View.GONE);
            mFromButtonsView.setVisibility(View.GONE);
            view_video.setVisibility(View.VISIBLE);
        } else {
            dialog_status = 0;
        }

        sw_media_type.setEnabled(true);
        mCategoryButton.setEnabled(true);
        mSubCategoryButton.setEnabled(true);
        mBlipTitleEditText.setEnabled(true);
        mActiveSwitch.setEnabled(true);
        mTypeSwitch.setEnabled(true);
        owner_check.setEnabled(true);
        mOfferValueEditText.setEnabled(true);
        mQuantityEditText.setEnabled(true);
        mExpirationButton.setEnabled(true);
        mDescriptionEditText.setEnabled(true);
        mFinePrintEditText.setEnabled(true);
        mPositiveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_apply, 0, 0, 0);
        mPositiveButton.setText(R.string.save);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(getActivity());
                mBlipTitleTextInputLayout.setErrorEnabled(false);
                mOfferValueTextInputLayout.setErrorEnabled(false);
                if (contenttype.equals("0")) {
                    if (mPhotoBitmap == null && (smallImageUrl == null || imageUrl == null || smallImageUrl.isEmpty() || imageUrl.isEmpty())) {
                        Snackbar.make(mRootView, R.string.add_blip_image, Snackbar.LENGTH_INDEFINITE).setDuration(3000).setAction(R.string.add_image, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scrollEdit_Details.fullScroll(ScrollView.FOCUS_UP);
                            }
                        }).show();
                        return;
                    }
                }
                if (contenttype.equals("1")) {
                    if (videotype.equals("0")) {
                        Snackbar.make(mRootView, R.string.add_blip_video, Snackbar.LENGTH_INDEFINITE).setDuration(3000).setAction(R.string.add_video, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scrollEdit_Details.fullScroll(ScrollView.FOCUS_UP);
                            }
                        }).show();
                        return;
                    }
                }
                if (mSelectedLocations.size() < 1) {
                    Snackbar.make(mRootView, R.string.add_1_location, Snackbar.LENGTH_INDEFINITE).setDuration(3000).setAction(R.string.add_locations, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mLocationsButton.performClick();
                        }
                    }).show();
                    return;
                } else if (!mTypeSwitch.isChecked()) {
                    boolean ready = false;
                    for (Location location : mSelectedLocations) {
                        if (!location.isNationwide()) ready = true;
                    }
                    if (!ready) {
                        Snackbar.make(mRootView, R.string.add_1_local_location, Snackbar.LENGTH_INDEFINITE).setDuration(3000).setAction(R.string.add_locations, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLocationsButton.performClick();
                            }
                        }).show();
                        return;
                    }
                } else {
                    boolean ready = false;
                    for (Location location : mSelectedLocations) {
                        if (location.isNationwide()) ready = true;
                    }
                    if (!ready) {
                        Snackbar.make(mRootView, R.string.add_1_nationwide_location, Snackbar.LENGTH_INDEFINITE).setDuration(3000).setAction(R.string.add_locations, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLocationsButton.performClick();
                            }
                        }).show();
                        return;
                    }
                }
                if (mOfferValueEditText.getText().toString().isEmpty()) {
                    mOfferValueTextInputLayout.setError(getResources().getString(R.string.enter_blip_value));
                    mOfferValueEditText.requestFocus();
                    return;
                } else {

                }
                if (mBlipTitleEditText.getText().toString().isEmpty()) {
                    mBlipTitleTextInputLayout.setError(getResources().getString(R.string.enter_blip_title));
                    mBlipTitleEditText.requestFocus();
                    return;
                }
                if (mDescriptionEditText.getText().toString().isEmpty()) {
                    mDescriptionEditText.requestFocus();
                    return;
                }
                applyBlipSummary();
                //initViewMode();
            }
        });
        mNegativeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_cancel, 0, 0, 0);
        mNegativeButton.setText(android.R.string.cancel);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEdit) {
                    getActivity().onBackPressed();
                } else {
                    loadBlipDetails(mBlip);
                    initViewMode();
                }
            }
        });
        //mOfferDollarRadioButton.setVisibility(View.VISIBLE);
        //mOfferPercentRadioButton.setVisibility(View.VISIBLE);
        mValueTypeLayout.setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.tv_blip_value).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.tv_category_title).setVisibility(View.VISIBLE);
        //mOfferDollarView.setVisibility(View.VISIBLE);
        //mOfferPercentView.setVisibility(View.VISIBLE);
        mLocationsButton.setEnabled(true);
        mLocationsButton.setText(R.string.add_locations);
    }

    private void hideSoftKeyboard(Activity activity) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder token = activity.getCurrentFocus().getWindowToken();
            if (token != null) {
                inputMethodManager.hideSoftInputFromWindow(token, 0);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

    }

    private final ArrayList<Location> mSelectedLocations = new ArrayList<>();

    private void loadBlipLocations(final List<Location> locations, final LinearLayout linearLayout) {
        linearLayout.removeViews(1, linearLayout.getChildCount() - 1);
        for (final Location location : locations) {
            RelativeLayout relativeLayout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.edit_blip_location, linearLayout, false);
//            if (location.getDescription().trim().isEmpty()) {
//                ((TextView) relativeLayout.findViewById(R.id.tv_description)).setText(" ");
//            } else {
//                ((TextView) relativeLayout.findViewById(R.id.tv_description)).setText(location.getDescription());
//            }

            ImageButton removeLocationButton = (ImageButton) relativeLayout.findViewById(R.id.btn_remove_location);
            removeLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearLayout.removeViewAt(locations.indexOf(location) + 1);
                    locations.remove(location);
                }
            });

            LinearLayout locationLinearLayout = (LinearLayout) relativeLayout.findViewById(R.id.ll_data);

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
            if (!location.getZipCode().isEmpty()) {
                if (!loc.isEmpty()) loc += "  ";
                loc += location.getZipCode();
            }
            if (!loc.isEmpty()) {
                TextView textView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.location_data, linearLayout, false);
                if (loc.contains("NATIONWIDE VENDOR")) loc = "Online Location";
                textView.setText(loc);

                locationLinearLayout.addView(textView);
            }
            if (!location.getPhone().isEmpty()) {
                TextView textView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.location_data, linearLayout, false);
                textView.setText(location.getPhone());
                locationLinearLayout.addView(textView);
            }
            if (location.getWebsite() != null && !location.getWebsite().isEmpty()) {
                TextView textView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.location_data, linearLayout, false);
                textView.setText(location.getWebsite());
                locationLinearLayout.addView(textView);
            }
            linearLayout.addView(relativeLayout);
        }
    }

    private Category mSelectedCategory;
    private List<SubCategory> mSelectedSubCategories = new ArrayList<>();
    private Bitmap mPhotoBitmap = null;
    private String smallImageUrl = null;
    private String imageUrl = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1234:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedCategory = (Category) data.getSerializableExtra("category");
                    mSelectedCategory.setSubCategories(mSelectedSubCategories);
                    mSelectedSubCategories.clear();
                    mSubCategoryButton.setText(R.string.add_subcategory);
                    mCategoryButton.setText(mSelectedCategory.getCategoryName());
                }
                break;
            case 1245:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedSubCategories.clear();
                    String buttonText = getResources().getString(R.string.add_subcategory);
                    SubCategory subCategory = (SubCategory) data.getSerializableExtra("subcat1");
                    if (subCategory != null) {
                        mSelectedSubCategories.add(subCategory);
                        buttonText = subCategory.getSubCategoryName();
                    }
                    subCategory = (SubCategory) data.getSerializableExtra("subcat2");
                    if (subCategory != null) {
                        if (mSelectedSubCategories.size() < 1) buttonText = "";
                        else buttonText += "\n";
                        buttonText += subCategory.getSubCategoryName();
                        mSelectedSubCategories.add(subCategory);
                    }
                    mSubCategoryButton.setText(buttonText);
                }
                break;
            case 1937:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedLocations.clear();
                    List<Integer> selectedIds = data.getIntegerArrayListExtra("locations");
                    if (selectedIds != null && selectedIds.size() > 0) {
                        for (int id : selectedIds) {
                            mSelectedLocations.add(TemporaryStorageSingleton.getInstance().getMemberDetails().getLocationById(id));
                        }
                    }
                    loadBlipLocations(mSelectedLocations, mLocationsLinearLayout);
                }
                break;
            case 1222:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        File f = new File(picturePath);
                        /*if ( 2 * 1024 * 1024 < f.length() ) {
                            Snackbar.make(mRootView, R.string.select_file, Snackbar.LENGTH_INDEFINITE).show();
                            return;
                        }*/
                        mPhotoBitmap = BitmapFactory.decodeFile(picturePath);
                        int width = mPhotoBitmap.getWidth();
                        int height = mPhotoBitmap.getHeight();
                        if (Math.max(width, height) > 1024) {
                            float scaleFactor = 1024f / Math.max(width, height);
                            mPhotoBitmap = Bitmap.createScaledBitmap(mPhotoBitmap, Math.round(width * scaleFactor), Math.round(height * scaleFactor), false);
                        }
                        imageUrl = null;
                        smallImageUrl = null;
                        setBlipImage(mPhotoBitmap);
                    }
                }
                break;


            case 1212:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri selectedVideo = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedVideo, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String videoPath = cursor.getString(columnIndex);
                        mediaPath = cursor.getString(columnIndex);
                        cursor.close();

                    }
                }
                break;

            case 1423:
                if (resultCode == Activity.RESULT_OK) {
                    ImageFile imageFile = (ImageFile) data.getSerializableExtra("imageFile");
                    smallImageUrl = "https://localblip.com/" + imageFile.getSource().substring(3).replace("\\", "");
                    imageUrl = smallImageUrl.replace("-small", "");
                    mPhotoBitmap = null;
                    loadBlipImage(smallImageUrl);
                }
                break;

            case 2501:
                if (resultCode == Activity.RESULT_OK) {
                    mFromButtonsView.setVisibility(View.GONE);
                    mediaPath = (String) data.getStringExtra("mediaPath");
                    videotype = (String) data.getStringExtra("videotype");
                    videoUrl = (String) data.getStringExtra("videoUrl");
                    Log.e("mediaPath", "" + mediaPath);
                    Log.e("videotype", "" + videotype);
                    Log.e("videoUrl", "" + videoUrl);
                    if (videotype.equals("1")) {
                        mBlipImageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(mediaPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                    }
                    if (videotype.equals("2")) {

                        String code = videoUrl.split("v=")[1];
                        String link = "http://img.youtube.com/vi/" + code + "/0.jpg";
                        loadBlipImage(link);

                    }
                }
                break;
        }
    }

    private void saveBlip() {

        MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        HashMap<String, String> params = new HashMap<>();
        boolean newCupon = mBlip.getCouponId() < 0;
        String subCategories = String.valueOf(TemporaryStorageSingleton.getInstance().getCategoryById(mSelectedCategory.getCategoryId()).getSubCategories().get(0).getSubCategoryId());
        String location = "0";

        if (mBlip.getLocations() != null && mBlip.getLocations().size() > 0) {
            location = "";
            for (Location location1 : mBlip.getLocations()) {
                location = location + "," + location1.getLocationId();
            }
            location = location.substring(1);
        }
        if (mBlip.getCategories().get(0).getSubCategories() != null && mBlip.getCategories().get(0).getSubCategories().size() > 0) {
            subCategories = "";
            for (SubCategory subCategory : mBlip.getCategories().get(0).getSubCategories()) {
                subCategories = subCategories + "," + subCategory.getSubCategoryId();
            }
            subCategories = subCategories.substring(1);
        }
        if (!newCupon) {
            params.put("franchise", mBlip.getFranchise_owner());
            params.put("title", mBlip.getCouponTitle());
            params.put("valuetype", String.valueOf(mBlip.getValueType()));
            params.put("blipvalue", String.valueOf(mBlip.getValue()));
            params.put("category", String.valueOf(mBlip.getCategories().get(0).getCategoryId()));
            params.put("subcategory", subCategories);//String.valueOf(mBlip.getCategories().get(0).getSubCategories().get(0).getSubCategoryId()));
            params.put("location", location);
        }
//        if (!mBlip.getFranchise_owner().isEmpty()) {
//            owner_check.setChecked(true);
////            params.put("franchise", mBlip.getFranchise_owner());
//        }

        if (!mBlip.getDescription().isEmpty()) {
            params.put("description", mBlip.getDescription());
        }
        if (!mBlip.getFinePrint().isEmpty()) {
            params.put("fineprint", mBlip.getFinePrint());
        }
        if (mBlip.getExpireDate() != null && !mBlip.getExpireDate().isEmpty()) {
            if (counter == 1) {
                params.put("expiremonth", String.valueOf(mBlip.getExpMonth()));
                params.put("expireday", exPiryDay);
                params.put("expireyear", String.valueOf(mBlip.getExpYear()));
                System.out.println("expiremonth  " + String.valueOf(mBlip.getExpMonth())
                        + "\nexpireday  " + exPiryDay
                        + "\nexpireyear  " + String.valueOf(mBlip.getExpYear()));
            } else {

                if (!mExpirationButton.getText().toString().isEmpty() && !mExpirationButton.getText().toString().equalsIgnoreCase("Never") && !mExpirationButton.getText().toString().equalsIgnoreCase("0")) {

                    mYear = Integer.parseInt(mExpirationButton.getText().toString().split("/")[2]);
                    mMonth = Integer.parseInt(mExpirationButton.getText().toString().split("/")[0]);
                    mDay = Integer.parseInt(mExpirationButton.getText().toString().split("/")[1]);

                    params.put("expiremonth", String.valueOf(mMonth));
                    params.put("expireday", String.valueOf(mDay));
                    params.put("expireyear", String.valueOf(mYear));
                    System.out.println("expiremonth  " + String.valueOf(mBlip.getExpMonth())
                            + "\nexpireday  " + exPiryDay
                            + "\nexpireyear  " + String.valueOf(mBlip.getExpYear()));

                }
            }
        } else if (!newCupon) {
            params.put("neverexp", "1");
        }
        if (mBlip.getQuantity() != null && !mBlip.getQuantity().isEmpty()) {
            params.put("quantity", mBlip.getQuantity());
        } else if (!newCupon) {
            params.put("unlimited", "1");
        }
        params.put("status", mBlip.isStatus() ? "1" : "0");
        params.put("online", mBlip.isOnline() ? "1" : "0");

        Callback<Blip> callback = new Callback<Blip>() {
            @Override
            public void onResponse(Response<Blip> response, Retrofit retrofit) {
                mProgressDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("action", "reload");
                intent.putExtra("id", "id");
                intent.putExtra("message", (mBlip.getCouponId() < 0 ? getResources().getString(R.string.blip_created) : getResources().getString(R.string.blip_updated)));
                getActivity().setResult(Activity.RESULT_OK, intent);
                if (mBlip.getCouponId() < 0) {
                    getActivity().finish();
                } else {
                    getActivity().onBackPressed();
//
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(mRootView, getResources().getString(R.string.operation_failed) + " " + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                mProgressDialog.dismiss();
            }
        };
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        if (mPhotoBitmap == null) {
            RequestBody video = null;
            params.put("imgurl", mBlip.getBlipImage());
            byte[] videobytes = null;
            if (!mediaPath.isEmpty() && mediaPath != null) {
                File file = new File(mediaPath);


                byte[] byteArray = null;
                try {
                    InputStream inputStream = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] b = new byte[500];
                    int bytesRead = 0;

                    while ((bytesRead = inputStream.read(b)) != -1) {
                        bos.write(b, 0, bytesRead);
                    }

                    byteArray = bos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("Length", "" + file.length());
                if (file.length() > 60000000) {
                    System.out.println("Applicable" + "no");
                } else {
                    System.out.println("Applicable" + "yes");
                }
                video = RequestBody.create(MediaType.parse("video/mp4"), byteArray);


            } else {
                video = null;
            }

            if (newCupon) {


                if (videotype.equalsIgnoreCase("0")) {
                    System.out.println("videotype1------" + videotype);
                    System.out.println("contenttype1------" + contenttype);
                    System.out.println("videourl1------" + videoUrl);

                    Call<Blip> call = Api.getInstance().getMethods().createBlip(
                            memberDetail.getEmail(),
                            memberDetail.getApiKey(),
                            String.valueOf(mBlip.getCouponTitle()),
                            mBlip.getValueType(),
                            mBlip.getValue(),
                            String.valueOf(mBlip.getCategories().get(0).getCategoryId()),
                            subCategories,
                            location,

                            frann,
                            params,
                            Integer.valueOf(videotype), Integer.valueOf(contenttype)
                    );
                    call.enqueue(callback);
                } else if (videotype.equalsIgnoreCase("1")) {
                    System.out.println("videotype2------" + videotype);
                    System.out.println("contenttype2------" + contenttype);
                    System.out.println("videourl2------" + videoUrl);

                    Call<Blip> call = Api.getInstance().getMethods().createBlip(
                            memberDetail.getEmail(),
                            memberDetail.getApiKey(),
                            String.valueOf(mBlip.getCouponTitle()),
                            mBlip.getValueType(),
                            mBlip.getValue(),
                            String.valueOf(mBlip.getCategories().get(0).getCategoryId()),
                            subCategories,
                            location,

                            frann,
                            params,
                            Integer.valueOf(videotype), Integer.valueOf(contenttype), video

                    );
                    call.enqueue(callback);
                } else {
                    System.out.println("videotype3------" + videotype);
                    System.out.println("contenttype3------" + contenttype);
                    System.out.println("videourl3------" + videoUrl);

                    Call<Blip> call = Api.getInstance().getMethods().createBlip(
                            memberDetail.getEmail(),
                            memberDetail.getApiKey(),
                            String.valueOf(mBlip.getCouponTitle()),
                            mBlip.getValueType(),
                            mBlip.getValue(),
                            String.valueOf(mBlip.getCategories().get(0).getCategoryId()),
                            subCategories,
                            location,

                            frann,
                            params,
                            Integer.valueOf(videotype), Integer.valueOf(contenttype), videoUrl

                    );
                    call.enqueue(callback);
                }
            } else {
                if (videotype.equalsIgnoreCase("0")) {
                    System.out.println("videotype44------" + videotype);
                    System.out.println("contenttype44------" + contenttype);
                    System.out.println("videourl44------" + videoUrl);
                    Api.getInstance().getMethods().updateBlip(
                            memberDetail.getEmail(),
                            memberDetail.getApiKey(),
                            mBlip.getCouponId(),
                            params,
                            Integer.valueOf(videotype), Integer.valueOf(contenttype)

                    ).enqueue(callback);
                } else if (videotype.equalsIgnoreCase("1")) {
                    System.out.println("videotype4------" + videotype);
                    System.out.println("contenttype4------" + contenttype);
                    System.out.println("videourl4------" + videoUrl);

                    if (video != null) {
                        Api.getInstance().getMethods().updateBlip(
                                memberDetail.getEmail(),
                                memberDetail.getApiKey(),
                                mBlip.getCouponId(),
                                params,
                                Integer.valueOf(videotype), Integer.valueOf(contenttype), video

                        ).enqueue(callback);

                    } else {
                        Api.getInstance().getMethods().updateBlip(
                                memberDetail.getEmail(),
                                memberDetail.getApiKey(),
                                mBlip.getCouponId(),
                                params,
                                Integer.valueOf(videotype), Integer.valueOf(contenttype)

                        ).enqueue(callback);
                    }
                } else {
                    System.out.println("videotype5------" + videotype);
                    System.out.println("contenttype5------" + contenttype);
                    System.out.println("videourl5------" + videoUrl);
                    Api.getInstance().getMethods().updateBlip(
                            memberDetail.getEmail(),
                            memberDetail.getApiKey(),
                            mBlip.getCouponId(),
                            params,
                            Integer.valueOf(videotype), Integer.valueOf(contenttype), videoUrl

                    ).enqueue(callback);
                }
            }
        } else {
            //params.put("img", "TYPE=FILE");
            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
            mPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayBitmapStream);
            byte[] bytes = byteArrayBitmapStream.toByteArray();
            byte[] videobytes = null;
            RequestBody video = null;
            if (!mediaPath.isEmpty() && mediaPath != null) {
                File file = new File(mediaPath);
                byte[] byteArray = null;
                try {
                    InputStream inputStream = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] b = new byte[500];
                    int bytesRead = 0;

                    while ((bytesRead = inputStream.read(b)) != -1) {
                        bos.write(b, 0, bytesRead);
                    }

                    byteArray = bos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                video = RequestBody.create(MediaType.parse("video/mp4"), byteArray);
            } else {
                video = null;
            }
//
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
//             video = RequestBody.create(MediaType.parse("video/mp4"), file);

            if (newCupon) {

                if (!videotype.equalsIgnoreCase("1")) {
                    System.out.println("videotype6------" + videotype);
                    System.out.println("contenttype6------" + contenttype);
                    System.out.println("videourl6------" + videoUrl);
                    Call<Blip> call = Api.getInstance().getMethods().createBlip(
                            memberDetail.getEmail(),
                            memberDetail.getApiKey(),
                            String.valueOf(mBlip.getCouponTitle()),
                            mBlip.getValueType(),
                            mBlip.getValue(),
                            String.valueOf(mBlip.getCategories().get(0).getCategoryId()),
                            subCategories,
                            location, Integer.valueOf(videotype), Integer.valueOf(contenttype), videoUrl,
                            params,
                            requestBody
                    );
                    call.enqueue(callback);

                } else {
                    System.out.println("videotype7------" + videotype);
                    System.out.println("contenttype7------" + contenttype);
                    System.out.println("videourl7------" + videoUrl);
                    Call<Blip> call = Api.getInstance().getMethods().createBlip(
                            memberDetail.getEmail(),
                            memberDetail.getApiKey(),
                            String.valueOf(mBlip.getCouponTitle()),
                            mBlip.getValueType(),
                            mBlip.getValue(),
                            String.valueOf(mBlip.getCategories().get(0).getCategoryId()),
                            subCategories,
                            location, Integer.valueOf(videotype), Integer.valueOf(contenttype),
                            params,
                            requestBody, video
                    );
                    call.enqueue(callback);
                }


            } else {

                if (!videotype.equalsIgnoreCase("1")) {
                    System.out.println("videotype8------" + videotype);
                    System.out.println("contenttype8------" + contenttype);
                    System.out.println("videourl8------" + videoUrl);
                    Api.getInstance().getMethods().updateBlip(
                            memberDetail.getEmail(),
                            memberDetail.getApiKey(),
                            mBlip.getCouponId(),
                            Integer.valueOf(videotype), Integer.valueOf(contenttype), videoUrl,
                            params,
                            requestBody
                    ).enqueue(callback);
                } else {
                    System.out.println("videotype9------" + videotype);
                    System.out.println("contenttype9------" + contenttype);
                    System.out.println("videourl9------" + videoUrl);
                    Api.getInstance().getMethods().updateBlip(
                            memberDetail.getEmail(),
                            memberDetail.getApiKey(),
                            mBlip.getCouponId(), Integer.valueOf(videotype), Integer.valueOf(contenttype),
                            params,
                            requestBody, video
                    ).enqueue(callback);
                }


            }
        }
    }


    public byte[] read(File file) throws IOException {

        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }

    public void setwebviewVideo(WebView webView_editblip, String blipvideo, int valueType) {
        Log.e("VIDEOURLLINK", blipvideo);
        Log.e("VIDEOURLTYPE", String.valueOf(valueType));

        if (!blipvideo.isEmpty()) {
            Log.e("1111111", String.valueOf(55555));


            String videoUrl = blipvideo;
            Log.e("videoUrl", videoUrl);
//            videoUrl = videoUrl.split("v=")[1];
//            videoUrl = "https://www.youtube.com/embed/" + videoUrl;
//            if (videoUrl.contains("&")) {
//                videoUrl = videoUrl.split("&")[0];//&feature=youtu.be
//                Log.e("VIDEOURLLINKSEe", blipvideo);
//            }
//            if (valueType == 1) {
//                videoUrl = "" + videoUrl;
//
//
//            }
            //https://www.youtube.com/watch?v=P-N8To7zCwo&feature=youtu.be
            if (valueType==2) {
                //videoUrl = "https://www.youtube.com/watch?v=VubcWIQTbdg";
                videoUrl = videoUrl.split("v=")[1];
                videoUrl = "https://www.youtube.com/embed/" + videoUrl;
                if (videoUrl.contains("&")) {
                    videoUrl = videoUrl.split("&")[0];//&feature=youtu.be
                    Log.e("VIDEOURLLINKSEe", blipvideo);
                }
            }
            if (valueType == 1) {
                videoUrl = "" + videoUrl;
            }
            webView_editblip.getSettings().setLoadsImagesAutomatically(true);
            webView_editblip.getSettings().setJavaScriptEnabled(true);
            webView_editblip.getSettings().setLoadWithOverviewMode(true);
            webView_editblip.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView_editblip.getSettings().setUseWideViewPort(true);
            webView_editblip.getSettings().setDomStorageEnabled(true);
            webView_editblip.getSettings().setSupportMultipleWindows(true);
            webView_editblip.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView_editblip.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        holder.blip_video.getSettings().setBuiltInZoomControls(true);
            webView_editblip.getSettings().setLoadsImagesAutomatically(true);
            webView_editblip.getSettings().setPluginState(WebSettings.PluginState.ON);

            webView_editblip.loadUrl(videoUrl);
            Log.e("1111111", String.valueOf(44444));
            webView_editblip.setWebViewClient(new MyBrowser());


        } else {
            Log.e("1111111", String.valueOf(66666));
            view_video.setVisibility(View.GONE);

            // txt_videos.setText("Upload Video");
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


}
