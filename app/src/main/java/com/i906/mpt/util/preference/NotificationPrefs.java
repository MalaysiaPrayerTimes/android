package com.i906.mpt.util.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationPrefs extends Prefs {

    @Inject
    public NotificationPrefs(Context context) {
        super(context);
        convertLegacyPrefs();
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
    }

    @Nullable
    protected String getLegacyString(String key) {
        return getLegacyString(key, null);
    }

    protected String getLegacyString(String key, String defaultValue) {
        final SharedPreferences sharedPreferences = getSharedPreferences(mContext);
        return sharedPreferences.getString(key, defaultValue);
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
