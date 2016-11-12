package com.i906.mpt;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.i906.mpt.alarm.StartupReceiver;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.FabricTree;
import com.i906.mpt.internal.Graph;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public abstract class BaseMptApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        onPreCreate();
        StartupReceiver.startup(this);
    }

    public void onPreCreate() {
        Fabric.with(this, new Crashlytics());
        Timber.plant(new FabricTree());

        getGraph()
                .getAnalyticsProvider()
                .initialize(this);
    }

    public Graph getGraph() {
        return Dagger.getGraph(this);
    }

    public void setGraph(Graph graph) {
        Dagger.setGraph(graph);
    }
}
