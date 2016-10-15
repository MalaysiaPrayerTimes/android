package com.i906.mpt.api.prayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Noorzaini Ilhami
 */
public class RetrofitPrayerClient implements PrayerClient {

    private static final String MPT_URL = "https://mpt.i906.my/api/";

    private final PrayerApi mApi;

    public RetrofitPrayerClient(OkHttpClient client) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PrayerData.class, new PrayerDataTypeAdapter())
                .create();

        mApi = new Retrofit.Builder()
                .baseUrl(MPT_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(PrayerApi.class);
    }

    @Override
    public Observable<PrayerData> getPrayerTimesByCode(String code, int year, int month) {
        return mApi.getPrayerTimesByCode(code, year, month)
                .map(new Func1<PrayerResponse, PrayerData>() {
                    @Override
                    public PrayerData call(PrayerResponse r) {
                        return r.getData();
                    }
                });
    }

    @Override
    public Observable<PrayerData> getPrayerTimesByCoordinates(double lat, double lng, int year, int month) {
        return mApi.getPrayerTimesByCoordinates(lat, lng, year, month)
                .map(new Func1<PrayerResponse, PrayerData>() {
                    @Override
                    public PrayerData call(PrayerResponse r) {
                        return r.getData();
                    }
                });
    }

    @Override
    public Observable<List<PrayerCode>> getSupportedCodes() {
        return mApi.getSupportedCodes()
                .flatMap(new Func1<Map<String, List<PrayerCode>>, Observable<List<PrayerCode>>>() {
                    @Override
                    public Observable<List<PrayerCode>> call(Map<String, List<PrayerCode>> m) {
                        return Observable.from(m.entrySet())
                                .flatMap(new Func1<Map.Entry<String, List<PrayerCode>>, Observable<PrayerCode>>() {
                                    @Override
                                    public Observable<PrayerCode> call(final Map.Entry<String, List<PrayerCode>> e) {
                                        return Observable.from(e.getValue())
                                                .map(new Func1<PrayerCode, PrayerCode>() {
                                                    @Override
                                                    public PrayerCode call(PrayerCode c) {
                                                        c.provider = e.getKey();
                                                        return c;
                                                    }
                                                });
                                    }
                                })
                                .toList();
                    }
                });
    }
}
