package com.i906.mpt.view;

import android.content.Context;
import android.text.format.DateFormat;
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

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DefaultPrayerView extends PrayerView {

    private final static String FORMAT_24 = "kk:mm";
    private final static String FORMAT_12 = "hh:mm";

    protected String[] mPrayerNames;
    protected String[] mHijriNames;
    protected PrayerListAdapter mAdapter;
    protected String mDateFormat;

    @Bind(R.id.content_container)
    protected View mContentView;

    @Bind(R.id.progress_container)
    protected View mProgressView;

    @Bind(R.id.error_container)
    protected View mErrorView;

    @Bind(R.id.tv_prayer_time)
    protected TextView mMainTimeView;

    @Bind(R.id.tv_prayer_name)
    protected TextView mMainPrayerView;

    @Bind(R.id.tv_location)
    protected TextView mLocationView;

    @Bind(R.id.tv_date)
    protected TextView mDateView;

    @Bind(R.id.tv_error)
    protected TextView mErrorMessageView;

    @Bind(R.id.list_prayer)
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
        ButterKnife.bind(this);

        mPrayerNames = getResources().getStringArray(R.array.mpt_prayer_names);
        mHijriNames = getResources().getStringArray(R.array.mpt_hijri_months);
        mDateFormat = DateFormat.is24HourFormat(context) ? FORMAT_24 : FORMAT_12;
    }

    private void updatePrayerHeader() {
        List<Date> times = getInterface().getCurrentDayPrayerTimes();
        Date nextTime = getInterface().getNextPrayerTime();
        int currentIndex = getInterface().getCurrentPrayerIndex();
        int nextIndex = getInterface().getNextPrayerIndex();
        String location = getInterface().getLocation();
        List<Integer> hijri = getInterface().getHijriDate();

        String date = getResources().getString(R.string.label_date, hijri.get(PrayerInterface.DATE_DAY),
                mHijriNames[hijri.get(PrayerInterface.DATE_MONTH)], hijri.get(PrayerInterface.DATE_YEAR));

        if (mAdapter == null) {
            mAdapter = new PrayerListAdapter(times, mPrayerNames, mDateFormat);
        }

        mAdapter.setHighlightedIndex(currentIndex);

        mMainTimeView.setText(getFormattedDate(nextTime));
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
        setErrorVisibility(false, true);
    }

    @Override
    public void onError(int type, String code) {
        setContentVisibility(false, true);
        setProgressVisibility(false, true);
        setErrorVisibility(true, true);

        switch (type) {
            case PrayerInterface.ERROR_NETWORK:
                mErrorMessageView.setText(R.string.mpt_error_no_network);
                break;
            case PrayerInterface.ERROR_LOCATION:
                if (PrayerInterface.LOCATION_ERROR_ADDRESS.equals(code) ||
                        PrayerInterface.LOCATION_ERROR_PLACE.equals(code)) {
                    mErrorMessageView.setText(R.string.mpt_error_undetectable_location);
                } else {
                    mErrorMessageView.setText(R.string.mpt_error_no_location);
                }
                break;
            case PrayerInterface.ERROR_PLAY_SERVICES:
                mErrorMessageView.setText(R.string.mpt_error_play_service);
                break;
            default:
                mErrorMessageView.setText(R.string.mpt_error_unexpected);
        }
    }

    private String getFormattedDate(Date date) {
        return DateFormat.format(mDateFormat, date).toString();
    }
}
