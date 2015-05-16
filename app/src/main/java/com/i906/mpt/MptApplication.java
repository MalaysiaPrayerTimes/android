package com.i906.mpt;

import android.app.Application;
import android.content.Context;

import com.i906.mpt.di.MptComponent;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MptApplication extends Application {

    private MptComponent mComponent;
    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        mRefWatcher = LeakCanary.install(this);
        mComponent = MptComponent.Initializer.init(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((MptApplication) context.getApplicationContext()).mRefWatcher;
    }

    public static MptComponent component(Context context) {
        return ((MptApplication) context.getApplicationContext()).mComponent;
    }
}
