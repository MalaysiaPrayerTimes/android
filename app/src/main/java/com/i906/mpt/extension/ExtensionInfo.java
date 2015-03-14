package com.i906.mpt.extension;

import java.util.ArrayList;
import java.util.List;

public class ExtensionInfo {

    public String name;
    public String author;
    public List<Screen> screens = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("[ExtensionInfo name=\"%s\" author=\"%s\" screens=%s]",
                name, author, screens);
    }

    public static class Screen {

        public String apk;
        public String name;
        public String view;

        @Override
        public String toString() {
            return String.format("[Screen name=\"%s\" apk=\"%s\" view=\"%s\"]", name, apk, view);
        }
    }
}
