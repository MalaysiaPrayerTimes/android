package com.i906.mpt;

import android.app.Application;
import android.content.Context;

import com.i906.mpt.di.Graph;
import com.i906.mpt.di.MptComponent;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

public class MptApplication extends Application {

    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        mRefWatcher = LeakCanary.install(this);
        JodaTimeAndroid.init(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((MptApplication) context.getApplicationContext()).mRefWatcher;
    }

    public static MptComponent component(Context context) {
        return Graph.get(context);
    }
}
