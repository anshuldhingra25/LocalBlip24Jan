package com.ordiacreativeorg.localblip.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.BlipAlert;
import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.ImageFile;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Blip_Mail extends Fragment {
    private ScrollView scroll_Blip_Mail;
    private Spinner distance;
    private Spinner gender;
    private TextView category;
    private Spinner hour;
    private Spinner minute;
    private CalendarView calendar;
    private ImageView image;
    private EditText et_message;
    private TextView txt_counter;
    private EditText zipcode;
    private RadioGroup rg, coupangroup;
    private EditText subject;
    private RadioButton allAge, coupanyes, coupanno;
    private RadioButton older;
    private Button sentMail;
    private String zip, message, cat, stsubject, stimage;
    private String newDate, age;
    private int val = 0;
    private TextView blipList, blipText;
    private String coupanID = "";

//    public Blip_Mail() {
//        // Required empty public constructor
//        mTextEditorWatcher = null;
//    }

    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.blip_mail, container, false);

        mRootView.setFocusableInTouchMode(true);
        mRootView.requestFocus();
        mRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        Log.e("lkkj", "" + 1);
                        getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, new IncreaseMyVisibility()).addToBackStack(null).commit();
                        ((MainActivity) getActivity()).changeFragment();
//                        acc.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;

            }
        });
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getActivity().setTitle(R.string.mail);
        scroll_Blip_Mail = (ScrollView) mRootView.findViewById(R.id.scroll_Blip_Mail);
        distance = (Spinner) mRootView.findViewById(R.id.mail_distance);
        ArrayList<String> dis_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.miles_array)));
        ArrayAdapter<String> disAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, dis_list);
        disAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distance.setAdapter(disAdapter);
        distance.setSelection(0);

        gender = (Spinner) mRootView.findViewById(R.id.mail_gender);
        ArrayList<String> gen_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.visibility_gender)));
        ArrayAdapter<String> genAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, gen_list);
        genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genAdapter);
        gender.setSelection(0);

        hour = (Spinner) mRootView.findViewById(R.id.mail_hour_select);
        ArrayList<String> hour_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.hour)));
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, hour_list);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hour.setAdapter(hourAdapter);
        hour.setSelection(0);

        minute = (Spinner) mRootView.findViewById(R.id.mail_minute_select);
        ArrayList<String> minute_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.minute)));
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, minute_list);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minute.setAdapter(minuteAdapter);
        minute.setSelection(0);

        ImageButton removeImageButton = (ImageButton) mRootView.findViewById(R.id.btn_remove_image);
        ImageButton fromImagesButton = (ImageButton) mRootView.findViewById(R.id.btn_images);
        ImageButton fromWebButton = (ImageButton) mRootView.findViewById(R.id.btn_web);
        image = (ImageView) mRootView.findViewById(R.id.mail_blip);
        et_message = (EditText) mRootView.findViewById(R.id.et_message);
        txt_counter = (TextView) mRootView.findViewById(R.id.txt_counter);
        zipcode = (EditText) mRootView.findViewById(R.id.mail_zipcode);
        subject = (EditText) mRootView.findViewById(R.id.mail_subject);
        rg = (RadioGroup) mRootView.findViewById(R.id.rg_offer);
        allAge = (RadioButton) mRootView.findViewById(R.id.all_ages);
        older = (RadioButton) mRootView.findViewById(R.id.older);
        category = (TextView) mRootView.findViewById(R.id.mail_category);
        sentMail = (Button) mRootView.findViewById(R.id.sentMail);
        calendar = (CalendarView) mRootView.findViewById(R.id.mail_calendar);
        allAge.setChecked(true);


        blipList = (TextView) mRootView.findViewById(R.id.blip_list_mail);
        blipText = (TextView) mRootView.findViewById(R.id.txt_blip_list_mail);
        coupangroup = (RadioGroup) mRootView.findViewById(R.id.coupangroup);
        coupanyes = (RadioButton) mRootView.findViewById(R.id.coupanyes);
        coupanno = (RadioButton) mRootView.findViewById(R.id.coupanno);
//        initializeCalendar();

        coupangroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.coupanyes) {
                    blipList.setVisibility(View.VISIBLE);
                    blipText.setVisibility(View.VISIBLE);
                } else if (i == R.id.coupanno) {
                    coupanID = "";
                    mSelectedBlip = null;
                    blipList.setVisibility(View.GONE);
                    blipText.setVisibility(View.GONE);
                }
            }
        });

        blipList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBlipDialog blipDialog = new MyBlipDialog();
                blipDialog.setTargetFragment(Blip_Mail.this, 2501);
                blipDialog.show(getActivity().getSupportFragmentManager(), "blip");
            }
        });

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        newDate = mMonth + "/" + mDay + "/" + mYear;

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int day, int month, int year) {

                month = month + 1;
                // output to log cat **not sure how to format year to two places here**
                newDate = month + "/" + year + "/" + day;
                Log.d("NEW_DATE", "" + newDate);
                Toast.makeText(getContext(), day + " : " + month + " : " + year, Toast.LENGTH_LONG).show();
            }
        });

