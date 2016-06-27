package com.i906.mpt.mosque;

import com.i906.mpt.api.foursquare.Mosque;

import java.util.List;

/**
 * @author Noorzaini Ilhami
 */
interface MosqueView {

    void showMosqueList(List<Mosque> mosqueList);
    void showError(Throwable error);
    void showLoading();
}
