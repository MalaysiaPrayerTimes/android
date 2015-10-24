package com.i906.mpt.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.i906.mpt.model.Tone;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Noorzaini Ilhami on 18/10/2015.
 */
@Singleton
public class RingtoneHelper {

    private StorIOContentResolver mResolver;

    @Inject
    public RingtoneHelper(Context context) {
        mResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(context.getContentResolver())
                .build();
    }

    public Observable<List<Tone>> getExternalMediaList() {
        Query q = Query.builder()
                .uri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                .build();

        return mResolver.get()
                .listOfObjects(Tone.class)
                .withQuery(q)
                .withGetResolver(TONE_RESOLVER)
                .prepare()
                .createObservable()
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
                .createObservable()
                .take(1);
    }

    public Observable<List<Tone>> getToneList() {
        return getInternalMediaList()
                .mergeWith(getExternalMediaList())
                .flatMap(Observable::from)
                .toSortedList((x, y) -> {
                    return x.getName().compareTo(y.getName());
                });
    }

    @Nullable
    public String getToneName(String uri) {
        Observable<String> i = findTone(uri, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        Observable<String> e = findTone(uri, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

        return Observable.concat(i ,e)
                .toBlocking()
                .firstOrDefault(null);
    }

    private Observable<String> findTone(String toneUri, Uri contentUri) {
        Query q = Query.builder()
                .uri(contentUri)
                .where(MediaStore.Audio.AudioColumns.DATA + " = ?")
                .whereArgs(toneUri)
                .build();

        return mResolver.get()
                .listOfObjects(Tone.class)
                .withQuery(q)
                .withGetResolver(TONE_RESOLVER)
                .prepare()
                .createObservable()
                .take(1)
                .flatMap(tones -> {
                    if (!tones.isEmpty()) {
                        return Observable.just(tones.get(0).getName());
                    } else {
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
