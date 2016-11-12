package com.i906.mpt;

import android.app.Activity;
import android.view.WindowManager;

import com.i906.mpt.rules.DisableAnimationsRule;

import org.junit.Before;
import org.junit.ClassRule;

/**
 * @author Noorzaini Ilhami
 */
public abstract class ActivityTest {

    @ClassRule
    public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    protected abstract Activity getActivity();

    @Before
    public void unlockScreen() {
        final Activity activity = getActivity();
        if (activity == null) return;

        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };

        activity.runOnUiThread(wakeUpDevice);
    }
}
