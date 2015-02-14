package com.nextapp.tasks;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.nextapp.webservice.CallWebservice;
import com.nxtapp.utils.DebugReportOnLocat;

public class CallPostWebseviceTask extends AsyncTask<Void, Void, String>
{
    private ProgressDialog progressDialog;
    private List<NameValuePair> nameValuePairs;
    private MultipartEntity entityBuilder;
    private String subUrl;

    // G code
    private Context mContext;

    private void dismissPDialog()
    {
	try
	{

	    if (progressDialog != null)
	    {

		if (progressDialog.isShowing())
		    progressDialog.dismiss();

	    }

	} catch (IllegalStateException e)
	{

	     DebugReportOnLocat.e(e);

	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}
    }

    public CallPostWebseviceTask(Context context, boolean showPDialog, String subUrl, List<NameValuePair> nameValuePairs)
    {

	this.subUrl = subUrl;
	this.nameValuePairs = nameValuePairs;
	this.entityBuilder = null;
	mContext = context;
	if (showPDialog)
	{
	    progressDialog = new ProgressDialog(context);
	    progressDialog.setCanceledOnTouchOutside(false);
	    progressDialog.setMessage("Please Wait...");
	}

    }

    public CallPostWebseviceTask(Context context, boolean showPDialog, String subUrl, MultipartEntity entityBuilder)
    {

	this.subUrl = subUrl;
	this.nameValuePairs = null;
	this.entityBuilder = entityBuilder;
	mContext = context;
	
	
	try
	{/*
	    if (showPDialog)
		{
		    progressDialog = new ProgressDialog(context);
		    progressDialog.setCanceledOnTouchOutside(false);
		    progressDialog.setMessage("Please Wait...");
		}
	    
	*/} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}

    }

    @Override
    protected void onPreExecute()
    {

	super.onPreExecute();
	// if (progressDialog != null)
	// {
	// progressDialog.show();
	// }
    }

    @Override
    protected String doInBackground(Void... p)
    {

	String response = null;
	try
	{
	    if (nameValuePairs != null)
	    {
		response = CallWebservice.callPostMethod(subUrl, nameValuePairs, mContext);
	    }
	    else if (entityBuilder != null)
	    {
		response = CallWebservice.callPostMethodWithMultipart(subUrl, entityBuilder);
	    }
	    if (response != null)
	    {
		parseResponse(response);
	    }

	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}
	return response;
    }

    /***
     * This method will process on thread
     * 
     * @param response
     */
    protected void parseResponse(String response)
    {

    }

    @Override
    protected void onPostExecute(String result)
    {
	// TODO Auto-generated method stub
	super.onPostExecute(result);
	dismissPDialog();
    }

}
