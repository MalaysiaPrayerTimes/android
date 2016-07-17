package com.i906.mpt;

import android.app.Application;
import android.content.Context;

import com.i906.mpt.internal.AppModule;
import com.i906.mpt.internal.DaggerGraph;
import com.i906.mpt.internal.Graph;
import com.mpt.i906.internal.ApiModule;

import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

public class MptApplication extends Application {

    private Graph mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);

        setGraph(DaggerGraph.builder()
                .apiModule(new ApiModule())
                .appModule(new AppModule(this))
                .build());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public Graph getGraph() {
        return mComponent;
    }

    public void setGraph(Graph graph) {
        mComponent = graph;
    }

    public static Graph graph(Context context) {
        return ((MptApplication) context.getApplicationContext()).getGraph();
    }
}
