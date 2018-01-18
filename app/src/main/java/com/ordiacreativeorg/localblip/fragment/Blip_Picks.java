package com.ordiacreativeorg.localblip.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ScrollView;
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

import java.util.Calendar;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Blip_Picks extends Fragment {
    private ScrollView scroll_Blip_Picks;
    private TextView category;
    private CalendarView calendar;
    private TextView blipList;
    private EditText zipcode;
    private Button sentPick;
    private String zip, cat, selectDate, newDate, blip;
    private TextView txtDate;

//    private ListView myBlips;
//    ArrayList<Blip> items;
//    VendorBlipsAdapter vendorAdapter;

    public Blip_Picks() {
        // Required empty public constructor
    }

    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.blip_picks, container, false);
        mRootView.setFocusableInTouchMode(true);
        mRootView.requestFocus();
        mRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
//                        Log.e("lkkj",""+1);
//                        getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, new IncreaseMyVisibility()).addToBackStack(null).commit();
//                        ((MainActivity) getActivity()).changeFragment();
//                        acc.setVisibility(View.GONE);
                        getFragmentManager().popBackStackImmediate();
                        return true;
                    }
                }
                return false;

            }
        });
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getActivity().setTitle(R.string.blipspicks);

//        items = new ArrayList<>();
        scroll_Blip_Picks = (ScrollView) mRootView.findViewById(R.id.scroll_Blip_Picks);
        category = (TextView) mRootView.findViewById(R.id.pick_category);
        zipcode = (EditText) mRootView.findViewById(R.id.pickZipcode);
        calendar = (CalendarView) mRootView.findViewById(R.id.pickcalendar);
        sentPick = (Button) mRootView.findViewById(R.id.btn_pick);
        txtDate = (TextView) mRootView.findViewById(R.id.selectWeek);
        blipList = (TextView) mRootView.findViewById(R.id.blip_list);

//        initializeCalendar();

        blipList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBlipDialog blipDialog = new MyBlipDialog();
                blipDialog.setTargetFragment(Blip_Picks.this, 2501);
                blipDialog.show(getActivity().getSupportFragmentManager(), "blip");
            }
        });

//        calendar.setFirstDayOfWeek(2);
//        Log.e("ooo",""+calendar);

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                c.add(Calendar.DAY_OF_MONTH, 0);
                break;
            case Calendar.MONDAY:
                c.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case Calendar.TUESDAY:
                c.add(Calendar.DAY_OF_MONTH, -2);
                break;
            case Calendar.WEDNESDAY:
                c.add(Calendar.DAY_OF_MONTH, -3);
                break;
            case Calendar.THURSDAY:
                c.add(Calendar.DAY_OF_MONTH, -4);
                break;
            case Calendar.FRIDAY:
                c.add(Calendar.DAY_OF_MONTH, -5);
                break;
            case Calendar.SATURDAY:
                c.add(Calendar.DAY_OF_MONTH, -6);
                break;
            default:
                break;
        }
        int dayWeek = c.get(Calendar.DAY_OF_MONTH);
        int monthYear = c.get(Calendar.MONTH);
        int yearfinal = c.get(Calendar.YEAR);
        c.add(Calendar.DAY_OF_MONTH, 6);
        int dayWeekLast = c.get(Calendar.DAY_OF_MONTH);
        int monthYearLast = c.get(Calendar.MONTH);
        int yearfinalLast = c.get(Calendar.YEAR);
//                Toast.makeText(getActivity(), (monthYear + 1) + "/" + dayWeek + "/" + yearfinal, Toast.LENGTH_LONG).show();
//                Toast.makeText(getActivity(), (monthYearLast + 1) + "/" + dayWeekLast + "/" + yearfinalLast, Toast.LENGTH_LONG).show();

        newDate = (monthYear + 1) + "/" + dayWeek + "/" + yearfinal;
        txtDate.setText((monthYear + 1) + "/" + dayWeek + "/" + yearfinal + " - " +
                (monthYearLast + 1) + "/" + dayWeekLast + "/" + yearfinalLast);
        txtDate.setBackgroundColor(0xffFFAB00);


        calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // display the selected date by using a toast
