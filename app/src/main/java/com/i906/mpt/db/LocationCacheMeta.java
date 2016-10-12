package com.i906.mpt.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

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

public class LocationCacheMeta implements TypeColumns {

    public static final String TABLE = "LocationCache";

    private LocationCacheMeta() {
    }

    public interface Columns extends BaseColumns {
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String PROVIDER_CODE = "jakim";
        String CODE = "code";
    }

    static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + " (" +
                Columns._ID + TYPE_INTEGER + TYPE_KEY + COMMA +
                Columns.LATITUDE + TYPE_DOUBLE + COMMA +
                Columns.LONGITUDE + TYPE_DOUBLE + COMMA +
                Columns.PROVIDER_CODE + TYPE_TEXT + COMMA +
                Columns.CODE + TYPE_TEXT +
                ")";
    }

    public static LocationCache createCacheModel(PrayerData prayer, Location location) {
        return new LocationCache.Builder()
                .setCode(prayer.getCode())
                .setProviderCode(prayer.getProviderCode())
                .setLatitude(location.getLatitude())
                .setLongitude(location.getLongitude())
                .build();
    }

    private static ContentValues createContentValues(@NonNull LocationCache object) {
        ContentValues v = new ContentValues(5);
        v.put(Columns._ID, object.getId());
        v.put(Columns.LATITUDE, object.getLatitude());
        v.put(Columns.LONGITUDE, object.getLongitude());
        v.put(Columns.PROVIDER_CODE, object.getJakimCode());
        v.put(Columns.CODE, object.getCode());
        return v;
    }

    private static final PutResolver<LocationCache> PUT_RESOLVER = new DefaultPutResolver<LocationCache>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull LocationCache object) {
            return InsertQuery.builder()
                    .table(TABLE)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull LocationCache object) {
            return UpdateQuery.builder()
                    .table(TABLE)
                    .where(Columns._ID + " = ?")
                    .whereArgs(object.getId())
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull LocationCache object) {
            return createContentValues(object);
        }
    };

    private static final GetResolver<LocationCache> GET_RESOLVER = new DefaultGetResolver<LocationCache>() {
        @NonNull
        @Override
        public LocationCache mapFromCursor(@NonNull Cursor cursor) {
            return new LocationCache.Builder()
                    .setId(cursor.getLong(cursor.getColumnIndex(Columns._ID)))
                    .setLatitude(cursor.getDouble(cursor.getColumnIndex(Columns.LATITUDE)))
                    .setLongitude(cursor.getDouble(cursor.getColumnIndex(Columns.LONGITUDE)))
                    .setProviderCode(cursor.getString(cursor.getColumnIndex(Columns.PROVIDER_CODE)))
                    .setCode(cursor.getString(cursor.getColumnIndex(Columns.CODE)))
                    .build();
        }
    };

    private static final DeleteResolver<LocationCache> DELETE_RESOLVER = new DefaultDeleteResolver<LocationCache>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull LocationCache object) {
            return DeleteQuery.builder()
                    .table(TABLE)
                    .where(Columns._ID + " = ?")
                    .whereArgs(object.getId())
                    .build();
        }
    };

    static final SQLiteTypeMapping<LocationCache> MAPPER = SQLiteTypeMapping.<LocationCache>builder()
            .putResolver(PUT_RESOLVER)
            .getResolver(GET_RESOLVER)
            .deleteResolver(DELETE_RESOLVER)
            .build();
}
