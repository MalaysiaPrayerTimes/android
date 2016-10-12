package com.i906.mpt.internal;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
public class FirebaseTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (t != null) {
            if (!shouldReport(t)) return;
            FirebaseCrash.report(t);
        } else {
            FirebaseCrash.log(message);
        }
    }

    private boolean shouldReport(Throwable t) {
        return true;
    }

    protected boolean isLoggable(int priority) {
        return priority != Log.VERBOSE;
    }
}
