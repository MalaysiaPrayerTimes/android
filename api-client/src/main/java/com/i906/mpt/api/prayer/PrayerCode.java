package com.i906.mpt.api.prayer;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerCode {

    String code;
    String city;
    String state;
    String country;
    String provider;

    public String getCode() {
        return code;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getProvider() {
        return provider;
    }

    public static class Builder {
        private final PrayerCode data;

        public Builder() {
            data = new PrayerCode();
        }

        public Builder setCode(String code) {
            data.code = code;
            return this;
        }

        public Builder setCity(String city) {
            data.city = city;
            return this;
        }

        public PrayerCode build() {
            return data;
        }
    }

    @Override
    public String toString() {
        return "PrayerCode{" +
                "code='" + code + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