//        initializeCalendar();

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoriesDialog categoriesDialog = new CategoriesDialog();
                categoriesDialog.setTargetFragment(Blip_Mail.this, 1234);
                categoriesDialog.show(getActivity().getSupportFragmentManager(), "categories");
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
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1222);
            }
        });

        fromWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagesDialog imagesDialog = new ImagesDialog();
                imagesDialog.setTargetFragment(Blip_Mail.this, 1423);
                imagesDialog.show(getActivity().getSupportFragmentManager(), "images");
            }
        });


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (allAge.isChecked()) {
                    age = "0";
                } else {
                    age = "3";
                }

            }
        });

        et_message.addTextChangedListener(mTextEditorWatcher); // Add counter with EditText

        sentMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                stsubject = subject.getText().toString().trim();
//                stimage = image.getDrawable().toString().trim();
                zip = zipcode.getText().toString().trim();
                message = et_message.getText().toString().trim().replaceAll("\\n", "<br />");
                cat = category.getText().toString().trim();
                stsubject = subject.getText().toString();
                Log.e("value", "" + val);
                // Check for empty data in the form
                if (!zip.isEmpty() && !message.isEmpty() && !stsubject.isEmpty() && !cat.isEmpty() && val != 0
                        ) {
                    // login user
                    blipMail();
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getActivity(),
                            "Please Enter Zipcode, Subject, Image, Message and Category!", Toast.LENGTH_LONG).show();
                }
            }
        });
        scroll_Blip_Mail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(zipcode.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(subject.getWindowToken(), 0);
                return false;
            }
        });

//        zipcode.setLines(1);
//        zipcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_GO){
//
//                    InputMethodManager mgr = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                    return true;
//                }
//                return false;
//            }
//        });

        subject.setLines(2);
//        et_message.setVerticallyScrolling
        subject.setHorizontallyScrolling(false);
        subject.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager mgr = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        et_message.setLines(6);
