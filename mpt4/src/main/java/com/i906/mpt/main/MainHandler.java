package com.i906.mpt.main;

import android.location.Location;

/**
 * @author Noorzaini Ilhami
 */
interface MainHandler {
    void handleLocation(Location location);
    void handleError(Throwable t);
}
