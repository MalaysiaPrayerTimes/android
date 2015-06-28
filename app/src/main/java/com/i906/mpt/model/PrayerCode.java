package com.i906.mpt.model;

public class PrayerCode {

    private Long id;
    private String district;
    private String place;
    private String jakim;
    private String code;
    private String origin;
    private String duplicate;

    public Long getId() {
        return id;
    }

    public String getDistrict() {
        return district;
    }

    public String getPlace() {
        return place;
    }

    public String getJakimCode() {
        return jakim;
    }

    public String getCode() {
        return code;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDuplicateOf() {
        return duplicate;
    }

    public static class Builder {

        private PrayerCode code;

        public Builder() {
            code = new PrayerCode();
        }

        public Builder setId(Long id) {
            code.id = id;
            return this;
        }

        public Builder setDistrict(String district) {
            code.district = district;
            return this;
        }

        public Builder setPlace(String place) {
            code.place = place;
            return this;
        }

        public Builder setJakimCode(String jakim) {
            code.jakim = jakim;
            return this;
        }

        public Builder setCode(String mptCode) {
            code.code = mptCode;
            return this;
        }

        public Builder setOrigin(String origin) {
            code.origin = origin;
            return this;
        }

        public Builder setDuplicateOf(String id) {
            code.duplicate = id;
            return this;
        }

        public PrayerCode build() {
            return code;
        }
    }

    @Override
    public String toString() {
        return "PrayerCode{" +
                "id=" + id +
                ", district='" + district + '\'' +
                ", place='" + place + '\'' +
                ", jakim='" + jakim + '\'' +
                ", code='" + code + '\'' +
                ", origin='" + origin + '\'' +
                ", duplicate='" + duplicate + '\'' +
                '}';
    }
}
