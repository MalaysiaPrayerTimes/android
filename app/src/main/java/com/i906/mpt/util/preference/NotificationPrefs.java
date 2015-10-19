package com.i906.mpt.util.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationPrefs extends Prefs {

    @Inject
    public NotificationPrefs(Context context) {
        super(context);

        if (!getBoolean("converted_legacy", false)) {
            convertLegacyPrefs();
            setBoolean("converted_legacy", true);
        }
    }

    @Override
    protected String getPrefix() {
        return "notification_";
    }

    public long getAppearBeforeDuration() {
        return Long.valueOf(getString("appear_before_duration", "900000"));
    }

    public long getClearAfterDuration() {
        return Long.valueOf(getString("clear_after_duration", "900000"));
    }

    public long getAlarmOffset() {
        return Long.valueOf(getString("alarm_offset", "0"));
    }

    public boolean isPrayerEnabled(int prayer) {
        return getBoolean("prayer_" + prayer, true);
    }

    public void setPrayerEnabled(int prayer, boolean enabled) {
        setBoolean("prayer_" + prayer, enabled);
    }

    public boolean isNotificationEnabled(int prayer) {
        return getBoolean("notification_" + prayer, true);
    }

    public void setNotificationEnabled(int prayer, boolean enabled) {
        setBoolean("notification_" + prayer, enabled);
    }

    public boolean isVibrationEnabled(int prayer) {
        return getBoolean("vibrate_" + prayer, true);
    }

    public void setVibrationEnabled(int prayer, boolean enabled) {
        setBoolean("vibrate_" + prayer, enabled);
    }

    public boolean isSoundEnabled(int prayer) {
        String s = getString("sound_" + prayer);
        return s == null || s.length() == 0;
    }

    @Nullable
    public Uri getSound(int prayer) {
        String s = getString("sound_" + prayer);
        if (s == null) return null;
        return Uri.parse(s);
    }

    protected void convertLegacyPrefs() {
        String nb = getLegacyString("notf_before");
        String na = getLegacyString("notf_after");
        String ao = getLegacyString("alarm_offset");

        if (nb != null) {
            clearLegacy("notf_before");
            long nbl = Long.parseLong(nb);
            setString("appear_before_duration", Long.toString(nbl * 60000));
        }

        if (na != null) {
            clearLegacy("notf_after");
            long nal = Long.parseLong(na);
            setString("clear_after_duration", Long.toString(nal * 60000));
        }

        if (ao != null) {
            clearLegacy("alarm_offset");
            long aol = Long.parseLong(ao);
            setString("alarm_offset", Long.toString(aol * 60000));
        }

        for (int i = 0; i < 8; i++) {
            String ak = "azan_" + i;
            String nt = "noti_" + i;
            String vb = "vibr_" + i;
            String sd = "ring_" + i;
            boolean b = getLegacyBoolean(ak, true);
            boolean n = getLegacyBoolean(nt, true);
            boolean v = getLegacyBoolean(vb, true);
            String s = getLegacyString(sd);

            clearLegacy(ak);
            clearLegacy(nt);
            clearLegacy(vb);
            clearLegacy(sd);
            setBoolean("prayer_" + i, b);
            setBoolean("notification_" + i, n);
            setBoolean("vibrate_" + i, v);
            setString("sound_" + i, s);
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
