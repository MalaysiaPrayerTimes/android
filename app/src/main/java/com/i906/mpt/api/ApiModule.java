package com.i906.mpt.api;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.i906.mpt.BuildConfig;
import com.i906.mpt.model.PrayerData;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import timber.log.Timber;

@Module
public class ApiModule {

    private static final String MPT_ID = "mpt-android";
    private static final String MPT_URL = "http://mpt.i906.my";

    protected Context mContext;

    public ApiModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    public PrayerApi providePrayerApi(OkHttpClient http) {
        OkHttpClient mptHttp = http.clone();

        mptHttp.interceptors()
                .add(chain -> {
                    Request originalRequest = chain.request();

                    String url = Uri.parse(originalRequest.urlString())
                            .buildUpon()
                            .appendQueryParameter("appid", MPT_ID)
                            .appendQueryParameter("appurl", MPT_URL)
                            .build()
                            .toString();

                    Request requestWithUserAgent = originalRequest.newBuilder()
                            .url(url)
                            .build();

                    return chain.proceed(requestWithUserAgent);
                });

        mptHttp.interceptors().add(new LoggingInterceptor());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PrayerData.class, new PrayerDataTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MPT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mptHttp)
                .build();

        return retrofit.create(PrayerApi.class);
    }

    static class LoggingInterceptor implements Interceptor {
        @Override public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Timber.v("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers());

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Timber.v("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers());

            return response;
        }
    }
}
