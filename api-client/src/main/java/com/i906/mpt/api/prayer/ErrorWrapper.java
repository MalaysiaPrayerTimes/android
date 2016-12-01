package com.i906.mpt.api.prayer;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
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
        if (e instanceof HttpException) {
            HttpException he = (HttpException) e;

            ErrorResponse r = parseError(he.response());

            if (r != null && r.message != null) {
                if (r.message.contains("No provider support found for coordinate")) {
                    UnsupportedCoordinatesException uce = new UnsupportedCoordinatesException(r.message, e);
                    uce.setProviderName(r.provider);

                    return Observable.error(uce);
                }

                if (r.message.contains("Unknown place code")) {
                    UnknownPlaceCodeException upce = new UnknownPlaceCodeException(r.message, e);
                    upce.setProviderName(r.provider);

                    return Observable.error(upce);
                }

                if (r.message.contains("Data format at e-solat") || r.message.contains("Error connecting to")) {
                    PrayerProviderException ppe = new PrayerProviderException(r.message, e);

                    if (!r.hasProviderName()) {
                        ppe.setProviderName("JAKIM");
                    } else {
                        ppe.setProviderName(r.provider);
                    }

                    return Observable.error(ppe);
                }
            }
        }

        return Observable.error(e);
    }

    private ErrorResponse parseError(Response<?> response) {
        if (response == null) return null;

        Converter<ResponseBody, ErrorResponse> converter = mRetrofit
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
