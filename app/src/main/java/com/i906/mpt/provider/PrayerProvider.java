package com.i906.mpt.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.i906.mpt.di.Graph;
import com.i906.mpt.provider.MptContract.CurrentDataInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Noorzaini Ilhami on 24/10/2015.
 */
public class PrayerProvider extends ContentProvider {

    private static final int CURRENT_DATA = 1;

    private static final String AUTHORITY = MptContract.AUTHORITY;
    private static final Uri BASE_URI = MptContract.AUTHORITY_URI;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String[] CURRENT_DATA_COLUMNS = {
            CurrentDataInfo.Columns.LOCATION,
            CurrentDataInfo.Columns.CURRENT_PRAYER_TIME,
            CurrentDataInfo.Columns.CURRENT_PRAYER_INDEX,
            CurrentDataInfo.Columns.NEXT_PRAYER_TIME,
            CurrentDataInfo.Columns.NEXT_PRAYER_INDEX,
            CurrentDataInfo.Columns.PRAYER_IMSAK,
            CurrentDataInfo.Columns.PRAYER_SUBUH,
            CurrentDataInfo.Columns.PRAYER_SYURUK,
            CurrentDataInfo.Columns.PRAYER_DHUHA,
            CurrentDataInfo.Columns.PRAYER_ZOHOR,
            CurrentDataInfo.Columns.PRAYER_ASAR,
            CurrentDataInfo.Columns.PRAYER_MAGRHIB,
            CurrentDataInfo.Columns.PRAYER_ISYAK,
            CurrentDataInfo.Columns.HIJRI_DAY,
            CurrentDataInfo.Columns.HIJRI_MONTH,
            CurrentDataInfo.Columns.HIJRI_YEAR,
    };

    static {
        URI_MATCHER.addURI(AUTHORITY, CurrentDataInfo.BASE_PATH, CURRENT_DATA);
    }

    @Inject
    protected MptInterface mPrayerInterface;

    @Override
    public boolean onCreate() {
        Graph.get(getContext()).inject(this);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case CURRENT_DATA:
                return getCurrentData();
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        int c;

        switch (uriType) {
            case CURRENT_DATA:
                c = 1;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return c;
    }

    private Cursor getCurrentData() {
        MatrixCursor c = new MatrixCursor(CURRENT_DATA_COLUMNS, 1);
        c.addRow(getDataFromInterface());
        return c;
    }

    private List<Object> getDataFromInterface() {
        List<Object> r = new ArrayList<>();

        if (!mPrayerInterface.isPrayerTimesLoaded()) {
            mPrayerInterface.refreshBlocking();
        }

        r.add(mPrayerInterface.getLocation());
        r.add(mPrayerInterface.getCurrentPrayerTime().getTime());
        r.add(mPrayerInterface.getCurrentPrayerIndex());
        r.add(mPrayerInterface.getNextPrayerTime().getTime());
        r.add(mPrayerInterface.getNextPrayerIndex());

        for (Date d : mPrayerInterface.getCurrentDayPrayerTimes()) {
            r.add(d.getTime());
        }

        r.addAll(mPrayerInterface.getHijriDate());

        return r;
    }
}
