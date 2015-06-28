package com.i906.mpt.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.i906.mpt.MptApplication;
import com.i906.mpt.model.PrayerCode;
import com.i906.mpt.model.database.PrayerCodesTableMeta;
import com.i906.mpt.model.database.LocationCacheTableMeta;
import com.i906.mpt.util.PrayerCodePopulator;

import java.util.List;

import javax.inject.Inject;

public class MptDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mpt.db";
    public static final int DATABASE_VERSION = 9;

    @Inject
    protected PrayerCodePopulator mCodePopulator;

    public MptDatabase(Context context) {
        this(context, null);
    }

    public MptDatabase(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, errorHandler);
        MptApplication.component(context).inject(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        initLocationCache(db);
        initPrayerCodes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE IF EXISTS " + LocationCacheTableMeta.TABLE);
        }
        if (oldVersion < 8) {
            db.execSQL("DROP TABLE IF EXISTS Codex");
        }

        switch (oldVersion) {
            case 8:
                upgrade8To9(db);
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion " + newVersion);
        }
    }

    private void upgrade8To9(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS MptStatus");
        db.execSQL("DROP TABLE IF EXISTS Codex");
        initPrayerCodes(db);
    }

    private void initLocationCache(SQLiteDatabase db) {
        db.execSQL(LocationCacheTableMeta.getCreateTableQuery());
    }

    private void initPrayerCodes(SQLiteDatabase db) {
        db.execSQL(PrayerCodesTableMeta.getCreateTableQuery());
        List<PrayerCode> codes = mCodePopulator.getJakimCodes();

        db.beginTransaction();
        try {
            for (int i = 0; i < codes.size(); i++) {
                db.insert(PrayerCodesTableMeta.TABLE, "", PrayerCodesTableMeta.mapToContentValues(codes.get(i)));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
