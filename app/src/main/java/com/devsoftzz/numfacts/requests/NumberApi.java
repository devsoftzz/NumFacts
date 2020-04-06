package com.devsoftzz.numfacts.requests;

import com.devsoftzz.numfacts.models.Fact;
import com.devsoftzz.numfacts.models.FactOfTheDay;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NumberApi {

    //FactOfTheDay
    @GET("{today_date}/date?json")
    Call<FactOfTheDay> getTodayFact(@Path(value = "today_date",encoded = true) String today_date);

    //FactOfMath
    @GET("random/math?json")
    Call<Fact> getMathFact();

    //FactYouMayDon'tKnow
    @GET("random/trivia?json")
    Call<Fact> getTriviaFact();

}
