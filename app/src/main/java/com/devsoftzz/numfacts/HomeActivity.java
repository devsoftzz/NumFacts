package com.devsoftzz.numfacts;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.devsoftzz.numfacts.adapter.HomeRecyclerAdapter;
import com.devsoftzz.numfacts.models.Fact;
import com.devsoftzz.numfacts.utils.Constants;
import com.devsoftzz.numfacts.utils.myUtils;
import com.devsoftzz.numfacts.viewmodels.HomeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements HomeRecyclerAdapter.onCardListener {

    private static final String TAG = Constants.TAG;

    private HomeViewModel mHomeViewModel;
    private DiscreteScrollView mScrollView;
    private TextView mFOTD;
    private HomeRecyclerAdapter mAdapter;
    private ProgressBar mProgress, mHorizontal;
    private Snackbar mSnackbar;
    private LinearLayout mSheet;
    private BottomSheetBehavior mBottomBehavior;

    private ArrayList<Fact> mDataSet;
    private Integer count = 15;
    private Boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        subscribeObservers();
    }

    private void init() {

        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mDataSet = new ArrayList<>();

        mProgress = findViewById(R.id.progress_factoftheday);
        mHorizontal = findViewById(R.id.horizontalprocessing);
        mScrollView = findViewById(R.id.recyclerHome);
        mFOTD = findViewById(R.id.factoftheday_textview);
        mSheet = findViewById(R.id.bottom_sheet);

        mBottomBehavior = BottomSheetBehavior.from(mSheet);
        mBottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mScrollView.setItemTransitionTimeMillis(400);
        mScrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.0f)
                .setMinScale(0.9f)
                .build());
        mScrollView.addOnItemChangedListener(new RecyclerviewObserver());
        mScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> mBottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));

        mAdapter = new HomeRecyclerAdapter(mDataSet, this);
        mScrollView.setAdapter(mAdapter);

        showProgressbar(true);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCardLongClick(int position) {
        mBottomBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onCardClick() {
        if (mBottomBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            Toast.makeText(this, "Long Press to Share", Toast.LENGTH_SHORT).show();
        }
    }

    private class RecyclerviewObserver implements DiscreteScrollView.OnItemChangedListener {

        @Override
        public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int i) {

            //mBottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            setBubbleDecorColor(i);
            if (i == mDataSet.size() - 1 && mDataSet.size() == count && isConnected) {
                count += 10;
            }
            if (i == mDataSet.size() - 1 && mDataSet.size() != count && isConnected) {
                mHomeViewModel.FactsApi(5);
                mHorizontal.setVisibility(View.VISIBLE);
            } else {
                mHorizontal.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void subscribeObservers() {

        mHomeViewModel.getFactOfTheDay().observe(this, factOfTheDay -> {

            if (factOfTheDay != null) {

                mFOTD.setText(factOfTheDay.getText());
                mProgress.setVisibility(View.INVISIBLE);
                Log.d(TAG, "FactOfTheDay :" + factOfTheDay.getText());

            } else if (mFOTD.getText().toString().equals("") && isConnected) {
                mHomeViewModel.TodayFactApi(myUtils.getDate());
            }

        });

        mHomeViewModel.getFacts().observe(this, facts -> {

            if (facts != null && checkForInsertion(facts)) {

                mDataSet.add(facts);
                mAdapter.notifyDataSetChanged();
                HomeActivity.super.showProgressbar(false);
                Log.d(TAG, "Fact :" + facts.getText());

            }
            if (mDataSet.size() < count && isConnected) {
                mHomeViewModel.FactsApi(3);
            }

        });

        mHomeViewModel.getIsDisconnected().observe(this, status -> {
            isConnected = !status;
            if (status) {
                //Disconnect
                isConnected = false;
                mHorizontal.setVisibility(View.INVISIBLE);
                mProgress.setVisibility(View.INVISIBLE);
                showProgressbar(false);
                showSnackbar();
            }
        });
    }

    private void showSnackbar() {

        mSnackbar = Snackbar.make(getFrame(), "No Internet Connection", BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setAction("Try Again", new snackBarHandler());
        mSnackbar.show();

    }

    private class snackBarHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mSnackbar.dismiss();
            if (mDataSet.size() == 0) {
                mHomeViewModel.FactsApi(15);
                showProgressbar(true);
                mHorizontal.setVisibility(View.VISIBLE);
            } else {
                mHomeViewModel.FactsApi(10);
                mHorizontal.setVisibility(View.VISIBLE);
            }
            if (mFOTD.getText().toString().equals("")) {
                mHomeViewModel.TodayFactApi(myUtils.getDate());
                mProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean checkForInsertion(Fact facts) {
        boolean status = true;
        if (facts.getText().contains("atomic number") || facts.getText().contains("-Infinity is negative infinity.")) {
            status = false;
        } else {
            for (Fact fact : mDataSet) {
                if (fact.getText().equals(facts.getText())) {
                    status = false;
                    break;
                }
            }
        }
        return status;
    }

}
