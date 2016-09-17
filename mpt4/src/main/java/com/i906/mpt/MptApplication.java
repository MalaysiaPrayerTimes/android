package com.i906.mpt;

import android.app.Application;
import android.content.Context;

import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.Graph;

import timber.log.Timber;

public class MptApplication extends Application {

    private Graph mComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        setGraph(Dagger.getGraph(this));

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
