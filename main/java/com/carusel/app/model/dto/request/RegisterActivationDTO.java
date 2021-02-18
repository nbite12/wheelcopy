package com.carusel.app.model.dto.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public class RegisterActivationDTO{
    @SerializedName("request_code")
    private String requestCode;

    @SerializedName("activation_code")
    private String activationCode;

    public String getRequestCode(){
        return requestCode;
    }

    public void setRequestCode(String requestCode){
        this.requestCode = requestCode;
    }

    public String getActivationCode(){
        return activationCode;
    }

    public void setActivationCode(String activationCode){
        this.activationCode = activationCode;
    }
}
