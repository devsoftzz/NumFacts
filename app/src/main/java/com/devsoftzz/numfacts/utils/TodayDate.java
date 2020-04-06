package com.devsoftzz.numfacts.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TodayDate {

    public static String getDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("M/d");
        return df.format(date);
    }
}
