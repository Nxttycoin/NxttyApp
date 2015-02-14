package com.nextapp.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

public class DecryptImageLoad extends AsyncTask<String, String, String>
{

    private static final String TAG = "DecryptImage";
    String avatar = "";
    Context mContext;
    boolean isGroup;

    ImageView imgView;
    public DecryptImageLoad(Context mContext, String imagePath,ImageView imgView,boolean isGroup)
    {
	this.avatar = imagePath;
	this.mContext = mContext;
	this.imgView=imgView;
	this.isGroup=isGroup;
    }

    @Override
    protected String doInBackground(String... params)
    {

	if(avatar==null) return null;
	
	String imageFilePath = imageCheck(avatar,isGroup);

	return imageFilePath;

    }

    @Override
    protected void onPostExecute(String result)
    {

	super.onPostExecute(result);

	if (result != null && !result.equalsIgnoreCase("null"))
	{
	
	  try
	    {
		
		 Bitmap bitmap = BitmapFactory.decodeFile(result);
		 imgView.setImageBitmap(bitmap);
		    
		    return;
		    
	    } catch (OutOfMemoryError e)
	    {
		// TODO: handle exception
	    }catch (Exception e)
	    {
		// TODO: handle exception
	    }
	  
	   // imageLoader.displayImage(Constants.baseUrl_Images + avtar, ivProfilePic, options);
	}
	
	try
	{
	    Drawable drawable = mContext.getResources().getDrawable( R.drawable.loading );
		imgView.setImageDrawable( drawable );
		
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
	
	
	
	
    }

    public String imageCheck(String imagePath,boolean isGroup)
    {

	String nonce = "", data = "";

	try
	{

	    String json = "";
	    JSONObject jObj = null;

	    File imagesFolderPath = mContext.getExternalFilesDir("images");

	    String filePath = imagesFolderPath.getAbsolutePath() + "/" + imagePath;

	    DebugReportOnLocat.ln(" filePath >>> " + filePath);

	    File file = new File(filePath);

	    DebugReportOnLocat.ln(" file  " + file.getName());

	    if (file.exists())
	    {

		DebugReportOnLocat.ln(" File is exist ");

		return filePath;

	    }
	    else
	    {

		InputStream is = null;

		DefaultHttpClient httpClient = new DefaultHttpClient();
			String url=Constants.baseUrl_Images + imagePath;
		
		if(isGroup) url=Constants.baseUrl_ImagesGroup+ imagePath;
		
		DebugReportOnLocat.ln(" url >>> "+url);
		
		HttpGet httpGet = new HttpGet(url);

		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();

		try
		{
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		    StringBuilder sb = new StringBuilder();
		    String line = null;
		    while ((line = reader.readLine()) != null)
		    {
			sb.append(line + "\n");
		    }
		    is.close();
		    json = sb.toString();
		} catch (Exception e)
		{
		    DebugReportOnLocat.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try
		{
		    
		    jObj = new JSONObject(json);
		    
		        nonce = jObj.getString("nonce");
			data = jObj.getString("data");

			String chatMsg = decryptMessage(nonce, data, Constants.NxtAcId);

			byte[] bytevalue;

			bytevalue = Hex.decodeHex(chatMsg.toCharArray());

			writeToFile(filePath, bytevalue);
			
		    
		} catch (JSONException e)
		{
		    DebugReportOnLocat.e("JSON Parser", "Error parsing data " + e.toString());
		}

		

		return filePath;

	    }

	} catch (IllegalStateException e)
	{

	    DebugReportOnLocat.e(e);
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}

	return "null";
    }

    public static String decryptMsg = "";

    private static String decryptMessage(String nonce, String data, String senderId)
    {

	HttpClient httpclient = new DefaultHttpClient();
	HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 15000);
	String url = Constants.baseUrl + "?";
	HttpPost httppost = new HttpPost(url);

	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	try
	{

	    String secretPhrase = Constants.sharedPreferences.getString("SecretPhrase", "");
	    DebugReportOnLocat.e(TAG, "decrypt,Constants.secretPhrase=>" + secretPhrase);

	    nameValuePairs.add(new BasicNameValuePair("requestType", "decryptFrom"));
	    nameValuePairs.add(new BasicNameValuePair("secretPhrase", secretPhrase));
	    nameValuePairs.add(new BasicNameValuePair("account", senderId));

	    DebugReportOnLocat.e(TAG, "decrypt,nonce=>" + nonce);
	    DebugReportOnLocat.e(TAG, "decrypt,data=>" + data);
	    DebugReportOnLocat.e(TAG, "decrypt,senderId=>" + senderId);

	    nameValuePairs.add(new BasicNameValuePair("data", data));
	    nameValuePairs.add(new BasicNameValuePair("decryptedMessageIsText", "false"));// "true"
	    nameValuePairs.add(new BasicNameValuePair("nonce", nonce));

	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    HttpResponse response = httpclient.execute(httppost);
	    String res = EntityUtils.toString(response.getEntity());
	    DebugReportOnLocat.e(TAG, "  responce for decrypt,res-->  " + res);
	    try
	    {
		JSONObject jsonObject = new JSONObject(res);
		if (jsonObject.has("decryptedMessage"))
		{

		    String decryptMsgFromRes = jsonObject.getString("decryptedMessage");

		    decryptMsg = StringEscapeUtils.unescapeJava(decryptMsgFromRes);
		    DebugReportOnLocat.e(TAG, "decrypt msg escape=>" + decryptMsg);

		}
	    } catch (JSONException e)
	    {
		DebugReportOnLocat.e(e);
		decryptMsg = "";
	    }

	    DebugReportOnLocat.e(TAG, "  responce for decrypt-->  " + decryptMsg);

	} catch (ClientProtocolException e)
	{
	} catch (IOException e)
	{
	}
	return decryptMsg;
    }

    @SuppressWarnings("resource")
    public void writeToFile(String path, byte[] array)
    {
	try
	{

	    FileOutputStream stream = new FileOutputStream(path);
	    stream.write(array);

	} catch (FileNotFoundException e1)
	{
	    e1.printStackTrace();
	} catch (IOException e)
	{

	    e.printStackTrace();
	}
    }

}
