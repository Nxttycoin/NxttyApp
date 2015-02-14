package com.nextapp.tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;

public class EncryptImageAsynk extends AsyncTask<String, String, String>
{

    String imagePath = "";
    boolean isFile;

    public EncryptImageAsynk(String imagePath, boolean isFile)
    {
	this.imagePath = imagePath;
	this.isFile = isFile;
    }

    @Override
    protected void onPreExecute()
    {

	super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params)
    {

	String message = "";

	try
	{
	    Bitmap bitmap = null;

	    File f = new File(imagePath);

	    bitmap = decodeFile(f);

	    if (bitmap != null)
	    {

		try
		{
		    // Image to Hex binary string conversion
		    byte[] bytevalue = getBytesFromBitmap(bitmap);

		    message = new String(Hex.encodeHex(bytevalue));

		} catch (Throwable e)
		{

		    DebugReportOnLocat.e(e);

		}

	    }

	} catch (Exception e)
	{

	    DebugReportOnLocat.e(e);

	}

	try
	{

	    if (message != null && message.length() > 100)
	    {

		String response = encryptMessage(message, isFile);

		DebugReportOnLocat.ln(" encryptMessage response " + response);

		return response;
	    }

	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}

	return "";

    }

    @Override
    protected void onPostExecute(String result)
    {

	super.onPostExecute(result);

    }

    private String encryptMessage(String content, boolean isFile)
    {
	String respStr = null;

	HttpClient httpclient = new DefaultHttpClient();
	HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 150000);
	String url = Constants.baseUrl + "?";
	HttpPost httppost = new HttpPost(url);

	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	try
	{

	    String secretPhrase = Constants.sharedPreferences.getString("SecretPhrase", "");
	
	    DebugReportOnLocat.ln(">>G encrypt,secretPhrase=> " + secretPhrase);
	    DebugReportOnLocat.ln(">>G encrypt,recipient=> " + Constants.NxtAcId);
	    DebugReportOnLocat.ln(">>G encrypt, messageToEncrypt=> " + content);
	    
	    
	    nameValuePairs.add(new BasicNameValuePair("requestType", "encryptTo"));
	    nameValuePairs.add(new BasicNameValuePair("secretPhrase", secretPhrase));
	    nameValuePairs.add(new BasicNameValuePair("recipient", Constants.NxtAcId));
	    nameValuePairs.add(new BasicNameValuePair("messageToEncrypt", content));
	    nameValuePairs.add(new BasicNameValuePair("messageToEncryptIsText", "false"));

	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    HttpResponse response = httpclient.execute(httppost);
	    respStr = EntityUtils.toString(response.getEntity());

	    DebugReportOnLocat.ln(">>G  responce for encrypt-->  " + respStr);

	} catch (ClientProtocolException e)
	{
	    DebugReportOnLocat.e(e);

	} catch (IOException e)
	{
	    DebugReportOnLocat.e(e);

	} catch (Exception e)
	{

	    DebugReportOnLocat.e(e);
	}

	DebugReportOnLocat.ln(" respStr == >>> " + respStr);

	return respStr;
    }

    final int IMAGE_MAX_SIZE = 500;

    private Bitmap decodeFile(File f)
    {
	Bitmap b = null;

	// Decode image size
	BitmapFactory.Options o = new BitmapFactory.Options();
	o.inJustDecodeBounds = true;

	FileInputStream fis = null;
	try
	{
	    fis = new FileInputStream(f);
	    BitmapFactory.decodeStream(fis, null, o);

	} catch (FileNotFoundException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally
	{

	    try
	    {
		fis.close();
	    } catch (IOException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	int scale = 1;
	if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE)
	{
	    scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
	}

	DebugReportOnLocat.ln(" scale>>>" + scale);
	// Decode with inSampleSize
	BitmapFactory.Options o2 = new BitmapFactory.Options();
	o2.inSampleSize = scale;
	try
	{
	    fis = new FileInputStream(f);
	} catch (FileNotFoundException e)
	{

	    e.printStackTrace();
	}
	b = BitmapFactory.decodeStream(fis, null, o2);
	try
	{
	    fis.close();
	} catch (IOException e)
	{

	    e.printStackTrace();
	}

	DebugReportOnLocat.ln("  byte length >>>>" + b.getByteCount());

	return b;
    }

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap)
    {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	bitmap.compress(CompressFormat.JPEG, 100, stream);
	return stream.toByteArray();
    }

}
