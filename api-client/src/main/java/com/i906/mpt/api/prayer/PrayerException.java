package com.i906.mpt.api.prayer;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerException extends RuntimeException {

    public PrayerException(String s) {
        super(s);
    }

    public PrayerException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
