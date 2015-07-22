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
import com.i906.mpt.util.DateTimeHelper;
import com.i906.mpt.util.GeocoderHelper;
import com.i906.mpt.util.preference.NotificationPrefs;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import timber.log.Timber;

public class AlarmSetupService extends IntentService {

    public static final String ACTION_SET_ALARM = "com.i906.mpt.action.ACTION_SET_ALARM";
    public static final String ACTION_SET_ALL_ALARMS = "com.i906.mpt.action.ACTION_SET_ALL_ALARMS";
    public static final String ACTION_REFRESH_ALARMS = "com.i906.mpt.action.ACTION_REFRESH_ALARMS";

    public static final String ACTION_NOTIFICATION_PRAYER = "com.i906.mpt.action.NOTIFICATION_PRAYER";
    public static final String ACTION_NOTIFICATION_REMINDER = "com.i906.mpt.action.NOTIFICATION_REMINDER";
    public static final String ACTION_NOTIFICATION_REMINDER_TICK = "com.i906.mpt.action.NOTIFICATION_REMINDER_TICK";
    public static final String ACTION_NOTIFICATION_CANCEL = "com.i906.mpt.action.NOTIFICATION_CANCEL";

    public static final String EXTRA_PRAYER_INDEX = "prayer_index";
    public static final String EXTRA_PRAYER_TIME = "prayer_time";
    public static final String EXTRA_PRAYER_LOCATION = "prayer_location";

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

    @Inject
    protected DateTimeHelper mDateTimeHelper;

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

            if (ACTION_SET_ALARM.equals(action)) {
                int prayerIndex = intent.getIntExtra(EXTRA_PRAYER_INDEX, -1);
                handleSetAlarm(prayerIndex);
            }
        }
    }

    private void handleRefreshAlarms() {
        Timber.d("Refreshing alarms.");
        if (mInterface.isPrayerTimesLoaded()) {
            processPrayerTimes(-1);
        }
    }

    private void handleSetAllAlarms() {
        Timber.d("Setting all alarms.");
        try {
            mInterface.refreshBlocking();
            processPrayerTimes(-1);
        } catch (GeocoderHelper.GeocoderError e) {
            Timber.e(e, "Geocoding error occurred while setting alarms.");
        } catch (RetrofitError e) {
            Timber.e(e, "Download error occured while setting alarms.");
        }
    }

    private void handleSetAlarm(int index) {
        if (index != -1) {
            Timber.d("Setting alarm %s.", index);
            try {
                mInterface.refreshBlocking();
                processPrayerTimes(index);
            } catch (GeocoderHelper.GeocoderError e) {
                Timber.e(e, "Geocoding error occurred while setting alarm %s.", index);
            } catch (RetrofitError e) {
                Timber.e(e, "Download error occured while setting alarm %s.", index);
            }
        }
    }

    private void processPrayerTimes(int index) {
        String location = mInterface.getLocation();

        if (index == -1) {
            for (int PRAYER : PRAYERS) {
                createAlarm(PRAYER, location);
            }
        } else {
            createAlarm(PRAYERS[index], location);
        }
    }

    private void createAlarm(int prayer, String location) {
        List<Date> prayerTimes = mInterface.getCurrentDayPrayerTimes();
        List<Date> nextPrayerTimes = mInterface.getNextDayPrayerTimes();

        if (prayerTimes == null || nextPrayerTimes == null) {
            Timber.e("No prayer times were available.");
            return;
        }

        long currentTime = prayerTimes.get(prayer).getTime();
        long nextTime = nextPrayerTimes.get(prayer).getTime();
        long now = mDateTimeHelper.getCurrentTime();
        boolean passed = mInterface.prayerHasPassed(prayer);
        long pt = mNotificationOffset + 250;
        long ct = mNotificationClearAfter + mNotificationOffset;

        if (mNotificationAppearBefore > 0) {
            setReminderUpdater(prayer, passed ? nextTime : currentTime, location);
        }

        setAlarm(ACTION_NOTIFICATION_PRAYER, prayer, passed ? nextTime : currentTime, pt, location);

        if (mNotificationClearAfter > 0) {
            boolean cancelPassed = passed && (currentTime + ct) < now;
            setAlarm(ACTION_NOTIFICATION_CANCEL, prayer,
                    cancelPassed ? nextTime : currentTime, ct, location);
        }
    }

    private void setReminderUpdater(int index, long prayerTime, String location) {
        long now = mDateTimeHelper.getCurrentTime();
        long trigger;
        boolean first = false;

        int updates = (int) (mNotificationAppearBefore / 60000);
        String action;

        for (int i = updates - 1; i > 0; i--) {
            trigger = (i * mNotificationAppearBefore / updates);

            if (now < prayerTime - trigger + mNotificationOffset) {
                action = !first ? ACTION_NOTIFICATION_REMINDER : ACTION_NOTIFICATION_REMINDER_TICK;
                first = true;
                setAlarm(action, index, prayerTime, -trigger + mNotificationOffset, location);
            }
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

    private static void startService(Context context, String action, int prayerIndex) {
        Intent intent = new Intent(context, AlarmSetupService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_PRAYER_INDEX, prayerIndex);
        context.startService(intent);
    }

    public static void setAllAlarms(Context context) {
        startService(context, ACTION_SET_ALL_ALARMS, -1);
    }

    public static void refreshAlarms(Context context) {
        startService(context, ACTION_REFRESH_ALARMS, -1);
    }

    public static void setAlarm(Context context, int prayerIndex) {
        startService(context, ACTION_SET_ALARM, prayerIndex);
    }
}
