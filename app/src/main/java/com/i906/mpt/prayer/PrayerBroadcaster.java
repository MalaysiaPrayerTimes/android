package com.i906.mpt.prayer;

import android.content.Context;
import android.content.Intent;

import com.i906.mpt.extension.Extension;
import com.i906.mpt.widget.WidgetService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class PrayerBroadcaster {

    private final Context mContext;

    @Inject
    public PrayerBroadcaster(Context context) {
        mContext = context;
    }

    public void sendPrayerUpdatedBroadcast() {
        mContext.sendBroadcast(new Intent(Extension.ACTION_PRAYER_CONTEXT_UPDATED));
        mContext.getContentResolver().update(Extension.PRAYER_CONTEXT_URI, null, null, null);
        WidgetService.start(mContext);
    }
}
