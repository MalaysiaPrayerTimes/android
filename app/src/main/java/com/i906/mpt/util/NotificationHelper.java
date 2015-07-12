package com.i906.mpt.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.i906.mpt.R;
import com.i906.mpt.provider.MptInterface;
import com.i906.mpt.ui.MainActivity;
import com.i906.mpt.util.preference.NotificationPrefs;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationHelper {

    private final static long[] PATTERN_VIBRATE = {0, 100, 100, 100, 100, 100, 100, 100};

    private Context mContext;
    private NotificationManagerCompat mNotifier;
    private NotificationPrefs mNotificationPrefs;
    private Vibrator mVibrator;

    private String[] mPrayerNames;

    @Inject
    public NotificationHelper(Context context, NotificationManagerCompat notifier,
                              NotificationPrefs prefs, Vibrator vibrator) {
        mContext = context;
        mNotifier = notifier;
        mNotificationPrefs = prefs;
        mVibrator = vibrator;
        fillStrings();
    }

    public void showPrayerReminder(int prayer, long time, String location) {
        long pt = time + mNotificationPrefs.getAlarmOffset();
        int minutes = (int) (pt - System.currentTimeMillis()) / 60000;
        String prayerName = mPrayerNames[prayer];
        String reminder = getReminderText(prayer, minutes);

        if (mNotificationPrefs.isNotificationEnabled(prayer)) {
            NotificationCompat.Builder builder = getNotificationTemplate();

            builder.setTicker(reminder)
                    .setWhen(time)
                    .setContentTitle(prayerName)
                    .setContentText(reminder)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(reminder)
                                    .setSummaryText(location)
                    );

            mNotifier.notify("reminder", prayer, builder.build());
        }
    }

    private String getReminderText(int prayer, int minutes) {
        Resources r = mContext.getResources();
        String name = mPrayerNames[prayer];

        if (minutes != 0) {
            return r.getQuantityString(R.plurals.notification_reminder, minutes, name, minutes);
        } else {
            return r.getString(R.string.notification_reminder_soon, name);
        }
    }

    public void showPrayerNotification(int prayer, long time, String location) {
        String prayerName = mPrayerNames[prayer];
        String notification = getPrayerText(prayer);

        if (mNotificationPrefs.isNotificationEnabled(prayer)) {
            NotificationCompat.Builder builder = getNotificationTemplate();

            builder.setTicker(notification)
                    .setWhen(time)
                    .setContentTitle(prayerName)
                    .setContentText(notification)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(notification)
                                    .setSummaryText(location)
                    );

            if (mNotificationPrefs.isVibrationEnabled(prayer)) {
                builder.setVibrate(PATTERN_VIBRATE);
            }

            if (mNotificationPrefs.isSoundEnabled(prayer)) {
                builder.setSound(mNotificationPrefs.getSound(prayer));
            }

            mNotifier.notify("prayer", prayer, builder.build());
        } else {
            if (mNotificationPrefs.isVibrationEnabled(prayer)) {
                playVibration();
            }

            if (mNotificationPrefs.isSoundEnabled(prayer)) {
                playRingtone(mNotificationPrefs.getSound(prayer));
            }
        }

        cancel("reminder", prayer);
    }

    private String getPrayerText(int prayer) {
        Resources r = mContext.getResources();
        String name = mPrayerNames[prayer];

        switch (prayer) {
            case MptInterface.PRAYER_IMSAK:
            case MptInterface.PRAYER_SYURUK:
            case MptInterface.PRAYER_DHUHA:
                return r.getString(R.string.notification_prayer_others, name);
            default:
                return r.getString(R.string.notification_prayer_normal, name);
        }
    }

    private NotificationCompat.Builder getNotificationTemplate() {
        Resources r = mContext.getResources();
        Intent i = new Intent(mContext, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                mContext, 907, i, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(mContext)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_prayer)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setColor(r.getColor(R.color.mpt_color_accent))
                .setAutoCancel(true)
                .setContentIntent(pi);
    }

    private void playRingtone(Uri sound) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(mContext, sound);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playVibration() {
        mVibrator.vibrate(PATTERN_VIBRATE, -1);
    }

    public void cancel(int prayerIndex) {
        cancel("prayer", prayerIndex);
    }

    private void cancel(String tag, int prayerIndex) {
        mNotifier.cancel(tag, prayerIndex);
    }

    private void fillStrings() {
        Resources r = mContext.getResources();
        mPrayerNames = r.getStringArray(R.array.mpt_prayer_names);
    }
}
