package com.i906.mpt.settings.locationpicker;

import com.i906.mpt.RxJavaResetRule;
import com.i906.mpt.api.prayer.PrayerCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Noorzaini Ilhami
 */
public class LocationPickerPresenterTest {

    @Rule
    public RxJavaResetRule mResetRule = new RxJavaResetRule();

    @Mock
    CodeManager mCodeManager;

    @Mock
    CodeView mView;

    @Captor
    private ArgumentCaptor<List<PrayerCode>> mContentCaptor;

    private LocationPickerPresenter mPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mPresenter = new LocationPickerPresenter(mCodeManager);
        mPresenter.setView(mView);
    }

    @Test
    public void loadsNormalLocationList() {
        when(mCodeManager.getSupportedCodes(anyBoolean()))
                .thenReturn(Observable.just(getPrayerCodes()));

        mPresenter.getCodeList(false);
        verify(mView, never()).showError(any(Throwable.class));

        verify(mView).showCodeList(mContentCaptor.capture());
        List<PrayerCode> codes = mContentCaptor.getValue();
        assertThat(codes).hasSize(3);
    }

    @Test
    public void storesCodeInMemory() {
        when(mCodeManager.getSupportedCodes(anyBoolean()))
                .thenReturn(Observable.just(getPrayerCodes()));

        mPresenter.getCodeList(false);
        mPresenter.getCodeList(false);

        verify(mView, never()).showError(any(Throwable.class));
        verify(mCodeManager, times(1)).getSupportedCodes(anyBoolean());
    }

    @Test
    public void forcesRefresh() {
        when(mCodeManager.getSupportedCodes(anyBoolean()))
                .thenReturn(Observable.just(getPrayerCodes()));

        mPresenter.getCodeList(false);
        mPresenter.getCodeList(true);

        verify(mView, never()).showError(any(Throwable.class));
        verify(mCodeManager, times(2)).getSupportedCodes(anyBoolean());
    }

    @Test
    public void relaysErrors() {
        when(mCodeManager.getSupportedCodes(anyBoolean()))
                .thenReturn(Observable.<List<PrayerCode>>error(new Exception("tehe")));

        mPresenter.getCodeList(false);
        verify(mView, times(1)).showError(any(Throwable.class));
    }

    @Test
    public void filtersFirstWord() {
        when(mCodeManager.getSupportedCodes(anyBoolean()))
                .thenReturn(Observable.just(getPrayerCodes()));

        mPresenter.filter("J");
        verify(mView, never()).showError(any(Throwable.class));

        verify(mView).showCodeList(mContentCaptor.capture());
        List<PrayerCode> codes = mContentCaptor.getValue();
        assertThat(codes).hasSize(1);

        PrayerCode code = codes.get(0);
        assertThat(code.getCity()).isEqualTo("Jitra");
    }

    @Test
    public void filtersSubsequentWords() {
        when(mCodeManager.getSupportedCodes(anyBoolean()))
                .thenReturn(Observable.just(getPrayerCodes()));

        mPresenter.filter("Bangi");
        verify(mView, never()).showError(any(Throwable.class));

        verify(mView).showCodeList(mContentCaptor.capture());
        List<PrayerCode> codes = mContentCaptor.getValue();
        assertThat(codes).hasSize(1);

        PrayerCode code = codes.get(0);
        assertThat(code.getCity()).isEqualTo("Bandar Baru Bangi");
    }

    @Test
    public void refreshRetainsFilter() {
        when(mCodeManager.getSupportedCodes(anyBoolean()))
                .thenReturn(Observable.just(getPrayerCodes()));

        mPresenter.filter("Kajang");
        verify(mView, never()).showError(any(Throwable.class));

        verify(mView, times(1)).showCodeList(mContentCaptor.capture());
        List<PrayerCode> codes = mContentCaptor.getValue();
        assertThat(codes).hasSize(1);

        PrayerCode code = codes.get(0);
        assertThat(code.getCity()).isEqualTo("Kajang");

        mPresenter.getCodeList(true);

        verify(mView, times(2)).showCodeList(mContentCaptor.capture());
        List<PrayerCode> codes2 = mContentCaptor.getValue();
        assertThat(codes2).hasSize(1);

        PrayerCode code2 = codes2.get(0);
        assertThat(code2.getCity()).isEqualTo("Kajang");
    }

    private List<PrayerCode> getPrayerCodes() {
        List<PrayerCode> codes = new ArrayList<>(2);

        codes.add(new PrayerCode.Builder()
                .setCity("Jitra")
                .setCode("ext-153")
                .build());

        codes.add(new PrayerCode.Builder()
                .setCity("Bandar Baru Bangi")
                .setCode("ext-306")
                .build());

        codes.add(new PrayerCode.Builder()
                .setCity("Kajang")
                .setCode("ext-308")
                .build());

        return codes;
    }
}
