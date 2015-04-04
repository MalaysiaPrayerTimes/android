package com.i906.mpt.api;

import com.i906.mpt.model.PrayerResponse;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface PrayerApi {

    @GET("/mpt.json")
    Observable<PrayerResponse> getPrayerData(
            @Query("code") String code,
            @Query("month") int month,
            @Query("year") int year
    );
}
