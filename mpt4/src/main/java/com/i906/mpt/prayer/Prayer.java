package com.i906.mpt.prayer;

import java.util.Date;

/**
 * Created by Noorzaini Ilhami on 24/10/2015.
 */
public interface Prayer {

    int PRAYER_IMSAK = 0;
    int PRAYER_SUBUH = 1;
    int PRAYER_SYURUK = 2;
    int PRAYER_DHUHA = 3;
    int PRAYER_ZOHOR = 4;
    int PRAYER_ASAR = 5;
    int PRAYER_MAGRHIB = 6;
    int PRAYER_ISYAK = 7;

    int getIndex();
    Date getTime();
}
