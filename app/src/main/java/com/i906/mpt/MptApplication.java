package com.i906.mpt;

import android.app.Application;
import android.content.Context;

import com.i906.mpt.di.MptComponent;

public class MptApplication extends Application {

    private MptComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mComponent = MptComponent.Initializer.init(this);
    }

    public static MptComponent component(Context context) {
        return ((MptApplication) context.getApplicationContext()).mComponent;
    }
}
