package com.i906.mpt.internal;

import com.google.firebase.crash.FirebaseCrash;

/**
 * @author Noorzaini Ilhami
 */
public class FirebaseTree extends BaseMptTree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (t != null) {
            if (!shouldReport(t)) return;
            FirebaseCrash.report(t);
        } else {
            FirebaseCrash.log(message);
        }
    }
}
