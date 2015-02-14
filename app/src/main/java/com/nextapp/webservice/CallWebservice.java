package com.nextapp.webservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.Network;

public class CallWebservice
{

    public static String callPostMethod(String subUrl, List<NameValuePair> nameValuePairs, Context ctx) throws Exception
    {

	String result = null;

	if (Network.isNetworkConnected(ctx))
	{

	    if (!subUrl.equals("unread_messages"))
	    {
		DebugReportOnLocat.e("web_service", "callPostMethod url: " + Constants.BASE_SUBSCRIBER_URL + subUrl);
	    }

	    HttpClient httpClient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(Constants.BASE_SUBSCRIBER_URL + subUrl);

	    DebugReportOnLocat.ln(" >>> Method called " + Constants.BASE_SUBSCRIBER_URL + subUrl);

	    if (nameValuePairs != null)
	    {
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    }

	    HttpResponse response = httpClient.execute(httpPost);
	    result = EntityUtils.toString(response.getEntity());

	    DebugReportOnLocat.ln(" >>> Method Response  " + result);

	    if (!subUrl.equals("unread_messages"))
	    {
		// Log.i("web_service", "result: " + result);
	    }

	}

	return result;

    }

    public static String callPostMethodWithMultipart(String subUrl, MultipartEntity entityBuilder) throws Exception
    {
	InputStream is;
	String line;
	String result = null;
	 DebugReportOnLocat.ln("callPostMethodWithMultipart url: " + Constants.BASE_SUBSCRIBER_URL + subUrl);
	HttpClient httpClient = new DefaultHttpClient();
	HttpPost httpPost = new HttpPost(Constants.BASE_SUBSCRIBER_URL + subUrl);

	if (entityBuilder != null)
	{
	    httpPost.setEntity(entityBuilder);
	}

	HttpResponse response = httpClient.execute(httpPost);
	HttpEntity entityResponse = response.getEntity();
	is = entityResponse.getContent();

	BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
	StringBuilder sb = new StringBuilder();

	while ((line = reader.readLine()) != null)
	{

	    sb.append(line + "\n");
	}

	is.close();
	result = sb.toString();
	// Log.i("web_service", "result: " + result);
	return result;

    }

    public static String callPostMethodWithMultipart2(String subUrl, MultipartEntity entityBuilder) throws Exception
    {
	InputStream is;
	String line;
	String result = null;
	Log.i("web_service", "callPostMethodWithMultipart url 2 : " + Constants.baseUrl_Group + subUrl);
	HttpClient httpClient = new DefaultHttpClient();
	HttpPost httpPost = new HttpPost(Constants.baseUrl_Group + subUrl);

	if (entityBuilder != null)
	{
	    httpPost.setEntity(entityBuilder);
	}

	HttpResponse response = httpClient.execute(httpPost);
	HttpEntity entityResponse = response.getEntity();
	is = entityResponse.getContent();

	BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
	StringBuilder sb = new StringBuilder();

	while ((line = reader.readLine()) != null)
	{

	    sb.append(line + "\n");
	}

	is.close();
	result = sb.toString();
	// Log.i("web_service", "result: " + result);
	return result;

    }
}
