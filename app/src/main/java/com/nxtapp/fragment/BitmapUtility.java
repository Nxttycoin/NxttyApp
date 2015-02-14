package com.nxtapp.fragment;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Base64;

public class BitmapUtility
{
    public static Bitmap decodeBitmapFromBase64(String base64, int reqWidth, int reqHeight)
    {
	return decodeBitmapFromByte(Base64.decode(base64, Base64.DEFAULT), reqWidth, reqHeight);
    }
    
    public static Bitmap decodeBitmapFromPath(String path, int reqWidth, int reqHeight)
    {

	// First decode with inJustDecodeBounds=true to check dimensions
	final BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = true;
	BitmapFactory.decodeFile(path, options);

	// Calculate inSampleSize
	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	// Decode bitmap with inSampleSize set
	options.inJustDecodeBounds = false;
	return BitmapFactory.decodeFile(path, options);
    }
    
    public static Bitmap decodeBitmapFromByte(byte[] byteArray, int reqWidth, int reqHeight)
    {

	// First decode with inJustDecodeBounds=true to check dimensions
	final BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = true;
	BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

	// Calculate inSampleSize
	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	// Decode bitmap with inSampleSize set
	options.inJustDecodeBounds = false;
	return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {

	int width = bm.getWidth();

	int height = bm.getHeight();

	float scaleWidth = ((float) newWidth) / width;

	float scaleHeight = ((float) newHeight) / height;

	// CREATE A MATRIX FOR THE MANIPULATION

	Matrix matrix = new Matrix();

	// RESIZE THE BIT MAP

	matrix.postScale(scaleWidth, scaleHeight);

	// RECREATE THE NEW BITMAP

	Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

	return resizedBitmap;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
	// Raw height and width of image
	final int height = options.outHeight;
	final int width = options.outWidth;
	int inSampleSize = 1;

	if (height > reqHeight || width > reqWidth)
	{

	    final int halfHeight = height / 2;
	    final int halfWidth = width / 2;

	    // Calculate the largest inSampleSize value that is a power of 2 and
	    // keeps both
	    // height and width larger than the requested height and width.
	    while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth)
	    {
		inSampleSize *= 2;
	    }
	}

	return inSampleSize;
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap)
    {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	byte[] byte_arr = stream.toByteArray();
	// String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

	return byte_arr;
    }

    public static Bitmap maskingRoundBitmap(Bitmap original)
    {
        int minWidth = Math.min(original.getWidth(), original.getHeight());
        Bitmap mask = Bitmap.createBitmap(minWidth, minWidth, Config.ARGB_8888);

        Canvas canvas = new Canvas(mask);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(minWidth / 2, minWidth / 2, minWidth / 2, paint);

        Bitmap result = Bitmap.createBitmap(minWidth, minWidth, Config.ARGB_8888);

        Canvas mCanvas = new Canvas(result);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(original, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);

        paint.setXfermode(null);

        if (mask != null)
        {
            mask.recycle();
            mask = null;
        }
        if (original != null)
        {
            original.recycle();
            original = null;
        }

        return result;
    }
}
