package com.carusel.app.model.schema;

import com.google.gson.annotations.SerializedName;

public class GetRequestCodeSchema{
	@SerializedName("request_code")
	private String requestCode;

	public String getRequestCode(){
		return requestCode;
	}
}
