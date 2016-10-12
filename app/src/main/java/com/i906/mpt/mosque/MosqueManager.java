package com.i906.mpt.mosque;

import android.location.Location;

import com.i906.mpt.api.foursquare.FoursquareClient;
import com.i906.mpt.api.foursquare.Mosque;
import com.i906.mpt.location.LocationRepository;

import java.util.List;
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

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class MosqueManager {

    private final LocationRepository mLocationRepository;
    private final FoursquareClient mFoursquareClient;

    private Location mLastLocation;
    private Subject<List<Mosque>, List<Mosque>> mMosqueStream;
    private AtomicBoolean mIsLoading = new AtomicBoolean(false);

    @Inject
    public MosqueManager(LocationRepository location, FoursquareClient client) {
        mLocationRepository = location;
        mFoursquareClient = client;
    }

    public Observable<List<Mosque>> getMosqueList(final boolean refresh) {
        if (mMosqueStream == null || (refresh && !isLoading())) {
            mMosqueStream = BehaviorSubject.create();
        }

        mLocationRepository.getLocation()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mIsLoading.set(true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Location, Observable<List<Mosque>>>() {
                    @Override
                    public Observable<List<Mosque>> call(Location location) {
                        float distance = LocationRepository.getDistance(mLastLocation, location);
                        if (distance >= 5000 || refresh) {
                            mLastLocation = location;
                            return mFoursquareClient.getMosqueList(location.getLatitude(),
                                    location.getLongitude(), 20000);
                        }

                        return Observable.empty();
                    }
                })
                .subscribe(new Action1<List<Mosque>>() {
                    @Override
                    public void call(List<Mosque> mosques) {
                        mIsLoading.set(false);
                        mMosqueStream.onNext(mosques);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mIsLoading.set(false);
                        mMosqueStream.onError(throwable);
                    }
                });

        return mMosqueStream.asObservable();
    }

    public boolean isLoading() {
        return mIsLoading.get();
    }
}
