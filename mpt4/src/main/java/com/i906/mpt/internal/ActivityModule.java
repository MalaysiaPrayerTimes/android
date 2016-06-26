package com.i906.mpt.internal;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * @author Noorzaini Ilhami
 */
@Module
public class ActivityModule {

    private Context mContext;

    public ActivityModule(Context context) {
        mContext = context;
    }

    @Provides
    @PerActivity
    Context provideContext() {
        return mContext;
    }
}
