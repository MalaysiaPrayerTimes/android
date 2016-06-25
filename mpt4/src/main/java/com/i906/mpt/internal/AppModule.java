package com.i906.mpt.internal;

import android.content.Context;

import com.i906.mpt.prefs.CommonPreferences;
import com.i906.mpt.prefs.SharedCommonPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    CommonPreferences provideCommonPreferences(Context context) {
        return new SharedCommonPreferences(context);
    }
}