//        et_message.setVerticallyScrolling
        et_message.setHorizontallyScrolling(false);
        et_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager mgr = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        return mRootView;
    }

    // Character Counter
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            txt_counter = (TextView) mRootView.findViewById(R.id.txt_counter);

            txt_counter.setText(String.valueOf(charSequence.length()));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void removeBlipImage() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(image, "alpha", 1f, 0f);
        objectAnimator.setDuration(150);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                image.setImageResource(R.drawable.no_image);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(image, "alpha", 0f, 1f);
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
        val = 0;
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
        ImageLoader.getInstance().cancelDisplayTask(image);
        // Load the image
        ImageLoader.getInstance().displayImage(imageUrl, image, options);
    }

    private void setBlipImage(final Bitmap bitmap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(image, "alpha", 1f, 0f);
        objectAnimator.setDuration(150);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                image.setImageBitmap(bitmap);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(image, "alpha", 0f, 1f);
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


//    public void initializeCalendar() {
//        calendar = (CalendarView) mRootView.findViewById(R.id.mail_calendar);
//        // sets whether to show the week number.
//        calendar.setShowWeekNumber(false);
//        // sets the first day of week according to Calendar.
//        // here we set Monday as the first day of the Calendar
//        calendar.setFirstDayOfWeek(2);
//        //The background color for the selected week.
//        calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));
//        //sets the color for the dates of an unfocused month.
////        calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));
//        //sets the color for the separator line between weeks.
////        calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));
//        //sets the color for the vertical bar shown at the beginning and at the end of the selected date.
//        calendar.setSelectedDateVerticalBar(R.drawable.rect_comment);
//        //sets the listener to be notified upon selected date change.
//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            //show the selected date as a toast
//
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
//                Toast.makeText(getActivity(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private Category mSelectedCategory;
    private Bitmap mPhotoBitmap = null;
    private String smallImageUrl = null;
    private String imageUrl = null;
    private Blip mSelectedBlip;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1234:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedCategory = (Category) data.getSerializableExtra("category");
                    category.setText(mSelectedCategory.getCategoryName());
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
                            val = 1;
                        }
                        imageUrl = null;
                        smallImageUrl = null;
                        setBlipImage(mPhotoBitmap);
                    }
                }
                break;
            case 1423:
                if (resultCode == Activity.RESULT_OK) {
                    ImageFile imageFile = (ImageFile) data.getSerializableExtra("imageFile");
                    smallImageUrl = "https://localblip.com/" + imageFile.getSource().substring(3).replace("\\", "");
                    imageUrl = smallImageUrl.replace("-small", "");
                    val = 2;
                    mPhotoBitmap = null;
                    loadBlipImage(smallImageUrl);
                }
                break;
            case 2501:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedBlip = (Blip) data.getSerializableExtra("blip");
                    Log.e("seeee", "" + mSelectedBlip.getCouponTitle());
                    blipList.setText(mSelectedBlip.getCouponTitle());
                }
                break;
        }
    }

    private void blipMail() {
        MemberDetail akki = TemporaryStorageSingleton.getInstance().getMemberDetails();
        HashMap<String, String> params = new HashMap<>();
        RequestBody requestBody = null;
//        final String stzipcode = zipcode.getText().toString();
//        final String stmessage = et_message.getText().toString();
        final String dis = String.valueOf(distance.getSelectedItem().toString());
        final String stgender = String.valueOf(gender.getSelectedItemPosition());
        final String sthour = String.valueOf(hour.getSelectedItemPosition());
        final String stminute = String.valueOf(minute.getSelectedItemPosition());
//        final String stcalender = String.valueOf(calendar.getDate());
        cat = String.valueOf(mSelectedCategory.getCategoryId());
        if (mSelectedBlip != null) {
            coupanID = String.valueOf(mSelectedBlip.getCouponId());
        }
//        final String stcategory = String.valueOf(mSelectedCategory.getCategoryId());
//        System.out.println("stzipcode---"+stzipcode+"\nstmessage-----"+stmessage+"\ndis-------"+dis+"\nstgender-------"+stgender
//        +"\nsthour---"+sthour+"\nstminute--------"+stminute+"\nstcalender-------"+stcalender);


        if (mPhotoBitmap == null) {
            params.put("imgurl", smallImageUrl);

            Api.getInstance().getMethods().blipMail(
                    coupanID,
                    zip,
                    dis,
                    age,
                    stgender,
                    cat,
                    stsubject,
                    message,
                    newDate,
                    sthour,
                    stminute,
                    "5",
                    "0",
                    akki.getEmail(),
                    akki.getApiKey(),
                    params


            ).enqueue(new Callback<BlipAlert>() {
                @Override
                public void onResponse(Response<BlipAlert> response, Retrofit retrofit) {
                    Log.e("respooo", "" + response.body());
                    BlipAlert m = response.body();
//               if (null == m.getResponse()) {
//                   Log.e("getress",""+ m.getResponse());
//                }  else {
//                Snackbar.make(mRootView,getResources().getString(R.string.alert_sent), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                }).show();
                    Snackbar.make(mRootView, getResources().getString(R.string.alert_sent), Snackbar.LENGTH_SHORT).show();
                    getFragmentManager().popBackStackImmediate();
//                }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("", "test" + t.getMessage());
                    Snackbar.make(mRootView, getResources().getString(R.string.alert_failed), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
//                mProgressDialog.dismiss();
                }
            });
        } else {

            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
            mPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayBitmapStream);
            byte[] bytes = byteArrayBitmapStream.toByteArray();
            requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
            Api.getInstance().getMethods().blipMail(
                    coupanID,
                    zip,
                    dis,
                    age,
                    stgender,
                    cat,
                    stsubject,
                    message,
                    newDate,
                    sthour,
                    stminute,
                    "5",
                    "0",
                    akki.getEmail(),
                    akki.getApiKey(),

                    requestBody


            ).enqueue(new Callback<BlipAlert>() {
                @Override
                public void onResponse(Response<BlipAlert> response, Retrofit retrofit) {
                    Log.e("respooo", "" + response.body());
                    BlipAlert m = response.body();
//               if (null == m.getResponse()) {
//                   Log.e("getress",""+ m.getResponse());
//                }  else {
//                Snackbar.make(mRootView,getResources().getString(R.string.alert_sent), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                }).show();
                    Snackbar.make(mRootView, getResources().getString(R.string.alert_sent), Snackbar.LENGTH_SHORT).show();
                    getFragmentManager().popBackStackImmediate();
//                }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("", "test" + t.getMessage());
                    Snackbar.make(mRootView, getResources().getString(R.string.alert_failed), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
//                mProgressDialog.dismiss();
                }
            });

        }
        /*dev2.localblip.com/admin/apiincreasemyvisibilitycustom.php?zipcode=75201&distance=5
        &age=0&gender=1&category=1&content=Enter%20your%20message%20hereEnter%20your%20message
        %20hereEnter%20your%20message%20hereEnter%20your%20message%20hereEnter%20your%20message
        %20hereEnter%20your%20message%20hereEnter%20your%20message%20here&date=12/19/2016&hour=6&min=50&productid=4&senttype=0*/

        Log.e("ttttt", "" + dis);
        Log.e("hhhh", "" + stgender);
        Log.e("ageee", "" + age);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle("BlipMail", false);
    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        ((MainActivity) getActivity()).setBarTitle(R.string.mail, false);
//    }
}