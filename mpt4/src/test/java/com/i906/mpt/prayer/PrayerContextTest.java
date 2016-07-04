package com.i906.mpt.prayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.i906.mpt.api.prayer.PrayerData;
import com.i906.mpt.api.prayer.PrayerDataTypeAdapter;
import com.i906.mpt.date.DateTimeHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerContextTest {

    @Mock
    private DateTimeHelper mDateHelper;

    private PrayerContext mPrayerContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        InputStream in6 = this.getClass().getClassLoader().getResourceAsStream("prayer-6.json");
        InputStream in7 = this.getClass().getClassLoader().getResourceAsStream("prayer-7.json");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PrayerData.class, new PrayerDataTypeAdapter())
                .create();

        PrayerData p6 = gson.fromJson(new InputStreamReader(in6), PrayerData.class);
        PrayerData p7 = gson.fromJson(new InputStreamReader(in7), PrayerData.class);

        mPrayerContext = new PrayerContextImpl(mDateHelper, p6, p7);
    }

    @Test
    public void getCurrentPrayer() {
        Calendar n = Calendar.getInstance();
        n.set(2016, 5, 3, 14, 0, 0);

        Calendar c = Calendar.getInstance();

        when(mDateHelper.getCurrentDate())
                .thenReturn(3);

        when(mDateHelper.getNow())
                .thenReturn(n);

        when(mDateHelper.getCalendarInstance())
                .thenReturn(c);

        Prayer prayer = mPrayerContext.getCurrentPrayer();
        assertThat(prayer.getIndex()).isEqualTo(Prayer.PRAYER_ZOHOR);
        assertThat(prayer.getTime()).hasTime(1464930840000L);
    }

    @Test
    public void getNextPrayerSameDay() {
        Calendar n = Calendar.getInstance();
        n.set(2016, 5, 3, 14, 0, 0);

        Calendar c = Calendar.getInstance();

        when(mDateHelper.getCurrentDate())
                .thenReturn(3);

        when(mDateHelper.getNextDate())
                .thenReturn(4);

        when(mDateHelper.isTommorowNewMonth())
                .thenReturn(false);

        when(mDateHelper.getNow())
                .thenReturn(n);

        when(mDateHelper.getCalendarInstance())
                .thenReturn(c);

        Prayer prayer = mPrayerContext.getNextPrayer();
        assertThat(prayer.getIndex()).isEqualTo(Prayer.PRAYER_ASAR);
        assertThat(prayer.getTime()).hasTime(1464943140000L);
    }

    @Test
    public void getNextPrayerDifferentDay() {
        Calendar n = Calendar.getInstance();
        n.set(2016, 5, 3, 21, 0, 0);

        Calendar c = Calendar.getInstance();

        when(mDateHelper.getCurrentDate())
                .thenReturn(3);

        when(mDateHelper.getNextDate())
                .thenReturn(4);

        when(mDateHelper.isTommorowNewMonth())
                .thenReturn(false);

        when(mDateHelper.getNow())
                .thenReturn(n);

        when(mDateHelper.getCalendarInstance())
                .thenReturn(c);

        Prayer prayer = mPrayerContext.getNextPrayer();
        assertThat(prayer.getIndex()).isEqualTo(Prayer.PRAYER_IMSAK);
        assertThat(prayer.getTime()).hasTime(1464989400000L);
    }

    @Test
    public void getNextPrayerDifferentMonth() {
        Calendar n = Calendar.getInstance();
        n.set(2016, 5, 30, 21, 0, 0);

        Calendar c = Calendar.getInstance();

        when(mDateHelper.getCurrentDate())
                .thenReturn(30);

        when(mDateHelper.getNextDate())
                .thenReturn(1);

        when(mDateHelper.isTommorowNewMonth())
                .thenReturn(true);

        when(mDateHelper.getNow())
                .thenReturn(n);

        when(mDateHelper.getCalendarInstance())
                .thenReturn(c);

        Prayer prayer = mPrayerContext.getNextPrayer();
        assertThat(prayer.getIndex()).isEqualTo(Prayer.PRAYER_IMSAK);
        assertThat(prayer.getTime()).hasTime(1467322440000L);
    }

    @Test
    public void getCurrentPrayerList() {
        when(mDateHelper.getCurrentDate())
                .thenReturn(30);

        List<Prayer> list = mPrayerContext.getCurrentPrayerList();

        Prayer p = null;
        for (int i = 0; i < 8; i++) {
            Prayer c = list.get(i);

            assertThat(c.getIndex()).isEqualTo(i);
            assertThat(c.getTime()).isInSameDayAs("2016-06-30");

            if (p != null) {
                assertThat(c.getTime()).isAfter(p.getTime());
            }

            p = c;
        }
    }

    @Test
    public void getNextPrayerListSameMonth() {
        when(mDateHelper.getCurrentDate())
                .thenReturn(3);

        when(mDateHelper.getNextDate())
                .thenReturn(4);

        when(mDateHelper.isTommorowNewMonth())
                .thenReturn(false);

        List<Prayer> list = mPrayerContext.getNextPrayerList();

        Prayer p = null;
        for (int i = 0; i < 8; i++) {
            Prayer c = list.get(i);

            assertThat(c.getIndex()).isEqualTo(i);
            assertThat(c.getTime()).isInSameDayAs("2016-06-04");

            if (p != null) {
                assertThat(c.getTime()).isAfter(p.getTime());
            }

            p = c;
        }
    }

    @Test
    public void getNextPrayerListDifferentMonth() {
        when(mDateHelper.getCurrentDate())
                .thenReturn(30);

        when(mDateHelper.getNextDate())
                .thenReturn(1);

        when(mDateHelper.isTommorowNewMonth())
                .thenReturn(true);

        List<Prayer> list = mPrayerContext.getNextPrayerList();

        Prayer p = null;
        for (int i = 0; i < 8; i++) {
            Prayer c = list.get(i);

            assertThat(c.getIndex()).isEqualTo(i);
            assertThat(c.getTime()).isInSameDayAs("2016-07-01");

            if (p != null) {
                assertThat(c.getTime()).isAfter(p.getTime());
            }

            p = c;
        }
    }
}
