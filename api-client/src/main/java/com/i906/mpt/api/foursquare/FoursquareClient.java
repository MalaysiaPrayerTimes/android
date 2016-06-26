package com.i906.mpt.api.foursquare;

import java.util.List;

import rx.Observable;

public interface FoursquareClient {

    Observable<List<Mosque>> getMosqueList(double lat, double lng, int radius);
}
