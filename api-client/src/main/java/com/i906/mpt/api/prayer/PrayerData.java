package com.i906.mpt.api.prayer;

import java.util.Date;
import java.util.List;

public class PrayerData {

    private String provider;
    private String code;
    private int year;
    private int month;
    private String place;
    private List<List<Date>> times;

    public String getProvider() {
        return provider;
    }

    void setProvider(String provider) {
        this.provider = provider;
    }

    void setCode(String code) {
        this.code = code;
    }

    public int getYear() {
        return year;
    }

    void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    void setMonth(int month) {
        this.month = month;
    }

    public String getLocation() {
        return place;
    }

    void setLocation(String place) {
        this.place = place;
    }

    public List<List<Date>> getPrayerTimes() {
        return times;
    }

    void setPrayerTimes(List<List<Date>> times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return String.format("[PrayerData code=\"%s\" place=\"%s\" date=\"%s/%s\"]",
                code, place, month, year);
    }
}
