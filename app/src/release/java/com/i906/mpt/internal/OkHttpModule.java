package com.i906.mpt.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;

/**
 * @author Noorzaini Ilhami
 */
@Module
class OkHttpModule {

    @Provides
    @Singleton
    @Named("data")
    List<Interceptor> provideOkHttpInterceptors() {
        List<Interceptor> list = new ArrayList<>();
        list.add(new HeaderInterceptor());

        return list;
    }

    @Provides
    @Singleton
    @Named("network")
    List<Interceptor> provideOkHttpNetworkInterceptors() {
        return Collections.emptyList();
    }
}
