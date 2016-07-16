package com.i906.mpt.prayer;

import android.location.Location;

import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.api.prayer.PrayerData;
import com.i906.mpt.date.DateTimeHelper;
import com.i906.mpt.location.LocationRepository;

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
import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class PrayerManager {

    private final DateTimeHelper mDateHelper;
    private final LocationRepository mLocationRepository;
    private final PrayerClient mPrayerClient;

    private Location mLastLocation;
    private PrayerContext mLastPrayerContext;
    private Subject<PrayerContext, PrayerContext> mPrayerStream;

    private AtomicBoolean mIsLoading = new AtomicBoolean(false);
    private AtomicBoolean mIsError = new AtomicBoolean(false);

    @Inject
    public PrayerManager(DateTimeHelper date, LocationRepository location, PrayerClient prayer) {
        mDateHelper = date;
        mLocationRepository = location;
        mPrayerClient = prayer;
    }

    public Observable<PrayerContext> getPrayerContext(final boolean refresh) {
        Timber.v("Error: %s, Loading: %s, Refresh: %s", hasError(), isLoading(), refresh);

        if (mPrayerStream == null || hasError() && !isLoading()) {
            mPrayerStream = BehaviorSubject.create();
            Timber.v("New behavior subject.");
        }

        if (isLoading() && !refresh) {
            return mPrayerStream.asObservable();
        }

        mLocationRepository.getLocation()
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
                        float distance = LocationRepository.getDistance(mLastLocation, location);
                        if (distance >= 5000 || refresh || mLastPrayerContext == null) {
                            mLastLocation = location;

                            return getCurrentPrayerTimesByCoordinate(location)
                                    .zipWith(getNextPrayerTimesByCoordinate(location), mPrayerContextCreator)
                                    .doOnNext(new Action1<PrayerContext>() {
                                        @Override
                                        public void call(PrayerContext prayerContext) {
                                            mLastPrayerContext = prayerContext;
                                        }
                                    });
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

    private Observable<PrayerData> getCurrentPrayerTimesByCoordinate(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        int year = mDateHelper.getCurrentYear();
        int month = mDateHelper.getCurrentMonth();

        return mPrayerClient.getPrayerTimesByCoordinates(lat, lng, year, month + 1);
    }

    private Observable<PrayerData> getNextPrayerTimesByCoordinate(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        int year = mDateHelper.getCurrentYear();
        int month = mDateHelper.getNextMonth();

        if (mDateHelper.isNextMonthNewYear()) {
            year = mDateHelper.getNextYear();
        }

        return mPrayerClient.getPrayerTimesByCoordinates(lat, lng, year, month + 1);
    }

    private final Func2<PrayerData, PrayerData, PrayerContext> mPrayerContextCreator =
            new Func2<PrayerData, PrayerData, PrayerContext>() {
                @Override
                public PrayerContext call(PrayerData current, PrayerData next) {
                    return new PrayerContextImpl(mDateHelper, current, next);
                }
            };

    public boolean hasError() {
        return mIsError.get();
    }

    public boolean isLoading() {
        return mIsLoading.get();
    }
}
