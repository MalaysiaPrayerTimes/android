package com.i906.mpt.api.prayer;

import com.i906.mpt.api.MockApiUtils;
import com.i906.mpt.date.DateTimeHelper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

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
        Timber.v("Performing network tasks. Code: %s Year: %s Month: %s", code, year, month);
        return Observable.just(mUtils.randomInt(0, 10))
                .delay(mUtils.randomInt(0, 5000), TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Integer, Observable<PrayerData>>() {
                    @Override
                    public Observable<PrayerData> call(Integer r) {
                        if (r % 4 == 0) {
                            Timber.v("Returning error.");
                            return Observable.error(new RuntimeException("Random error."));
                        } else {
                            Timber.v("Returning data.");
                            PrayerData data = mUtils.getData(PrayerData.class, "json/prayer.json");

                            List<List<Date>> dailyDates = data.getPrayerTimes();

                            for (List<Date> dates : dailyDates) {
                                for (Date date : dates) {
                                    date.setMonth(month - 1);
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
        Timber.v("Performing network tasks. Lat: %s Lng: %s Year: %s Month: %s", lat, lng, year, month);
        return getPrayerTimesByCode("xxx", year, month);
    }
}
