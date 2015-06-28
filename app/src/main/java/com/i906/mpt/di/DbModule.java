package com.i906.mpt.di;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.i906.mpt.database.MptDatabase;
import com.i906.mpt.model.PrayerCode;
import com.i906.mpt.model.LocationCache;
import com.i906.mpt.model.database.PrayerCodesTableMeta;
import com.i906.mpt.model.database.LocationCacheTableMeta;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides
    @Singleton
    public StorIOSQLite provideStorIOSQLite(SQLiteOpenHelper sqLiteOpenHelper) {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(PrayerCode.class, PrayerCodesTableMeta.MAPPER)
                .addTypeMapping(LocationCache.class, LocationCacheTableMeta.MAPPER)
                .build();
    }

    @Provides
    @Singleton
    public SQLiteOpenHelper provideSQSqLiteOpenHelper(Context context) {
        return new MptDatabase(context);
    }
}
