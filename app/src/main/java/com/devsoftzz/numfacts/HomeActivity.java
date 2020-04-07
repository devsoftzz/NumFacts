package com.devsoftzz.numfacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.devsoftzz.numfacts.adapter.HomeRecyclerAdapter;
import com.devsoftzz.numfacts.models.Fact;
import com.devsoftzz.numfacts.utils.BitmapIO;
import com.devsoftzz.numfacts.utils.Constants;
import com.devsoftzz.numfacts.utils.WriteOnImage;
import com.devsoftzz.numfacts.utils.myUtils;
import com.devsoftzz.numfacts.viewmodels.HomeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends BaseActivity implements HomeRecyclerAdapter.onCardListener {

    private static final String TAG = Constants.TAG;

    private HomeViewModel mHomeViewModel;
    private DiscreteScrollView mScrollView;
    private TextView mFOTD;
    private HomeRecyclerAdapter mAdapter;
    private ProgressBar mProgress, mHorizontal;
    private Snackbar mSnackbar;
    private LinearLayout mSheet;
    private TextView mShare, mSave, mInsta;
    private BottomSheetBehavior mBottomBehavior;

    private ArrayList<Fact> mDataSet;
    private ArrayList<Integer> mColorSet = myUtils.getColorSet();
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
        mShare = findViewById(R.id.share);
        mSave = findViewById(R.id.save);
        mInsta = findViewById(R.id.insta);
        mBottomBehavior = BottomSheetBehavior.from(mSheet);

        mBottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomBehavior.setBottomSheetCallback(new BottomSheetObserver());
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
        mSave.setOnClickListener(new saveFactToDevice());
        mInsta.setOnClickListener(new shareInstaStory());
        mShare.setOnClickListener(new shareText());
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

    private class shareText implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String shareBody = mDataSet.get(mScrollView.getCurrentItem()).getText();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Did you know?");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent,"Choose To Proceed"));
            mBottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private class shareInstaStory implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int position = mScrollView.getCurrentItem();
            if (isStoragePermissionGranted(2)) {
                Bitmap bitmap = WriteOnImage.drawMultilineTextToImage(HomeActivity.this, mDataSet.get(position).getText(), 26, mColorSet.get(position % mColorSet.size()));
                String path = BitmapIO.saveBitmapToExternalStorage(HomeActivity.this, bitmap, "numfacts" + String.valueOf(Calendar.getInstance().getTimeInMillis()).substring(2, 10) + ".jpg");
                if (path != null) {
                    File file = new File(path);
                    Uri backgroundAssetUri = FileProvider.getUriForFile(HomeActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
                    Intent storiesIntent = new Intent("com.instagram.share.ADD_TO_STORY");
                    storiesIntent.setDataAndType(backgroundAssetUri, "image/*");
                    storiesIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    storiesIntent.setPackage("com.instagram.android");
                    HomeActivity.this.grantUriPermission("com.instagram.android", backgroundAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(storiesIntent);

                } else {
                    Toast.makeText(HomeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                mBottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    private class saveFactToDevice implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int position = mScrollView.getCurrentItem();
            if (isStoragePermissionGranted(3)) {
                Bitmap bitmap = WriteOnImage.drawMultilineTextToImage(HomeActivity.this, mDataSet.get(position).getText(), 26, mColorSet.get(position % mColorSet.size()));
                BitmapIO.saveBitmapToExternalStorage(HomeActivity.this, bitmap, "numfacts" + String.valueOf(Calendar.getInstance().getTimeInMillis()).substring(2, 10) + ".jpg");
                mBottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    private class BottomSheetObserver extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (mDataSet.size() == 0) {
                mBottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (mDataSet.size() == 0) {
                mBottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    private class RecyclerviewObserver implements DiscreteScrollView.OnItemChangedListener {

        @Override
        public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int i) {

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

    public boolean isStoragePermissionGranted(int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, i);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mSave.callOnClick();
                }
                break;

            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mInsta.callOnClick();
                }
                break;
        }
    }

}
