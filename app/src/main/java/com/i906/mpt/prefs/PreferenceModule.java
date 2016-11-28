package com.i906.mpt.prefs;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Noorzaini Ilhami
 */
@Module
public class PreferenceModule {

    @Provides
    @Singleton
    protected CommonPreferences provideCommonPreferences(Context context) {
        return new CommonPreferences(context);
    }

    @Provides
    @Singleton
    protected HiddenPreferences provideHiddenPreferences(Context context) {
        return new HiddenPreferences(context);
    }

    @Provides
    @Singleton
    protected InterfacePreferences provideInterfacePreferences(Context context) {
        return new InterfacePreferences(context);
    }

    @Provides
    @Singleton
    protected LocationPreferences provideLocationPreferences(Context context) {
        return new LocationPreferences(context);
    }

    @Provides
    @Singleton
    protected NotificationPreferences provideNotificationPreferences(Context context) {
        return new NotificationPreferences(context);
    }

    @Provides
    @Singleton
    protected WidgetPreferences provideWidgetPreferences(Context context) {
        return new WidgetPreferences(context);
    }
}
