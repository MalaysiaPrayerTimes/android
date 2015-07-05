package com.i906.mpt.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.i906.mpt.MptApplication;
import com.i906.mpt.provider.MptInterface;
import com.i906.mpt.receiver.AlarmReceiver;
import com.i906.mpt.util.GeocoderHelper;
import com.i906.mpt.util.preference.NotificationPrefs;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import timber.log.Timber;

public class AlarmSetupService extends IntentService {

    public static final String ACTION_SET_ALL_ALARMS = "com.i906.mpt.action.ACTION_SET_ALL_ALARMS";
    public static final String ACTION_REFRESH_ALARMS = "com.i906.mpt.action.ACTION_REFRESH_ALARMS";

    public static final String ACTION_NOTIFICATION_PRAYER = "com.i906.mpt.action.NOTIFICATION_PRAYER";
    public static final String ACTION_NOTIFICATION_REMINDER = "com.i906.mpt.action.NOTIFICATION_REMINDER";
    public static final String ACTION_NOTIFICATION_CANCEL = "com.i906.mpt.action.NOTIFICATION_CANCEL";

    private static final String EXTRA_PRAYER_INDEX = "prayer_index";
    private static final String EXTRA_PRAYER_TIME = "prayer_time";
    private static final String EXTRA_PRAYER_LOCATION = "prayer_location";

    private static final int[] PRAYERS = {
            MptInterface.PRAYER_IMSAK,
            MptInterface.PRAYER_SUBUH,
            MptInterface.PRAYER_SYURUK,
            MptInterface.PRAYER_DHUHA,
            MptInterface.PRAYER_ZOHOR,
            MptInterface.PRAYER_ASAR,
            MptInterface.PRAYER_MAGRHIB,
            MptInterface.PRAYER_ISYAK,
    };

    private long mNotificationAppearBefore;
    private long mNotificationClearAfter;
    private long mNotificationOffset;

    @Inject
    protected NotificationPrefs mNotificationPrefs;

    @Inject
    protected MptInterface mInterface;

    @Inject
    protected AlarmManager mAlarmManager;

    public AlarmSetupService() {
        super("AlarmSetupService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MptApplication.component(this).inject(this);
        mNotificationAppearBefore = mNotificationPrefs.getAppearBeforeDuration();
        mNotificationClearAfter = mNotificationPrefs.getClearAfterDuration();
        mNotificationOffset = mNotificationPrefs.getAlarmOffset();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_SET_ALL_ALARMS.equals(action)) {
                handleSetAllAlarms();
            }

            if (ACTION_REFRESH_ALARMS.equals(action)) {
                handleRefreshAlarms();
            }
        }
    }

    private void handleRefreshAlarms() {
        Timber.v("Refreshing alarms.");
        if (mInterface.isPrayerTimesLoaded()) {
            processPrayerTimes();
        }
    }

    private void handleSetAllAlarms() {
        Timber.v("Setting all alarms.");
        try {
            mInterface.refreshBlocking();
            processPrayerTimes();
        } catch (GeocoderHelper.GeocoderError e) {
            Timber.e(e, "Geocoding error occurred while setting alarms.");
        } catch (RetrofitError e) {
            Timber.e(e, "Download error occured while setting alarms.");
        }
    }

    private void processPrayerTimes() {
        List<Date> prayerTimes = mInterface.getCurrentDayPrayerTimes();
        List<Date> nextPrayerTimes = mInterface.getNextDayPrayerTimes();
        String location = mInterface.getLocation();

        if (prayerTimes == null) {
            Timber.e("No prayer times were available.");
            return;
        }

        for (int i = 0, prayersLength = PRAYERS.length; i < prayersLength; i++) {
            int p = PRAYERS[i];
            if (mInterface.prayerHasPassed(p)) {
                createAlarm(p, nextPrayerTimes.get(i), location);
            } else {
                createAlarm(p, prayerTimes.get(i), location);
            }
        }
    }

    private void createAlarm(int prayer, Date prayerTime, String location) {
        long time = prayerTime.getTime();
        long rt = -mNotificationAppearBefore + mNotificationOffset;
        long pt = mNotificationOffset + 250;
        long ct = mNotificationClearAfter + mNotificationOffset;

        if (mNotificationAppearBefore > 0) {
            setAlarm(ACTION_NOTIFICATION_REMINDER, prayer, time, rt, location);
        }

        setAlarm(ACTION_NOTIFICATION_PRAYER, prayer, time, pt, location);

        if (mNotificationClearAfter > 0) {
            setAlarm(ACTION_NOTIFICATION_CANCEL, prayer, time, ct, location);
        }
    }

    private void setAlarm(String action, int index, long time, long triggerOffset, String location) {
        Intent i = createIntent(action, index, time, location);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, time + triggerOffset, pi);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, time + triggerOffset, pi);
        }
        
        Timber.v("Created alarm %s: %s %s %s", index, action, new Date(time + triggerOffset), location);
    }

    private Intent createIntent(String action, int index, long time, String location) {
        Intent h = new Intent(this, AlarmReceiver.class);
        h.setAction(action);
        h.setData(Uri.parse("mpt://" + action + "/" + index));
        h.putExtra(EXTRA_PRAYER_INDEX, index);
        h.putExtra(EXTRA_PRAYER_TIME, time);
        h.putExtra(EXTRA_PRAYER_LOCATION, location);
        return h;
    }

    private static void startService(Context context, String action) {
        Intent intent = new Intent(context, AlarmSetupService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void setAllAlarms(Context context) {
        startService(context, ACTION_SET_ALL_ALARMS);
    }

    public static void refreshAlarms(Context context) {
        startService(context, ACTION_REFRESH_ALARMS);
    }
}
