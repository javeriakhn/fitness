package com.example.fitnessquest.client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    final static String base_url="https://exercisedb.p.rapidapi.com/";

    static Retrofit retrofit= null;

    public static  Retrofit getClient(){
        if(retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return  retrofit;
    }
}
