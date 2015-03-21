package com.i906.mpt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.adapter.PrayerListAdapter;
import com.i906.mpt.extension.PrayerInterface;
import com.i906.mpt.extension.PrayerView;
import com.linearlistview.LinearListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DefaultPrayerView extends PrayerView {

    protected SimpleDateFormat mDateFormatter;
    protected String[] mPrayerNames;
    protected String[] mHijriNames;
    protected PrayerListAdapter mAdapter;

    protected TextView mMainTimeView;
    protected TextView mMainPrayerView;
    protected TextView mLocationView;
    protected TextView mDateView;
    protected LinearListView mPrayerListView;

    public DefaultPrayerView(Context context) {
        this(context, null);
    }

    public DefaultPrayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultPrayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_default_prayer, this, true);

        mDateFormatter = new SimpleDateFormat("hh:mm");
        mPrayerNames = getResources().getStringArray(R.array.mpt_prayer_names);
        mHijriNames = getResources().getStringArray(R.array.mpt_hijri_dates);

        mMainTimeView = (TextView) findViewById(R.id.tv_prayer_time);
        mMainPrayerView = (TextView) findViewById(R.id.tv_prayer_name);
        mLocationView = (TextView) findViewById(R.id.tv_location);
        mDateView = (TextView) findViewById(R.id.tv_date);
        mPrayerListView = (LinearListView) findViewById(R.id.list_prayer);
    }

    private void updatePrayerHeader() {
        List<Date> times = getInterface().getCurrentDayPrayerTimes();
        Date nextTime = getInterface().getNextPrayerTime();
        int currentIndex = getInterface().getCurrentPrayerIndex();
        int nextIndex = getInterface().getNextPrayerIndex();
        String location = getInterface().getLocation();
        int[] hijri = getInterface().getHijriDate();

        String date = String.format("%s %s %s", hijri[PrayerInterface.DATE_DATE],
                mHijriNames[hijri[PrayerInterface.DATE_MONTH]], hijri[PrayerInterface.DATE_YEAR]);

        if (mAdapter == null) {
            mAdapter = new PrayerListAdapter(times, mPrayerNames);
        }

        mAdapter.setHighlightedIndex(currentIndex);

        mMainTimeView.setText(mDateFormatter.format(nextTime));
        mMainPrayerView.setText(mPrayerNames[nextIndex]);
        mLocationView.setText(location);
        mDateView.setText(date);
        mPrayerListView.setAdapter(mAdapter);
    }

    @Override
    public void onInterfaceLoaded() {
        updatePrayerHeader();
    }
}
