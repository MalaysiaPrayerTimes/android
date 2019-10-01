package com.i906.mpt.prayer;

import android.content.Context;
import android.content.Intent;

import com.i906.mpt.alarm.AlarmService;
import com.i906.mpt.extension.Extension;
import com.i906.mpt.prefs.WidgetPreferences;
import com.i906.mpt.widget.WidgetService;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class PrayerBroadcaster {

    private final Context mContext;
    private final WidgetPreferences mWidgetPreferences;

    @Inject
    public PrayerBroadcaster(Context context, WidgetPreferences widget) {
        mContext = context;
        mWidgetPreferences = widget;
    }

    public void sendPrayerUpdatedBroadcast() {
        Timber.i("Broadcasting updated prayer context");

        mContext.sendBroadcast(new Intent(Extension.ACTION_PRAYER_CONTEXT_UPDATED));
        mContext.getContentResolver().update(Extension.PRAYER_CONTEXT_URI, null, null, null);

        AlarmService.start(mContext, Extension.ACTION_PRAYER_CONTEXT_UPDATED);

        if (mWidgetPreferences.hasWidgets()) {
            WidgetService.start(mContext);
        }
    }
}
