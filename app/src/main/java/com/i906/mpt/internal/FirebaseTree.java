package com.i906.mpt.internal;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;

/**
 * @author Noorzaini Ilhami
 */
public class FirebaseTree extends BaseMptTree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        try {
            FirebaseApp.getInstance();
        } catch (IllegalStateException e) {
            // Skip logging if Firebase not initialized
            return;
        }

        if (t != null) {
            if (!shouldReport(t)) return;
            FirebaseCrash.report(t);
        } else {
            FirebaseCrash.log(message);
        }
    }
}
