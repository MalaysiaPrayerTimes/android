package com.i906.mpt.prayer;

import android.location.Location;

import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.api.prayer.PrayerData;
import com.i906.mpt.date.DateTimeHelper;
import com.i906.mpt.location.LocationRepository;
import com.i906.mpt.prefs.HiddenPreferences;
import com.i906.mpt.prefs.InterfacePreferences;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class PrayerManager {

    private final long mLocationDistanceLimit;

    private final DateTimeHelper mDateHelper;
    private final InterfacePreferences mPreferences;
    private final LocationRepository mLocationRepository;
    private final PrayerCacheManager mPrayerCache;
    private final PrayerClient mPrayerClient;
    private final PrayerBroadcaster mPrayerBroadcaster;

    private Location mLastLocation;
    private PrayerContext mLastPrayerContext;
    private Subject<PrayerContext, PrayerContext> mPrayerStream;

    private AtomicBoolean mIsLoading = new AtomicBoolean(false);
    private AtomicBoolean mIsError = new AtomicBoolean(false);

    @Inject
    public PrayerManager(DateTimeHelper date,
                         InterfacePreferences prefs,
                         LocationRepository location,
                         PrayerCacheManager cache,
                         PrayerClient prayer,
                         PrayerBroadcaster broadcaster,
                         HiddenPreferences hprefs) {
        mDateHelper = date;
        mPreferences = prefs;
        mLocationRepository = location;
        mPrayerCache = cache;
        mPrayerClient = prayer;
        mPrayerBroadcaster = broadcaster;

        mLocationDistanceLimit = hprefs.getLocationDistanceLimit();
    }

    public Observable<PrayerContext> getPrayerContext(final boolean refresh) {
        if (mPrayerStream == null || hasError() && !isLoading()) {
            mPrayerStream = BehaviorSubject.create();
        }

        if (isLoading() && !refresh) {
            return mPrayerStream.asObservable();
        }

        mLocationRepository.getLocation(refresh)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mIsLoading.set(true);
                        mIsError.set(false);
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Location, Observable<PrayerContext>>() {
                    @Override
                    public Observable<PrayerContext> call(Location location) {
                        mLastLocation = location;

                        if (shouldUpdatePrayerContext(location)) {
                            return updatePrayerContext(location);
                        }

                        return Observable.just(mLastPrayerContext);
                    }
                })
                .subscribe(new Action1<PrayerContext>() {
                    @Override
                    public void call(PrayerContext prayer) {
                        mIsLoading.set(false);
                        mIsError.set(false);
                        mPrayerStream.onNext(prayer);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mIsLoading.set(false);
                        mIsError.set(true);
                        mPrayerStream.onError(throwable);
                    }
                });

        return mPrayerStream.asObservable();
    }

    private void broadcastPrayerContext(PrayerContext context) {
        mPrayerStream.onNext(context);
        mPrayerBroadcaster.sendPrayerUpdatedBroadcast();
    }

    public boolean shouldUpdatePrayerContext(Location location) {
        if (mLastPrayerContext == null) {
            return true;
        }

        float distance = LocationRepository.getDistance(mLastLocation, location);

        if (distance >= mLocationDistanceLimit) {
            return true;
        }

        return false;
    }

    public Observable<PrayerContext> updatePrayerContext(Location location) {
        return getCurrentPrayerTimesByCoordinate(location)
                .zipWith(getNextPrayerTimesByCoordinate(location), mPrayerContextCreator)
                .doOnNext(new Action1<PrayerContext>() {
                    @Override
                    public void call(PrayerContext prayerContext) {
                        mLastPrayerContext = prayerContext;
                        broadcastPrayerContext(prayerContext);
                    }
                });
    }

    private Observable<PrayerData> getCurrentPrayerTimesByCoordinate(final Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        int year = mDateHelper.getCurrentYear();
        int month = mDateHelper.getCurrentMonth() + 1;

        Observable<PrayerData> cache = mPrayerCache.get(year, month, location);

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

        Observable<PrayerData> cache = mPrayerCache.get(year, month, location);

        Observable<PrayerData> api = mPrayerClient.getPrayerTimesByCoordinates(lat, lng, year, month)
                .doOnNext(new Action1<PrayerData>() {
                    @Override
                    public void call(PrayerData data) {
                        mPrayerCache.save(data, location);
                    }
                });

        return cache.switchIfEmpty(api);
    }

    private final Func2<PrayerData, PrayerData, PrayerContext> mPrayerContextCreator =
            new Func2<PrayerData, PrayerData, PrayerContext>() {
                @Override
                public PrayerContext call(PrayerData current, PrayerData next) {
                    return new PrayerContextImpl(mDateHelper, mPreferences, current, next);
                }
            };

    public boolean hasError() {
        return mIsError.get();
    }

    public boolean isLoading() {
        return mIsLoading.get();
    }
}
