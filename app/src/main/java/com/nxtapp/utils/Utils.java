package com.nxtapp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.nxtty.main.R;

public class Utils
{

    public static String imagePath = null;

    public interface OnImageSavedListener
    {
	public void onSave();

	public void onFail();
    }

    public static enum STORAGE
    {
	PHONE, SDCARD, NONE
    }

    private static OnImageSavedListener onImageSavedListener;

    public void setOnScreenshotSaveListener(OnImageSavedListener onImageSavedListener)
    {
	Utils.onImageSavedListener = onImageSavedListener;
    }

    public static class SaveImageTask extends AsyncTask<Void, Void, Uri>
    {

	private Bitmap bitmap;

	private Context context;

	private boolean isTemp;

	public SaveImageTask(Bitmap bitmap, Context context, boolean isTemp)
	{
	    this.bitmap = bitmap;
	    this.context = context;
	    this.isTemp = isTemp;

	}

	@Override
	protected Uri doInBackground(Void... params)
	{
	    return saveImage(bitmap, context, isTemp);
	}

    }

    private static Uri saveToSDCard(Context context, Bitmap bitmap, boolean isTemp)
    {
	File screenShot;
	try
	{

	    String rootDirPath = getRootPath(context, STORAGE.SDCARD);

	    File rootDir = new File(rootDirPath);
	    if (!rootDir.exists())
	    {
		rootDir.mkdirs();
	    }
	    imagePath = getImagePath(context, STORAGE.SDCARD, isTemp);
	    screenShot = new File(imagePath);

	    boolean success = false;

	    // Encode the file as a PNG image.
	    FileOutputStream outStream;
	    try
	    {

		outStream = new FileOutputStream(screenShot);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
		/* 100 to keep full quality of the image */
		outStream.flush();
		outStream.close();
		success = true;
	    } catch (FileNotFoundException e)
	    {
		 DebugReportOnLocat.e(e);
		return null;
	    } catch (IOException e)
	    {
		 DebugReportOnLocat.e(e);
		return null;
	    }
	    if (success)
	    {
		if (onImageSavedListener != null)
		{
		    onImageSavedListener.onSave();
		}
	    }
	    else
	    {
		if (onImageSavedListener != null)
		{
		    onImageSavedListener.onFail();
		}
		return null;
	    }
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	    return null;
	}

	if (screenShot.exists())
	{
	    return Uri.fromFile(screenShot);
	}
	else
	{
	    return null;
	}
    }

    private static Uri saveToPhoneMemory(Context context, Bitmap bitmap, boolean isTemp)
    {
	File image;
	try
	{

	    String rootDirPath = getRootPath(context, STORAGE.PHONE);

	    File rootDir = new File(rootDirPath);
	    if (!rootDir.exists())
	    {
		rootDir.mkdirs();
	    }
	    imagePath = getImagePath(context, STORAGE.PHONE, isTemp);
	    image = new File(imagePath);

	    boolean success = false;

	    // Encode the file as a PNG image.
	    FileOutputStream outStream;
	    try
	    {

		outStream = new FileOutputStream(image);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
		/* 100 to keep full quality of the image */
		outStream.flush();
		outStream.close();
		success = true;
	    } catch (FileNotFoundException e)
	    {
		 DebugReportOnLocat.e(e);
		return null;
	    } catch (IOException e)
	    {
		 DebugReportOnLocat.e(e);
		return null;
	    }
	    if (success)
	    {
		if (onImageSavedListener != null)
		{
		    onImageSavedListener.onSave();
		}
	    }
	    else
	    {
		if (onImageSavedListener != null)
		{
		    onImageSavedListener.onFail();
		}
		return null;
	    }
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	    return null;
	}

	if (image.exists())
	{
	    return Uri.fromFile(image);
	}
	else
	{
	    return null;
	}
    }

    private static Uri saveImage(Bitmap bitmap, Context context, boolean isTemp)
    {

	Uri uri = null;
	STORAGE availableStorage = getStorageWithFreeSpace(context);
	switch (availableStorage)
	{
	case NONE:
	    Toast.makeText(context, "No free space available.", Toast.LENGTH_SHORT).show();
	    break;
	case PHONE:
	    uri = saveToPhoneMemory(context, bitmap, isTemp);
	    break;
	case SDCARD:
	    uri = saveToSDCard(context, bitmap, isTemp);
	    break;
	}

	return uri;
    }

    public static STORAGE getStorageWithFreeSpace(Context context)
    {
	STORAGE storage;
	if (!StorageCheck.isSDCardMounted())
	{

	    if (StorageCheck.isFreeSpace(Environment.getDataDirectory().getAbsolutePath()))
	    {
		storage = STORAGE.PHONE;
	    }
	    else
	    {
		storage = STORAGE.NONE;
	    }

	}
	else
	{
	    if (StorageCheck.isFreeSpace(Environment.getExternalStorageDirectory().getAbsolutePath()))
	    {

		storage = STORAGE.SDCARD;

	    }
	    else if (StorageCheck.isFreeSpace(Environment.getDataDirectory().getAbsolutePath()))
	    {
		storage = STORAGE.PHONE;
	    }
	    else
	    {
		storage = STORAGE.NONE;
	    }
	}

	Log.d("log_tag", "Available Storage: " + storage);
	return storage;
    }

    public static String getImagePath(Context context, STORAGE availableStorage, boolean isTemp)
    {

	String path = null;
	String rootPath = getRootPath(context, availableStorage);

	String imageName = "wp_" + System.currentTimeMillis();
	if (isTemp)
	{
	    imageName = "temp_" + System.currentTimeMillis();
	}

	switch (availableStorage)
	{
	case NONE:
	    break;
	case PHONE:
	case SDCARD:
	    if (rootPath != null)
		path = rootPath + File.separator + imageName + ".png";
	    break;
	}
	Log.d("log_tag", "Image Path: " + path);
	return path;
    }

