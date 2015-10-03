package com.i906.mpt.extension;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExtensionInfo {

    protected String name;
    protected String author;
    protected int version;
    protected List<Screen> screens = new ArrayList<>();
    protected String apk;

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getVersion() {
        return version;
    }

    public List<Screen> getScreens() {
        return screens;
    }

    public String getApk() {
        return apk;
    }

    @Override
    public String toString() {
        return String.format("[ExtensionInfo name=\"%s\" author=\"%s\" version=%s screens=%s]",
                name, author, version, screens);
    }

    public static class Screen {

        protected boolean isNative = false;
        protected String apk;
        protected String name;
        protected String view;
        protected String settings;
        protected Class<? extends PrayerView> nativeView;

        public String getApk() {
            return apk;
        }

        public String getName() {
            return name;
        }

        public String getView() {
            return view;
        }

        public boolean isNative() {
            return isNative;
        }

        public Class<? extends PrayerView> getNativeView() {
            return nativeView;
        }

        public boolean hasSettings() {
            return getSettings() != null;
        }

        @Nullable
        public String getSettings() {
            return settings;
        }

        @Override
        public String toString() {
            return String.format("[Screen name=\"%s\" apk=\"%s\" view=\"%s\"]", name, apk, view);
        }
    }
}
