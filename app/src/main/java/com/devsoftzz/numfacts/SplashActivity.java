package com.devsoftzz.numfacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.devsoftzz.numfacts.utils.Colours;
import com.devsoftzz.numfacts.utils.Constants;
import com.devsoftzz.numfacts.utils.TodayDate;
import com.devsoftzz.numfacts.viewmodels.HomeViewModel;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        CardView cardView = findViewById(R.id.card);
        ArrayList<Integer> colorSet = Colours.getColorSet();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int cnt = sharedPreferences.getInt(Constants.AppStartCount, 0);
        cardView.setCardBackgroundColor(colorSet.get(colorSet.size() - (cnt % colorSet.size() + 1)));
        sharedPreferences.edit().putInt(Constants.AppStartCount, cnt + 1).apply();

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.TodayFactApi(TodayDate.getDate());
        homeViewModel.FactsApi(15);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this,HomeActivity.class));
            finish();
        },2000);

    }
}
