package com.i906.mpt.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides
    @Singleton
    StorIOSQLite provideStorIOSQLite(SQLiteOpenHelper sqLiteOpenHelper) {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(PrayerCache.class, PrayerCacheMeta.MAPPER)
                .addTypeMapping(LocationCache.class, LocationCacheMeta.MAPPER)
                .build();
    }

    @Provides
    @Singleton
    SQLiteOpenHelper provideSQSqLiteOpenHelper(Context context) {
        return new MptDatabase(context);
    }
}
