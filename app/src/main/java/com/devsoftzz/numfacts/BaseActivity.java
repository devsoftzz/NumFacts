package com.devsoftzz.numfacts;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.devsoftzz.numfacts.utils.myUtils;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private ImageView BubbleDecor;
    private FrameLayout mFrame;
    private ArrayList<Integer> ColorSet = myUtils.getColorSet();

    @Override
    public void setContentView(int layoutResID) {
        View rootView = getLayoutInflater().inflate(R.layout.activity_base, null);
        mFrame = rootView.findViewById(R.id.contentFrame);

        mProgressBar = rootView.findViewById(R.id.progressbar);
        BubbleDecor = rootView.findViewById(R.id.bubble);

        mProgressBar.setVisibility(View.INVISIBLE);


        getLayoutInflater().inflate(layoutResID, mFrame, true);
        super.setContentView(rootView);
    }

    public FrameLayout getFrame(){
        return mFrame;
    }

    public void showProgressbar(boolean visibility) {
        mProgressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }

    public void setBubbleDecorColor(int position) {

        position = position % ColorSet.size();
        Drawable background = BubbleDecor.getBackground();

        if (background instanceof ShapeDrawable) {

            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ColorSet.get(position));

        } else if (background instanceof GradientDrawable) {

            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ColorSet.get(position));

        } else if (background instanceof ColorDrawable) {

            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ColorSet.get(position));

        }
    }

}
