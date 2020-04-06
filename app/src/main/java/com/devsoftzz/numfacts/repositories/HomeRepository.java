package com.devsoftzz.numfacts.repositories;

import androidx.lifecycle.LiveData;

import com.devsoftzz.numfacts.models.FactOfTheDay;
import com.devsoftzz.numfacts.models.Fact;
import com.devsoftzz.numfacts.requests.NumberApiClient;

public class HomeRepository {

    private static HomeRepository instance;
    private NumberApiClient mNumberApiClient;

    public static HomeRepository getInstance() {
        if (instance == null) {
            instance = new HomeRepository();
        }
        return instance;
    }

    private HomeRepository() {
        mNumberApiClient = NumberApiClient.getInstance();
    }

    public LiveData<FactOfTheDay> getTodayFact() {
        return mNumberApiClient.getTodayFact();
    }

    public LiveData<Fact> getFacts() {
        return mNumberApiClient.getFacts();
    }

    public LiveData<Boolean> getIsDisconnected() {
        return mNumberApiClient.getIsDisconnected();
    }

    public void FactsApi(Integer numbers) {
        mNumberApiClient.FactsApi(numbers);
    }

    public void TodayFactApi(String date) {
        mNumberApiClient.TodayFactApi(date);
    }

}
