package com.i906.mpt.internal;

import android.app.Service;

import dagger.Module;
import dagger.Provides;

/**
 * @author Noorzaini Ilhami
 */
@Module
public class ServiceModule {

    private Service mContext;

    public ServiceModule(Service context) {
        mContext = context;
    }

    @Provides
    @PerService
    Service provideContext() {
        return mContext;
    }
}
