package com.i906.mpt.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.i906.mpt.MptApplication;
import com.i906.mpt.extension.Extension;
import com.i906.mpt.prayer.PrayerBroadcaster;

import javax.inject.Inject;

import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {

    @Inject
    NotificationHelper mNotificationHelper;

    @Inject
    PrayerBroadcaster mPrayerBroadcaster;

    @Override
    public void onReceive(Context context, Intent intent) {
        MptApplication.graph(context).inject(this);
        Timber.d("Received alarm action: %s", intent);

        String action = intent.getAction();
        int prayer = intent.getIntExtra(AlarmService.EXTRA_PRAYER_INDEX, -1);
        long time = intent.getLongExtra(AlarmService.EXTRA_PRAYER_TIME, -1);
        String location = intent.getStringExtra(AlarmService.EXTRA_PRAYER_LOCATION);

        if (Extension.ACTION_PRAYER_TIME_UPDATED.equals(action)) {
            startAlarmService(context, Extension.ACTION_PRAYER_TIME_UPDATED);
        }

        if (prayer == -1 || time == -1) return;

        if (AlarmService.ACTION_NOTIFICATION_REMINDER.equals(action)) {
            mNotificationHelper.showPrayerReminder(prayer, time, location, true);
            startAlarmService(context, AlarmService.ACTION_UPDATE_REMINDER);
        }

        if (AlarmService.ACTION_NOTIFICATION_REMINDER_TICK.equals(action)) {
            mNotificationHelper.showPrayerReminder(prayer, time, location, false);
            startAlarmService(context, AlarmService.ACTION_UPDATE_REMINDER);
        }

        if (AlarmService.ACTION_NOTIFICATION_PRAYER.equals(action)) {
            mNotificationHelper.showPrayerNotification(prayer, time, location);
            mPrayerBroadcaster.sendPrayerUpdatedBroadcast();
        }

        if (AlarmService.ACTION_NOTIFICATION_CANCEL.equals(action)) {
            mNotificationHelper.cancel(prayer);
        }
    }

    public static void startAlarmService(Context context, String action) {
        Intent alarm = new Intent(context, AlarmService.class);
        alarm.setAction(action);
        context.startService(alarm);
    }
}
