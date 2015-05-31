package com.i906.mpt.provider;

import android.support.annotation.Nullable;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.extension.PrayerInterface;
import com.i906.mpt.model.PrayerData;
import com.i906.mpt.util.DateTimeHelper;
import com.i906.mpt.util.GeocoderHelper;
import com.i906.mpt.util.PrayerHelper;
import com.i906.mpt.util.preference.GeneralPrefs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class MptInterface implements PrayerInterface {

    protected DateTimeHelper mDateTimeHelper;
    protected PrayerHelper mPrayerHelper;
    protected List<PrayerListener> mPrayerListeners;
    protected MptListener mMptListener;

    protected PrayerData mPrayerData;
    protected PrayerData mNextPrayerData;
    protected GeocoderHelper mGeocoderHelper;

    protected GeneralPrefs mPrefs;

    @Inject
    public MptInterface(DateTimeHelper h1, PrayerHelper h2, GeocoderHelper h3, GeneralPrefs p) {
        mDateTimeHelper = h1;
        mPrayerHelper = h2;
        mGeocoderHelper = h3;
        mPrefs = p;
        refresh();
        Timber.tag("mpt-PrayerInterface");
    }

    @Nullable
    @Override
    public Date getCurrentPrayerTime() {
        return getCurrentDayPrayerTime(getCurrentPrayerIndex());
    }

    @Nullable
    @Override
    public Date getNextPrayerTime() {
        int index = getNextPrayerIndex();

        if (index == 0 && subuhPassed()) {
            return getNextDayPrayerTime(index);
        } else {
            return getCurrentDayPrayerTime(index);
        }
    }

    @Override
    public int getCurrentPrayerIndex() {
        int pos = 0;
        Calendar c = mDateTimeHelper.getNewCalendarInstance();
        Calendar n = mDateTimeHelper.getNewCalendarInstance();

        List<Date> pt = getCurrentDayPrayerTimes();
        if (pt == null) return -1;

        for (int i = 0; i < pt.size(); i++) {
            n.setTime(pt.get(i));
            if (n.after(c)) break;
            pos++;
        }

        return (pos - 1 == -1) ? 7 : pos - 1;
    }

    @Override
    public int getNextPrayerIndex() {
        int cpi = getCurrentPrayerIndex();
        if (cpi == -1) return -1;
        return (cpi + 1) % 8;
    }

    @Nullable
    @Override
    public List<Date> getCurrentDayPrayerTimes() {
        if (mPrayerData == null) return null;
        return mPrayerData.getPrayerTimes().get(mDateTimeHelper.getCurrentDate() - 1);
    }

    @Nullable
    private Date getCurrentDayPrayerTime(int index) {
        List<Date> cdpt = getCurrentDayPrayerTimes();
        if (cdpt == null) return null;
        return cdpt.get(index);
    }

    public List<Date> getNextDayPrayerTimes() {
        if (!mDateTimeHelper.isTommorowNewMonth()) {
            return mPrayerData.getPrayerTimes().get(mDateTimeHelper.getNextDate() - 1);
        } else {
            return mNextPrayerData.getPrayerTimes().get(0);
        }
    }

    private Date getNextDayPrayerTime(int index) {
        return getNextDayPrayerTimes().get(index);
    }

    @Override
    public int[] getHijriDate() {
        return new int[] { 22, 4, 1436 } ;
    }

    @Override
    public String getLocation() {
        return mPrayerData.getLocation();
    }

    @Override
    public int getAppVersion() {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    public void refresh() {
        Observable.zip(mPrayerHelper.getPrayerData(), mPrayerHelper.getNextPrayerData(), PrayerResult::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PrayerResult>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleError(e);
                    }

                    @Override
                    public void onNext(PrayerResult result) {
                        mPrayerData = result.current;
                        mNextPrayerData = result.next;
                        onPrayerTimesChanged();
                    }
                });
    }

    @Override
    public void addPrayerListener(PrayerInterface.PrayerListener listener) {
        if (mPrayerListeners == null) {
            mPrayerListeners = new ArrayList<>();
        }
        mPrayerListeners.add(listener);
    }

    @Override
    public void removePrayerListener(PrayerInterface.PrayerListener listener) {
        if (mPrayerListeners != null) {
            mPrayerListeners.remove(listener);
        }
    }

    public void setMptListener(MptListener listener) {
        mMptListener = listener;
    }

    protected void onPrayerTimesChanged() {
        if (mPrayerListeners != null) {
            for (int i = mPrayerListeners.size() - 1; i >= 0; i--) {
                mPrayerListeners.get(i).onPrayerTimesChanged();
            }
        }
    }

    protected void onError(int type, String code) {
        if (mPrayerListeners != null) {
            for (int i = mPrayerListeners.size() - 1; i >= 0; i--) {
                mPrayerListeners.get(i).onError(type, code);
            }
        }
    }

    protected void handleError(Throwable e) {
        e.printStackTrace();
        if (e instanceof RetrofitError) {
            RetrofitError error = (RetrofitError) e;
            switch (error.getKind()) {
                case NETWORK:
                    onError(ERROR_NETWORK, null);
                    break;
                case CONVERSION:
                    onError(ERROR_CONVERSION, null);
                    break;
                case HTTP:
                    onError(ERROR_HTTP, null);
                    break;
                default:
                   onError(ERROR_OTHER, null);
            }
        } else if (e instanceof GeocoderHelper.GeocoderError) {
            if (e instanceof GeocoderHelper.EmptyPlaceError) {
                onError(ERROR_LOCATION, "EMPTY_PLACE");
            } else if (e instanceof GeocoderHelper.EmptyAddressError) {
                onError(ERROR_LOCATION, "EMPTY_ADDRESS");
            } else {
                onError(ERROR_NETWORK, "ERROR_GEOCODER");
            }
        } else {
            onError(ERROR_OTHER, null);
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPrayerTimesLoaded() {
        return mPrayerData != null;
    }

    @Override
    public void onPrayerExtensionCrashed(Throwable t) {
        mPrefs.resetSelectedPrayerView();
        if (mMptListener != null) mMptListener.onPrayerExtensionCrashed(t);
        Timber.e(t, "Extension has crashed.");
    }

    private boolean subuhPassed() {
        Calendar n = mDateTimeHelper.getNewCalendarInstance();
        Calendar s = mDateTimeHelper.getNewCalendarInstance();

        s.setTime(getCurrentDayPrayerTimes().get(PRAYER_SUBUH));
        return n.after(s);
    }

    public interface MptListener {
        void onPrayerExtensionCrashed(Throwable t);
    }

    protected static class PrayerResult {
        PrayerData current;
        PrayerData next;

        public PrayerResult(PrayerData current, PrayerData next) {
            this.current = current;
            this.next = next;
        }
    }
}
