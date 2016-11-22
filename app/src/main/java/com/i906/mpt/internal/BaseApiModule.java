package com.i906.mpt.internal;

import android.content.Context;

import com.i906.mpt.R;
import com.i906.mpt.api.foursquare.FoursquareClient;
import com.i906.mpt.api.foursquare.FoursquareHttpInterceptor;
import com.i906.mpt.api.foursquare.RetrofitFoursquareClient;
import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.api.prayer.RetrofitPrayerClient;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * @author Noorzaini Ilhami
 */
class BaseApiModule {

    private BaseApiModule() {
    }

    static PrayerClient getPrayerClient(OkHttpClient client) {
        OkHttpClient.Builder mptClient = client.newBuilder();
        return new RetrofitPrayerClient(mptClient.build());
    }

    static FoursquareClient getFoursquareClient(OkHttpClient client, Context context) {
        String fid = context.getString(R.string.foursquare_id);
        String fsc = context.getString(R.string.foursquare_secret);

        Interceptor i = new FoursquareHttpInterceptor(fid, fsc);

        OkHttpClient.Builder foursquareClient = client.newBuilder();
        foursquareClient.interceptors().add(0, i);

        return new RetrofitFoursquareClient(foursquareClient.build());
    }
}
