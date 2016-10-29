package com.i906.mpt.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationPreferences {

    private static final String ALARM_OFFSET_KEY = "alarm_offset";
    private static final String APPEAR_KEY = "notification_appear_before_duration";
    private static final String CLEAR_KEY = "notification_clear_after_duration";
    private static final String NOTIFICATION_KEY = "notification_";
    private static final String NOTIFICATION_TONE_KEY = "tone_notification";
    private static final String PRAYER_KEY = "prayer_";
    private static final String REMINDER_TONE_KEY = "tone_reminder";
    private static final String VIBRATE_KEY = "vibrate_";

    private final SharedPreferences mPrefs;

    @Inject
    public NotificationPreferences(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public long getAppearBeforeDuration() {
        return Long.valueOf(mPrefs.getString(APPEAR_KEY, "900000"));
    }

    public long getClearAfterDuration() {
        return Long.valueOf(mPrefs.getString(CLEAR_KEY, "900000"));
    }

    public long getAlarmOffset() {
        return Long.valueOf(mPrefs.getString(ALARM_OFFSET_KEY, "0"));
    }

    public boolean isPrayerEnabled(int prayer) {
        return mPrefs.getBoolean(PRAYER_KEY + prayer, true);
    }

    public void setPrayerEnabled(int prayer, boolean enabled) {
        mPrefs.edit()
                .putBoolean(PRAYER_KEY + prayer, enabled)
                .apply();
    }

    public boolean isNotificationEnabled(int prayer) {
        return mPrefs.getBoolean(NOTIFICATION_KEY + prayer, true);
    }

    public void setNotificationEnabled(int prayer, boolean enabled) {
        mPrefs.edit()
                .putBoolean(NOTIFICATION_KEY + prayer, enabled)
                .apply();
    }

    public boolean isVibrationEnabled(int prayer) {
        return mPrefs.getBoolean(VIBRATE_KEY + prayer, true);
    }

    public void setVibrationEnabled(int prayer, boolean enabled) {
        mPrefs.edit()
                .putBoolean(VIBRATE_KEY + prayer, enabled)
                .apply();
    }

    public boolean hasReminderTone(int prayer) {
        String s = mPrefs.getString(REMINDER_TONE_KEY + prayer, null);
        return s != null && s.length() != 0;
    }

    @Nullable
    public String getReminderTone(int prayer) {
        return mPrefs.getString(REMINDER_TONE_KEY + prayer, null);
    }

    public void setReminderTone(int prayer, String uri) {
        mPrefs.edit()
                .putString(REMINDER_TONE_KEY + prayer, uri)
                .apply();
    }

    public boolean hasNotificationTone(int prayer) {
        String s = mPrefs.getString(NOTIFICATION_TONE_KEY + prayer, null);
        return s != null && s.length() != 0;
    }

    @Nullable
    public String getNotificationTone(int prayer) {
        return mPrefs.getString(NOTIFICATION_TONE_KEY + prayer, null);
    }

    public void setNotificationTone(int prayer, String uri) {
        mPrefs.edit()
                .putString(NOTIFICATION_TONE_KEY + prayer, uri)
                .apply();
    }

    public boolean isHeadsUpEnabled() {
        return mPrefs.getBoolean("prayer_headsup", false);
    }

    public void convertLegacyPreferences() {
        String nb = mPrefs.getString("notf_before", null);
        String na = mPrefs.getString("notf_after", null);
        String ao = mPrefs.getString(ALARM_OFFSET_KEY, null);

        if (nb != null) {
            long nbl = Long.parseLong(nb);

            mPrefs.edit()
                    .remove("notf_before")
                    .putString(APPEAR_KEY, Long.toString(nbl * 60000))
                    .apply();
        }

        if (na != null) {
            long nal = Long.parseLong(na);
            mPrefs.edit()
                    .remove("notf_after")
                    .putString(CLEAR_KEY, Long.toString(nal * 60000))
                    .apply();
        }

        if (ao != null) {
            long aol = Long.parseLong(ao);
            mPrefs.edit()
                    .putString(CLEAR_KEY, Long.toString(aol * 60000))
                    .apply();
        }

        for (int i = 0; i < 8; i++) {
            String ak = "azan_" + i;
            String nt = "noti_" + i;
            String vb = "vibr_" + i;
            String sd = "ring_" + i;
            String rd = "rmdr_" + i;
            boolean b = mPrefs.getBoolean(ak, true);
            boolean n = mPrefs.getBoolean(nt, true);
            boolean v = mPrefs.getBoolean(vb, true);
            String s = mPrefs.getString(sd, null);
            String r = mPrefs.getString(rd, null);

            mPrefs.edit()
                    .remove(ak)
                    .remove(nt)
                    .remove(vb)
                    .remove(sd)
                    .remove(rd)
                    .putBoolean(PRAYER_KEY + i, b)
                    .putBoolean(NOTIFICATION_KEY + i, n)
                    .putBoolean(VIBRATE_KEY + i, v)
                    .putString(NOTIFICATION_TONE_KEY + i, s)
                    .putString(REMINDER_TONE_KEY + i, r)
                    .apply();
        }
    }
}
