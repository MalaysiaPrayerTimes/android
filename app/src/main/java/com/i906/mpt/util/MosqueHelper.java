package com.i906.mpt.util;

import com.i906.mpt.api.FoursquareApi;
import com.i906.mpt.model.Mosque;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class MosqueHelper {

    protected String mIntent = "browse";
    protected String mCategoryId = "4bf58dd8d48988d138941735";
    protected int mRadius = 5000;
    protected int mMaxResults = 15;

    protected FoursquareApi mApi;
    protected LocationHelper mLocationHelper;

    @Inject
    public MosqueHelper(FoursquareApi api, LocationHelper h1) {
        mApi = api;
        mLocationHelper = h1;
    }

    public Observable<List<Mosque>> getNearbyMosques() {
        return mLocationHelper.getLocation()
                .flatMap(location -> mApi.searchVenue(mIntent, mRadius, mMaxResults, mCategoryId,
                        String.format("%s,%s", location.getLatitude(), location.getLongitude()),
                        location.getAccuracy()))
                .flatMap(foursquareResponse -> Observable.just(foursquareResponse.getMosques()));
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    public void setMaxResults(int maxResults) {
        mMaxResults = maxResults;
    }
}
