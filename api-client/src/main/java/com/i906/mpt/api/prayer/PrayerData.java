package com.i906.mpt.api.prayer;

import java.util.Date;
import java.util.List;

public class PrayerData {

    private String provider;
    private String provider_code = "";
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

    public String getProviderCode() {
        return provider_code;
    }

    void setProviderCode(String providerCode) {
        this.provider_code = providerCode;
    }

    public String getCode() {
        return code;
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

    public static class Builder {

        private final PrayerData data;

        public Builder() {
            data = new PrayerData();
        }

        public Builder setProvider(String provider) {
            data.setProvider(provider);
            return this;
        }

        public Builder setProviderCode(String code) {
            data.setProviderCode(code);
            return this;
        }

        public Builder setCode(String code) {
            data.setCode(code);
            return this;
        }

        public Builder setYear(int year) {
            data.setYear(year);
            return this;
        }

        public Builder setMonth(int month) {
            data.setMonth(month);
            return this;
        }

        public Builder setLocation(String place) {
            data.setLocation(place);
            return this;
        }

        public Builder setPrayerTimes(List<List<Date>> times) {
            data.setPrayerTimes(times);
            return this;
        }

        public PrayerData build() {
            return data;
        }
    }

    @Override
    public String toString() {
        return String.format("[PrayerData code=\"%s\" place=\"%s\" date=\"%s/%s\"]",
                code, place, month, year);
    }
}
