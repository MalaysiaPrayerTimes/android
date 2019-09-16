package com.i906.mpt.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.alarm.NotificationHelper;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.ServiceModule;
import com.i906.mpt.prayer.PrayerContext;
import com.pushtorefresh.storio.StorIOException;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
public class WidgetService extends Service implements WidgetHandler {

    public static final String ACTION_PRAYER_TIME_ERROR = BuildConfig.APPLICATION_ID + ".action.PRAYER_TIME_ERROR";
    public static final String ACTION_PRAYER_TIME_UPDATED = BuildConfig.APPLICATION_ID + ".action.PRAYER_TIME_UPDATED";

    public static final String ERROR_LOCATION_DISABLED = "LOCATION_DISABLED";

    @Inject
    WidgetDelegate mPresenter;

    @Inject
    NotificationHelper mNotificationHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.i("Created service");

        Dagger.getGraph(this)
                .serviceGraph(new ServiceModule(this))
                .inject(this);

        executeForeground();
        mPresenter.setHandler(this);
    }

    private void executeForeground() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        startForeground(2, mNotificationHelper.createForegroundNotification("Updating widgets..."));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("Received start command");
        mPresenter.refreshPrayerContext();
        return START_STICKY;
    }

    @Override
    public void handlePrayerContext(PrayerContext prayerContext) {
        Intent intent = new Intent(ACTION_PRAYER_TIME_UPDATED);
        intent.putExtra("prayer_context", (Parcelable) prayerContext);

        Timber.i("Broadcasting intent: %s", intent);

        broadcastWidgetIntent(intent);
        stopSelf();
    }

    @Override
    public void handleError(Throwable throwable) {
        Intent intent = new Intent(ACTION_PRAYER_TIME_ERROR);

        if (throwable instanceof StorIOException) {
            Throwable c = throwable.getCause();
            String msg = c.getMessage();

            if (msg != null) {
                intent.putExtra("message", msg);

                if (msg.contains("Location disabled")) {
                    intent.putExtra("type", ERROR_LOCATION_DISABLED);
                }
            }
        } else {
            Timber.w(throwable, "WidgetService");
        }

        broadcastWidgetIntent(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("Destroyed service");
        mPresenter.setHandler(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(Context context) {
        Timber.i("Starting service");
        Intent alarm = new Intent(context, WidgetService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(alarm);
        } else {
            context.startService(alarm);
        }
    }
}
