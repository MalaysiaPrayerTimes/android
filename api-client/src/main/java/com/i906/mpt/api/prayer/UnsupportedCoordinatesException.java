package com.i906.mpt.api.prayer;

/**
 * @author Noorzaini Ilhami
 */
public class UnsupportedCoordinatesException extends PrayerException {

    public UnsupportedCoordinatesException(String s) {
        super(s);
    }

    public UnsupportedCoordinatesException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
