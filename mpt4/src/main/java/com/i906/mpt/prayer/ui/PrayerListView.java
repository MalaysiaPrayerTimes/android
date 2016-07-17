package com.i906.mpt.prayer.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.i906.mpt.R;
import com.i906.mpt.prayer.Prayer;
import com.i906.mpt.prayer.PrayerContext;
import com.linearlistview.LinearListView;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.charmas.android.reactivelocation.observables.GoogleAPIConnectionException;

public class PrayerListView extends ViewFlipper implements PrayerView {

    private final static String FORMAT_24 = "kk:mm";
    private final static String FORMAT_12 = "hh:mm";

    private String[] mPrayerNames;
    private String[] mHijriNames;
    private PrayerListAdapter mAdapter;
    private String mDateFormat;
    private PrayerContext mPrayerContext;
    private SwipeRefreshLayout.OnRefreshListener mListener;

    @BindView(R.id.content_container)
    View mContentView;

    @BindView(R.id.progress_container)
    View mProgressView;

    @BindView(R.id.error_container)
    View mErrorView;

    @BindView(R.id.tv_prayer_time)
    TextView mMainTimeView;

    @BindView(R.id.tv_prayer_name)
    TextView mMainPrayerView;

    @BindView(R.id.tv_location)
    TextView mLocationView;

    @BindView(R.id.tv_date)
    TextView mDateView;

    @BindView(R.id.tv_error)
    TextView mErrorMessageView;

    @BindView(R.id.list_prayer)
    LinearListView mPrayerListView;

    public PrayerListView(Context context) {
        this(context, null);
    }

    public PrayerListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrayerListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_default_prayer, this, true);
        ButterKnife.bind(this);

        mPrayerNames = getResources().getStringArray(R.array.prayer_names);
        mHijriNames = getResources().getStringArray(R.array.hijri_months);
        mDateFormat = DateFormat.is24HourFormat(context) ? FORMAT_24 : FORMAT_12;
        setDisplayedChild(1);
    }

    @Override
    public void showPrayerContext(PrayerContext prayerContext) {
        mPrayerContext = prayerContext;
        updatePrayerHeader();
    }

    private void updatePrayerHeader() {
        List<Prayer> times = mPrayerContext.getCurrentPrayerList();
        Date nextTime = mPrayerContext.getNextPrayer().getDate();
        int currentIndex = mPrayerContext.getCurrentPrayer().getIndex();
        int nextIndex = mPrayerContext.getNextPrayer().getIndex();
        String location = mPrayerContext.getLocationName();
        List<Integer> hijri = mPrayerContext.getHijriDate();

        String date = getResources().getString(R.string.label_date, hijri.get(0),
                mHijriNames[hijri.get(1)], hijri.get(2));

        if (mAdapter == null) {
            mAdapter = new PrayerListAdapter(times, mPrayerNames, mDateFormat);
        }

        mAdapter.setHighlightedIndex(currentIndex);

        mMainTimeView.setText(getFormattedDate(nextTime));
        mMainPrayerView.setText(mPrayerNames[nextIndex]);
        mLocationView.setText(location);
        mDateView.setText(date);
        mPrayerListView.setAdapter(mAdapter);

        setDisplayedChild(0);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePrayerHeader();
            }
        }, getTimeToNextPrayer());
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mListener = listener;
    }

    @OnClick(R.id.btn_retry)
    protected void onRetryButtonClicked() {
        setDisplayedChild(1);
        if (mListener != null) {
            mListener.onRefresh();
        }
    }

    private long getTimeToNextPrayer() {
        long next = mPrayerContext.getNextPrayer().getDate().getTime();
        return next - System.currentTimeMillis();
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void showError(Throwable e) {
        setDisplayedChild(2);

        if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
            mErrorMessageView.setText(R.string.error_no_network);
        } else if (e instanceof GoogleAPIConnectionException) {
            mErrorMessageView.setText(R.string.error_play_service);
        } else {
            mErrorMessageView.setText(R.string.error_unexpected);
        }
    }

    private String getFormattedDate(Date date) {
        return DateFormat.format(mDateFormat, date).toString();
    }
}
