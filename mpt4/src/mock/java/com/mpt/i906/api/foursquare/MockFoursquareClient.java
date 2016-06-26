package com.mpt.i906.api.foursquare;

import android.util.Log;

import com.i906.mpt.api.foursquare.FoursquareClient;
import com.i906.mpt.api.foursquare.Mosque;
import com.mpt.i906.api.MockApiUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

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
        Log.w("MockFoursquareClient", "Performing network tasks. Lat: " + lat + " Lng: " + lng);
        return Observable.just(randomInt(0, 10))
                .delay(randomInt(0, 5000), TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Integer, Observable<List<Mosque>>>() {
                    @Override
                    public Observable<List<Mosque>> call(Integer r) {
                        if (r % 4 == 0) {
                            Log.w("MockFoursquareClient", "Returning error.");
                            return Observable.error(new RuntimeException("Random error."));
                        } else {
                            Log.w("MockFoursquareClient", "Returning data.");
                            List<Mosque> mosqueList = mUtils.getDataList(Mosque.class, "json/mosque.json");
                            return Observable.just(mosqueList);
                        }
                    }
                });
    }

    private int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
