package com.i906.mpt.prayer;

import android.location.Location;

import com.i906.mpt.api.prayer.EmptyPrayerData;
import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.api.prayer.PrayerData;
import com.i906.mpt.api.prayer.PrayerException;
import com.i906.mpt.date.DateTimeHelper;
import com.i906.mpt.prefs.InterfacePreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
class PrayerDownloader {

    private final InterfacePreferences mInterfacePreferences;
    private final DateTimeHelper mDateHelper;
    private final PrayerClient mPrayerClient;
    private final PrayerCacheManager mPrayerCache;

    @Inject
    PrayerDownloader(DateTimeHelper helper,
                     PrayerClient client,
                     PrayerCacheManager cache,
                     InterfacePreferences iprefs) {
        mDateHelper = helper;
        mPrayerClient = client;
        mPrayerCache = cache;
        mInterfacePreferences = iprefs;
    }

    Observable<PrayerContext> getPrayerTimes(String code) {
        return getCurrentPrayerTimesByCode(code)
                .zipWith(getNextPrayerTimesByCode(code), mPrayerContextCreator);
    }

    Observable<PrayerContext> getPrayerTimes(Location location) {
        return getCurrentPrayerTimesByCoordinate(location)
                .zipWith(getNextPrayerTimesByCoordinate(location), mPrayerContextCreator);
    }

    private Observable<PrayerData> getCurrentPrayerTimesByCoordinate(final Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        int year = mDateHelper.getCurrentYear();
        int month = mDateHelper.getCurrentMonth() + 1;

        Observable<PrayerData> cache = mPrayerCache.get(year, month, location)
                .doOnNext(new Action1<PrayerData>() {
                    @Override
                    public void call(PrayerData prayerData) {
                        Timber.v("Using cache for current prayer times coordinate");
                    }
                });

        Observable<PrayerData> api = mPrayerClient.getPrayerTimesByCoordinates(lat, lng, year, month)
                .doOnNext(new Action1<PrayerData>() {
                    @Override
                    public void call(PrayerData data) {
                        mPrayerCache.save(data, location);
                    }
                });

        return cache.switchIfEmpty(api);
    }

    private Observable<PrayerData> getNextPrayerTimesByCoordinate(final Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        int year = mDateHelper.getCurrentYear();
        int month = mDateHelper.getNextMonth() + 1;

        if (mDateHelper.isNextMonthNewYear()) {
            year = mDateHelper.getNextYear();
        }

        Observable<PrayerData> cache = mPrayerCache.get(year, month, location)
                .doOnNext(new Action1<PrayerData>() {
                    @Override
                    public void call(PrayerData prayerData) {
                        Timber.v("Using cache for next prayer times coordinate");
                    }
                });

        Observable<PrayerData> api = mPrayerClient.getPrayerTimesByCoordinates(lat, lng, year, month)
                .doOnNext(new Action1<PrayerData>() {
                    @Override
                    public void call(PrayerData data) {
                        mPrayerCache.save(data, location);
                    }
                })
                .onErrorResumeNext(mPrayerProviderErrorResume);

        return cache.switchIfEmpty(api);
    }

    private Observable<PrayerData> getCurrentPrayerTimesByCode(final String code) {
        int year = mDateHelper.getCurrentYear();
        int month = mDateHelper.getCurrentMonth() + 1;

        Observable<PrayerData> cache = mPrayerCache.get(year, month, code)
                .doOnNext(new Action1<PrayerData>() {
                    @Override
                    public void call(PrayerData prayerData) {
                        Timber.v("Using cache for current prayer times code");
                    }
                });

        Observable<PrayerData> api = mPrayerClient.getPrayerTimesByCode(code, year, month)
                .doOnNext(new Action1<PrayerData>() {
                    @Override
                    public void call(PrayerData data) {
                        mPrayerCache.save(data);
                    }
                });

        return cache.switchIfEmpty(api);
    }

    private Observable<PrayerData> getNextPrayerTimesByCode(final String code) {
        int year = mDateHelper.getCurrentYear();
        int month = mDateHelper.getNextMonth() + 1;

        if (mDateHelper.isNextMonthNewYear()) {
            year = mDateHelper.getNextYear();
        }

        Observable<PrayerData> cache = mPrayerCache.get(year, month, code)
                .doOnNext(new Action1<PrayerData>() {
                    @Override
                    public void call(PrayerData prayerData) {
                        Timber.v("Using cache for next prayer times code");
                    }
                });

        Observable<PrayerData> api = mPrayerClient.getPrayerTimesByCode(code, year, month)
                .doOnNext(new Action1<PrayerData>() {
                    @Override
                    public void call(PrayerData data) {
                        mPrayerCache.save(data);
                    }
                })
                .onErrorResumeNext(mPrayerProviderErrorResume);

        return cache.switchIfEmpty(api);
    }

    private final Func1<Throwable, Observable<? extends PrayerData>> mPrayerProviderErrorResume =
            new Func1<Throwable, Observable<? extends PrayerData>>() {
                @Override
                public Observable<? extends PrayerData> call(Throwable throwable) {
                    if (throwable instanceof PrayerException) {
                        PrayerException e = (PrayerException) throwable;
                        return Observable.just(new EmptyPrayerData(e.getProviderName()));
                    }

                    return Observable.just(new EmptyPrayerData(null));
                }
            };

    private final Func2<PrayerData, PrayerData, PrayerContext> mPrayerContextCreator =
            new Func2<PrayerData, PrayerData, PrayerContext>() {
                @Override
                public PrayerContext call(PrayerData current, PrayerData next) {
                    return new PrayerContextImpl(mDateHelper, mInterfacePreferences, current, next);
                }
            };
}
