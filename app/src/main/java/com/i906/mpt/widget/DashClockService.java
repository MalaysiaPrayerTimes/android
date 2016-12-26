package com.i906.mpt.widget;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.i906.mpt.R;
import com.i906.mpt.extension.Extension;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.ServiceModule;
import com.i906.mpt.main.MainActivity;
import com.i906.mpt.prayer.Prayer;
import com.i906.mpt.prayer.PrayerContext;
import com.i906.mpt.prefs.NotificationPreferences;

import java.util.Date;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
public class DashClockService extends DashClockExtension implements WidgetHandler {

    private final static String FORMAT_24 = "kk:mm";
    private final static String FORMAT_12 = "hh:mm";

    @Inject
    NotificationPreferences mNotificationPreferences;

    @Inject
    WidgetDelegate mPresenter;

    @Override
    public void onCreate() {
        super.onCreate();

        Dagger.getGraph(this)
                .serviceGraph(new ServiceModule(this))
                .inject(this);

        mPresenter.setHandler(this);
    }

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);
        addWatchContentUris(new String[] {Extension.PRAYER_CONTEXT_URI.toString()});
    }

    @Override
    protected void onUpdateData(int reason) {
        mPresenter.refreshPrayerContext();
    }

    @Override
    public void handlePrayerContext(PrayerContext prayerContext) {
        Intent intent = new Intent(this, MainActivity.class);

        String[] prayerNames = getResources().getStringArray(R.array.prayer_names);
        Prayer prayer = prayerContext.getNextPrayer();
        int prayerIndex = prayer.getIndex();
        Date prayerTime = prayer.getDate();

        String prayerName = prayerNames[prayerIndex];
        String formattedDate = getFormattedDate(prayerTime);

        String status = String.format("%s\n%s", prayerName, formattedDate);
        String title = String.format("%s — %s", prayerName, formattedDate);
        String body = prayerContext.getLocationName();

        boolean visible = prayer.getDate().getTime() <= System.currentTimeMillis()
                + mNotificationPreferences.getDashClockVisibilityDuration();

        publishUpdate(new ExtensionData()
                .visible(visible)
                .clickIntent(intent)
                .icon(R.drawable.ic_dashclock)
                .status(status)
                .expandedTitle(title)
                .expandedBody(body)
        );
    }

    @Override
    public void handleError(Throwable throwable) {
    }

    private String getFormattedDate(Date date) {
        String f = DateFormat.is24HourFormat(this) ? FORMAT_24 : FORMAT_12;
        return DateFormat.format(f, date).toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.setHandler(null);
    }

    public static void start(Context context) {
        Intent alarm = new Intent(context, DashClockService.class);
        context.startService(alarm);
    }
}
