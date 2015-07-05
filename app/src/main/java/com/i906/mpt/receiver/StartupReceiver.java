package com.i906.mpt.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.i906.mpt.service.AlarmSetupService;

import timber.log.Timber;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.v("Received startup action.");
        AlarmSetupService.setAllAlarms(context);
    }
}
