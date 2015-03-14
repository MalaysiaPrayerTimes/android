package com.i906.mpt.di;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import dagger.Module;
import dagger.Provides;

@Module
public class SystemModule {

    private Application mContext;

    public SystemModule(Application application) {
        mContext = application;
    }

    @Provides
    public Context provideContext() {
        return mContext;
    }

    @Provides
    public PackageManager providePackageManager() {
        return mContext.getPackageManager();
    }
}
