package com.i906.mpt.api;

import android.app.Application;
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

    private static final String FOURSQUARE_CLIENT_ID = "SIRVWW1NZJ1DFUZI5GSFAZX1FEVD5ELYNVTAO4BJ4BAT2R2W";
    private static final String FOURSQUARE_CLIENT_SECRET = "RNGOIIJAN4GMIEGR40KQXB3NWS5XCGUPA2HOF52HPDS4RSV3";
    private static final String FOURSQUARE_API_VERSION = "20150905";
    private static final String FOURSQUARE_URL = "https://api.foursquare.com/v2/";

    protected Application mContext;

    public ApiModule(Application application) {
        mContext = application;
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

    @Provides
    @Singleton
    public FoursquareApi provideFoursquareApi(OkHttpClient http) {
        OkHttpClient fsqHttp = http.clone();

        fsqHttp.interceptors()
                .add(chain -> {
                    Request originalRequest = chain.request();

                    String url = Uri.parse(originalRequest.urlString())
                            .buildUpon()
                            .appendQueryParameter("client_id", FOURSQUARE_CLIENT_ID)
                            .appendQueryParameter("client_secret", FOURSQUARE_CLIENT_SECRET)
                            .appendQueryParameter("v", FOURSQUARE_API_VERSION)
                            .build()
                            .toString();

                    Request requestWithUserAgent = originalRequest.newBuilder()
                            .url(url)
                            .build();

                    return chain.proceed(requestWithUserAgent);
                });

        fsqHttp.interceptors().add(new LoggingInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FOURSQUARE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(fsqHttp)
                .build();

        return retrofit.create(FoursquareApi.class);
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
