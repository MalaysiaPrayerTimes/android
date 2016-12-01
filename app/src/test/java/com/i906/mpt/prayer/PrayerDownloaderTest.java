package com.i906.mpt.prayer;

import android.location.Location;

import com.i906.mpt.RxJavaResetRule;
import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.api.prayer.PrayerData;
import com.i906.mpt.api.prayer.PrayerProviderException;
import com.i906.mpt.date.DateTimeHelper;
import com.i906.mpt.prefs.InterfacePreferences;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerDownloaderTest {

    @Rule
    public RxJavaResetRule mResetRule = new RxJavaResetRule();

    @Mock
    private DateTimeHelper mDateHelper;

    @Mock
    private PrayerCacheManager mPrayerCache;

    @Mock
    private PrayerClient mPrayerClient;

    @Mock
    private InterfacePreferences mInterfacePreferences;

    @Mock
    private Location mLocation;

    @Mock
    private PrayerData mPrayerData1;

    @Mock
    private PrayerData mPrayerData2;

    @Mock
    private PrayerData mPrayerData3;

    @Mock
    private PrayerData mPrayerData4;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getPrayerContextSameYear() {
        PrayerDownloader d = new PrayerDownloader(
                mDateHelper,
                mPrayerClient,
                mPrayerCache,
                mInterfacePreferences
        );

        when(mLocation.getLatitude())
                .thenReturn(3.28011);

        when(mLocation.getLongitude())
                .thenReturn(101.556);

        when(mLocation.distanceTo(any(Location.class)))
                .thenReturn(0f);

        when(mDateHelper.getCurrentMonth())
                .thenReturn(3);

        when(mDateHelper.getCurrentYear())
                .thenReturn(2016);

        when(mDateHelper.getNextMonth())
                .thenReturn(4);

        when(mDateHelper.isNextMonthNewYear())
                .thenReturn(false);

        when(mPrayerCache.get(anyInt(), anyInt(), eq(mLocation)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(4)))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(5)))
                .thenReturn(Observable.just(mPrayerData2));

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();
        d.getPrayerTimes(mLocation).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();
        assertThat(prayers).hasSize(1);

        verify(mPrayerCache, times(1)).get(eq(2016), eq(4), eq(mLocation));
        verify(mPrayerCache, times(1)).get(eq(2016), eq(5), eq(mLocation));

        verify(mPrayerClient, times(1)).getPrayerTimesByCoordinates(eq(3.28011), eq(101.556), eq(2016), eq(4));
        verify(mPrayerClient, times(1)).getPrayerTimesByCoordinates(eq(3.28011), eq(101.556), eq(2016), eq(5));

        verify(mPrayerCache, times(1)).save(eq(mPrayerData1), eq(mLocation));
        verify(mPrayerCache, times(1)).save(eq(mPrayerData2), eq(mLocation));
    }

    @Test
    public void getPrayerContextDifferentYear() {
        PrayerDownloader d = new PrayerDownloader(
                mDateHelper,
                mPrayerClient,
                mPrayerCache,
                mInterfacePreferences
        );

        when(mLocation.getLatitude())
                .thenReturn(3.28011);

        when(mLocation.getLongitude())
                .thenReturn(101.556);

        when(mLocation.distanceTo(any(Location.class)))
                .thenReturn(0f);

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

        when(mPrayerCache.get(anyInt(), anyInt(), eq(mLocation)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(12)))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(1)))
                .thenReturn(Observable.just(mPrayerData2));

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();
        d.getPrayerTimes(mLocation).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();
        assertThat(prayers).hasSize(1);

        verify(mPrayerCache, times(1)).get(eq(2016), eq(12), eq(mLocation));
        verify(mPrayerCache, times(1)).get(eq(2017), eq(1), eq(mLocation));

        verify(mPrayerClient, times(1)).getPrayerTimesByCoordinates(eq(3.28011), eq(101.556), eq(2016), eq(12));
        verify(mPrayerClient, times(1)).getPrayerTimesByCoordinates(eq(3.28011), eq(101.556), eq(2017), eq(1));

        verify(mPrayerCache, times(1)).save(eq(mPrayerData1), eq(mLocation));
        verify(mPrayerCache, times(1)).save(eq(mPrayerData2), eq(mLocation));
    }

    @Test
    public void getPrayerContextCodeSameYear() {
        String code = "ext-157";

        PrayerDownloader d = new PrayerDownloader(
                mDateHelper,
                mPrayerClient,
                mPrayerCache,
                mInterfacePreferences
        );

        when(mDateHelper.getCurrentMonth())
                .thenReturn(3);

        when(mDateHelper.getCurrentYear())
                .thenReturn(2016);

        when(mDateHelper.getNextMonth())
                .thenReturn(4);

        when(mDateHelper.isNextMonthNewYear())
                .thenReturn(false);

        when(mPrayerCache.get(anyInt(), anyInt(), eq(code)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(4)))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(5)))
                .thenReturn(Observable.just(mPrayerData2));

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();
        d.getPrayerTimes(code).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();
        assertThat(prayers).hasSize(1);

        verify(mPrayerCache, times(1)).get(eq(2016), eq(4), eq(code));
        verify(mPrayerCache, times(1)).get(eq(2016), eq(5), eq(code));

        verify(mPrayerClient, times(1)).getPrayerTimesByCode(eq(code), eq(2016), eq(4));
        verify(mPrayerClient, times(1)).getPrayerTimesByCode(eq(code), eq(2016), eq(5));

        verify(mPrayerCache, times(1)).save(eq(mPrayerData1));
        verify(mPrayerCache, times(1)).save(eq(mPrayerData2));
    }

    @Test
    public void getPrayerContextCodeNextYear() {
        String code = "ext-157";

        PrayerDownloader d = new PrayerDownloader(
                mDateHelper,
                mPrayerClient,
                mPrayerCache,
                mInterfacePreferences
        );

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

        when(mPrayerCache.get(anyInt(), anyInt(), eq(code)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(12)))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(1)))
                .thenReturn(Observable.just(mPrayerData2));

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();
        d.getPrayerTimes(code).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();
        assertThat(prayers).hasSize(1);

        verify(mPrayerCache, times(1)).get(eq(2016), eq(12), eq(code));
        verify(mPrayerCache, times(1)).get(eq(2017), eq(1), eq(code));

        verify(mPrayerClient, times(1)).getPrayerTimesByCode(eq(code), eq(2016), eq(12));
        verify(mPrayerClient, times(1)).getPrayerTimesByCode(eq(code), eq(2017), eq(1));

        verify(mPrayerCache, times(1)).save(eq(mPrayerData1));
        verify(mPrayerCache, times(1)).save(eq(mPrayerData2));
    }

    @Test
    public void prayerContextCoordinateCache() {
        PrayerDownloader d = new PrayerDownloader(
                mDateHelper,
                mPrayerClient,
                mPrayerCache,
                mInterfacePreferences
        );

        when(mLocation.getLatitude())
                .thenReturn(3.28011);

        when(mLocation.getLongitude())
                .thenReturn(101.556);

        when(mLocation.distanceTo(any(Location.class)))
                .thenReturn(0f);

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

        when(mPrayerCache.get(eq(2016), eq(12), eq(mLocation)))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerCache.get(eq(2017), eq(1), eq(mLocation)))
                .thenReturn(Observable.just(mPrayerData2));

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(12)))
                .thenReturn(Observable.just(mPrayerData3));

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(1)))
                .thenReturn(Observable.just(mPrayerData4));

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();
        d.getPrayerTimes(mLocation).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();
        assertThat(prayers).hasSize(1);

        PrayerContextImpl pc = (PrayerContextImpl) prayers.get(0);
        assertThat(pc.getCurrentPrayerData()).isEqualTo(mPrayerData1);
        assertThat(pc.getNextPrayerData()).isEqualTo(mPrayerData2);
    }

    @Test
    public void prayerContextCodeCache() {
        PrayerDownloader d = new PrayerDownloader(
                mDateHelper,
                mPrayerClient,
                mPrayerCache,
                mInterfacePreferences
        );

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

        when(mPrayerCache.get(eq(2016), eq(12), eq("ext-157")))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerCache.get(eq(2017), eq(1), eq("ext-157")))
                .thenReturn(Observable.just(mPrayerData2));

        when(mPrayerClient.getPrayerTimesByCode(anyString(), anyInt(), eq(12)))
                .thenReturn(Observable.just(mPrayerData3));

        when(mPrayerClient.getPrayerTimesByCode(anyString(), anyInt(), eq(1)))
                .thenReturn(Observable.just(mPrayerData4));

        TestSubscriber<PrayerContext> testSubscriber = new TestSubscriber<>();
        d.getPrayerTimes("ext-157").subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        List<PrayerContext> prayers = testSubscriber.getOnNextEvents();
        assertThat(prayers).hasSize(1);

        PrayerContextImpl pc = (PrayerContextImpl) prayers.get(0);
        assertThat(pc.getCurrentPrayerData()).isEqualTo(mPrayerData1);
        assertThat(pc.getNextPrayerData()).isEqualTo(mPrayerData2);
    }

    @Test
    public void throwsErrorWhenCurrentPrayerNotAvailable() {
        String code = "ext-153";

        PrayerDownloader d = new PrayerDownloader(
                mDateHelper,
                mPrayerClient,
                mPrayerCache,
                mInterfacePreferences
        );

        when(mLocation.getLatitude())
                .thenReturn(3.28011);

        when(mLocation.getLongitude())
                .thenReturn(101.556);

        when(mLocation.distanceTo(any(Location.class)))
                .thenReturn(0f);

        when(mDateHelper.getCurrentMonth())
                .thenReturn(3);

        when(mDateHelper.getCurrentYear())
                .thenReturn(2016);

        when(mDateHelper.getNextMonth())
                .thenReturn(4);

        when(mDateHelper.isNextMonthNewYear())
                .thenReturn(false);

        when(mPrayerCache.get(anyInt(), anyInt(), eq(mLocation)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerCache.get(anyInt(), anyInt(), eq(code)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(4)))
                .thenReturn(Observable.<PrayerData>error(new PrayerProviderException("JAKIM")));

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(5)))
                .thenReturn(Observable.just(mPrayerData2));

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(4)))
                .thenReturn(Observable.<PrayerData>error(new PrayerProviderException("JAKIM")));

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(5)))
                .thenReturn(Observable.just(mPrayerData2));

        TestSubscriber<PrayerContext> testSubscriber1 = new TestSubscriber<>();
        d.getPrayerTimes(mLocation).subscribe(testSubscriber1);
        testSubscriber1.assertError(PrayerProviderException.class);

        TestSubscriber<PrayerContext> testSubscriber2 = new TestSubscriber<>();
        d.getPrayerTimes(code).subscribe(testSubscriber2);
        testSubscriber2.assertError(PrayerProviderException.class);
    }

    @Test
    public void swallowPrayerErrorWhenNextPrayerNotAvailable() {
        String code = "ext-153";

        PrayerDownloader d = new PrayerDownloader(
                mDateHelper,
                mPrayerClient,
                mPrayerCache,
                mInterfacePreferences
        );

        when(mLocation.getLatitude())
                .thenReturn(3.28011);

        when(mLocation.getLongitude())
                .thenReturn(101.556);

        when(mLocation.distanceTo(any(Location.class)))
                .thenReturn(0f);

        when(mDateHelper.getCurrentMonth())
                .thenReturn(3);

        when(mDateHelper.getCurrentYear())
                .thenReturn(2016);

        when(mDateHelper.getNextMonth())
                .thenReturn(4);

        when(mDateHelper.isNextMonthNewYear())
                .thenReturn(false);

        when(mPrayerCache.get(anyInt(), anyInt(), eq(mLocation)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerCache.get(anyInt(), anyInt(), eq(code)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(4)))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(5)))
                .thenReturn(Observable.<PrayerData>error(new PrayerProviderException("JAKIM")));

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(4)))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(5)))
                .thenReturn(Observable.<PrayerData>error(new PrayerProviderException("JAKIM")));

        TestSubscriber<PrayerContext> testSubscriber1 = new TestSubscriber<>();
        d.getPrayerTimes(mLocation).subscribe(testSubscriber1);
        testSubscriber1.assertNoErrors();

        TestSubscriber<PrayerContext> testSubscriber2 = new TestSubscriber<>();
        d.getPrayerTimes(code).subscribe(testSubscriber2);
        testSubscriber2.assertNoErrors();
    }

    @Test
    public void throwOtherErrorWhenNextPrayerNotAvailable() {
        String code = "ext-153";

        PrayerDownloader d = new PrayerDownloader(
                mDateHelper,
                mPrayerClient,
                mPrayerCache,
                mInterfacePreferences
        );

        when(mLocation.getLatitude())
                .thenReturn(3.28011);

        when(mLocation.getLongitude())
                .thenReturn(101.556);

        when(mLocation.distanceTo(any(Location.class)))
                .thenReturn(0f);

        when(mDateHelper.getCurrentMonth())
                .thenReturn(3);

        when(mDateHelper.getCurrentYear())
                .thenReturn(2016);

        when(mDateHelper.getNextMonth())
                .thenReturn(4);

        when(mDateHelper.isNextMonthNewYear())
                .thenReturn(false);

        when(mPrayerCache.get(anyInt(), anyInt(), eq(mLocation)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerCache.get(anyInt(), anyInt(), eq(code)))
                .thenReturn(Observable.<PrayerData>empty());

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(4)))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerClient.getPrayerTimesByCoordinates(anyDouble(), anyDouble(), anyInt(), eq(5)))
                .thenReturn(Observable.<PrayerData>error(new RuntimeException("JAKIM")));

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(4)))
                .thenReturn(Observable.just(mPrayerData1));

        when(mPrayerClient.getPrayerTimesByCode(eq(code), anyInt(), eq(5)))
                .thenReturn(Observable.<PrayerData>error(new RuntimeException("JAKIM")));

        TestSubscriber<PrayerContext> testSubscriber1 = new TestSubscriber<>();
        d.getPrayerTimes(mLocation).subscribe(testSubscriber1);
        testSubscriber1.assertError(RuntimeException.class);

        TestSubscriber<PrayerContext> testSubscriber2 = new TestSubscriber<>();
        d.getPrayerTimes(code).subscribe(testSubscriber2);
        testSubscriber1.assertError(RuntimeException.class);
    }
}
