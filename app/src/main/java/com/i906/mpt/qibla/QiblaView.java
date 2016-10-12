package com.i906.mpt.qibla;

/**
 * @author Noorzaini Ilhami
 */
interface QiblaView {

    void showAzimuth(float azimuth);
    void showError(Throwable error);
    void showLoading();
}
