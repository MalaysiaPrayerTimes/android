package com.i906.mpt.internal;

import com.i906.mpt.alarm.AlarmReceiver;
import com.i906.mpt.alarm.AlarmService;
import com.i906.mpt.main.MainActivity;

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
    void inject(AlarmService service);
    void inject(AlarmReceiver receiver);
}
