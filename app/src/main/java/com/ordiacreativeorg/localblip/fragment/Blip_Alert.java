package com.ordiacreativeorg.localblip.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.BlipAlert;
import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Blip_Alert extends Fragment {
    private ScrollView scroll_Blip_Alert;
    private EditText zipcode;
    private Spinner distance;
    private Spinner gender;
    private TextView category;
    private Spinner hour;
    private TextView blipList;
    private Spinner minute;
    private CalendarView calendar;
    private EditText et_message;
    private TextView counter, blipText;
    private Button sentAlert;
    private ProgressDialog mProgressDialog;
    private RadioButton allAge;
    private RadioButton aboveAge;
    private RadioGroup rg;
    private String age;
    private String zip, message, cat;
    private String newDate;
    private ScrollView scrollView;
    private String coupanID = "";
    private RadioGroup coupangroup;
    private RadioButton coupanyes, coupanno;

    public Blip_Alert() {
        // Required empty public constructor
    }

    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.blip_alert, container, false);
        mRootView.setFocusableInTouchMode(true);
        mRootView.requestFocus();
        mRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        Log.e("lkkj", "" + 1);

                        getFragmentManager().popBackStackImmediate();
//                        getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, new IncreaseMyVisibility()).addToBackStack(null).commit();
//                        ((MainActivity) getActivity()).changeFragment();
//                        acc.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;

            }
        });
//        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getMenuInflater();
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getActivity().setTitle(R.string.alert);
//        ImageView menuIconImage = (ImageView) mRootView.findViewById(R.id.iv_menu_navigation);
//        menuIconImage.setVisibility(View.GONE);
        scroll_Blip_Alert = (ScrollView) mRootView.findViewById(R.id.scroll_Blip_Alert);
        distance = (Spinner) mRootView.findViewById(R.id.distance);
        ArrayList<String> dis_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.miles_array)));
        ArrayAdapter<String> disAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, dis_list);
//        disAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distance.setAdapter(disAdapter);
        distance.setSelection(0);

        gender = (Spinner) mRootView.findViewById(R.id.gender);
        ArrayList<String> gen_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.visibility_gender)));
        ArrayAdapter<String> genAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, gen_list);
//        genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genAdapter);
        gender.setSelection(0);

        hour = (Spinner) mRootView.findViewById(R.id.hour_select);
        ArrayList<String> hour_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.hour)));
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, hour_list);
//        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hour.setAdapter(hourAdapter);
        hour.setSelection(0);

        minute = (Spinner) mRootView.findViewById(R.id.minute_select);
        ArrayList<String> minute_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.minute)));
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnercustom, minute_list);
//        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minute.setAdapter(minuteAdapter);
        minute.setSelection(0);
        age = "0";
        category = (TextView) mRootView.findViewById(R.id.alert_category);
        et_message = (EditText) mRootView.findViewById(R.id.alert_review);
        counter = (TextView) mRootView.findViewById(R.id.alert_counter);
        sentAlert = (Button) mRootView.findViewById(R.id.sendAlert);
        zipcode = (EditText) mRootView.findViewById(R.id.alert_zipcode);
        calendar = (CalendarView) mRootView.findViewById(R.id.calendar);
        rg = (RadioGroup) mRootView.findViewById(R.id.rg_offer);
        allAge = (RadioButton) mRootView.findViewById(R.id.rb_dollar);
        aboveAge = (RadioButton) mRootView.findViewById(R.id.rb_percent);
        mProgressDialog = new ProgressDialog();
        allAge.setChecked(true);


        blipList = (TextView) mRootView.findViewById(R.id.blip_list_alert);


        blipText = (TextView) mRootView.findViewById(R.id.txt_blip_list_alert);
        coupangroup = (RadioGroup) mRootView.findViewById(R.id.coupangroup1);
        coupanyes = (RadioButton) mRootView.findViewById(R.id.coupanyes1);
        coupanno = (RadioButton) mRootView.findViewById(R.id.coupanno1);
//        initializeCalendar();

        coupangroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.coupanyes1) {
                    blipList.setVisibility(View.VISIBLE);
                    blipText.setVisibility(View.VISIBLE);
                } else if (i == R.id.coupanno1) {
                    coupanID = "";
                    mSelectedBlip = null;
                    blipList.setVisibility(View.GONE);
                    blipText.setVisibility(View.GONE);
                }
            }
        });


//        initializeCalendar();

        blipList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBlipDialog blipDialog = new MyBlipDialog();
                blipDialog.setTargetFragment(Blip_Alert.this, 2501);
                blipDialog.show(getActivity().getSupportFragmentManager(), "blip");
            }
        });

//        int month=1, year = 0, day =0;
//        month = month;
//        // output to log cat **not sure how to format year to two places here**
//        newDate = year+"/"+month+"/"+day;
//        Log.d("NEW_DATE","" +newDate);

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        newDate = mMonth + "/" + mDay + "/" + mYear;

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int day, int month, int year) {

//                int cal;
                month = month + 1;
                // output to log cat **not sure how to format year to two places here**
                newDate = month + "/" + year + "/" + day;
                Log.d("NEW_DATE", "" + newDate);
                Toast.makeText(getContext(), month + " : " + year + " : " + day, Toast.LENGTH_LONG).show();
            }
        });

//        initializeCalendar();


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

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoriesDialog categoriesDialog = new CategoriesDialog();
                categoriesDialog.setTargetFragment(Blip_Alert.this, 1234);
                categoriesDialog.show(getActivity().getSupportFragmentManager(), "categories");
            }
        });

        et_message.addTextChangedListener(mTextEditorWatcher); // Add counter with EditText

        sentAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                zip = zipcode.getText().toString().trim();
                message = et_message.getText().toString().trim().replaceAll("\\n", "<br />");
                cat = category.getText().toString().trim();
                // Check for empty data in the form
                if (!zip.isEmpty() && !message.isEmpty() && !cat.isEmpty()) {
                    // login user
                    blipAlert();
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getActivity(),
                            "Please Enter Zipcode, Message and Category!", Toast.LENGTH_LONG).show();
                }
            }

        });
        scroll_Blip_Alert.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(zipcode.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0);
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

            counter = (TextView) mRootView.findViewById(R.id.alert_counter);

            counter.setText(String.valueOf(charSequence.length()));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    private Category mSelectedCategory;
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
            case 2501:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedBlip = (Blip) data.getSerializableExtra("blip");
                    Log.e("seeee", "" + mSelectedBlip.getCouponTitle());
                    blipList.setText(mSelectedBlip.getCouponTitle());
                }
                break;
        }
    }

//    private final ProgressDialog mProgressDialog = new ProgressDialog();

    private void blipAlert() {
        MemberDetail akki = TemporaryStorageSingleton.getInstance().getMemberDetails();

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

        Api.getInstance().getMethods().blipAlert(
                coupanID,
                zip,
                dis,
                age,
                stgender,
                cat,
                message,
                newDate,
                sthour,
                stminute,
                "4",
                "0",
                akki.getEmail(),
                akki.getApiKey()

        ).enqueue(new Callback<BlipAlert>() {
            @Override
            public void onResponse(Response<BlipAlert> response, Retrofit retrofit) {
                Log.e("respooo", "" + response.body());
                BlipAlert m = response.body();
//               if (null == m.getResponse()) {
//                   Log.e("getress",""+ m.getResponse());
//                }  else {
//                Snackbar.make(mRootView, getResources().getString(R.string.alert_sent), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
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

        Log.e("ttttt", "" + dis);
        Log.e("hhhh", "" + stgender);
        Log.e("ageee", "" + age);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle("Blip Alert", false);

    }
}
