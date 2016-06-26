package com.mpt.i906.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.i906.mpt.api.foursquare.FoursquareClient;
import com.mpt.i906.api.MockApiUtils;
import com.mpt.i906.api.foursquare.MockFoursquareClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Noorzaini Ilhami
 */
@Module
public class ApiModule {

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    FoursquareClient provideFoursquareClient(MockApiUtils utils) {
        return new MockFoursquareClient(utils);
    }
}
