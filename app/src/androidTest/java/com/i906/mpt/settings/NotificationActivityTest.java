package com.i906.mpt.settings;

import android.app.Activity;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.i906.mpt.ActivityTest;
import com.i906.mpt.R;
import com.i906.mpt.prefs.NotificationPreferences;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.i906.mpt.TestUtils.withRecyclerView;
import static com.i906.mpt.espresso.ViewAssertions.haveItemCount;
import static org.junit.Assert.assertFalse;

/**
 * @author Noorzaini Ilhami
 */
@RunWith(AndroidJUnit4.class)
public class NotificationActivityTest extends ActivityTest {

    @Rule
    public final IntentsTestRule<NotificationActivity> mActivityRule =
            new IntentsTestRule<>(NotificationActivity.class);

    @Override
    protected Activity getActivity() {
        return mActivityRule.getActivity();
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

        assertFalse("Prayer settings", prefs.isPrayerEnabled(0));

        onView(withRecyclerView(R.id.fragment_prayer_notification)
                .atPositionOnView(1, R.id.cb_notification))
                .perform(click());

        assertFalse("Notification settings", prefs.isNotificationEnabled(1));

        onView(withRecyclerView(R.id.fragment_prayer_notification)
                .atPositionOnView(2, R.id.cb_vibrate))
                .perform(click());

        assertFalse("Vibration settings", prefs.isVibrationEnabled(2));
    }
}
