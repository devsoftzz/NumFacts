package com.devsoftzz.numfacts.requests;

import com.devsoftzz.numfacts.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
            .baseUrl(Constants.baseUrl)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static NumberApi numberApi = retrofit.create(NumberApi.class);

    public static NumberApi getNumberApi(){
        return numberApi;
    }
}