    public static String getRootPath(Context context, STORAGE availableStorage)
    {
	String path = null;
	String appName = context.getString(R.string.app_name);
	switch (availableStorage)
	{
	case NONE:
	    break;
	case PHONE:
	  //  path = Environment.getDataDirectory().getPath() + File.separator + appName;
	    
	    path = Environment.getDataDirectory().getAbsolutePath()+ File.separator + appName;
	    
	    break;
	case SDCARD:
	    path = Environment.getExternalStorageDirectory().getPath() + File.separator + appName;
	    break;
	}

	Log.d("log_tag", "Root Path: " + path);
	return path;
    }

    public static void hideKeypad(Activity activity)
    {
	try
	{
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
	    imm.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 1);
	    // imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); //
	    // show
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}

    }

    public static void hideKeypadForDialog(Activity activity)
    {
	InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showKeypadForDialog(Activity activity)
    {
	try
	{
	    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}
    }

    public static boolean isAppInstalledOrNot(Context context, String uri)
    {
	PackageManager pm = context.getPackageManager();
	boolean isAppInstalled = false;
	try
	{
	    pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
	    isAppInstalled = true;
	} catch (PackageManager.NameNotFoundException e)
	{
	    isAppInstalled = false;
	}
	return isAppInstalled;
    }

    public static boolean rotateAndSaveImage(Context mContext, Uri imageUri, float angle, Bitmap mSource)
    {
	if (imageUri == null && mSource == null)
	    return false;

	FileOutputStream fOut;
	Bitmap source = null;
	try
	{
	    if (mSource != null)
	    {
		source = mSource;
	    }
	    else
	    {
		source = BitmapFactory.decodeFile(getRealPathFromURI(mContext, imageUri));
	    }

	    Matrix matrix = new Matrix();
	    matrix.postRotate(angle);
	    source = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

	    fOut = new FileOutputStream(getRealPathFromURI(mContext, imageUri));
	    source.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
	    fOut.flush();
	    fOut.close();
	    source.recycle();

	    return true;
	} catch (Exception e)
	{
	}

	return false;
    }

    public static String getRealPathFromURI(Context mContext, Uri contentURI)
    {
	String result;

	Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
	if (cursor == null)
	{ // Source is Dropbox or other similar local file
	  // path
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

    public static String getImagePathFromURI(Context mContext, Uri contentUri, File targetFile) throws IOException
    {
	InputStream inputStream = mContext.getContentResolver().openInputStream(contentUri);
	FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
	copyStream(inputStream, fileOutputStream);
	fileOutputStream.close();
	inputStream.close();

	return targetFile.getAbsolutePath();
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException
    {

	byte[] buffer = new byte[1024];
	int bytesRead;
	while ((bytesRead = input.read(buffer)) != -1)
	{
	    output.write(buffer, 0, bytesRead);
	}
    }

    public static int getOrientationFromExif(String imagePath)
    {
	int orientation = -1;
	try
	{
	    ExifInterface exif = new ExifInterface(imagePath);
	    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

	    switch (exifOrientation)
	    {
	    case ExifInterface.ORIENTATION_ROTATE_270:
		orientation = 270;

		break;
	    case ExifInterface.ORIENTATION_ROTATE_180:
		orientation = 180;

		break;
	    case ExifInterface.ORIENTATION_ROTATE_90:
		orientation = 90;

		break;

	    case ExifInterface.ORIENTATION_NORMAL:
		orientation = 0;

		break;
	    default:
		break;
	    }
	} catch (IOException e)
	{
	}

	return orientation;
    }

    /**
     * 
     * @param url
     *            - Url from where the image has to be downloaded.
     * @return Local path of the downloaded image. Returns null if the image is
     *         NOT downloaded/saved successfully.
     */
    public static String downloadImageFromUrl(Context context, String urlStr)
    {
	try
	{
	    URL url = new URL(urlStr);

	    STORAGE availableStorage = Utils.getStorageWithFreeSpace(context);
	    String rootPath = Utils.getRootPath(context, availableStorage);

	    File folder = new File(rootPath);

	    if (!folder.isDirectory())
	    {
		folder.mkdir();
	    }

	    String localPath = Utils.getImagePath(context, availableStorage, true);
	    File file = new File(localPath);
	    Log.d("log_tag", "uri: " + file.getAbsolutePath());

	    if (!file.exists())
		try
		{
		    file.createNewFile();
		} catch (IOException e)
		{
		     DebugReportOnLocat.e(e);
		}

	    /* Open a connection to that URL. */
	    URLConnection ucon = url.openConnection();
	    InputStream inputStream = null;
	    HttpURLConnection httpConn = (HttpURLConnection) ucon;
	    httpConn.setRequestMethod("GET");
	    httpConn.connect();

	    if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
	    {
		inputStream = httpConn.getInputStream();
	    }

	    /*
	     * Define InputStreams to read from the URLConnection.
	     */
	    // InputStream is = ucon.getInputStream();
	    /*
	     * Read bytes to the Buffer until there is nothing more to read(-1).
	     */

	    FileOutputStream fos = new FileOutputStream(file);
	    int size = 1024 * 1024;
	    byte[] buf = new byte[size];
	    int byteRead;
	    while (((byteRead = inputStream.read(buf)) != -1))
	    {
		fos.write(buf, 0, byteRead);
	    }
	    /* Convert the Bytes read to a String. */

	    fos.flush();
	    fos.close();

	    return localPath;
	} catch (IOException io)
	{
	    io.printStackTrace();
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}

	return null;
    }

}
