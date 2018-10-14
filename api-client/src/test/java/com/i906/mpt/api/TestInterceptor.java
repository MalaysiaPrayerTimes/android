package com.i906.mpt.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Noorzaini Ilhami
 */
public class TestInterceptor implements Interceptor {

    private int mCode = 200;
    private String mResponseBody;
    private String mResponseType = "application/json";

    public TestInterceptor setCode(int code) {
        mCode = code;
        return this;
    }

    public TestInterceptor setType(String type) {
        mResponseType = type;
        return this;
    }

    public TestInterceptor setFile(String file) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(file);
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        int result;

        try {
            result = bis.read();
            while (result != -1) {
                buf.write((byte) result);
                result = bis.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mResponseBody = buf.toString();
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        ResponseBody body = ResponseBody.create(MediaType.parse(mResponseType), mResponseBody);

        return new Response.Builder()
                .code(mCode)
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .body(body)
                .message("")
                .addHeader("content-type", mResponseType)
                .build();
    }
}
