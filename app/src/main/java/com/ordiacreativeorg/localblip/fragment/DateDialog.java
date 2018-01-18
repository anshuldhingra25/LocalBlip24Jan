package com.ordiacreativeorg.localblip.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CompoundButton;

import com.ordiacreativeorg.localblip.R;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/18/2015
 */
public class DateDialog extends DialogFragment {
    public static boolean neverVar = false;


    public DateDialog() {
        // Required empty public constructor
    }

    public static DateDialog newInstance(long millis, OnDateSelectedListener onDateSelectedListener) {

        Bundle args = new Bundle();
        DateDialog fragment = new DateDialog();
        args.putLong("millis", millis);
        fragment.setArguments(args);
        fragment.setOnDateSelectedListener(onDateSelectedListener);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    private int mYear, mMonth, mDay;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mYear=EditBlipDetails.mYear;
        mMonth=EditBlipDetails.mMonth;
        mDay=EditBlipDetails.mDay;
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.date_dialog, container, false);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        final SwitchCompat switchCompat = (SwitchCompat) rootView.findViewById(R.id.sw_never);
        final CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.cv_date_chooser);
        final View calendarViewDisabler = rootView.findViewById(R.id.v_date_chooser_disabler);
        long curDate = calendarView.getDate();
        // 1964 issue workaround
        calendarView.setMinDate(curDate - 3600000);
        int firstDayOfWeek = calendarView.getFirstDayOfWeek();
        calendarView.setDate(curDate + 1000L * 60 * 60 * 24 * 40);
        calendarView.setFirstDayOfWeek((firstDayOfWeek + 1) % 7);
        calendarView.setDate(curDate);
        calendarView.setFirstDayOfWeek(firstDayOfWeek);
        long millis = -1;
        if (getArguments() != null && getArguments().containsKey("millis")) {
            millis = getArguments().getLong("millis");
        }

        if (millis >= curDate) {
            calendarView.setDate(millis, false, true);
            calendarViewDisabler.setVisibility(View.GONE);
        } else {
            switchCompat.setChecked(true);
            calendarViewDisabler.setVisibility(View.VISIBLE);
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                mYear = year;
                mMonth = month + 1;
                mDay = dayOfMonth;
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) calendarViewDisabler.setVisibility(View.VISIBLE);
                else calendarViewDisabler.setVisibility(View.GONE);
            }
        });
        rootView.findViewById(R.id.btn_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Checked", "" + switchCompat.isChecked());
                if (onDateSelectedListener != null) {
                    if (switchCompat.isChecked()) {
                        Log.e("Checked1", "" + switchCompat.isChecked());
                        onDateSelectedListener.onDateChosen(-1, -1, -1);
                    } else {
                        Log.e("Checked2", "" + mYear + mMonth + mDay);
                        onDateSelectedListener.onDateChosen(mYear, mMonth, mDay);
                    }
                }
                dismiss();
            }
        });

        return rootView;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    OnDateSelectedListener onDateSelectedListener;

    public interface OnDateSelectedListener {
        void onDateChosen(int year, int month, int day);
    }
}
