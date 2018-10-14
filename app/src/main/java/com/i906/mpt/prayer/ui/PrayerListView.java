package com.i906.mpt.prayer.ui;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.prayer.Prayer;
import com.i906.mpt.prayer.PrayerContext;
import com.linearlistview.LinearListView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PrayerListView extends FrameLayout implements PrayerView {

    private final static String FORMAT_24 = "kk:mm";
    private final static String FORMAT_12 = "hh:mm";
    private final static String FORMAT_12_AMPM = "hh:mm a";

    private PrayerListAdapter mAdapter;
    private String mDateFormat;
    private PrayerContext mPrayerContext;

    @BindView(R.id.tv_prayer_time)
    TextView mMainTimeView;

    @BindView(R.id.tv_prayer_ampm)
    TextView mMainTimeAmPm;

    @BindView(R.id.tv_prayer_name)
    TextView mMainPrayerView;

    @BindView(R.id.tv_location)
    TextView mLocationView;

    @BindView(R.id.tv_date)
    TextView mDateView;

    @BindView(R.id.list_prayer)
    LinearListView mPrayerListView;

    @BindArray(R.array.prayer_names)
    String[] mPrayerNames;

    @BindArray(R.array.hijri_months)
    String[] mHijriNames;

    @BindArray(R.array.masihi_months)
    String[] mMasihiNames;

    public PrayerListView(Context context) {
        this(context, null);
    }

    public PrayerListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrayerListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_default_prayer, this, true);
        ButterKnife.bind(this);

        mDateFormat = DateFormat.is24HourFormat(context) ? FORMAT_24 : FORMAT_12;
    }

    @Override
    public void showPrayerContext(PrayerContext prayerContext) {
        mPrayerContext = prayerContext;
        updatePrayerHeader();
    }

    private void updatePrayerHeader() {
        List<Prayer> times = mPrayerContext.getCurrentPrayerList();
        Date currentTime = mPrayerContext.getCurrentPrayer().getDate();
        Date nextTime = mPrayerContext.getNextPrayer().getDate();

        int currentIndex = mPrayerContext.getCurrentPrayer().getIndex();
        int nextIndex = mPrayerContext.getNextPrayer().getIndex();

        String location = mPrayerContext.getLocationName();
        List<Integer> hijriDate = mPrayerContext.getHijriDate();
        PrayerContext.ViewSettings settings = mPrayerContext.getViewSettings();

        boolean showMasihi = settings.isMasihiDateEnabled();
        boolean showHijri = settings.isHijriDateEnabled();
        boolean showAMPM = settings.isAmPmEnabled();

        Resources r = getResources();
        Calendar c = Calendar.getInstance();
        c.setTime(currentTime);

        String date = null;
        String hijri = r.getString(R.string.label_date,
                hijriDate.get(0),
                mHijriNames[hijriDate.get(1)],
                hijriDate.get(2)
        );
        String masihi = r.getString(R.string.label_date,
                c.get(Calendar.DATE),
                mMasihiNames[c.get(Calendar.MONTH)],
                c.get(Calendar.YEAR)
        );

        String format12 = showAMPM ? FORMAT_12_AMPM : FORMAT_12;
        String dateFormat = DateFormat.is24HourFormat(getContext()) ? FORMAT_24 : format12;

        if (showHijri && showMasihi) {
            date = r.getString(R.string.label_date_combined, hijri, masihi);
        } else if (showHijri) {
            date = hijri;
        } else if (showMasihi) {
            date = masihi;
        }

        if (mAdapter == null) {
            mAdapter = new PrayerListAdapter(mPrayerNames);
        }

        mAdapter.setViewSettings(settings);
        mAdapter.setPrayerList(times);
        mAdapter.setHighlightedIndex(currentIndex);
        mAdapter.setDateFormat(dateFormat);

        mMainTimeView.setText(getFormattedDate(nextTime));
        mMainPrayerView.setText(mPrayerNames[nextIndex]);
        mLocationView.setText(location);
        mPrayerListView.setAdapter(mAdapter);

        mMainTimeAmPm.setText(getAmPm(nextTime));
        mMainTimeAmPm.setVisibility(showAMPM ? VISIBLE : GONE);

        if (date != null) {
            mDateView.setText(date);
            mDateView.setVisibility(VISIBLE);
        } else {
            mDateView.setVisibility(GONE);
        }

        long ttnp = getTimeToNextPrayer();

        if (ttnp < 0) {
            return;
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePrayerHeader();
            }
        }, ttnp);
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
    }

    private String getFormattedDate(Date date) {
        return DateFormat.format(mDateFormat, date).toString();
    }

    private String getAmPm(Date date) {
        return DateFormat.format("a", date).toString();
    }
}
