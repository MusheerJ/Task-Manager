package com.taskmanager.horkrux.Notification;

import com.taskmanager.horkrux.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {
    public static Retrofit retrofit;

    public static ApiInterface getClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        return retrofit.create(ApiInterface.class);
    }
}
