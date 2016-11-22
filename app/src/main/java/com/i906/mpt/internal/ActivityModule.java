package com.i906.mpt.internal;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

/**
 * @author Noorzaini Ilhami
 */
@Module
public class ActivityModule {

    private Activity mContext;

    public ActivityModule(Activity context) {
        mContext = context;
    }

    @Provides
    @PerActivity
    protected Activity provideContext() {
        return mContext;
    }
}
