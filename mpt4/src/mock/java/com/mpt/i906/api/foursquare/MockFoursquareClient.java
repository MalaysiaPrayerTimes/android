package com.mpt.i906.api.foursquare;

import com.i906.mpt.api.foursquare.FoursquareClient;
import com.i906.mpt.api.foursquare.Mosque;
import com.mpt.i906.api.MockApiUtils;

import java.util.List;

import rx.Observable;

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
        List<Mosque> mosqueList = mUtils.getDataList(Mosque.class, "json/mosque.json");
        return Observable.just(mosqueList);
    }
}
