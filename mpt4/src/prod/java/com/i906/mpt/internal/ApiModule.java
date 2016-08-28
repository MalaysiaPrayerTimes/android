package com.i906.mpt.internal;

import android.app.Application;
import android.os.Build;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.api.foursquare.FoursquareClient;
import com.i906.mpt.api.foursquare.FoursquareHttpInterceptor;
import com.i906.mpt.api.foursquare.RetrofitFoursquareClient;
import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.api.prayer.RetrofitPrayerClient;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author Noorzaini Ilhami
 */
@Module
public class ApiModule {

    @Provides
    @Singleton
    PrayerClient providePrayerClient(OkHttpClient client) {
        OkHttpClient.Builder mptClient = client.newBuilder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor hli = new HttpLoggingInterceptor();
            hli.setLevel(HttpLoggingInterceptor.Level.BODY);
            mptClient.addInterceptor(hli);
        }

        return new RetrofitPrayerClient(mptClient.build());
    }

    @Provides
    @Singleton
    FoursquareClient provideFoursquareClient(OkHttpClient client) {
        Interceptor i = new FoursquareHttpInterceptor(BuildConfig.FOURSQUARE_ID,
                BuildConfig.FOURSQUARE_SECRET);

        OkHttpClient.Builder foursquareClient = client.newBuilder()
                .addInterceptor(i);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor hli = new HttpLoggingInterceptor();
            hli.setLevel(HttpLoggingInterceptor.Level.BODY);
            foursquareClient.addInterceptor(hli);
        }

        return new RetrofitFoursquareClient(foursquareClient.build());
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        return new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    Cache provideCache(Application context) {
        return new Cache(createDefaultCacheDir(context), 10 * 1024 * 1024);
    }

    private File createDefaultCacheDir(Application context) {
        File cache = new File(context.getCacheDir(), "okhttp-cache");
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    private class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request r = chain.request()
                    .newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", getDefaultUserAgent(BuildConfig.VERSION_NAME))
                    .build();

            return chain.proceed(r);
        }
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
