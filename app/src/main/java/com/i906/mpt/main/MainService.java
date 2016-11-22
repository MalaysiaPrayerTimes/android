package com.i906.mpt.main;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.ServiceModule;
import com.i906.mpt.location.LocationDisabledException;
import com.i906.mpt.prayer.PrayerContext;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
public class MainService extends Service implements MainHandler {

    public static final String ACTION_STARTUP = BuildConfig.APPLICATION_ID + ".action.ACTION_STARTUP";

    @Inject
    MainDelegate mDelegate;

    @Override
    public void onCreate() {
        super.onCreate();

        Dagger.getGraph(this)
                .serviceGraph(new ServiceModule(this))
                .inject(this);

        mDelegate.setHandler(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_STARTUP.equals(action)) {
                mDelegate.startLocationListener();
                return START_REDELIVER_INTENT;
            }
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void handlePrayerContext(PrayerContext prayerContext) {
        Timber.i("Got updated prayer context %s", prayerContext);
    }

    @Override
    public void handleLocation(Location location) {
        Timber.i("Got updated location %s", location);
    }

    @Override
    public void handleError(Throwable t) {
        if (t instanceof SecurityException || t instanceof LocationDisabledException) {
            stopSelf();
        } else {
            Timber.w(t);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDelegate.setHandler(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
