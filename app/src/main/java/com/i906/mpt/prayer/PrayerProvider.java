package com.i906.mpt.prayer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.i906.mpt.extension.Columns;
import com.i906.mpt.extension.Extension;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.location.LocationDisabledException;
import com.i906.mpt.location.LocationTimeoutException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

/**
 * Created by Noorzaini Ilhami on 24/10/2015.
 */
public class PrayerProvider extends ContentProvider {

    private static final int PRAYER_CONTEXT = 1;

    private static final String AUTHORITY = Extension.AUTHORITY;
    private static final Uri BASE_URI = Extension.AUTHORITY_URI;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String[] PRAYER_CONTEXT_COLUMNS = {
            Columns.LOCATION,
            Columns.CURRENT_PRAYER_TIME,
            Columns.CURRENT_PRAYER_INDEX,
            Columns.NEXT_PRAYER_TIME,
            Columns.NEXT_PRAYER_INDEX,
            Columns.PRAYER_IMSAK,
            Columns.PRAYER_SUBUH,
            Columns.PRAYER_SYURUK,
            Columns.PRAYER_DHUHA,
            Columns.PRAYER_ZOHOR,
            Columns.PRAYER_ASAR,
            Columns.PRAYER_MAGRHIB,
            Columns.PRAYER_ISYAK,
            Columns.HIJRI_DAY,
            Columns.HIJRI_MONTH,
            Columns.HIJRI_YEAR,
    };

    static {
        URI_MATCHER.addURI(AUTHORITY, Extension.PRAYER_CONTEXT, PRAYER_CONTEXT);
    }

    @Inject
    PrayerManager mPrayerManager;

    @Override
    public boolean onCreate() {
        Dagger.getGraph(getContext()).inject(this);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case PRAYER_CONTEXT:
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
            case PRAYER_CONTEXT:
                c = 1;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return c;
    }

    private Cursor getCurrentData() {
        MatrixCursor c = new MatrixCursor(PRAYER_CONTEXT_COLUMNS, 1);
        c.addRow(getPrayerContext());
        return c;
    }

    private List<Object> getPrayerContext() {
        List<Object> r = new ArrayList<>();

        try {
            PrayerContext prayerContext = mPrayerManager.getPrayerContext(false)
                    .observeOn(Schedulers.immediate())
                    .toBlocking()
                    .first();

            r.add(prayerContext.getLocationName());
            r.add(prayerContext.getCurrentPrayer().getDate().getTime());
            r.add(prayerContext.getCurrentPrayer().getIndex());
            r.add(prayerContext.getNextPrayer().getDate().getTime());
            r.add(prayerContext.getNextPrayer().getIndex());

            for (Prayer p : prayerContext.getCurrentPrayerList()) {
                r.add(p.getDate().getTime());
            }

            for (Integer i : prayerContext.getHijriDate()) {
                r.add(i);
            }

            return r;
        } catch (LocationDisabledException e) {
            throw new IllegalStateException("Location disabled.");
        } catch (LocationTimeoutException e) {
            throw new IllegalStateException("Location timed out.");
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
