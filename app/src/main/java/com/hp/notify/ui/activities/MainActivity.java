package com.hp.notify.ui.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hp.notify.ui.fragments.ScheduleFragment;
import com.hp.notify.utilities.NotificationPublisher;
import com.hp.notify.R;
import com.hp.notify.utilities.custom.SectionsPagerAdapter;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @Bind(R.id.container)
    ViewPager mViewPager;

    // Layout components
    @Bind(R.id.mainLayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.tabs)
    TabLayout mTabLayout;

    // Instance variables
    private int mID = 0;
    private boolean mIs24HourView = false;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Set up toolbar
        setSupportActionBar(mToolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up tabs
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).select();

        // Set up FAB

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Notification methods
    private void scheduleNotification(Notification notification, Calendar date) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, mID);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, mID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mID++;

        long scheduledTimeInMillis = date.getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTimeInMillis, pendingIntent);
    }

    private Notification getNotification(String title, String message, Calendar date) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSmallIcon(R.drawable.ic_check_white);
        builder.setWhen(date.getTimeInMillis());
        return builder.build();
    }
}
