package com.nxtapp.utils;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.net.Uri;
import android.util.Log;


public class DebugReportOnLocat {
    
	public static boolean dbg=false;

	public static void ln(String string) {
	
		if(dbg)
		System.out.println(" >>>>> "+string);
		
	}
	
	public static void println(String string) {
		
		if(dbg)
		System.out.println(" >>>>> "+string);
		
	}

	public static void e(String TAG, String message)
	{
	    
	    if(dbg)
	    Log.e(TAG,message);
	}

	public static void e(String logTag, String string, MalformedURLException e)
	{
	    if(dbg)
		e.printStackTrace();
	    
	}

	public static void e(String logTag, String string, IOException e)
	{

	    if(dbg)
		e.printStackTrace();
	    
	}

	public static void e(String logTag, String string, JSONException e)
	{
	    if(dbg)
		e.printStackTrace();
	    
	}

	public static void e(String tag, String string, Exception ex)
	{
	    if(dbg)
		ex.printStackTrace();
	    
	}

	public static void i(String TAG, String message)
	{
	    if(dbg)
	    Log.e(TAG,message);
	    
	}

	public static void e(ClientProtocolException e)
	{
	    if(dbg)
	 		e.printStackTrace();
	    
	    
	}
	public static void e(NullPointerException e)
	{
	    if(dbg)
	 		e.printStackTrace();
	    
	    
	}
	public static void e(IOException e)
	{
	    if(dbg)
 		e.printStackTrace();
	    
	}

	public static void e(Exception e)
	{
	    if(dbg)
 		e.printStackTrace();
	    
	}

	public static void e(OutOfMemoryError e)
	{
	    if(dbg)
 		e.printStackTrace();
	    
	}

	public static void e(Throwable e)
	{
	    if(dbg)
 		e.printStackTrace();
	    
	}

	public static void e(String tAG, Uri path)
	{
	    // TODO Auto-generated method stub
	    
	}

}
