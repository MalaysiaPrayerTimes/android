package com.i906.mpt.prayer;

import android.location.Location;

import com.i906.mpt.RxJavaResetRule;
import com.i906.mpt.location.LocationRepository;
import com.i906.mpt.location.PreferredLocation;
import com.i906.mpt.prefs.HiddenPreferences;
import com.i906.mpt.prefs.LocationPreferences;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerManagerTest {

    @Rule
    public RxJavaResetRule mResetRule = new RxJavaResetRule();

    @Mock
    private LocationRepository mLocationRepository;

    @Mock
    private Location mLocation;

    @Mock
    private PrayerContext mPrayerContext1;

    @Mock
    private PrayerContext mPrayerContext2;

    @Mock
    private PrayerBroadcaster mPrayerBroadcaster;

    @Mock
    private PrayerDownloader mPrayerDownloader;

    @Mock
    private HiddenPreferences mHiddenPreferences;

    @Mock
    private LocationPreferences mLocationPreferences;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void automaticLocation() {
        when(mHiddenPreferences.getLocationDistanceLimit())
                .thenReturn(5000L);

        when(mLocationPreferences.isUsingAutomaticLocation())
                .thenReturn(true);

        when(mPrayerDownloader.getPrayerTimes(eq(mLocation)))
                .thenReturn(Observable.just(mPrayerContext1));

        when(mLocationRepository.getLocation(anyBoolean()))
                .thenReturn(Observable.just(mLocation));

        PrayerManager prayerManager = new PrayerManager(
                mPrayerDownloader,
                mLocationRepository,
                mPrayerBroadcaster,
                mHiddenPreferences,
                mLocationPreferences
        );

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();

        prayerManager.getPrayerContext(false).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();

        assertThat(prayers).hasSize(1);
        assertThat(prayers.get(0)).isEqualTo(mPrayerContext1);
    }

    @Test
    public void manualLocationButNotSet() {
        when(mHiddenPreferences.getLocationDistanceLimit())
                .thenReturn(5000L);

        when(mLocationPreferences.isUsingAutomaticLocation())
                .thenReturn(false);

        when(mLocationPreferences.getPreferredLocation())
                .thenReturn(null);

        when(mLocationPreferences.hasPreferredLocation())
                .thenReturn(false);

        when(mLocationRepository.getLocation(anyBoolean()))
                .thenReturn(Observable.just(mLocation));

        when(mPrayerDownloader.getPrayerTimes(eq(mLocation)))
                .thenReturn(Observable.just(mPrayerContext1));

        PrayerManager prayerManager = new PrayerManager(
                mPrayerDownloader,
                mLocationRepository,
                mPrayerBroadcaster,
                mHiddenPreferences,
                mLocationPreferences
        );

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();

        prayerManager.getPrayerContext(false).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();

        assertThat(prayers).hasSize(1);
        assertThat(prayers.get(0)).isEqualTo(mPrayerContext1);
    }


    @Test
    public void manualLocation() {
        PreferredLocation pl = new PreferredLocation("ext-157", "Jitra");

        when(mHiddenPreferences.getLocationDistanceLimit())
                .thenReturn(5000L);

        when(mLocationPreferences.isUsingAutomaticLocation())
                .thenReturn(false);

        when(mLocationPreferences.getPreferredLocation())
                .thenReturn(pl);

        when(mLocationPreferences.hasPreferredLocation())
                .thenReturn(true);

        when(mPrayerDownloader.getPrayerTimes(eq(mLocation)))
                .thenReturn(Observable.just(mPrayerContext1));

        when(mPrayerDownloader.getPrayerTimes(eq(pl.getCode())))
                .thenReturn(Observable.just(mPrayerContext2));

        PrayerManager prayerManager = new PrayerManager(
                mPrayerDownloader,
                mLocationRepository,
                mPrayerBroadcaster,
                mHiddenPreferences,
                mLocationPreferences
        );

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();

        prayerManager.getPrayerContext(false).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();

        assertThat(prayers).hasSize(1);
        assertThat(prayers.get(0)).isEqualTo(mPrayerContext2);
    }
}
