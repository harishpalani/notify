package com.hp.notify.ui.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.hp.notify.R;
import com.hp.notify.utilities.EventDecorator;
import com.hp.notify.utilities.NotificationPublisher;
import com.hp.notify.utilities.custom.RangeTimePickerDialog;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;

import java.util.Calendar;
import java.util.HashSet;

public class ScheduleFragment extends Fragment {

    // Constants
    public static final String TAG = ScheduleFragment.class.getSimpleName();

    // Instance variables
    private static int mID = 0;
    private boolean mIs24HourView = false;
    private Calendar mCalendar;

    private int color = Color.WHITE;
    private HashSet<CalendarDay> dates = new HashSet<CalendarDay>();

    // Static variables
    private static EventDecorator sDecorator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        // findViewById for Fragment elements
        final EditText titleEdit = (EditText) rootView.findViewById(R.id.titleEdit);
        final EditText messageEdit = (EditText) rootView.findViewById(R.id.messageEdit);
        FloatingActionButton scheduleNotificationFAB = (FloatingActionButton) rootView.findViewById(R.id.scheduleNotificationFAB);
        AppCompatButton sendNowButton = (AppCompatButton) rootView.findViewById(R.id.sendNowButton);

        // Set up sendNowButton
        sendNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEdit.getText().toString();
                String message = messageEdit.getText().toString();

                Notification.Builder builder = new Notification.Builder(getActivity());
                    builder.setContentTitle(title);
                    builder.setContentText(message);
                    builder.setDefaults(Notification.DEFAULT_VIBRATE);
                    builder.setSmallIcon(R.drawable.ic_check_white);

                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(mID, builder.build());
                mID++;
            }
        });

        // Set up FAB
        scheduleNotificationFAB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(getString(R.string.colorPrimaryDark))));
        scheduleNotificationFAB.setOnClickListener(new View.OnClickListener() {

            Calendar date = Calendar.getInstance();
            DatePickerDialog datePickerDialog;
            RangeTimePickerDialog timePickerDialog;
            RangeTimePickerDialog.OnTimeSetListener onTimeSetListener = new RangeTimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    date.set(Calendar.MINUTE, minute);
                    date.set(Calendar.SECOND, 0);

                    // Send notifications
                    String title = titleEdit.getText().toString();
                    String message = messageEdit.getText().toString();

                    Log.i(TAG, String.valueOf(date.getTimeInMillis()));
                    Log.i(TAG, date.getTime().toString());

                    scheduleNotification(getNotification(title, message, date), date);

                    String ampm;

                    if (date.get(Calendar.AM_PM) == 0) {
                        ampm = " AM";
                    } else {
                        ampm = " PM";
                    }

                    // Decorate MaterialCalendarView in timeline fragment
                    CalendarDay calendarDay = CalendarDay.from(date);
                    dates.add(calendarDay);
                    sDecorator = new EventDecorator(color, dates);
                    TimelineFragment.getCalendarView().addDecorator(sDecorator);

                    // Let the user know WHEN they've just set a notification for
                    String notificationInfo = "Notification set for " + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE) + ampm
                            + " on " + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH) + "/" + date.get(Calendar.YEAR);
                    Snackbar.make(getView(), notificationInfo, Snackbar.LENGTH_LONG).show();
                    // Toast.makeText(getActivity(), notificationInfo, Toast.LENGTH_LONG).show();
                }
            };

            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    date.set(Calendar.YEAR, year);
                    date.set(Calendar.MONTH, monthOfYear);
                    date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    mCalendar = Calendar.getInstance();
                    timePickerDialog = new RangeTimePickerDialog(getActivity(), onTimeSetListener, mCalendar.get(Calendar.HOUR_OF_DAY),
                            mCalendar.get(Calendar.MINUTE), mIs24HourView);
                    timePickerDialog.setMin(mCalendar.getInstance().get(Calendar.HOUR_OF_DAY), mCalendar.getInstance().get(Calendar.MINUTE));
                    timePickerDialog.show();
                }
            };

            @Override
            public void onClick(View view) {
                mCalendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(getActivity(), onDateSetListener, mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(mCalendar.getInstance().getTimeInMillis());
                datePickerDialog.setTitle("");

                datePickerDialog.show();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    // Notification methods
    private void scheduleNotification(Notification notification, Calendar date) {
        Intent notificationIntent = new Intent(getContext(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, mID);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), mID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mID++;

        long scheduledTimeInMillis = date.getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTimeInMillis, pendingIntent);
    }

    private Notification getNotification(String title, String message, Calendar date) {
        Notification.Builder builder = new Notification.Builder(getContext());
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSmallIcon(R.drawable.ic_check_white);
        builder.setWhen(date.getTimeInMillis());
        return builder.build();
    }

    // Getters and setters
    public static EventDecorator getDecorator() {
        return sDecorator;
    }

    public static void setDecorator(EventDecorator decorator) {
        sDecorator = decorator;
    }

   /* public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public HashSet<CalendarDay> getDates() {
        return dates;
    }

    public void setDates(HashSet<CalendarDay> dates) {
        this.dates = dates;
    }*/
}