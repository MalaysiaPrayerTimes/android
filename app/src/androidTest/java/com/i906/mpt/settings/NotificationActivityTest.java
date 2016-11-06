package com.i906.mpt.settings;

import android.app.Activity;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.i906.mpt.R;
import com.i906.mpt.prefs.NotificationPreferences;
import com.i906.mpt.rules.DisableAnimationsRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.i906.mpt.TestUtils.withRecyclerView;
import static com.i906.mpt.espresso.ViewAssertions.haveItemCount;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Noorzaini Ilhami
 */
@RunWith(AndroidJUnit4.class)
public class NotificationActivityTest {

    private static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    private final IntentsTestRule<NotificationActivity> mActivityRule =
            new IntentsTestRule<>(NotificationActivity.class);

    @Rule
    public RuleChain rules = RuleChain.emptyRuleChain()
            .around(disableAnimationsRule)
            .around(mActivityRule);

    @Before
    public void unlockScreen() {
        final Activity activity = mActivityRule.getActivity();
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }

    @Test
    public void displaysEightPrayers() {
        onView(withId(R.id.fragment_prayer_notification))
                .check(matches(isDisplayed()));

        onView(withId(R.id.fragment_prayer_notification))
                .check(haveItemCount(8));
    }

    @Test
    public void savesSettings() {
        onView(withRecyclerView(R.id.fragment_prayer_notification)
                .atPositionOnView(0, R.id.sw_prayer))
                .perform(click());

        NotificationPreferences prefs = mActivityRule.getActivity().mNotificationPrefs;

        assertThat(prefs.isPrayerEnabled(0))
                .as("Prayer settings")
                .isFalse();

        onView(withRecyclerView(R.id.fragment_prayer_notification)
                .atPositionOnView(1, R.id.cb_notification))
                .perform(click());

        assertThat(prefs.isNotificationEnabled(1))
                .as("Notification settings")
                .isFalse();

        onView(withRecyclerView(R.id.fragment_prayer_notification)
                .atPositionOnView(2, R.id.cb_vibrate))
                .perform(click());

        assertThat(prefs.isVibrationEnabled(2))
                .as("Vibration settings")
                .isFalse();
    }
}
