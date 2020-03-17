package com.example.carwash.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static final String BASE_URL = "http://ritabestxpression.com.ng/api";;

    public static void showMessage(final Context context, final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void logMessage(Context context, String msg) {
        Log.i(Util.class.getSimpleName(), msg);
    }

    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(new Date());
    }

    public static String getTime() {

        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        return format.format(new Date());
    }

    public static String getMonth() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM", Locale.getDefault());
        return format.format(new Date());
    }

    public static String getYear() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.getDefault());
        return format.format(new Date());
    }
}
