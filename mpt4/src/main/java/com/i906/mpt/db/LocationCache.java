package com.i906.mpt.db;

public class LocationCache {

    private Long id;
    private double latitude;
    private double longitude;
    private String code;
    private String provider_code;

    public Long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCode() {
        return code;
    }

    public String getJakimCode() {
        return provider_code;
    }

    public static class Builder {

        private LocationCache cache;

        public Builder() {
            cache = new LocationCache();
        }

        public Builder setId(Long id) {
            cache.id = id;
            return this;
        }

        public Builder setLongitude(double longitude) {
            cache.longitude = longitude;
            return this;
        }

        public Builder setLatitude(double latitude) {
            cache.latitude = latitude;
            return this;
        }

        public Builder setProviderCode(String jakim) {
            cache.provider_code = jakim;
            return this;
        }

        public Builder setCode(String mptCode) {
            cache.code = mptCode;
            return this;
        }

        public LocationCache build() {
            return cache;
        }
    }

    @Override
    public String toString() {
        return "LocationCache{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", code='" + code + '\'' +
                ", provider_code='" + provider_code + '\'' +
                '}';
    }
}
