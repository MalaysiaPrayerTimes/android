package com.i906.mpt.di;

import android.app.Application;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.i906.mpt.BuildConfig;
import com.i906.mpt.api.PrayerApi;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module
public class MptModule {

    private static final String MPT_ID = "mpt-android";
    private static final String MPT_URL = "http://mpt.i906.my";

    protected Application mContext;

    public MptModule(Application application) {
        mContext = application;
    }

    @Provides
    @Singleton
    public PrayerApi providePrayerApi(OkHttpClient http) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context)
                        -> new Date(json.getAsJsonPrimitive().getAsLong() * 1000))
                .create();

        RestAdapter.Builder restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(request -> {
                    request.addQueryParam("appid", MPT_ID);
                    request.addQueryParam("appurl", MPT_URL);
                })
                .setEndpoint(MPT_URL)
                .setClient(new OkClient(http));


        if (BuildConfig.DEBUG) restAdapter.setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("mpt-pa"));

        return restAdapter.build().create(PrayerApi.class);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient httpClient = new OkHttpClient();

        httpClient.interceptors().add(chain -> {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", getDefaultUserAgent(BuildConfig.VERSION_NAME))
                    .build();
            return chain.proceed(requestWithUserAgent);
        });

        return httpClient;
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
}
