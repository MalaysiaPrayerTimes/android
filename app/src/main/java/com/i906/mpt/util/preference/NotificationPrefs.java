package com.i906.mpt.util.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationPrefs extends Prefs {

    public static final String ALARM_OFFSET_KEY = "alarm_offset";
    public static final String APPEAR_KEY = "appear_before_duration";
    public static final String CLEAR_KEY = "clear_after_duration";
    public static final String LEGACY_KEY = "converted_legacy";
    public static final String NOTIFICATION_KEY = "notification_";
    public static final String NOTIFICATION_TONE_KEY = "tone_notification";
    public static final String PRAYER_KEY = "prayer_";
    public static final String REMINDER_TONE_KEY = "tone_reminder";
    public static final String VIBRATE_KEY = "vibrate_";

    @Inject
    public NotificationPrefs(Context context) {
        super(context);

        if (!getBoolean(LEGACY_KEY, false)) {
            convertLegacyPrefs();
            setBoolean(LEGACY_KEY, true);
        }
    }

    @Override
    protected String getPrefix() {
        return NOTIFICATION_KEY;
    }

    public long getAppearBeforeDuration() {
        return Long.valueOf(getString(APPEAR_KEY, "900000"));
    }

    public long getClearAfterDuration() {
        return Long.valueOf(getString(CLEAR_KEY, "900000"));
    }

    public long getAlarmOffset() {
        return Long.valueOf(getString(ALARM_OFFSET_KEY, "0"));
    }

    public boolean isPrayerEnabled(int prayer) {
        return getBoolean(PRAYER_KEY + prayer, true);
    }

    public void setPrayerEnabled(int prayer, boolean enabled) {
        setBoolean(PRAYER_KEY + prayer, enabled);
    }

    public boolean isNotificationEnabled(int prayer) {
        return getBoolean(NOTIFICATION_KEY + prayer, true);
    }

    public void setNotificationEnabled(int prayer, boolean enabled) {
        setBoolean(NOTIFICATION_KEY + prayer, enabled);
    }

    public boolean isVibrationEnabled(int prayer) {
        return getBoolean(VIBRATE_KEY + prayer, true);
    }

    public void setVibrationEnabled(int prayer, boolean enabled) {
        setBoolean(VIBRATE_KEY + prayer, enabled);
    }

    public boolean hasReminderTone(int prayer) {
        String s = getString(REMINDER_TONE_KEY + prayer);
        return s != null && s.length() != 0;
    }

    @Nullable
    public String getReminderTone(int prayer) {
        return getString(REMINDER_TONE_KEY + prayer);
    }

    public void setReminderTone(int prayer, String uri) {
        setString(REMINDER_TONE_KEY + prayer, uri);
    }

    public boolean hasNotificationTone(int prayer) {
        String s = getString(NOTIFICATION_TONE_KEY + prayer);
        return s != null && s.length() != 0;
    }

    @Nullable
    public String getNotificationTone(int prayer) {
        return getString(NOTIFICATION_TONE_KEY + prayer);
    }

    public void setNotificationTone(int prayer, String uri) {
        setString(NOTIFICATION_TONE_KEY + prayer, uri);
    }

    protected void convertLegacyPrefs() {
        String nb = getLegacyString("notf_before");
        String na = getLegacyString("notf_after");
        String ao = getLegacyString(ALARM_OFFSET_KEY);

        if (nb != null) {
            clearLegacy("notf_before");
            long nbl = Long.parseLong(nb);
            setString(APPEAR_KEY, Long.toString(nbl * 60000));
        }

        if (na != null) {
            clearLegacy("notf_after");
            long nal = Long.parseLong(na);
            setString(CLEAR_KEY, Long.toString(nal * 60000));
        }

        if (ao != null) {
            clearLegacy(ALARM_OFFSET_KEY);
            long aol = Long.parseLong(ao);
            setString(ALARM_OFFSET_KEY, Long.toString(aol * 60000));
        }

        for (int i = 0; i < 8; i++) {
            String ak = "azan_" + i;
            String nt = "noti_" + i;
            String vb = "vibr_" + i;
            String sd = "ring_" + i;
            String rd = "rmdr_" + i;
            boolean b = getLegacyBoolean(ak, true);
            boolean n = getLegacyBoolean(nt, true);
            boolean v = getLegacyBoolean(vb, true);
            String s = getLegacyString(sd);
            String r = getLegacyString(rd);

            clearLegacy(ak);
            clearLegacy(nt);
            clearLegacy(vb);
            clearLegacy(sd);
            clearLegacy(rd);
            setBoolean(PRAYER_KEY + i, b);
            setBoolean(NOTIFICATION_KEY + i, n);
            setBoolean(VIBRATE_KEY + i, v);
            setString(NOTIFICATION_TONE_KEY + i, s);
            setString(REMINDER_TONE_KEY + i, r);
        }
    }

    @Nullable
    protected String getLegacyString(String key) {
        return getLegacyString(key, null);
    }

    protected String getLegacyString(String key, String defaultValue) {
        final SharedPreferences sharedPreferences = getSharedPreferences(mContext);
        return sharedPreferences.getString(key, defaultValue);
    }

    protected boolean getLegacyBoolean(String key, boolean defaultValue) {
        final SharedPreferences sharedPreferences = getSharedPreferences(mContext);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    protected void clearLegacy(String... keys) {
        if (keys == null || keys.length == 0) {
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences(mContext).edit();

        for (String key : keys) {
            editor.remove(key);
        }

        editor.apply();
    }
}
