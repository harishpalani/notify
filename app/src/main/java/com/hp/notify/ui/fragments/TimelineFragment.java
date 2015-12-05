package com.hp.notify.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hp.notify.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

public class TimelineFragment extends Fragment {

    // Layout components
    private static MaterialCalendarView sCalendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        // Layout components
        sCalendarView = (MaterialCalendarView) rootView.findViewById(R.id.timelineCalendarView);

        // Customize CalendarView
        sCalendarView.setDateTextAppearance(R.style.CustomDateTextAppearance);
        sCalendarView.setWeekDayTextAppearance(R.style.CustomWeekdayTextAppearance);
        sCalendarView.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance);
        sCalendarView.setArrowColor(android.R.color.white);

        sCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                // show event details
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(date.getMonth() + "/" + date.getDay() + "/" + date.getYear());


                AlertDialog dialog = builder.create();
                dialog.setView(dialog.getLayoutInflater().inflate(R.layout.custom_events_dialog, null));
                dialog.show();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // Getters and setters
    public static MaterialCalendarView getCalendarView() {
        return sCalendarView;
    }

    public static void setCalendarView(MaterialCalendarView calendarView) {
        sCalendarView = calendarView;
    }
}
