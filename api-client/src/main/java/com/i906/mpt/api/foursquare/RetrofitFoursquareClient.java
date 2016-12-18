package com.i906.mpt.api.foursquare;

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
public class RetrofitFoursquareClient implements FoursquareClient {

    private static final String FOURSQUARE_URL = "https://api.foursquare.com/v2/";

    private final FoursquareApi mApi;

    public RetrofitFoursquareClient(OkHttpClient client) {
        mApi = new Retrofit.Builder()
                .baseUrl(FOURSQUARE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(FoursquareApi.class);
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
