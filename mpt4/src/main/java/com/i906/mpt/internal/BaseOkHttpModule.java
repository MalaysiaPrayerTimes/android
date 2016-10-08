package com.i906.mpt.internal;

import android.content.Context;

import java.io.File;
import java.util.List;

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
class BaseOkHttpModule {

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache, List<Interceptor> interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache);

        builder.interceptors()
                .addAll(interceptors);

        return builder.build();
    }

    @Provides
    @Singleton
    Cache provideCache(Context context) {
        return new Cache(createDefaultCacheDir(context), 10 * 1024 * 1024);
    }

    private File createDefaultCacheDir(Context context) {
        File cache = new File(context.getCacheDir(), "okhttp-cache");
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }
}
