package com.nextapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class BaseManager {

 public SQLiteDatabase dataBase;
 private Context mContext;

 public BaseManager(Context context){
	
     
	 mContext = context;
	 dataBase = DataBaseWrapper.sharedInstance(context).openOrCreateDatabase();	
 }
 
 public void openOrCreateDatabase()
 {
	 dataBase = DataBaseWrapper.sharedInstance(mContext).openOrCreateDatabase();	
 }
 
}
