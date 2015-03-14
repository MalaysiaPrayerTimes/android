package com.i906.mpt.di;

import android.app.Application;

import dagger.Module;

@Module
public class MptModule {

    private Application mContext;

    public MptModule(Application application) {
        mContext = application;
    }
}
