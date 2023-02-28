package com.francescomalagrino.go4lunch.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LunchDateFormat {
    //Format Time and Date
    public String getTodayDate(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd", Locale.ITALY);
        return simpleDateFormat.format(date);
    }

    public String getRegisteredDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd", Locale.ITALY);
        return simpleDateFormat.format(date);
    }

    public String getHoursFormat(String hour){
        String time;
        if (hour.length()==3) {
            time = hour.substring(0,1) + ":" + hour.substring(1);
        } else {
            time = hour.substring(0,2)+":"+ hour.substring(2);
        }
        return time;
    }
}