package com.i906.mpt.api.prayer;

import org.junit.Before;
import org.junit.Test;

import okhttp3.OkHttpClient;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Noorzaini Ilhami
 */
public class RetrofitPrayerClientTest {

    private RetrofitPrayerClient mPrayerClient;
    private TestInterceptor mInterceptor;

    @Before
    public void setup() {
        mInterceptor = new TestInterceptor();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(mInterceptor)
                .build();

        mPrayerClient = new RetrofitPrayerClient(client);
    }

    @Test
    public void server200() {
        mInterceptor.setFile("wlp-0.2016.12.json");

        assertCompleted(mPrayerClient.getPrayerTimesByCode("wlp-0", 2016, 12));
        assertCompleted(mPrayerClient.getPrayerTimesByCoordinates(3.1390006, 101.677240, 2016, 12));
    }

    @Test
    public void codes200() {
        mInterceptor.setFile("codes.json");
        assertCompleted(mPrayerClient.getSupportedCodes());
    }

    @Test
    public void unsupportedAttributes() {
        mInterceptor.setFile("sgp-0.2016.12.json");

        assertCompleted(mPrayerClient.getPrayerTimesByCode("wlp-0", 2016, 12));
        assertCompleted(mPrayerClient.getPrayerTimesByCoordinates(3.1390006, 101.677240, 2016, 12));
    }

    @Test
    public void unsupportedFields() {
        mInterceptor.setFile("additional-fields.json");

        assertCompleted(mPrayerClient.getPrayerTimesByCode("wlp-0", 2016, 12));
        assertCompleted(mPrayerClient.getPrayerTimesByCoordinates(3.1390006, 101.677240, 2016, 12));
    }

    @Test
    public void jakimError() {
        mInterceptor.setFile("jakim-error.json")
                .setCode(502);

        Throwable t = assertError(mPrayerClient.getPrayerTimesByCode("wlp-0", 2016, 12), PrayerProviderException.class);
        PrayerProviderException ppe = (PrayerProviderException) t;

        assertThat(ppe.getProviderName()).isEqualToIgnoringCase("jakim");

        Throwable t2 = assertError(mPrayerClient.getPrayerTimesByCoordinates(3.1390006, 101.677240, 2016, 12), PrayerProviderException.class);
        PrayerProviderException ppe2 = (PrayerProviderException) t2;

        assertThat(ppe2.getProviderName()).isEqualToIgnoringCase("jakim");
    }

    @Test
    public void server502() {
        mInterceptor.setFile("server-502.html")
                .setType("text/html")
                .setCode(502);

        serverException();
    }

    @Test
    public void server500() {
        mInterceptor.setFile("php-500.json")
                .setType("application/json")
                .setCode(500);

        serverException();
    }

    @Test
    public void malformed() {
        mInterceptor.setFile("malformed.json")
                .setType("application/json")
                .setCode(200);

        serverException();
    }

    private void serverException() {
        assertError(mPrayerClient.getPrayerTimesByCode("wlp-0", 2016, 12), ServerException.class);
        assertError(mPrayerClient.getPrayerTimesByCoordinates(3.1390006, 101.677240, 2016, 12), ServerException.class);
        assertError(mPrayerClient.getSupportedCodes(), ServerException.class);
    }

    @Test
    public void invalidCode() {
        mInterceptor.setFile("invalid-code.json")
                .setCode(404);

        assertError(mPrayerClient.getPrayerTimesByCode("wlp-0", 2016, 12), UnknownPlaceCodeException.class);
    }

    @Test
    public void unsupportedCoordinates() {
        mInterceptor.setFile("unsupported-coordinates.json")
                .setCode(404);

        assertError(mPrayerClient.getPrayerTimesByCoordinates(3.1390006, 101.677240, 2016, 12), UnsupportedCoordinatesException.class);
    }

    private <O> Throwable assertError(Observable<O> o, Class<? extends Throwable> e) {
        TestSubscriber<O> ts = TestSubscriber.create();
        o.subscribe(ts);
        ts.assertError(e);

        return ts.getOnErrorEvents().get(0);
    }

    private <O> void assertCompleted(Observable<O> o) {
        TestSubscriber<O> ts = TestSubscriber.create();
        o.subscribe(ts);
        ts.assertNoErrors();
        ts.assertCompleted();
    }
}
