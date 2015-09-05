package com.i906.mpt.db.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.i906.mpt.model.PrayerCode;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

public class PrayerCodesTableMeta implements TypeColumns {

    public static final String TABLE = "PrayerCodes";

    public interface Columns extends BaseColumns {
        String DISTRICT = "district";
        String PLACE = "place";
        String JAKIM = "jakim";
        String CODE = "code";
        String ORIGIN = "origin";
        String DUPLICATE = "duplicate";
    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + " (" +
                Columns._ID + TYPE_INTEGER + TYPE_KEY + COMMA +
                Columns.DISTRICT + TYPE_TEXT + NOT_NULL + DEFAULT + EMPTY + COMMA +
                Columns.PLACE + TYPE_TEXT + NOT_NULL + DEFAULT + EMPTY + COMMA +
                Columns.JAKIM + TYPE_TEXT + NOT_NULL + DEFAULT + EMPTY + COMMA +
                Columns.CODE + TYPE_TEXT + NOT_NULL + DEFAULT + EMPTY + COMMA +
                Columns.ORIGIN + TYPE_TEXT + COMMA +
                Columns.DUPLICATE + TYPE_TEXT +
                ")";
    }

    public static ContentValues mapToContentValues(@NonNull PrayerCode object) {
        ContentValues v = new ContentValues(7);
        v.put(Columns._ID, object.getId());
        v.put(Columns.DISTRICT, object.getDistrict());
        v.put(Columns.PLACE, object.getPlace());
        v.put(Columns.JAKIM, object.getJakimCode());
        v.put(Columns.CODE, object.getCode());
        v.put(Columns.ORIGIN, object.getOrigin());
        v.put(Columns.DUPLICATE, object.getDuplicateOf());
        return v;
    }

    public static final PutResolver<PrayerCode> PUT_RESOLVER = new DefaultPutResolver<PrayerCode>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull PrayerCode object) {
            return InsertQuery.builder()
                    .table(TABLE)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull PrayerCode object) {
            return UpdateQuery.builder()
                    .table(TABLE)
                    .where(Columns._ID + " = ?")
                    .whereArgs(object.getId())
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull PrayerCode object) {
            return PrayerCodesTableMeta.mapToContentValues(object);
        }
    };

    public static final GetResolver<PrayerCode> GET_RESOLVER = new DefaultGetResolver<PrayerCode>() {
        @NonNull
        @Override
        public PrayerCode mapFromCursor(@NonNull Cursor cursor) {
            return new PrayerCode.Builder()
                    .setId(cursor.getLong(cursor.getColumnIndex(Columns._ID)))
                    .setDistrict(cursor.getString(cursor.getColumnIndex(Columns.DISTRICT)))
                    .setPlace(cursor.getString(cursor.getColumnIndex(Columns.PLACE)))
                    .setJakimCode(cursor.getString(cursor.getColumnIndex(Columns.JAKIM)))
                    .setCode(cursor.getString(cursor.getColumnIndex(Columns.CODE)))
                    .setOrigin(cursor.getString(cursor.getColumnIndex(Columns.ORIGIN)))
                    .setDuplicateOf(cursor.getString(cursor.getColumnIndex(Columns.DUPLICATE)))
                    .build();
        }
    };

    public static final DeleteResolver<PrayerCode> DELETE_RESOLVER = new DefaultDeleteResolver<PrayerCode>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull PrayerCode object) {
            return DeleteQuery.builder()
                    .table(TABLE)
                    .where(Columns._ID + " = ?")
                    .whereArgs(object.getId())
                    .build();
        }
    };

    public static final SQLiteTypeMapping<PrayerCode> MAPPER = SQLiteTypeMapping.<PrayerCode>builder()
            .putResolver(PUT_RESOLVER)
            .getResolver(GET_RESOLVER)
            .deleteResolver(DELETE_RESOLVER)
            .build();
}
