package com.i906.mpt.api.prayer;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerException extends RuntimeException {

    private String providerName;

    public PrayerException(String s) {
        super(s);
    }

    public PrayerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public String getProviderName() {
        return providerName;
    }

    public boolean hasProviderName() {
        return providerName != null;
    }

    public void setProviderName(String name) {
        providerName = name;
    }
}
