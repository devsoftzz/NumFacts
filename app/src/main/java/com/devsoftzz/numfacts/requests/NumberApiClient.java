package com.devsoftzz.numfacts.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.devsoftzz.numfacts.AppExecutors;
import com.devsoftzz.numfacts.models.FactOfTheDay;
import com.devsoftzz.numfacts.models.Fact;
import com.devsoftzz.numfacts.utils.Constants;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class NumberApiClient {

    private Integer NUMBER=15;

    private static NumberApiClient instance;

    private MutableLiveData<FactOfTheDay> mFactOfTheDayData;
    private MutableLiveData<Fact> mFactDataList;
    private MutableLiveData<Boolean> mIsDisconnected;

    private RetrieveTodayFact mRetrieveTodayFact;
    private RetrieveFacts mRetrieveFacts;
    private int Number;

    public static NumberApiClient getInstance() {
        if (instance == null) {
            instance = new NumberApiClient();
        }
        return instance;
    }

    private NumberApiClient() {
        mFactOfTheDayData = new MutableLiveData<>();
        mFactDataList = new MutableLiveData<>();
        mIsDisconnected = new MutableLiveData<>();
    }

    public LiveData<FactOfTheDay> getTodayFact() {
        return mFactOfTheDayData;
    }

    public LiveData<Fact> getFacts() {
        return mFactDataList;
    }

    public LiveData<Boolean> getIsDisconnected(){
        return mIsDisconnected;
    }

    public void TodayFactApi(String date) {

        if (mRetrieveTodayFact != null) {
            mRetrieveTodayFact = null;
        }

        mRetrieveTodayFact = new RetrieveTodayFact(date);
        final Future handler = AppExecutors.getInstance().NetworkIO().submit(mRetrieveTodayFact);

        AppExecutors.getInstance().NetworkIO().schedule(() -> {
            handler.cancel(true);
        }, Constants.Timeout, TimeUnit.MILLISECONDS);

    }

    private class RetrieveTodayFact implements Runnable {

        boolean cancelRequest;
        private String date;

        public RetrieveTodayFact(String date) {
            this.date = date;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                if (cancelRequest) {
                    return;
                }
                Response response = getTodayFact(date).execute();

                if (response.code() == 200) {
                    mFactOfTheDayData.postValue((FactOfTheDay) response.body());
                    mIsDisconnected.postValue(false);
                } else {
                    mFactOfTheDayData.postValue(null);
                    Log.e("NumFactsTodayFactClient", response.errorBody().toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("NumFactsTodayFactClient", e.getMessage());
                mIsDisconnected.postValue(true);
                mFactOfTheDayData.postValue(null);
            }
        }

        private Call<FactOfTheDay> getTodayFact(String date) {
            return ServiceGenerator.getNumberApi().getTodayFact(date);
        }

        private void cancelRequest() {
            cancelRequest = true;
        }
    }

    public void FactsApi(Integer number) {
        Number = number;
        for (int i = 0; i < Number; i++) {

            if (mRetrieveFacts != null) {
                mRetrieveFacts = null;
            }

            mRetrieveFacts = new RetrieveFacts();
            Future handler = AppExecutors.getInstance().NetworkIO().submit(mRetrieveFacts);
            AppExecutors.getInstance().NetworkIO().schedule(() -> {
                handler.cancel(true);
            }, Constants.Timeout, TimeUnit.MILLISECONDS);

        }
    }

    private class RetrieveFacts implements Runnable {

        boolean cancelRequest;

        public RetrieveFacts() {
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                if (cancelRequest) {
                    return;
                }
                Response response = getFact().execute();
                if (response.code() == 200) {
                    mFactDataList.postValue((Fact) response.body());
                    mIsDisconnected.postValue(false);
                } else {
                    Number++;
                    Log.e("NumFactsFactsClient 1", response.errorBody().toString());
                }
                if (cancelRequest) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                mIsDisconnected.postValue(true);
                Log.e("NumFactsFactsClient", e.getMessage());
                mFactOfTheDayData.postValue(null);
            }
        }

        private Call<Fact> getFact() {
            return ServiceGenerator.getNumberApi().getTriviaFact();
        }

        private void cancelRequest() {
            cancelRequest = true;
        }
    }

}
