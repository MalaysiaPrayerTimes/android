package com.i906.mpt.api.foursquare;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Noorzaini Ilhami
 */
public class RetrofitFoursquareClient implements FoursquareClient {

    private static final String FOURSQUARE_URL = "https://api.foursquare.com/v2/";

    private final Retrofit mRetrofit;
    private final FoursquareApi mApi;

    public RetrofitFoursquareClient(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FOURSQUARE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mRetrofit = retrofit;
        mApi = retrofit.create(FoursquareApi.class);
    }

    @Override
    public Observable<List<Mosque>> getMosqueList(double lat, double lng, int radius) {
        String latLng = String.format("%s,%s", lat, lng);
        return mApi.searchVenue(getQuery(), getIntent(), radius, 25, "4bf58dd8d48988d138941735", latLng)
                .map(new Func1<FoursquareResponse, List<Mosque>>() {
                    @Override
                    public List<Mosque> call(FoursquareResponse r) {
                        return r.getMosques();
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<Mosque>>>() {
                    @Override
                    public Observable<? extends List<Mosque>> call(Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException he = (HttpException) e;
                            Response<?> r = he.response();

                            if (r != null) {
                                Converter<ResponseBody, FoursquareResponse> converter = mRetrofit
                                        .responseBodyConverter(FoursquareResponse.class, new Annotation[0]);

                                FoursquareResponse body;

                                try {
                                    body = converter.convert(r.errorBody());

                                    if (body != null && "quota_exceeded".equals(body.meta.errorType)) {
                                        return Observable.error(new QuotaExceededException());
                                    }
                                } catch (IOException ie) {
                                }
                            }
                        }

                        return Observable.error(e);
                    }
                });
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public String getIntent() {
        return "browse";
    }
}
