package com.i906.mpt.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MptDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mpt.db";
    private static final int DATABASE_VERSION = 9;

    MptDatabase(Context context) {
        this(context, null);
    }

    MptDatabase(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 6:
                db.execSQL("DROP TABLE IF EXISTS LocationCache");
            case 7:
                db.execSQL("DROP TABLE IF EXISTS Codex");
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
        db.execSQL("DROP TABLE IF EXISTS PrayerData");
    }
}
