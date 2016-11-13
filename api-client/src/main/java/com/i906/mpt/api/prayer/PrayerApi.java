package com.i906.mpt.api.prayer;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Noorzaini Ilhami
 */
interface PrayerApi {

    @GET("prayer/{code}")
    Observable<PrayerResponse> getPrayerTimesByCode(
            @Path("code") String code,
            @Query("year") int year,
            @Query("month") int month
    );

    @GET("prayer/{lat},{lng}")
    Observable<PrayerResponse> getPrayerTimesByCoordinates(
            @Path("lat") double lat,
            @Path("lng") double lng,
            @Query("year") int year,
            @Query("month") int month
    );

    @GET("app/codes")
    Observable<List<PrayerProvider>> getSupportedCodes(
    );
}
