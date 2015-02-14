package com.nxtapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network
{

    
    
    public static boolean isNetworkConnected(Context ctx)
    {
	ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo ni = cm.getActiveNetworkInfo();
	if (ni == null)
	{
	    // There are no active networks.
	    return false;
	}
	else
	    return true;
    }
    
}
