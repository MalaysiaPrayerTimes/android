package com.i906.mpt.internal;

import com.i906.mpt.main.MainActivity;
import com.mpt.i906.internal.ApiModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Noorzaini Ilhami on 24/10/2015.
 */
@Singleton
@Component(modules = {
        ApiModule.class,
        AppModule.class,
})
public interface Graph {
    ActivityGraph activityGraph(ActivityModule module);
    void inject(MainActivity activity);
}
