package com.nxtty.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.nxtapp.utils.DebugReportOnLocat;

public class GetNewDataService extends Service
{

    private String TAG = "GetNewDataService";

    @Override
    public IBinder onBind(Intent intent)
    {

	throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
	// Perform your long running operations here.

    }

    @Override
    public void onDestroy()
    {
    }

    @Override
    public void onCreate()
    {

	 DebugReportOnLocat.e(TAG, "service=>keyValue=>" + MainActivity.key + ",publicKey=>" + MainActivity.pKey);

	new asynk(getApplicationContext()).start();

    }

    public class asynk extends WorkerThread
    {

	public asynk(Context context)
	{
	    super(context);
	    enableProgressBar(false);
	}

	@Override
	public String onWorkInBackground()
	{
	   

	    String respStr = null;

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 15000);
//	    HttpPost httppost = new HttpPost("https://wallet.nxtty.com/nxttyfaucet.php");
	    HttpPost httppost = new HttpPost("http://128.199.189.226/nxt.php");
	    
 
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {

		nameValuePairs.add(new BasicNameValuePair("recipient", MainActivity.key));
		nameValuePairs.add(new BasicNameValuePair("recipientPublicKey", MainActivity.pKey));

		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		 DebugReportOnLocat.e(TAG, "  responce for new data-->  " + respStr);

	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
	    }

	    return null;
	}

	@Override
	public synchronized void onWorkFinished(String result)
	{
	   
	    super.onWorkFinished(result);
	    stopSelf();
	}

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
	
	return super.onStartCommand(intent, flags, startId);
    }

}