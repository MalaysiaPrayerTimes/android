package com.i906.mpt.analytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Noorzaini Ilhami
 */
@Module
public class AnalyticsModule {

    @Provides
    @Singleton
    AnalyticsProvider provideAnalyticsProvider(FabricProvider fa,
                                               FacebookProvider fb,
                                               FirebaseProvider fi,
                                               GoogleProvider g) {
        return new CombinedAnalyticsProvider(fa, fb, fi, g);
    }
}
