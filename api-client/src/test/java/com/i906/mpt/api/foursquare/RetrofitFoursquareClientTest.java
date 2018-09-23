package com.i906.mpt.api.foursquare;

import com.i906.mpt.api.BaseClientTest;
import com.i906.mpt.api.TestInterceptor;

import org.junit.Before;
import org.junit.Test;

import okhttp3.OkHttpClient;

/**
 * @author Noorzaini Ilhami
 */
public class RetrofitFoursquareClientTest extends BaseClientTest {

    private RetrofitFoursquareClient mFoursquareClient;
    private TestInterceptor mInterceptor;

    @Before
    public void setup() {
        mInterceptor = new TestInterceptor();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(mInterceptor)
                .build();

        mFoursquareClient = new RetrofitFoursquareClient(client);
    }

    @Test
    public void quotaExceededError() {
        mInterceptor.setFile("4sq-429.json")
                .setCode(429);

        assertError(mFoursquareClient.getMosqueList(3.1390006, 101.677240, 20), QuotaExceededException.class);
    }
}
