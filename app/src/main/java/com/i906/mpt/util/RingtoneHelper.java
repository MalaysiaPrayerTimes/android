package com.i906.mpt.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.i906.mpt.settings.azanpicker.Tone;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Noorzaini Ilhami on 18/10/2015.
 */
@Singleton
public class RingtoneHelper {

    private final Context mContext;
    private final StorIOContentResolver mResolver;

    @Inject
    public RingtoneHelper(Context context) {
        mContext = context;
        mResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(context.getContentResolver())
                .build();
    }

    private boolean hasExternalStoragePermission() {
        return ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public Observable<List<Tone>> getExternalMediaList() {
        if (!hasExternalStoragePermission()) {
            List<Tone> empty = Collections.emptyList();
            return Observable.just(empty);
        }

        Query q = Query.builder()
                .uri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                .build();

        return mResolver.get()
                .listOfObjects(Tone.class)
                .withQuery(q)
                .withGetResolver(TONE_RESOLVER)
                .prepare()
                .asRxObservable()
                .take(1);
    }

    public Observable<List<Tone>> getInternalMediaList() {
        Query q = Query.builder()
                .uri(MediaStore.Audio.Media.INTERNAL_CONTENT_URI)
                .where(MediaStore.Audio.AudioColumns.IS_NOTIFICATION + " = 1 OR " +
                        MediaStore.Audio.AudioColumns.IS_ALARM + " = 1")
                .build();

        return mResolver.get()
                .listOfObjects(Tone.class)
                .withQuery(q)
                .withGetResolver(TONE_RESOLVER)
                .prepare()
                .asRxObservable()
                .take(1);
    }

    public Observable<List<Tone>> getToneList() {
        return getInternalMediaList()
                .mergeWith(getExternalMediaList())
                .flatMapIterable(new Func1<List<Tone>, Iterable<Tone>>() {
                    @Override
                    public Iterable<Tone> call(List<Tone> tones) {
                        return tones;
                    }
                })
                .toSortedList(new Func2<Tone, Tone, Integer>() {
                    @Override
                    public Integer call(Tone x, Tone y) {
                        return x.getName().compareTo(y.getName());
                    }
                });
    }

    @Nullable
    public String getToneName(String uri) {
        if (uri == null || uri.isEmpty()) {
            return null;
        }

        Observable<String> i = findTone(uri, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        Observable<String> e;

        if (!hasExternalStoragePermission()) {
            e = Observable.empty();
        } else {
            e = findTone(uri, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        }

        return Observable.concat(i, e)
                .toBlocking()
                .firstOrDefault(null);
    }

    private Observable<String> findTone(String toneUri, Uri contentUri) {
        if (toneUri == null || toneUri.isEmpty()) {
            return Observable.empty();
        }

        Query q = Query.builder()
                .uri(contentUri)
                .where(MediaStore.Audio.AudioColumns.DATA + " = ?")
                .whereArgs(toneUri)
                .build();

        return mResolver.get()
                .object(Tone.class)
                .withQuery(q)
                .withGetResolver(TONE_RESOLVER)
                .prepare()
                .asRxObservable()
                .take(1)
                .flatMap(new Func1<Tone, Observable<String>>() {
                    @Override
                    public Observable<String> call(Tone tone) {
                        if (tone != null) {
                            return Observable.just(tone.getName());
                        }
                        return Observable.empty();
                    }
                });
    }

    private static final DefaultGetResolver<Tone> TONE_RESOLVER = new DefaultGetResolver<Tone>() {
        @NonNull
        @Override
        public Tone mapFromCursor(@NonNull Cursor cursor) {
            Tone t = new Tone();
            t.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            t.setUri(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
            return t;
        }
    };
}
