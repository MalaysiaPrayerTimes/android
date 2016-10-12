package com.i906.mpt.internal;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Noorzaini Ilhami
 */
@Module
class OkHttpModule {

    @Provides
    @Singleton
    List<Interceptor> provideOkHttpInterceptors() {
        List<Interceptor> list = new ArrayList<>();
        list.add(new HeaderInterceptor());

        return list;
    }
}
