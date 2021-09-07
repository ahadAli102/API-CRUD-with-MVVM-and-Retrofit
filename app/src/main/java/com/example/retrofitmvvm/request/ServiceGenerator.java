package com.example.retrofitmvvm.request;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    public static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private static Retrofit retrofit= new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build();

    private static APIRequest requestApi = retrofit.create(APIRequest.class);

    public static APIRequest getRequestApi(){
        return requestApi;
    }
}
