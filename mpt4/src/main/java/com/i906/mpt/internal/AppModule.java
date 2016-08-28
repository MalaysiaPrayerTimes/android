package com.i906.mpt.internal;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application mContext;

    public AppModule(Application context) {
        mContext = context;
    }

    @Provides
    @Singleton
    Application provideContext() {
        return mContext;
    }
}
