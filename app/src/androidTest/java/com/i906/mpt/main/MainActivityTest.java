package com.i906.mpt.main;

import android.app.Activity;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.i906.mpt.ActivityTest;
import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsModule;
import com.i906.mpt.internal.AppModule;
import com.i906.mpt.internal.Graph;
import com.i906.mpt.prefs.CommonPreferences;
import com.i906.mpt.prefs.HiddenPreferences;
import com.i906.mpt.prefs.PreferenceModule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.i906.mpt.TestUtils.app;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Noorzaini Ilhami
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends ActivityTest {

    @Rule
    public DaggerMockRule<Graph> mDaggerRule = new DaggerMockRule<>(Graph.class, new AppModule(app()), new PreferenceModule())
            .set(new DaggerMockRule.ComponentSetter<Graph>() {
                @Override
                public void setComponent(Graph graph) {
                    app().setGraph(graph);
                }
            });

    @Rule
    public final IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<>(MainActivity.class, false, false);

    @Mock
    CommonPreferences mCommonPreferences;

    @Mock
    HiddenPreferences mHiddenPreferences;

    @Override
    protected Activity getActivity() {
        return mActivityRule.getActivity();
    }

    @Test
    public void trackScreens() {
        when(mCommonPreferences.isFirstStart())
                .thenReturn(false);

        when(mHiddenPreferences.isCompassEnabled())
                .thenReturn(false);

        mActivityRule.launchActivity(null);
        unlockScreen();

        AnalyticsModule.MockAnalytics analytics = (AnalyticsModule.MockAnalytics) app()
                .getGraph()
                .getAnalyticsProvider();

        assertThat("Track qibla", analytics.trackQiblaCount, equalTo(0));
        assertThat("Track prayer", analytics.trackPrayerTimesCount, equalTo(1));
        assertThat("Track mosque", analytics.trackMosqueListCount, equalTo(0));

        onView(
                allOf(
                        withContentDescription(R.string.label_mosque),
                        isDescendantOfA(withId(R.id.tabs))
                )
        ).perform(click())
                .check(matches(isDisplayed()));

        assertThat("Track qibla after pressing mosque", analytics.trackQiblaCount, equalTo(0));
        assertThat("Track prayer after pressing mosque", analytics.trackPrayerTimesCount, equalTo(1));
        assertThat("Track mosque after pressing mosque", analytics.trackMosqueListCount, equalTo(1));

        onView(
                allOf(
                        withContentDescription(R.string.label_qibla),
                        isDescendantOfA(withId(R.id.tabs))
                )
        ).perform(click())
                .check(matches(isDisplayed()));

        assertThat("Track qibla after pressing qibla", analytics.trackQiblaCount, equalTo(1));
        assertThat("Track prayer after pressing qibla", analytics.trackPrayerTimesCount, equalTo(1));
        assertThat("Track mosque after pressing qibla", analytics.trackMosqueListCount, equalTo(1));

        onView(
                allOf(
                        withContentDescription(R.string.label_prayer),
                        isDescendantOfA(withId(R.id.tabs))
                )
        ).perform(click())
                .check(matches(isDisplayed()));

        assertThat("Track qibla after pressing prayer", analytics.trackQiblaCount, equalTo(1));
        assertThat("Track prayer after pressing prayer", analytics.trackPrayerTimesCount, equalTo(2));
        assertThat("Track mosque after pressing prayer", analytics.trackMosqueListCount, equalTo(1));
    }
}
