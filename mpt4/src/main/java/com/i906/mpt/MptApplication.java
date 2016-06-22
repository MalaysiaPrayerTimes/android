package com.i906.mpt;

import android.app.Application;
import android.content.Context;

import com.i906.mpt.internal.AppModule;
import com.i906.mpt.internal.DaggerGraph;
import com.i906.mpt.internal.Graph;

public class MptApplication extends Application {

    private Graph mComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        setGraph(DaggerGraph.builder()
                .appModule(new AppModule(this))
                .build());
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
