package com.nxtapp.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class StaticUtility
{
    public static String getRealPathFromURI(Context context, Uri contentURI)
    {
	String result;
	Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
	
	if (cursor == null)
	{ // Source is Dropbox or other similar local file path
	    result = contentURI.getPath();
	}
	else
	{
	    cursor.moveToFirst();
	    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
	    result = cursor.getString(idx);
	    cursor.close();
	}
	return result;
    }
    
//    public static String getRealPathFromURI(Context context, Uri contentUri) {
//	  Cursor cursor = null;
//	  try { 
//	    String[] proj = { MediaStore.Images.Media.DATA };
//	    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
//	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//	    cursor.moveToFirst();
//	    return cursor.getString(column_index);
//	  } finally {
//	    if (cursor != null) {
//	      cursor.close();
//	    }
//	  }
//	}




    public static boolean isValidEmail(String email)
    {
//	final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
	String emailPattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
	                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
	                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
	                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
	                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
	                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
	 
	if(email.matches(emailPattern))
	{
	    return true;
	}else
	{
	    return false;
	}
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateFromGMTTime(long timeStamp)
    {
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
	calendar.setTimeInMillis(timeStamp);
	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
	sdf.setTimeZone(TimeZone.getDefault());
	return sdf.format(calendar.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getLastSeenFromGMTTime(long timeStamp)
    {
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

	int currentDate = calendar.get(Calendar.DATE);

	calendar.setTimeInMillis(timeStamp);
	SimpleDateFormat sdf = null;
	if (calendar.get(Calendar.DATE) == currentDate)
	{
	    sdf = new SimpleDateFormat("'Today at' hh:mm");
	}
	else
	{
	    sdf = new SimpleDateFormat("MM/dd/yyyy 'at' hh:mm");
	}

	sdf.setTimeZone(TimeZone.getDefault());
	return sdf.format(calendar.getTime());
    }
    
    
    public static File getResideMenuBackgroundImagePath(Context context)
    {
	File path=new File(context.getExternalFilesDir("background"), "bg.png");
	return path;
    }
    
    public static File getTempPath(Context context)
    {
	Date d=new Date();
	File path=new File(context.getExternalCacheDir(), d.getTime()+"temp.png");
	return path;
    }
}
