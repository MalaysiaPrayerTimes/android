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

    public void setCode(String code) {
        this.code = code;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setJakimCode(String jakim) {
        this.jakim = jakim;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setReadableDate(String readableDate) {
        this.readableDate = readableDate;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLocation() {
        return place;
    }

    public void setLocation(String place) {
        this.place = place;
    }

    public List<List<Date>> getPrayerTimes() {
        return times;
    }

    public void setPrayerTimes(List<List<Date>> times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return String.format("[PrayerData code=\"%s\" jakim=\"%s\" place=\"%s\" date=\"%s\"]",
                code, jakim, place, readableDate);
    }
}
