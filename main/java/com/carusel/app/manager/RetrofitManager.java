package com.carusel.app.manager;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager{
    // Singleton
    private static RetrofitManager instance;
    public static RetrofitManager getInstance(){
        if(instance == null){
            synchronized(RetrofitManager.class){
                if(instance == null){
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    // Fields
    private final Retrofit retrofit;

    // Constants
    private static final String BASE_URL = "http://nmnaufaldo.com/carusel-api/v1/";
    // private static final String BASE_URL = "http://localhost/carusel-api/v1/";

    // Constructor
    private RetrofitManager(){
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        init();
    }

    // Initialize
    private void init(){

    }

    // *****************************************************************************************
    // *** General *****************************************************************************
    // *****************************************************************************************

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
