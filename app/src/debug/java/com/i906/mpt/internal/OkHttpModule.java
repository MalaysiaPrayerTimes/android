package com.i906.mpt.internal;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author Noorzaini Ilhami
 */
@Module
class OkHttpModule {

    @Provides
    @Singleton
    @Named("data")
    List<Interceptor> provideOkHttpInterceptors() {
        HttpLoggingInterceptor hli = new HttpLoggingInterceptor();
        hli.setLevel(HttpLoggingInterceptor.Level.BODY);

        List<Interceptor> list = new ArrayList<>();

        list.add(new HeaderInterceptor());
        list.add(hli);

        return list;
    }

    @Provides
    @Singleton
    @Named("network")
    List<Interceptor> provideOkHttpNetworkInterceptors() {
        List<Interceptor> list = new ArrayList<>();
        list.add(new StethoInterceptor());
        return list;
    }
}
