package com.i906.mpt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.adapter.PrayerListAdapter;
import com.i906.mpt.extension.PrayerInterface;
import com.i906.mpt.extension.PrayerView;
import com.linearlistview.LinearListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DefaultPrayerView extends PrayerView {

    protected SimpleDateFormat mDateFormatter;
    protected String[] mPrayerNames;
    protected String[] mHijriNames;
    protected PrayerListAdapter mAdapter;

    @InjectView(R.id.content_container)
    protected View mContentView;

    @InjectView(R.id.progress_container)
    protected View mProgressView;

    @InjectView(R.id.error_container)
    protected View mErrorView;

    @InjectView(R.id.tv_prayer_time)
    protected TextView mMainTimeView;

    @InjectView(R.id.tv_prayer_name)
    protected TextView mMainPrayerView;

    @InjectView(R.id.tv_location)
    protected TextView mLocationView;

    @InjectView(R.id.tv_date)
    protected TextView mDateView;

    @InjectView(R.id.tv_error)
    protected TextView mErrorMessageView;

    @InjectView(R.id.list_prayer)
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
        ButterKnife.inject(this);

        mDateFormatter = new SimpleDateFormat("hh:mm");
        mPrayerNames = getResources().getStringArray(R.array.mpt_prayer_names);
        mHijriNames = getResources().getStringArray(R.array.mpt_hijri_dates);
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

    private void setViewVisibility(View view, boolean visible, boolean animate) {
        if (view.getVisibility() == View.VISIBLE && visible) return;
        if (view.getVisibility() == View.GONE && !visible) return;

        if (visible) {
            if (animate) {
                view.startAnimation(
                        AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
            view.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                view.startAnimation(
                        AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            } else {
                view.clearAnimation();
            }
            view.setVisibility(View.GONE);
        }
    }

    private void setContentVisibility(boolean visible, boolean animate) {
        setViewVisibility(mContentView, visible, animate);
    }

    private void setProgressVisibility(boolean visible, boolean animate) {
        setViewVisibility(mProgressView, visible, animate);
    }

    private void setErrorVisibility(boolean visible, boolean animate) {
        setViewVisibility(mErrorView, visible, animate);
    }

    @OnClick(R.id.btn_retry)
    protected void onRetryButtonClicked() {
        setContentVisibility(false, true);
        setErrorVisibility(false, true);
        setProgressVisibility(true, true);
        getInterface().refresh();
    }

    @Override
    public void onInterfaceLoaded() {
        if (getInterface().isPrayerTimesLoaded()) {
            setContentVisibility(true, true);
            setProgressVisibility(false, true);
            setErrorVisibility(false, true);
            updatePrayerHeader();
        } else {
            setContentVisibility(false, false);
            setProgressVisibility(true, false);
            setErrorVisibility(false, false);
        }
    }

    @Override
    public void onPrayerTimesChanged() {
        updatePrayerHeader();
        setContentVisibility(true, true);
        setProgressVisibility(false, true);
    }

    @Override
    public void onError(int type, String code) {
        setContentVisibility(false, true);
        setProgressVisibility(false, true);
        setErrorVisibility(true, true);

        switch (type) {
            case PrayerInterface.ERROR_NETWORK:
                mErrorMessageView.setText(R.string.error_no_network);
                break;
            case PrayerInterface.ERROR_LOCATION:
                if ("ERROR_ADDRESS".equals(code) || "ERROR_PLACE".equals(code)) {
                    mErrorMessageView.setText(R.string.error_undetectable_location);
                } else {
                    mErrorMessageView.setText(R.string.error_no_location);
                }
                break;
            default:
                mErrorMessageView.setText(R.string.error_unexpected);
        }
    }
}
