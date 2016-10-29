package com.i906.mpt.internal;

import android.util.Log;

import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
public abstract class BaseMptTree extends Timber.Tree {

    protected boolean shouldReport(Throwable t) {
        return true;
    }

    protected boolean isLoggable(String message, int priority) {
        return priority != Log.VERBOSE;
    }
}
