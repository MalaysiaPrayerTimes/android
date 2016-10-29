package com.i906.mpt;

import android.app.Application;
import android.content.Context;

import com.i906.mpt.alarm.StartupReceiver;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.FabricTree;
import com.i906.mpt.internal.FirebaseTree;
import com.i906.mpt.internal.Graph;

import timber.log.Timber;

public class BaseMptApplication extends Application {

    private Graph mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setGraph(Dagger.getGraph(this));
        onPreCreate();
        StartupReceiver.startup(this);
    }

    public void onPreCreate() {
        Timber.plant(new FirebaseTree());
        Timber.plant(new FabricTree());
    }

    public Graph getGraph() {
        return mComponent;
    }

    public void setGraph(Graph graph) {
        mComponent = graph;
    }

    public static Graph graph(Context context) {
        return ((BaseMptApplication) context.getApplicationContext()).getGraph();
    }
}
