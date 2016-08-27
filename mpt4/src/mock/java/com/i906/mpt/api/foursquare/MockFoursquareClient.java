package com.i906.mpt.api.foursquare;

import com.i906.mpt.api.MockApiUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
public class MockFoursquareClient implements FoursquareClient {

    private final MockApiUtils mUtils;

    public MockFoursquareClient(MockApiUtils utils) {
        mUtils = utils;
    }

    @Override
    public Observable<List<Mosque>> getMosqueList(double lat, double lng, int radius) {
        Timber.v("Performing network tasks. Lat: %s Lng: %s", lat, lng);
        return Observable.just(mUtils.randomInt(0, 10))
                .delay(mUtils.randomInt(0, 5000), TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Integer, Observable<List<Mosque>>>() {
                    @Override
                    public Observable<List<Mosque>> call(Integer r) {
                        if (r % 4 == 0) {
                            Timber.v("Returning error.");
                            return Observable.error(new RuntimeException("Random error."));
                        } else {
                            Timber.v("Returning data.");
                            List<Mosque> mosqueList = mUtils.getDataList(Mosque.class, "json/mosque.json");
                            return Observable.just(mosqueList);
                        }
                    }
                });
    }
}
