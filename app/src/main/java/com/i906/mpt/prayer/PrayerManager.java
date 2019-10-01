package com.i906.mpt.prayer;

import android.location.Location;

import com.i906.mpt.api.prayer.PrayerCode;
import com.i906.mpt.api.prayer.PrayerProviderException;
import com.i906.mpt.date.DateTimeHelper;
import com.i906.mpt.location.LocationRepository;
import com.i906.mpt.prefs.HiddenPreferences;
import com.i906.mpt.prefs.LocationPreferences;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;
import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class PrayerManager {

    private final long mLocationDistanceLimit;

    private final LocationPreferences mLocationPreferences;
    private final LocationRepository mLocationRepository;
    private final PrayerBroadcaster mPrayerBroadcaster;
    private final PrayerDownloader mPrayerDownloader;
    private final DateTimeHelper mDateTimeHelper;

    private Location mLastLocation;
    private PrayerCode mLastPreferredLocation;
    private PrayerContext mLastPrayerContext;
    private Subject<PrayerContext, PrayerContext> mPrayerStream;

    private AtomicBoolean mIsLoading = new AtomicBoolean(false);
    private AtomicBoolean mIsError = new AtomicBoolean(false);

    @Inject
    public PrayerManager(PrayerDownloader downloader,
                         LocationRepository location,
                         PrayerBroadcaster broadcaster,
                         HiddenPreferences hprefs,
                         LocationPreferences lprefs,
                         DateTimeHelper date) {
        mLocationPreferences = lprefs;
        mLocationRepository = location;
        mPrayerBroadcaster = broadcaster;
        mPrayerDownloader = downloader;
        mDateTimeHelper = date;

        mLocationDistanceLimit = hprefs.getLocationDistanceLimit();
    }

    public Observable<PrayerContext> getPrayerContext(final boolean refresh) {
        checkPrayerStream();

        if (isLoading() && !refresh) {
            return mPrayerStream.asObservable();
        }

        Observable<PrayerContext> data;

        if (!useAutomaticLocation()) {
            data = getPreferredPrayerContext(refresh);
        } else {
            data = getAutomaticPrayerContext(refresh);
        }

        data.subscribe(new Action1<PrayerContext>() {
            @Override
            public void call(PrayerContext prayer) {
                mIsError.set(false);
                mPrayerStream.onNext(prayer);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mIsError.set(true);
                mPrayerStream.onError(throwable);
            }
        });

        return mPrayerStream.asObservable();
    }

    private Observable<PrayerContext> getAutomaticPrayerContext(final boolean refresh) {
        return mLocationRepository.getLocation(refresh)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mIsError.set(false);
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Location, Observable<PrayerContext>>() {
                    @Override
                    public Observable<PrayerContext> call(Location location) {
                        mLastLocation = location;

                        if ((shouldUpdatePrayerContext(location) && !isLoading()) || refresh) {
                            return updatePrayerContext(location);
                        }

                        return getLastOrPendingPrayerContext();
                    }
                });
    }

    private Observable<PrayerContext> getPreferredPrayerContext(final boolean refresh) {
        final PrayerCode location = mLocationPreferences.getPreferredLocation();

        if (location == null) {
            return getAutomaticPrayerContext(refresh);
        }

        return Observable.just(location.getCode())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mIsError.set(false);
                    }
                })
                .flatMap(new Func1<String, Observable<PrayerContext>>() {
                    @Override
                    public Observable<PrayerContext> call(String code) {
                        if ((shouldUpdatePrayerContext(code) && !isLoading()) || refresh) {
                            mLastPreferredLocation = location;
                            return updatePrayerContext(code);
                        }

                        return getLastOrPendingPrayerContext();
                    }
                });
    }

    private Observable<PrayerContext> getLastOrPendingPrayerContext() {
        if (mLastPrayerContext != null) {
            Timber.i("Returning last prayer context");
            return Observable.just(mLastPrayerContext);
        } else {
            Timber.i("Awaiting prayer context");
            return mPrayerStream.asObservable()
                    .first();
        }
    }

    private boolean useAutomaticLocation() {
        return mLocationPreferences.isUsingAutomaticLocation()
                || !mLocationPreferences.hasPreferredLocation();
    }

    private void checkPrayerStream() {
        if (mPrayerStream == null || hasError() && !isLoading()) {
            mPrayerStream = BehaviorSubject.create();
        }
    }

    private void broadcastPrayerContext(PrayerContext context) {
        mPrayerStream.onNext(context);
        mPrayerBroadcaster.sendPrayerUpdatedBroadcast();
    }

    private boolean isLastPrayerContextStale() {
        if (mLastPrayerContext == null) {
            return true;
        }

        try {
            Prayer next = mLastPrayerContext.getNextPrayer();

            if (mDateTimeHelper.isInPast(next.getDate())) {
                return true;
            }
        } catch (PrayerProviderException ppe) {
            return true;
        }

        return false;
    }

    private boolean shouldUpdatePrayerContext(String code) {
        if (isLastPrayerContextStale()) {
            Timber.i("Last prayer context is stale");
            return true;
        }

        if (mLastPreferredLocation != null) {
            if (!mLastPreferredLocation.getCode().equals(code)) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldUpdatePrayerContext(Location location) {
        if (isLastPrayerContextStale()) {
            Timber.i("Last prayer context is stale");
            return true;
        }

        return shouldUpdateLocation(location);
    }

    private boolean shouldUpdateLocation(Location location) {
        float distance = LocationRepository.getDistance(mLastLocation, location);
        boolean update = distance >= mLocationDistanceLimit;

        if (update) {
            Timber.i("Last location is stale");
        }

        return update;
    }

    public Observable<PrayerContext> refreshPrayerContext(Location location) {
        checkPrayerStream();

        if (shouldUpdateLocation(location) && !isLoading()) {
            mLastLocation = location;
            updatePrayerContext(location)
                    .subscribe(new Action1<PrayerContext>() {
                        @Override
                        public void call(PrayerContext context) {
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable e) {
                        }
                    });
        }

        return mPrayerStream.asObservable();
    }

    private Observable<PrayerContext> updatePrayerContext(String code) {
        Timber.i("Updating preferred prayer context");

        return mPrayerDownloader.getPrayerTimes(code)
                .doOnNext(new Action1<PrayerContext>() {
                    @Override
                    public void call(PrayerContext prayerContext) {
                        mLastPrayerContext = prayerContext;
                        broadcastPrayerContext(prayerContext);
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mIsLoading.set(true);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mIsLoading.set(false);
                    }
                });
    }

    private Observable<PrayerContext> updatePrayerContext(Location location) {
        Timber.i("Updating prayer context");

        return mPrayerDownloader.getPrayerTimes(location)
                .doOnNext(new Action1<PrayerContext>() {
                    @Override
                    public void call(PrayerContext prayerContext) {
                        mLastPrayerContext = prayerContext;
                        broadcastPrayerContext(prayerContext);
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mIsLoading.set(true);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mIsLoading.set(false);
                    }
                });
    }

    public void notifyPreferenceChanged() {
        mLastPrayerContext = null;
    }

    public boolean hasError() {
        return mIsError.get();
    }

    public boolean isLoading() {
        return mIsLoading.get();
    }
}
