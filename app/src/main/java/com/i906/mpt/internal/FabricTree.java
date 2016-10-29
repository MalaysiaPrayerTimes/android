package com.i906.mpt.internal;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * @author Noorzaini Ilhami
 */
public class FabricTree extends BaseMptTree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (!Fabric.isInitialized()) return;

        if (t != null) {
            if (!shouldReport(t)) return;
            Crashlytics.logException(t);
        }

        Crashlytics.log(message);
    }
}
