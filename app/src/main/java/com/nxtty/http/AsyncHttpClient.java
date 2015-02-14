package com.nxtty.http;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.nxtapp.fragment.GroupDetailActivity;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.callback.ResultCallback;

public class AsyncHttpClient
{

    private ResultCallback mResultCallBack;
    private AsyncHttpRequest mAsyncHttpRequest;
    Context mContext; // Network.isNetworkConnected(mContext)

    /*
     * public static AsyncHttpClient getDefaultInstance() {
     * 
     * 
     * return new AsyncHttpClient(); }
     */

    public void executeJSONObject(Context mContext, AsyncHttpRequest req, final ResultCallback callback)
    {

	try
	{
	    mResultCallBack = callback;
	    this.mContext = mContext;

	} catch (Exception e)
	{
	    e.printStackTrace();
	}

	try
	{

	    new AsyncHttpClientTask().execute(req);
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
    };

    private String getDataFromWebService(AsyncHttpRequest req) throws IOException
    {

	String jsonResult = null;
	boolean isNetworkAvailale = isNetworkConnected(mContext);
	System.out.println(" isNetworkAvailale >> " + isNetworkAvailale + " >>> " + req.getUserInfo());

	if (isNetworkAvailale)
	{

	    mAsyncHttpRequest = req;

	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(req.getURI());

	    ArrayList<NameValuePair> pairs = req.getBody();

	    if (pairs != null)
		httpPost.setEntity(new UrlEncodedFormEntity(pairs));

	    HttpResponse httpResponse = httpClient.execute(httpPost);

	  

	    jsonResult = EntityUtils.toString(httpResponse.getEntity());

	  
	}

	return jsonResult;

    }

    private class AsyncHttpClientTask extends AsyncTask<AsyncHttpRequest, Boolean, String>
    {

	@Override
	protected String doInBackground(AsyncHttpRequest... params)
	{

	    try
	    {
		
		 DebugReportOnLocat.ln(" getDataFromWebService >>> " );
		 
		return getDataFromWebService(params[0]);
	    } catch (IOException e)
	    {

		DebugReportOnLocat.e(e);
	    }
	    return "";
	}

	@Override
	protected void onPostExecute(String JSONObjectString)
	{

	    DebugReportOnLocat.ln(" JSONObjectString >>>UserInfo >>>  "+mAsyncHttpRequest.getUserInfo()+" >>>  >>>>> " + JSONObjectString);

	    if (mResultCallBack != null)
	    {

		try
		{
		    mResultCallBack.onCompleted(null, JSONObjectString, mAsyncHttpRequest.getUserInfo());
		    
		 
		   
		    
		} catch (NullPointerException e)
		{
		   // GroupDetailActivity.dismissProgress(mContext);
		    DebugReportOnLocat.e(e);
		}
		
		try
		{
		    
		    if(mAsyncHttpRequest.getUserInfo().equals(HttpUri.GROUPS_FETCH_POST)){
			
			 GroupDetailActivity.dismissProgress(mContext);
		    }
		    
		} catch (Exception e)
		{
		   DebugReportOnLocat.e(e);
		}

	    }

	}
    }

    public boolean isNetworkConnected(Context ctx)
    {
	ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo ni = cm.getActiveNetworkInfo();
	if (ni == null)
	{
	    // There are no active networks.
	    return false;
	}
	else
	    return true;
    }

}
