package com.carusel.app.model.dto;

import com.carusel.app.model.schema.ActivationSchema;
import com.carusel.app.model.schema.GetRequestCodeSchema;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CaruselAPI{
    // End point
    String GET_REQUEST_CODE_END_POINT = "GetRequestCode.php";
    String END_POINT = "SerialNumberRegistration.php";

    @POST(GET_REQUEST_CODE_END_POINT)
    Observable<GetRequestCodeSchema> getRequestCode();

    @FormUrlEncoded
    @POST(END_POINT)
    Observable<ActivationSchema> registerActivation(
            @Field("request_code") String requestCode,
            @Field("activation_code") String activationCode
    );
}
