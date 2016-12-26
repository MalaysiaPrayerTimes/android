package com.i906.mpt.api.foursquare;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

interface FoursquareApi {

    @GET("venues/search")
    Observable<FoursquareResponse> searchVenue(
            @Query("query") String query,
            @Query("intent") String intent,
            @Query("radius") int radius,
            @Query("limit") int limit,
            @Query("categoryId") String categoryId,
            @Query("ll") String latLng
    );
}
