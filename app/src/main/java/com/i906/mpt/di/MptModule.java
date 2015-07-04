package com.i906.mpt.di;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.i906.mpt.BuildConfig;
import com.i906.mpt.api.FoursquareApi;
import com.i906.mpt.api.PrayerApi;
import com.i906.mpt.model.PrayerData;
import com.i906.mpt.model.PrayerDataTypeAdapter;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

@Module
public class MptModule {

    private static final String MPT_ID = "mpt-android";
    private static final String MPT_URL = "http://mpt.i906.my";

    private static final String FOURSQUARE_CLIENT_ID = "SIRVWW1NZJ1DFUZI5GSFAZX1FEVD5ELYNVTAO4BJ4BAT2R2W";
    private static final String FOURSQUARE_CLIENT_SECRET = "RNGOIIJAN4GMIEGR40KQXB3NWS5XCGUPA2HOF52HPDS4RSV3";
    private static final String FOURSQUARE_API_VERSION = "20150404";
    private static final String FOURSQUARE_URL = "https://api.foursquare.com/v2";

    protected Application mContext;

    public MptModule(Application application) {
        mContext = application;
    }

    @Provides
    @Singleton
    public PrayerApi providePrayerApi(OkHttpClient http) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PrayerData.class, new PrayerDataTypeAdapter())
                .create();

        RestAdapter.Builder restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(request -> {
                    request.addQueryParam("appid", MPT_ID);
                    request.addQueryParam("appurl", MPT_URL);
                })
                .setEndpoint(MPT_URL)
                .setClient(new OkClient(http))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RetrofitLogger());

        return restAdapter.build().create(PrayerApi.class);
    }

    @Provides
    @Singleton
    public FoursquareApi provideFoursquareApi(OkHttpClient http) {

        RestAdapter.Builder restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(request -> {
                    request.addQueryParam("client_id", FOURSQUARE_CLIENT_ID);
                    request.addQueryParam("client_secret", FOURSQUARE_CLIENT_SECRET);
                    request.addQueryParam("v", FOURSQUARE_API_VERSION);
                })
                .setEndpoint(FOURSQUARE_URL)
                .setClient(new OkClient(http))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RetrofitLogger());

        return restAdapter.build().create(FoursquareApi.class);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Context context) {
        OkHttpClient httpClient = new OkHttpClient();

        httpClient.interceptors().add(chain -> {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", getDefaultUserAgent(BuildConfig.VERSION_NAME))
                    .build();
            return chain.proceed(requestWithUserAgent);
        });

        httpClient.setCache(new Cache(createDefaultCacheDir(context), 10 * 1024 * 1024));

        return httpClient;
    }

    private File createDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), "okhttp-cache");
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    private String getDefaultUserAgent(String cversion) {
        StringBuilder result = new StringBuilder(64);

        if (cversion != null) {
            result.append("MalaysiaPrayerTimes/");
            result.append(cversion);
        } else {
            result.append("Dalvik/");
            result.append(System.getProperty("java.vm.version")); // such as 1.1.0
        }

        result.append(" (Linux; U; Android ");

        String version = Build.VERSION.RELEASE; // "1.0" or "3.4b5"
        result.append(version.length() > 0 ? version : "1.0");

        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            String model = Build.MODEL;
            if (model.length() > 0) {
                result.append("; ");
                result.append(model);
            }
        }
        String id = Build.ID; // "MASTER" or "M4-rc20"
        if (id.length() > 0) {
            result.append(" Build/");
            result.append(id);
        }
        result.append(")");
        return result.toString();
    }

    private static class RetrofitLogger implements RestAdapter.Log {

        public RetrofitLogger() {
            Timber.tag("Retrofit");
        }

        @Override
        public void log(String message) {
            Timber.v(message);
        }
    }
}
