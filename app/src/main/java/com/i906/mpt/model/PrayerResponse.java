package com.i906.mpt.model;

public class PrayerResponse {

    protected Meta meta;
    protected PrayerData response;

    public PrayerData getPrayerData() {
        return response;
    }

    public static class Meta {
        int code;
    }
}
