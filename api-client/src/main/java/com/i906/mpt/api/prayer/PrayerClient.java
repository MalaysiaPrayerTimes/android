package com.i906.mpt.api.prayer;

import java.util.List;

import rx.Observable;

/**
 * @author Noorzaini Ilhami
 */
public interface PrayerClient {

    Observable<PrayerData> getPrayerTimesByCode(String code, int year, int month);
    Observable<PrayerData> getPrayerTimesByCoordinates(double lat, double lng, int year, int month);
    Observable<List<PrayerCode>> getSupportedCodes();
}
