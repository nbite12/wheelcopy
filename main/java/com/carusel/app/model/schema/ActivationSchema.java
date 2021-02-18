package com.carusel.app.model.schema;

import com.carusel.app.constants.ActivationType;
import com.google.gson.annotations.SerializedName;
import lombok.ToString;

@ToString
public class ActivationSchema{
    @SerializedName("error")
    private boolean isError;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private ActivationType activationType;

    public boolean isError(){
        return isError;
    }

    public ActivationType getActivationType(){
        return activationType;
    }
}
