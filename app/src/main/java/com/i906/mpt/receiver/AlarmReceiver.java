package com.i906.mpt.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.i906.mpt.MptApplication;
import com.i906.mpt.service.AlarmSetupService;
import com.i906.mpt.util.NotificationHelper;

import javax.inject.Inject;

import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {

    @Inject
    protected NotificationHelper mNotificationHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        MptApplication.component(context).inject(this);
        Timber.d("Received alarm action: %s", intent);

        String action = intent.getAction();
        int prayer =  intent.getIntExtra(AlarmSetupService.EXTRA_PRAYER_INDEX, -1);
        long time = intent.getLongExtra(AlarmSetupService.EXTRA_PRAYER_TIME, -1);
        String location = intent.getStringExtra(AlarmSetupService.EXTRA_PRAYER_LOCATION);

        if (prayer == -1 || time == -1) return;

        if (AlarmSetupService.ACTION_NOTIFICATION_REMINDER.equals(action)) {
            mNotificationHelper.showPrayerReminder(prayer, time, location, true);
            AlarmSetupService.setAlarm(context, prayer);
        }

        if (AlarmSetupService.ACTION_NOTIFICATION_REMINDER_TICK.equals(action)) {
            mNotificationHelper.showPrayerReminder(prayer, time, location, false);
        }

        if (AlarmSetupService.ACTION_NOTIFICATION_PRAYER.equals(action)) {
            mNotificationHelper.showPrayerNotification(prayer, time, location);
            AlarmSetupService.setAllAlarms(context);
        }

        if (AlarmSetupService.ACTION_NOTIFICATION_CANCEL.equals(action)) {
            mNotificationHelper.cancel(prayer);
        }
    }
}
