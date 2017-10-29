package com.i906.mpt.api.prayer;

import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Noorzaini Ilhami
 */
class ErrorWrapper implements Func1<Throwable, Observable<? extends PrayerResponse>> {

    private final Retrofit mRetrofit;

    ErrorWrapper(Retrofit retrofit) {
        mRetrofit = retrofit;
    }

    @Override
    public Observable<? extends PrayerResponse> call(Throwable e) {
        return Observable.error(wrapError(mRetrofit, e));
    }

    static Throwable wrapError(Retrofit retrofit, Throwable e) {
        if (e instanceof HttpException) {
            HttpException he = (HttpException) e;

            ErrorResponse r = parseError(retrofit, he.response());

            if (r != null && r.message != null) {
                if (r.message.contains("No provider support found for coordinate")) {
                    UnsupportedCoordinatesException uce = new UnsupportedCoordinatesException(r.message, e);
                    uce.setProviderName(r.provider);

                    return uce;
                }

                if (r.message.contains("Unknown place code")) {
                    UnknownPlaceCodeException upce = new UnknownPlaceCodeException(r.message, e);
                    upce.setProviderName(r.provider);

                    return upce;
                }

                if (r.message.contains("Data format at e-solat") || r.message.contains("Error connecting to")) {
                    PrayerProviderException ppe = new PrayerProviderException(r.message, e);

                    if (!r.hasProviderName()) {
                        ppe.setProviderName("JAKIM");
                    } else {
                        ppe.setProviderName(r.provider);
                    }

                    return ppe;
                }
            }

            return new ServerException("Server error " + he.code(), he);
        }

        if (e instanceof MalformedJsonException) {
            return new ServerException("Malformed JSON", e);
        }

        return e;
    }

    private static ErrorResponse parseError(Retrofit retrofit, Response<?> response) {
        if (response == null) return null;

        Converter<ResponseBody, ErrorResponse> converter = retrofit
                .responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        ErrorResponse error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return null;
        }

        return error;
    }
}
