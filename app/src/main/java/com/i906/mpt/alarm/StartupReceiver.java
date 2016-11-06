package com.i906.mpt.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.i906.mpt.main.MainService;
import com.i906.mpt.widget.WidgetService;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startup(context);
    }

    public static void startup(Context context) {
        startPrayerService(context);
        WidgetService.start(context);
    }

    public static void startPrayerService(Context context) {
        Intent alarm = new Intent(context, MainService.class);
        alarm.setAction(MainService.ACTION_STARTUP);
        context.startService(alarm);
    }
}
