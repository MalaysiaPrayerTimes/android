package com.mpt.i906.internal;

import android.app.Application;
import android.os.Build;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.api.foursquare.FoursquareClient;
import com.i906.mpt.api.foursquare.FoursquareHttpInterceptor;
import com.i906.mpt.api.foursquare.RetrofitFoursquareClient;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * @author Noorzaini Ilhami
 */
@Module
public class ApiModule {

    @Provides
    @Singleton
    FoursquareClient provideFoursquareClient(OkHttpClient client) {
        Interceptor i = new FoursquareHttpInterceptor(BuildConfig.FOURSQUARE_ID,
                BuildConfig.FOURSQUARE_SECRET);

        OkHttpClient foursquareClient = client.newBuilder()
                .addInterceptor(i)
                .build();

        return new RetrofitFoursquareClient(foursquareClient);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Cache cache) {
        return new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    public Cache provideCache(Application context) {
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
