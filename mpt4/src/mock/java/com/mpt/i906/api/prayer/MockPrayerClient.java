package com.mpt.i906.api.prayer;

import android.util.Log;

import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.api.prayer.PrayerData;
import com.i906.mpt.date.DateTimeHelper;
import com.mpt.i906.api.MockApiUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author Noorzaini Ilhami
 */
public class MockPrayerClient implements PrayerClient {

    private final MockApiUtils mUtils;
    private final DateTimeHelper mDateHelper;

    public MockPrayerClient(MockApiUtils utils, DateTimeHelper date) {
        mUtils = utils;
        mDateHelper = date;
    }

    @Override
    public Observable<PrayerData> getPrayerTimesByCode(String code, final int year, final int month) {
        Log.w("MockPrayerClient", "Performing network tasks. Code: " + code
                + " Year: " + year + " Month: " + month);

        return Observable.just(mUtils.randomInt(0, 10))
                .delay(mUtils.randomInt(0, 5000), TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Integer, Observable<PrayerData>>() {
                    @Override
                    public Observable<PrayerData> call(Integer r) {
                        if (r % 4 == 0) {
                            Log.w("MockPrayerClient", "Returning error.");
                            return Observable.error(new RuntimeException("Random error."));
                        } else {
                            Log.w("MockPrayerClient", "Returning data.");
                            PrayerData data = mUtils.getData(PrayerData.class, "json/prayer.json");

                            List<List<Date>> dailyDates = data.getPrayerTimes();

                            for (List<Date> dates : dailyDates) {
                                for (Date date : dates) {
                                    date.setMonth(month);
                                    date.setYear(year - 1900);
                                }
                            }

                            return Observable.just(data);
                        }
                    }
                });
    }

    @Override
    public Observable<PrayerData> getPrayerTimesByCoordinates(double lat, double lng, int year, int month) {
        Log.w("MockPrayerClient", "Performing network tasks. Lat: " + lat + " Lng: " + lng
                + " Year: " + year + " Month: " + month);

        return getPrayerTimesByCode("xxx", year, month);
    }
}
