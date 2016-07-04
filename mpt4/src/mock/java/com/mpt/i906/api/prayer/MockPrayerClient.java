package com.mpt.i906.api.prayer;

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
        return Observable.just(mUtils.randomInt(0, 10))
                .delay(mUtils.randomInt(0, 5000), TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Integer, Observable<PrayerData>>() {
                    @Override
                    public Observable<PrayerData> call(Integer r) {
                        if (r % 4 == 0) {
                            return Observable.error(new RuntimeException("Random error."));
                        } else {
                            PrayerData data = mUtils.getData(PrayerData.class, "json/prayer.json");

                            List<List<Date>> dailyDates = data.getPrayerTimes();

                            for (List<Date> dates : dailyDates) {
                                for (Date date : dates) {
                                    date.setDate(mDateHelper.getCurrentDate());
                                    date.setMonth(month);
                                    date.setYear(year);
                                }
                            }

                            return Observable.just(data);
                        }
                    }
                });
    }

    @Override
    public Observable<PrayerData> getPrayerTimesByCoordinates(double lat, double lng, int year, int month) {
        return getPrayerTimesByCode("xxx", year, month);
    }
}
