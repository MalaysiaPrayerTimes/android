package com.i906.mpt.api.prayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

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
    private final ErrorWrapper mErrorWrapper;

    public RetrofitPrayerClient(OkHttpClient client) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PrayerData.class, new PrayerDataTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MPT_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mApi = retrofit.create(PrayerApi.class);

        mErrorWrapper = new ErrorWrapper(retrofit);
    }

    @Override
    public Observable<PrayerData> getPrayerTimesByCode(String code, int year, int month) {
        return mApi.getPrayerTimesByCode(code, year, month)
                .onErrorResumeNext(mErrorWrapper)
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
                .onErrorResumeNext(mErrorWrapper)
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
                .flatMapIterable(new Func1<List<PrayerProvider>, Iterable<PrayerProvider>>() {
                    @Override
                    public Iterable<PrayerProvider> call(List<PrayerProvider> providers) {
                        return providers;
                    }
                })
                .flatMap(new Func1<PrayerProvider, Observable<List<PrayerCode>>>() {
                    @Override
                    public Observable<List<PrayerCode>> call(final PrayerProvider provider) {
                        return Observable.from(provider.codes)
                                .map(new Func1<PrayerCode, PrayerCode>() {
                                    @Override
                                    public PrayerCode call(PrayerCode c) {
                                        c.provider = provider.provider;
                                        return c;
                                    }
                                })
                                .toList();
                    }
                });
    }
}
