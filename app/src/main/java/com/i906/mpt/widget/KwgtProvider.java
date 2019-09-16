package com.i906.mpt.widget;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Noorzaini Ilhami
 */
public class KwgtProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {

        MatrixCursor c = new MatrixCursor(new String[]{"Kode", "Description"});

        c.addRow(new String[]{"$br(mpt, loc)$", "Current location"});
        c.addRow(new String[]{"$br(mpt, cpi)$", "Current prayer index"});
        c.addRow(new String[]{"$br(mpt, cpn)$", "Current prayer name"});
        c.addRow(new String[]{"$df(hh:mm a, br(mpt, cpt))$", "Current prayer time"});
        c.addRow(new String[]{"$br(mpt, npi)$", "Next prayer index"});
        c.addRow(new String[]{"$br(mpt, npn)$", "Next prayer name"});
        c.addRow(new String[]{"$df(hh:mm a, br(mpt, npt))$", "Next prayer time"});
        c.addRow(new String[]{"$br(mpt, npn)$ in $tf(br(mpt, npt), M)$ minutes", "Minutes till next prayer"});
        c.addRow(new String[]{"$df(hh:mm a, br(mpt, pt1))$", "Current Subuh time"});
        c.addRow(new String[]{"$df(hh:mm a, br(mpt, pt4))$", "Current Zohor time"});
        c.addRow(new String[]{"$br(mpt, slm)$", "Last status message"});
        c.addRow(new String[]{"$df(hh:mm:ss a, br(mpt, slu))$", "Last status update"});

        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs) {
        return 0;
    }
}
