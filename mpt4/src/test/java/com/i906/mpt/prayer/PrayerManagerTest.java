package com.i906.mpt.prayer;

import android.location.Location;

import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.api.prayer.PrayerData;
import com.i906.mpt.date.DateTimeHelper;
import com.i906.mpt.location.LocationRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerManagerTest {

    @Mock
    private DateTimeHelper mDateHelper;

    @Mock
    private LocationRepository mLocationRepository;

    @Mock
    private PrayerClient mPrayerClient;

    @Mock
    private Location mLocation;

    @Mock
    private PrayerData mPrayerData;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getPrayerContextSameYear() {
        PrayerManager prayerManager = new PrayerManager(mDateHelper, mLocationRepository, mPrayerClient);

        when(mLocation.getLatitude())
                .thenReturn(3.28011);

        when(mLocation.getLongitude())
                .thenReturn(101.556);

        when(mDateHelper.getCurrentMonth())
                .thenReturn(3);

        when(mDateHelper.getCurrentYear())
                .thenReturn(2016);

        when(mDateHelper.getNextMonth())
                .thenReturn(4);

        when(mDateHelper.isNextMonthNewYear())
                .thenReturn(false);

        when(mLocationRepository.getLocation())
                .thenReturn(Observable.just(mLocation));

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), anyInt()))
                .thenReturn(Observable.just(mPrayerData));

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();
        TestSubscriber<PrayerContext> testSubscriber2 = new TestSubscriber<>();

        prayerManager.getPrayerContext(false).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        verify(mPrayerClient, times(1)).getPrayerTimesByCoordinates(eq(3.28011), eq(101.556), eq(2016), eq(4));
        verify(mPrayerClient, times(1)).getPrayerTimesByCoordinates(eq(3.28011), eq(101.556), eq(2016), eq(5));

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();
        assertThat(prayers).hasSize(1);

        prayerManager.getPrayerContext(false).subscribe(testSubscriber2);
        List<PrayerContext> prayers2 = testSubscriber2.getOnNextEvents();

        assertThat(prayers2).hasSize(1);
        assertThat(prayers2.get(0)).isEqualTo(prayers.get(0));
    }

    @Test
    public void getPrayerContextDifferentYear() {
        PrayerManager prayerManager = new PrayerManager(mDateHelper, mLocationRepository, mPrayerClient);

        when(mLocation.getLatitude())
                .thenReturn(3.28011);

        when(mLocation.getLongitude())
                .thenReturn(101.556);

        when(mDateHelper.getCurrentMonth())
                .thenReturn(11);

        when(mDateHelper.getCurrentYear())
                .thenReturn(2016);

        when(mDateHelper.getNextYear())
                .thenReturn(2017);

        when(mDateHelper.getNextMonth())
                .thenReturn(0);

        when(mDateHelper.isNextMonthNewYear())
                .thenReturn(true);

        when(mLocationRepository.getLocation())
                .thenReturn(Observable.just(mLocation));

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), anyInt()))
                .thenReturn(Observable.just(mPrayerData));

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();
        prayerManager.getPrayerContext(false).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mPrayerClient, times(1)).getPrayerTimesByCoordinates(eq(3.28011), eq(101.556), eq(2016), eq(12));
        verify(mPrayerClient, times(1)).getPrayerTimesByCoordinates(eq(3.28011), eq(101.556), eq(2017), eq(1));
    }
}