//                Toast.makeText(getActivity(), dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_LONG).show();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//                Toast.makeText(getActivity(), dayOfWeek + "", Toast.LENGTH_LONG).show();

                switch (dayOfWeek) {
                    case Calendar.SUNDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 0);
                        break;
                    case Calendar.MONDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        break;
                    case Calendar.TUESDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -2);
                        break;
                    case Calendar.WEDNESDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -3);
                        break;
                    case Calendar.THURSDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -4);
                        break;
                    case Calendar.FRIDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -5);
                        break;
                    case Calendar.SATURDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -6);
                        break;
                    default:
                        break;
                }
                int dayWeek = calendar.get(Calendar.DAY_OF_MONTH);
                int monthYear = calendar.get(Calendar.MONTH);
                int yearfinal = calendar.get(Calendar.YEAR);
                calendar.add(Calendar.DAY_OF_MONTH, 6);
                int dayWeekLast = calendar.get(Calendar.DAY_OF_MONTH);
                int monthYearLast = calendar.get(Calendar.MONTH);
                int yearfinalLast = calendar.get(Calendar.YEAR);
//                Toast.makeText(getActivity(), (monthYear + 1) + "/" + dayWeek + "/" + yearfinal, Toast.LENGTH_LONG).show();
//                Toast.makeText(getActivity(), (monthYearLast + 1) + "/" + dayWeekLast + "/" + yearfinalLast, Toast.LENGTH_LONG).show();

                newDate = (monthYear + 1) + "/" + dayWeek + "/" + yearfinal;
                txtDate.setText((monthYear + 1) + "/" + dayWeek + "/" + yearfinal + " - " +
                        (monthYearLast + 1) + "/" + dayWeekLast + "/" + yearfinalLast);
                txtDate.setBackgroundColor(0xffFFAB00);

            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoriesDialog categoriesDialog = new CategoriesDialog();
                categoriesDialog.setTargetFragment(Blip_Picks.this, 1234);
                categoriesDialog.show(getActivity().getSupportFragmentManager(), "categories");
            }
        });

        sentPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blip = blipList.getText().toString().trim();
                cat = category.getText().toString().trim();
//                newDate = String.valueOf(calendar.getFirstDayOfWeek());

                // Check for empty data in the form
                if (!blip.isEmpty() && !cat.isEmpty()) {
                    // login user
                    blipPick();
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getActivity(), "Please Select Blip, Category and Date", Toast.LENGTH_LONG).show();
                }
            }
        });
        scroll_Blip_Picks.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(zipcode.getWindowToken(), 0);
                return false;
            }
        });

        zipcode.setLines(1);
        zipcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

//    public void getStartEndOFWeek(int enterWeek, int enterYear){
////enterWeek is week number
////enterYear is year
//        Calendar calendar = Calendar.getInstance();
//        calendar.clear();
//        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
//        calendar.set(Calendar.YEAR, enterYear);
//
//        SimpleDateFormat formatter = new SimpleDateFormat("ddMMM yyyy"); // PST`
//        Date startDate = calendar.getTime();
//        String startDateInStr = formatter.format(startDate);
//        System.out.println("...date..."+startDateInStr);
//
//        calendar.add(Calendar.DATE, 6);
//        Date enddate = calendar.getTime();
//        String endDaString = formatter.format(enddate);
//        System.out.println("...date..."+endDaString);
//    }

    //    public void initializeCalendar() {
//        calendar = (CalendarView) mRootView.findViewById(R.id.pickcalendar);
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
//                month = month+1;
//                Toast.makeText(getActivity(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
    private Category mSelectedCategory;
    private Blip mSelectedBlip;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1234:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedCategory = (Category) data.getSerializableExtra("category");
                    Log.e("seeeyyye", "" + mSelectedCategory.getCategoryName());
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

    private void blipPick() {
        MemberDetail akki = TemporaryStorageSingleton.getInstance().getMemberDetails();

        zip = zipcode.getText().toString();
        cat = String.valueOf(mSelectedCategory.getCategoryId());
        blip = String.valueOf(mSelectedBlip.getCouponId());
//        Log.e("rrr",""+mSelectedBlip.getCouponId());
        Api.getInstance().getMethods().blipPick(
                blip,
                zip,
                cat,
                newDate,
                "1",
                akki.getEmail(),
                akki.getApiKey()

        ).enqueue(new Callback<BlipAlert>() {
            @Override
            public void onResponse(Response<BlipAlert> response, Retrofit retrofit) {
                Log.e("respooo", "" + response.body());

                Snackbar.make(mRootView, getResources().getString(R.string.alert_sent), Snackbar.LENGTH_SHORT).show();
                getFragmentManager().popBackStackImmediate();
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(mRootView, getResources().getString(R.string.alert_failed), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
//                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle("Blip's Picks", false);

    }
}