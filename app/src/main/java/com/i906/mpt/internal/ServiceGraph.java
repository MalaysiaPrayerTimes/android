package com.i906.mpt.internal;

import com.i906.mpt.alarm.AlarmService;
import com.i906.mpt.main.MainService;
import com.i906.mpt.widget.DashClockService;
import com.i906.mpt.widget.WidgetService;

import dagger.Subcomponent;

/**
 * @author Noorzaini Ilhami
 */
@PerService
@Subcomponent(modules = {
        ServiceModule.class,
})
public interface ServiceGraph {
    void inject(AlarmService service);
    void inject(DashClockService service);
    void inject(MainService service);
    void inject(WidgetService service);
}
