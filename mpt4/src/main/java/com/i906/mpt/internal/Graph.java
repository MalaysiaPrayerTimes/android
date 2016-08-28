package com.i906.mpt.internal;

import com.i906.mpt.alarm.AlarmReceiver;
import com.i906.mpt.alarm.AlarmService;
import com.i906.mpt.main.MainActivity;
import com.i906.mpt.settings.DonateActivity;
import com.i906.mpt.settings.NotificationActivity;
import com.i906.mpt.settings.azanpicker.AzanPickerFragment;
import com.i906.mpt.settings.prayer.PrayerNotificationFragment;
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
    void inject(AlarmService service);
    void inject(AlarmReceiver receiver);
    void inject(AzanPickerFragment fragment);
    void inject(DonateActivity activity);
    void inject(NotificationActivity activity);
    void inject(PrayerNotificationFragment fragment);
}
