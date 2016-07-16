package com.i906.mpt.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startAlarmStartup(context);
    }

    public static void startAlarmStartup(Context context) {
        Intent alarm = new Intent(context, AlarmService.class);
        alarm.setAction(AlarmService.ACTION_STARTUP);
        context.startService(alarm);
    }
}
