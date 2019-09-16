package com.i906.mpt.prayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.i906.mpt.extension.Extension;
import com.i906.mpt.prefs.NotificationPreferences;
import com.i906.mpt.widget.KwgtService;
import com.i906.mpt.widget.WidgetService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class PrayerBroadcaster {

    private final Context mContext;
    private final NotificationPreferences mPreferences;

    @Inject
    public PrayerBroadcaster(Context context, NotificationPreferences prefs) {
        mContext = context;
        mPreferences = prefs;
    }

    public void sendPrayerUpdatedBroadcast() {
        mContext.sendBroadcast(new Intent(Extension.ACTION_PRAYER_CONTEXT_UPDATED));
        mContext.getContentResolver().update(Extension.PRAYER_CONTEXT_URI, null, null, null);
        WidgetService.start(mContext);

        if (isKustomInstalled() && mPreferences.isKwgtEnabled()) {
            KwgtService.start(mContext);
        }
    }

    private boolean isKustomInstalled() {
        PackageManager pm = mContext.getPackageManager();

        String[] kustom = new String[] {
                "org.kustom.lockscreen",
                "org.kustom.wallpaper",
                "org.kustom.watch",
                "org.kustom.widget"
        };

        for (String k : kustom) {
            try {
                pm.getPackageInfo(k, PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                //
            }
        }

        return false;
    }
}
