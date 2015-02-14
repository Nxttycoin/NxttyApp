package com.nxtapp.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtility
{
    public static void hideKeypad(Activity activity)
    {
	try
	{
	    if(activity==null)return;
	    
	    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    // imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); //
	    // hide
	    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}

    }

    public static void showKeypad(Activity activity)
    {
	try
	{
	    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}

    }
}
