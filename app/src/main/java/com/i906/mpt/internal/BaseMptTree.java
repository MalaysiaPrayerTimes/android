package com.i906.mpt.internal;

import android.util.Log;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
public abstract class BaseMptTree extends Timber.Tree {

    protected boolean shouldReport(Throwable t) {
        if (t instanceof UnknownHostException) return false;
        if (t instanceof SocketTimeoutException) return false;
        return true;
    }

    protected boolean isLoggable(String message, int priority) {
        return priority != Log.VERBOSE;
    }
}
