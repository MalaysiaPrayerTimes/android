package com.i906.mpt.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.i906.mpt.R;
import com.i906.mpt.widget.BarWidgetProvider;
import com.i906.mpt.widget.MiniWidgetProvider;
import com.i906.mpt.widget.TextWidgetProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Noorzaini Ilhami
 */
public class WidgetPreferences {

    private static final String WIDGET_KEY = "widget_";

    private final SharedPreferences mPrefs;
    private final int mDefaultBackgroundColor;

    public WidgetPreferences(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDefaultBackgroundColor = ContextCompat.getColor(context, R.color.widget_background);

    }

    public int getBackgroundColor() {
        return mPrefs.getInt("widget_background_color", mDefaultBackgroundColor);
    }

    public boolean isDhuhaEnabled() {
        return mPrefs.getBoolean("widget_show_dhuha", true);
    }

    public boolean isImsakEnabled() {
        return mPrefs.getBoolean("widget_show_imsak", true);
    }

    public boolean isSyurukEnabled() {
        return mPrefs.getBoolean("widget_show_syuruk", true);
    }

    public boolean isHijriDateEnabled() {
        return mPrefs.getBoolean("widget_show_hijri", true);
    }

    public boolean isMasihiDateEnabled() {
        return mPrefs.getBoolean("widget_show_masihi", false);
    }

    public void disableWidget(Class clazz) {
        mPrefs.edit()
                .putBoolean(WIDGET_KEY + clazz.getName(), false)
                .apply();
    }

    public void enableWidget(Class clazz) {
        mPrefs.edit()
                .putBoolean(WIDGET_KEY + clazz.getName(), true)
                .apply();
    }

    public boolean hasWidgets() {
        Class[] widgets = new Class[] {
                BarWidgetProvider.class,
                MiniWidgetProvider.class,
                TextWidgetProvider.class,
        };

        for (Class w : widgets) {
            if (mPrefs.getBoolean(WIDGET_KEY + w.getName(), false)) {
                return true;
            }
        }

        return false;
    }

    public List<Class> getEnabledWidgetList() {
        List<Class> enabledWidgetList = new ArrayList<>();

        Class[] widgets = new Class[] {
                BarWidgetProvider.class,
                MiniWidgetProvider.class,
                TextWidgetProvider.class,
        };

        for (Class w : widgets) {
            if (mPrefs.getBoolean(WIDGET_KEY + w.getName(), false)) {
                enabledWidgetList.add(w);
            }
        }

        return enabledWidgetList;
    }
}
