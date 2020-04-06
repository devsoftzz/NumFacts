package com.devsoftzz.numfacts.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.devsoftzz.numfacts.models.FactOfTheDay;
import com.devsoftzz.numfacts.models.Fact;
import com.devsoftzz.numfacts.repositories.HomeRepository;

public class HomeViewModel extends ViewModel {

    private HomeRepository mHomeRepository;

    public HomeViewModel() {
        mHomeRepository = HomeRepository.getInstance();
    }

    public LiveData<FactOfTheDay> getFactOfTheDay(){
        return mHomeRepository.getTodayFact();
    }

    public LiveData<Boolean> getIsDisconnected() {
        return mHomeRepository.getIsDisconnected();
    }

    public void TodayFactApi(String date){
        mHomeRepository.TodayFactApi(date);
    }

    public LiveData<Fact> getFacts(){
        return mHomeRepository.getFacts();
    }

    public void FactsApi(Integer number){
            mHomeRepository.FactsApi(number);
    }
}
