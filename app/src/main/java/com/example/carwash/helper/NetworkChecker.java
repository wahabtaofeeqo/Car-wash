package com.example.carwash.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkChecker {

    private Context context;
    private static boolean isMobileCon = false;
    private static boolean isWifiCOn = false;

    public NetworkChecker(Context con) {
        context = con;
    }

    public static boolean isNetworkOn(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            for (Network network: manager.getAllNetworks()) {

                NetworkInfo info = manager.getNetworkInfo(network);

                if (info.getType() == ConnectivityManager.TYPE_WIFI)
                    isWifiCOn = info.isConnectedOrConnecting();
                else
                    isMobileCon = info.isConnectedOrConnecting();
            }
        }
        else {

            for(NetworkInfo info : manager.getAllNetworkInfo()) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI)
                    isWifiCOn = info.isConnectedOrConnecting();
                else isMobileCon = info.isConnectedOrConnecting();
            }
        }

        return isWifiCOn || isMobileCon;
    }
}
