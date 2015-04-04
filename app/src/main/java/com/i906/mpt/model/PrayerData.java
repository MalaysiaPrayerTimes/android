package com.i906.mpt.model;

import java.util.Date;
import java.util.List;

public class PrayerData {

    protected String code;
    protected String origin;
    protected String jakim;
    protected String source;
    protected String readableDate;
    protected String lastModified;
    protected String place;
    protected List<List<Date>> times;

    @Override
    public String toString() {
        return String.format("[PrayerData code=\"%s\" jakim=\"%s\" place=\"%s\"]",
                code, jakim, place);
    }
}
