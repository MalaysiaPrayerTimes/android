package com.i906.mpt;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.timber.StethoTree;

import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
public class MptApplication extends BaseMptApplication {

    @Override
    public void onPreCreate() {
        super.onPreCreate();
        Timber.plant(new Timber.DebugTree());
        Timber.plant(new StethoTree());
        Stetho.initializeWithDefaults(this);
    }
}
