package com.i906.mpt.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.i906.mpt.api.prayer.PrayerData;
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

import java.util.Date;
import java.util.List;

import static com.i906.mpt.db.TypeColumns.COMMA;
import static com.i906.mpt.db.TypeColumns.TYPE_INTEGER;
import static com.i906.mpt.db.TypeColumns.TYPE_KEY;
import static com.i906.mpt.db.TypeColumns.TYPE_TEXT;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerCacheMeta {

    public static final String TABLE = "PrayerCache";

    public interface Columns extends BaseColumns {
        String CODE = "code";
        String MONTH = "month";
        String PLACE = "place";
        String PROVIDER = "provider";
        String TIMES = "times";
        String YEAR = "year";
    }

    static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + " (" +
                Columns._ID + TYPE_INTEGER + TYPE_KEY + COMMA +
                Columns.CODE + TYPE_TEXT + COMMA +
                Columns.MONTH + TYPE_INTEGER + COMMA +
                Columns.PLACE + TYPE_TEXT + COMMA +
                Columns.PROVIDER + TYPE_TEXT + COMMA +
                Columns.TIMES + TYPE_TEXT + COMMA +
                Columns.YEAR + TYPE_INTEGER +
                ")";
    }

    public static PrayerCache createCacheModel(PrayerData prayer) {
        PrayerCache m = new PrayerCache();

        m.code = prayer.getCode();
        m.month = prayer.getMonth();
        m.place = prayer.getLocation();
        m.provider = prayer.getProvider();
        m.times = prayer.getPrayerTimes();
        m.year = prayer.getYear();

        return m;
    }

    private static PrayerCache createCacheModel(Cursor cursor) {
        Gson gson = new Gson();
        PrayerCache m = new PrayerCache();

        m.id = getLongValue(cursor, Columns._ID);
        m.code = getStringValue(cursor, Columns.CODE);
        m.month = getIntValue(cursor, Columns.MONTH);
        m.place = getStringValue(cursor, Columns.PLACE);
        m.provider = getStringValue(cursor, Columns.PROVIDER);
        m.year = getIntValue(cursor, Columns.YEAR);

        String t = getStringValue(cursor, Columns.TIMES);
        m.times = gson.fromJson(t, TimeHolder.class).times;

        return m;
    }

    private static ContentValues createContentValues(PrayerCache object) {
        Gson gson = new Gson();
        ContentValues v = new ContentValues(7);

        v.put(Columns._ID, object.id);
        v.put(Columns.CODE, object.code);
        v.put(Columns.MONTH, object.month);
        v.put(Columns.PLACE, object.place);
        v.put(Columns.PROVIDER, object.provider);
        v.put(Columns.TIMES, gson.toJson(new TimeHolder(object.times)));
        v.put(Columns.YEAR, object.year);

        return v;
    }

    private static final PutResolver<PrayerCache> PUT_RESOLVER = new DefaultPutResolver<PrayerCache>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull PrayerCache object) {
            return InsertQuery.builder()
                    .table(TABLE)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull PrayerCache object) {
            return UpdateQuery.builder()
                    .table(TABLE)
                    .where(Columns._ID + " = ?")
                    .whereArgs(object.id)
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull PrayerCache object) {
            return createContentValues(object);
        }
    };

    private static final GetResolver<PrayerCache> GET_RESOLVER = new DefaultGetResolver<PrayerCache>() {
        @NonNull
        @Override
        public PrayerCache mapFromCursor(@NonNull Cursor cursor) {
            return createCacheModel(cursor);
        }
    };

    private static final DeleteResolver<PrayerCache> DELETE_RESOLVER = new DefaultDeleteResolver<PrayerCache>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull PrayerCache object) {
            return DeleteQuery.builder()
                    .table(TABLE)
                    .where(Columns._ID + " = ?")
                    .whereArgs(object.id)
                    .build();
        }
    };

    static final SQLiteTypeMapping<PrayerCache> MAPPER = SQLiteTypeMapping.<PrayerCache>builder()
            .putResolver(PUT_RESOLVER)
            .getResolver(GET_RESOLVER)
            .deleteResolver(DELETE_RESOLVER)
            .build();

    private static long getLongValue(Cursor cursor, String column) {
        return cursor.getLong(cursor.getColumnIndex(column));
    }

    private static int getIntValue(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    private static String getStringValue(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    private static class TimeHolder {
        public List<List<Date>> times;

        TimeHolder(List<List<Date>> times) {
            this.times = times;
        }
    }
}
