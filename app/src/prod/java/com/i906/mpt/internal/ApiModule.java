package com.i906.mpt.internal;

import android.content.Context;

import com.i906.mpt.api.foursquare.FoursquareClient;
import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.prefs.HiddenPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * @author Noorzaini Ilhami
 */
@Module
class ApiModule {

    @Provides
    @Singleton
    PrayerClient providePrayerClient(OkHttpClient client) {
        return BaseApiModule.getPrayerClient(client);
    }

    @Provides
    @Singleton
    FoursquareClient provideFoursquareClient(OkHttpClient client, Context context, HiddenPreferences prefs) {
        return BaseApiModule.getFoursquareClient(client, context, prefs);
    }
}
