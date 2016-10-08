package com.i906.mpt;

import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
public class MptApplication extends BaseMptApplication {

    @Override
    public void onPreCreate() {
        super.onPreCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
