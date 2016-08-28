package com.i906.mpt.internal;

import android.content.Context;

/**
 * @author Noorzaini Ilhami
 */
public final class Dagger {

    static Graph graph;

    public static Graph getGraph(Context context) {
        if (graph == null) {
            graph = DaggerGraph.builder()
                    .apiModule(new ApiModule())
                    .appModule(new AppModule(context))
                    .build();
        }

        return graph;
    }
}
