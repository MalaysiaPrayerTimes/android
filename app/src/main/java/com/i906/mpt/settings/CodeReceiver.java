package com.i906.mpt.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Noorzaini Ilhami
 */
public class CodeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {
            HiddenSettingsActivity.start(context);
        }
    }
}
