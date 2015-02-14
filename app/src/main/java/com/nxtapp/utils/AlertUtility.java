package com.nxtapp.utils;

import com.nxtty.main.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.widget.Toast;

public class AlertUtility
{
    public static void showToast(Context context, String msg)
    {
	
//	    Toast toast = new Toast(context);
//	    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
//	    toast.setText(msg);
//	    toast.setDuration(300);
	//    Toast.makeText(context, msg, 300).show();
	//    toast.show();
	    final Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
	        toast.show();

	        Handler handler = new Handler();
	            handler.postDelayed(new Runnable() {
	               @Override
	               public void run() {
	                   toast.cancel(); 
	               }
	        }, 800);
	//Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
// changed by bala for cancel issue
    public static void showConfirmDialog(Context context, String msg, OnClickListener onYesClick)
    {
	new AlertDialog.Builder(context).setIcon(0).setCancelable(false).setMessage(msg).setNegativeButton(context.getResources().getString(R.string.no), null).setPositiveButton(context.getResources().getString(R.string.yes), onYesClick).show();
    }
}
