package com.i906.mpt.api;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class MockApiUtils {

    private Context mContext;
    private Gson mGson;

    @Inject
    public MockApiUtils(Context context, Gson gson) {
        mContext = context;
        mGson = gson;
    }

    public <M> M getData(Class<M> clazz, String path) {
        try {
            InputStream is = mContext.getAssets().open(path);
            Reader reader = new InputStreamReader(is);
            return mGson.fromJson(reader, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <M> List<M> getDataList(Class<M> clazz, String path) {
        try {
            InputStream is = mContext.getAssets().open(path);
            Reader reader = new InputStreamReader(is);
            return mGson.fromJson(reader, new WrappedType(List.class, clazz));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    static class WrappedType implements ParameterizedType {

        private Class raw;
        private Class<?> wrapped;

        public WrappedType(Class raw, Class wrapped) {
            this.raw = raw;
            this.wrapped = wrapped;
        }

        public Type[] getActualTypeArguments() {
            return new Type[] {wrapped};
        }

        public Type getRawType() {
            return raw;
        }

        public Type getOwnerType() {
            return null;
        }
    }
}
