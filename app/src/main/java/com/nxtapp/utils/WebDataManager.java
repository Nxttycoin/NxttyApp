package com.nxtapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class WebDataManager
{
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    
    @SuppressLint("CommitPrefEdits")
	public WebDataManager(Context context)
    {
	// TODO Auto-generated constructor stub
	mPreferences =context.getSharedPreferences("web_data", 0);
	mEditor=mPreferences.edit();
    }
    
    public void setSubscriberDetails(String json)
    {
	mEditor.putString("subscribe_detail", json).commit();
    }
    
    public String getSubscriberDetails()
    {
	return mPreferences.getString("subscribe_detail", null);
		
    }
}
