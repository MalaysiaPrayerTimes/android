package com.i906.mpt.internal;

import com.i906.mpt.alarm.AlarmReceiver;
import com.i906.mpt.prayer.PrayerProvider;

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
    ServiceGraph serviceGraph(ServiceModule module);
    void inject(AlarmReceiver receiver);
    void inject(PrayerProvider provider);
}
