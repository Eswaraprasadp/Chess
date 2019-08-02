package com.eswar.chess;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BaseActivity extends Activity {
    public DatabaseHelper dbh;
    public final static String tag = "tag";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh = new DatabaseHelper(BaseActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        dbh.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbh = new DatabaseHelper(BaseActivity.this);
    }

    public static List<String> getMonths(){
        return Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
    }

    public static String getFormattedDate(String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            String nowString = sdf.format(Calendar.getInstance().getTime());
            Date start = sdf.parse(date);
            Date now = sdf.parse(nowString);
            long diff = now.getTime() - start.getTime();
            long sec = diff/1000;
            if(sec < 1){ return "Just now"; }
            else if(sec < 60){ return sec + " seconds ago"; }
            else if(sec < 60*2){ return "A minute ago"; }
            else if(sec < 3600){ return sec/60 + " minutes ago"; }
            else if(sec < 3600*2){ return "An hour ago"; }
            else if(sec < 3600*24){ return sec/3600 + " hours ago"; }
            else if(sec < 3600*24*2){ return "Yesterday"; }
            else if(sec < 3600*24*4){ return sec/(3600*24) + " days ago"; }
            else{ return getMonths().get(Integer.parseInt(date.split("-")[1]) - 1) + " " + date.split("-")[2].split(" ")[0] ; }
        }
        catch (Exception e){
            e.printStackTrace();
            return date;
        }
    }
}
