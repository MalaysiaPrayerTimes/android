package com.i906.mpt.main;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.i906.mpt.internal.Dagger;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
public class MainService extends Service implements MainHandler {

    public static final String ACTION_STARTUP = "com.i906.mpt.action.ACTION_STARTUP";

    @Inject
    MainDelegate mDelegate;

    @Override
    public void onCreate() {
        super.onCreate();
        Dagger.getGraph(this).inject(this);
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
    public void handleLocation(Location location) {
    }

    @Override
    public void handleError(Throwable t) {
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
