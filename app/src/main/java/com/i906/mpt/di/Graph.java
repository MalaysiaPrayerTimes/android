package com.i906.mpt.di;

import android.content.Context;

import com.i906.mpt.api.ApiModule;

/**
 * Created by Noorzaini Ilhami on 24/10/2015.
 */
public final class Graph {

    private static MptComponent COMPONENT;

    public static MptComponent get(Context baseContext) {
        if (COMPONENT == null) {
            Context context = baseContext.getApplicationContext();

            COMPONENT = DaggerMptComponent.builder()
                    .apiModule(new ApiModule(context))
                    .systemModule(new SystemModule(context))
                    .build();
        }

        return COMPONENT;
    }
}
