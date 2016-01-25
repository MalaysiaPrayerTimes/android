package com.i906.mpt.provider;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.gson.stream.MalformedJsonException;
import com.i906.mpt.BuildConfig;
import com.i906.mpt.extension.PrayerInterface;
import com.i906.mpt.model.PrayerData;
import com.i906.mpt.service.AlarmSetupService;
import com.i906.mpt.util.DateTimeHelper;
import com.i906.mpt.util.GeocoderHelper;
import com.i906.mpt.util.LocationHelper;
import com.i906.mpt.util.PrayerHelper;
import com.i906.mpt.util.Utils;
import com.i906.mpt.util.preference.GeneralPrefs;
import com.i906.mpt.util.preference.NotificationPrefs;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.charmas.android.reactivelocation.observables.GoogleAPIConnectionException;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

@Singleton
public class MptInterface implements PrayerInterface {

    protected DateTimeHelper mDateTimeHelper;
    protected PrayerHelper mPrayerHelper;
    protected List<PrayerListener> mPrayerListeners;
    protected MptListener mMptListener;

    protected PrayerData mPrayerData;
    protected PrayerData mNextPrayerData;
    protected LocationHelper mLocationHelper;

    protected Context mContext;
    protected GeneralPrefs mPrefs;
    protected NotificationPrefs mNotificationPrefs;
    protected Date mLastRefreshed;
    protected Location mLastLocation;

    @Inject
    public MptInterface(Context context, DateTimeHelper h1, PrayerHelper h2, LocationHelper h4,
                        GeneralPrefs p, NotificationPrefs np) {
        mContext = context;
        mDateTimeHelper = h1;
        mPrayerHelper = h2;
        mLocationHelper = h4;
        mPrefs = p;
        mNotificationPrefs = np;
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

        for (int i = 0; i < 8; i++) {
            if (!prayerHasPassed(i)) break;
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
    public List<Integer> getHijriDate() {
        return mDateTimeHelper.getHijriDate(maghribPassed());
    }

    @Nullable
    @Override
    public String getLocation() {
        if (mPrayerData == null) return null;
        return mPrayerData.getLocation();
    }

    @Override
    public int getAppVersion() {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    public void refresh() {
        Timber.d("Refreshing prayer data.");
        mLocationHelper.getLocation()
                .doOnNext(this::updateLastLocation)
                .flatMap(location -> Observable.concat(
                        mPrayerHelper.getPrayerData(location),
                        mPrayerHelper.getNextPrayerData(location)
                ))
                .toList()
                .compose(Utils.applySchedulers())
                .subscribe(new Subscriber<List<PrayerData>>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleError(e);
                    }

                    @Override
                    public void onNext(List<PrayerData> prayerDatas) {
                        mPrayerData = prayerDatas.get(0);
                        mNextPrayerData = prayerDatas.get(1);
                        updateLastRefreshed();
                        onPrayerTimesChanged();
                        AlarmSetupService.refreshAlarms(mContext);
                    }
                });
    }

    public void refreshBlocking() throws RuntimeException {
        Timber.d("Refreshing prayer data (blocking).");
        mLocationHelper.getLocation()
                .doOnNext(this::updateLastLocation)
                .flatMap(location -> Observable.concat(
                        mPrayerHelper.getPrayerData(location),
                        mPrayerHelper.getNextPrayerData(location)
                ))
                .toList()
                .toBlocking()
                .forEach(prayerDatas -> {
                    mPrayerData = prayerDatas.get(0);
                    mNextPrayerData = prayerDatas.get(1);
                    updateLastRefreshed();
                });
    }

    @Nullable
    public Location getLastLocation() {
        return mLastLocation;
    }

    @Nullable
    public Date getLastRefreshed() {
        return mLastRefreshed;
    }

    private void updateLastLocation(Location location) {
        mLastLocation = location;
        Timber.d("Received location: %s", location);
    }

    private void updateLastRefreshed() {
        mLastRefreshed = new Date();
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
        Timber.d("Prayer time has changed.");
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
        if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
            onError(ERROR_NETWORK, null);
            Timber.e(e, "A network error has occurred.");
        } else if (e instanceof MalformedJsonException) {
            onError(ERROR_CONVERSION, null);
            Timber.e(e, "A conversion error has occurred.");
        } else if (e instanceof retrofit.HttpException) {
            onError(ERROR_HTTP, null);
            Timber.e(e, "A HTTP error has occurred.");
        } else if (e instanceof GeocoderHelper.GeocoderError) {
            if (e instanceof GeocoderHelper.EmptyPlaceError) {
                onError(ERROR_LOCATION, LOCATION_ERROR_PLACE);
            } else if (e instanceof GeocoderHelper.EmptyAddressError) {
                onError(ERROR_LOCATION, LOCATION_ERROR_ADDRESS);
            } else {
                onError(ERROR_NETWORK, LOCATION_ERROR_GEOCODER);
            }
        } else if (e instanceof GoogleAPIConnectionException) {
            GoogleAPIConnectionException ge = (GoogleAPIConnectionException) e;
            ConnectionResult cr = ge.getConnectionResult();
            onError(ERROR_PLAY_SERVICES, "PLAY_SERVICE_ERROR_" + cr.getErrorCode());
            if (mMptListener != null) mMptListener.onPlayServiceResult(cr);
            Timber.e(e, "error " + "PLAY_SERVICE_ERROR_" + cr.getErrorCode());
        } else {
            onError(ERROR_OTHER, null);
            Timber.e(e, "An unexpected error has occurred.");
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
        return prayerHasPassed(PRAYER_SUBUH);
    }

    private boolean maghribPassed() {
        return prayerHasPassed(PRAYER_MAGRHIB);
    }

    @Override
    public boolean prayerHasPassed(int prayer) {
        Calendar n = mDateTimeHelper.getNow();
        Calendar s = mDateTimeHelper.getNewCalendarInstance();
        List<Date> cdpt = getCurrentDayPrayerTimes();

        if (cdpt != null) {
            s.setTime(cdpt.get(prayer));
            s.add(Calendar.MILLISECOND, (int) mNotificationPrefs.getAlarmOffset());
            return n.after(s);
        } else {
            return false;
        }
    }

    public interface MptListener {
        void onPrayerExtensionCrashed(Throwable t);
        void onPlayServiceResult(ConnectionResult cr);
    }
}
