package com.i906.mpt.internal;

import android.content.Context;

/**
 * @author Noorzaini Ilhami
 */
public final class Dagger {

    private static Graph graph;

    private Dagger() {
    }

    public static Graph getGraph(Context context) {
        if (graph == null) {
            setGraph(DaggerGraph.builder()
                    .apiModule(new ApiModule())
                    .appModule(new AppModule(context))
                    .build());
        }

        return graph;
    }

    public static void setGraph(Graph newgraph) {
        graph = newgraph;
    }
}
