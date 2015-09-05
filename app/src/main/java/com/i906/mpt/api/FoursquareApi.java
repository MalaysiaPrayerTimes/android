package com.i906.mpt.api;

import com.i906.mpt.model.FoursquareResponse;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface FoursquareApi {

    @GET("venues/search")
    Observable<FoursquareResponse> searchVenue(
            @Query("intent") String intent,
            @Query("radius") int radius,
            @Query("limit") int limit,
            @Query("categoryId") String categoryId,
            @Query("ll") String latLng,
            @Query("llAcc") float accuracy
    );
}
