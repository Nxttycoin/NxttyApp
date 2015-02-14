package com.nextapp.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseWrapper extends SQLiteOpenHelper
{

	
	private SQLiteDatabase mDatabase;
	private Context mContext;
	private static String mDatabasePath;
	
	private static DataBaseWrapper mInstance;
	private static final String DATABASE_NAME = "nxtty.sqlite";
	private static final int DATABASE_VERSION = 1;
	
	  
	public DataBaseWrapper(Context context, String name, CursorFactory factory,
			int version) {
		
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public synchronized void close() {
 
    	    if(mDatabase != null)
    	    	mDatabase.close();
 
    	    super.close();
 
	}
	
	public static DataBaseWrapper sharedInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataBaseWrapper(context,DATABASE_NAME, null, DATABASE_VERSION);
        }
        mDatabasePath = "//data//data//" + context.getPackageName() + "/databases/";
        
        return mInstance;
    }
    
	
	public SQLiteDatabase openOrCreateDatabase()
	{
		try {
			
			if(mDatabase != null)
				mDatabase.close();
			
			String path = mDatabasePath + DATABASE_NAME;

			File file = new File(path);

			if (!file.exists()) {

				this.getReadableDatabase();
				copyDataBase();
			}
			mDatabase = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READWRITE);

	
		} catch (Exception e) {
			
			System.out.println("openOrCreateDatabase:: " + e.toString());
		}
		
		return mDatabase;
		
	}
	
	private void copyDataBase() throws IOException {

		InputStream inputStream = mContext.getAssets().open(DATABASE_NAME);

		String path = mDatabasePath + DATABASE_NAME;
		
		OutputStream outputStream = new FileOutputStream(path);

		byte[] buffer = new byte[1024];
		int length;

		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}

		outputStream.flush();
		outputStream.close();
		inputStream.close();

	}
	 
	 
	 public  void execSQL(String sql){
		mDatabase.execSQL(sql);
	}
	
}
