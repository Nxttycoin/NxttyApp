package com.nxtapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.nxtty.main.R;

public class CropperImageActivity extends Activity
{

    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final int ROTATE_NINETY_NEG_DEGREES = -90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    private static final int ON_TOUCH = 1;

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    // private int mAspectRatioX = 500;
    // private int mAspectRatioY = 500;

    Bitmap croppedImage;
    String path = "";
    CropImageView cropImageView;
    Button cropButton;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
	super.onSaveInstanceState(bundle);
	bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
	bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle)
    {
	super.onRestoreInstanceState(bundle);
	mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
	mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.crop_activity);

	DisplayMetrics metrics = getResources().getDisplayMetrics();
	int width = metrics.widthPixels;
	int height = metrics.heightPixels;


	Bundle bundle = getIntent().getExtras();
	
	// Initialize components of the app
		cropImageView = (CropImageView) findViewById(R.id.CropImageView);
		
	try
	{
	    
	    if (bundle != null)
		{
		    path = bundle.getString("Path");
		    Log.d("path", "~~~~>>>>" + path);
		}

	    
	    File imgFile = new File(path);
		

		Bitmap bitmap = null;
		if(imgFile.getAbsolutePath() !=null){
		bitmap = getBitmapDefault(imgFile.getAbsolutePath(), width, height);
		Log.d("Bitmap", ">>>" + bitmap);
		}
		
		
		if (bitmap != null)
		{
		    cropImageView.setImageBitmap(bitmap);
		}
		else{
		    
		    this.finish();
		}
		
	} catch (RuntimeException e)
	{
	     DebugReportOnLocat.e(e);
	    this.finish();
	    
	}
	catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	    this.finish();
	    
	}
	
	

	// Sets the rotate button
	final ImageButton rotateClockBtn = (ImageButton) findViewById(R.id.btnRotateClock);
	rotateClockBtn.setOnClickListener(new View.OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {
		cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
	    }
	});

	final ImageButton rotateAntiClockBtn = (ImageButton) findViewById(R.id.btnRotateAntiClock);
	rotateAntiClockBtn.setOnClickListener(new View.OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {
		cropImageView.rotateImage(ROTATE_NINETY_NEG_DEGREES);
	    }
	});

	

	// Sets initial aspect ratio to 10/10, for demonstration purposes
	cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
	cropImageView.setFixedAspectRatio(false);

	

	cropButton = (Button) findViewById(R.id.Button_crop);
	cropButton.setOnClickListener(new View.OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {
		croppedImage = cropImageView.getCroppedImage();

		FileOutputStream fout;
		try
		{
		    fout = new FileOutputStream(path);
		    croppedImage.compress(Bitmap.CompressFormat.PNG, 100, fout);
		    fout.flush();
		    fout.close();
		} catch (FileNotFoundException e)
		{
		     DebugReportOnLocat.e(e);
		} catch (IOException e)
		{
		     DebugReportOnLocat.e(e);
		}

		Log.d("Cropbutton", ">>>called");
		Intent i = getIntent();
		i.putExtra("picture_path", path);
		/* i.putExtra("picture_path", croppedImage); */
		setResult(RESULT_OK, i);
		finish();

	    }
	});

    }

    /*
     * Sets the font on all TextViews in the ViewGroup. Searches recursively for
     * all inner ViewGroups as well. Just add a check for any other views you
     * want to set as well (EditText, etc.)
     */
    public void setFont(ViewGroup group, Typeface font)
    {
	int count = group.getChildCount();
	View v;
	for (int i = 0; i < count; i++)
	{
	    v = group.getChildAt(i);
	    if (v instanceof TextView || v instanceof EditText || v instanceof Button)
	    {
		((TextView) v).setTypeface(font);
	    }
	    else if (v instanceof ViewGroup)
		setFont((ViewGroup) v, font);
	}
    }

    public Bitmap getBitmapDefault(String pathOfInputImage, int outwidth, int outheight)
    {
	try
	{
	    int inWidth = 0;
	    int inHeight = 0;
	    // pathOfInputImage = Environment.getExternalStorageDirectory()
	    // .toString() + "/ca7ch/" + pathOfInputImage;
	    InputStream in = new FileInputStream(pathOfInputImage);

	    // decode image size (decode metadata only, not the whole image)
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;

	    BitmapFactory.decodeStream(in, null, options);
	    in.close();
	    in = null;

	    // save width and height
	    inWidth = options.outWidth;
	    inHeight = options.outHeight;

	    // decode full image pre-resized
	    in = new FileInputStream(pathOfInputImage);
	    options = new BitmapFactory.Options();
	    // calc rought re-size (this is no exact resize)
	    options.inSampleSize = Math.max(inWidth / outwidth, inHeight / outheight);
	    // decode full image
	    Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);
	    if(roughBitmap != null){
	    // calc exact destination size
	    Matrix m = new Matrix();
	    RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
	    RectF outRect = new RectF(0, 0, outwidth, outheight);
	    m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
	    float[] values = new float[9];
	    m.getValues(values);

	    // resize bitmap
	    Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
	    /*
	     * Now just have to check orientation
	     */
	    
	    ExifInterface exif = new ExifInterface(pathOfInputImage);
	    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	    Log.e("log_tag", "orientation: " + orientation);
	    int angle = 0;

	    if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
	    {
		angle = 90;
	    }
	    else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
	    {
		angle = 180;
	    }
	    else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
	    {
		angle = 270;
	    }

	    Log.d("Tag", "Angle: " + angle);

	    Matrix mat = new Matrix();
	    mat.postRotate(angle);

	    if (angle != 0)
	    {
		Bitmap bitmap2 = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(), mat, true);

		resizedBitmap.recycle();

		return bitmap2;
	    }
	    return resizedBitmap;
	    }
	    else{
		return null;
	    }
	   
	} catch (IOException e)
	{
	    Log.e("Image", e.getMessage(), e);
	    return null;
	}
    }

}
