package com.i906.mpt.internal;

import com.i906.mpt.alarm.AlarmReceiver;
import com.i906.mpt.analytics.AnalyticsModule;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.db.DbModule;
import com.i906.mpt.prayer.PrayerProvider;
import com.i906.mpt.prefs.InterfacePreferences;
import com.i906.mpt.prefs.PreferenceModule;
import com.i906.mpt.prefs.WidgetPreferences;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Noorzaini Ilhami on 24/10/2015.
 */
@Singleton
@Component(modules = {
        AnalyticsModule.class,
        ApiModule.class,
        AppModule.class,
        BaseOkHttpModule.class,
        DbModule.class,
        OkHttpModule.class,
        PreferenceModule.class,
})
public interface Graph {
    ActivityGraph activityGraph(ActivityModule module);
    ServiceGraph serviceGraph(ServiceModule module);
    void inject(AlarmReceiver receiver);
    void inject(PrayerProvider provider);
    AnalyticsProvider getAnalyticsProvider();
    InterfacePreferences getInterfacePreferences();
    WidgetPreferences getWidgetPreferences();
}
