package com.devsoftzz.numfacts.utils;

import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class myUtils {

    public static ArrayList<Integer> getColorSet() {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#3F51B5"));
        colors.add(Color.parseColor("#2196F3"));
        colors.add(Color.parseColor("#03A9F4"));
        colors.add(Color.parseColor("#00BCD4"));
        colors.add(Color.parseColor("#009688"));
        colors.add(Color.parseColor("#4CAF50"));
        colors.add(Color.parseColor("#8BC34A"));
        colors.add(Color.parseColor("#CDDC39"));
        colors.add(Color.parseColor("#FFC107"));
        colors.add(Color.parseColor("#FF9800"));
        colors.add(Color.parseColor("#FF5722"));
        colors.add(Color.parseColor("#F44336"));
        colors.add(Color.parseColor("#E91E63"));
        colors.add(Color.parseColor("#673AB7"));
        colors.add(Color.parseColor("#9C27B0"));
        return colors;
    }

    public static String getDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("M/d");
        return df.format(date);
    }

}
