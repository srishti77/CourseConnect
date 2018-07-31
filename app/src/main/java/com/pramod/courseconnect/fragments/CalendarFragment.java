package com.pramod.courseconnect.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.CCMainActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CalendarFragment extends Fragment
        implements IUIFragment, OnDateSelectedListener, OnMonthChangedListener {

    @BindView(R.id.calendarView)
    MaterialCalendarView calendarView;
    //this gives the mainactivity the current selected date
    public static CalendarDay calendarDay;
    public static CalendarDay today;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, rootView);
        calendarView.setOnDateChangedListener(this);
        setInitialCalendarSettings(rootView);
        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {

                return today != null && day.equals(today);
            }

            @Override
            public void decorate(DayViewFacade view) {
                Drawable highlightDrawable = getResources().getDrawable(R.drawable.ic_brightness_1_black_24dp);
                view.setBackgroundDrawable(highlightDrawable);
                view.addSpan(new ForegroundColorSpan(Color.WHITE));
              }
        });

        return rootView;
    }

    private void setInitialCalendarSettings(ViewGroup rootView) {
        calendarView.setDateSelected(Calendar.getInstance().getTime(), true);
        today = calendarView.getSelectedDate();
        calendarDay = calendarView.getSelectedDate();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        calendarDay = date;

        ((CCMainActivity)getActivity()).viewEvents();

   }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
    }


}