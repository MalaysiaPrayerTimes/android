package com.i906.mpt.db;

import java.util.Date;
import java.util.List;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerCache {
    public Long id;
    public String code;
    public int month;
    public String place;
    public String provider;
    public String provider_code;
    public List<List<Date>> times;
    public int year;
}
