package com.nxtty.main;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import aniways.com.google.analytics.tracking.android.Log;

import com.aniways.Aniways;
import com.nxtapp.fragment.GroupDetailActivity;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.KeyboardUtility;
import com.nxtapp.utils.Network;

public class GroupDetailAddComment extends Activity
{

    private EditText editDetail;
    private String postID;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	Aniways.init(GroupDetailAddComment.this);
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.create_comment);

	mContext=this;
	if (getIntent().getExtras() != null)
	{
	    postID = getIntent().getExtras().getString("PostID");
	}

	editDetail = (EditText) findViewById(R.id.edit_CreateDetail);
	TextView txtPost = (TextView) findViewById(R.id.txt_create_post);
	TextView txtcancelBottom = (TextView) findViewById(R.id.txt_cancelBottom);
	TextView txtcancelTop = (TextView) findViewById(R.id.txt_create_cancel);

	txtcancelBottom.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		 Intent data = new Intent();
		setResult(Activity.RESULT_CANCELED, data);
		finish();
	    }
	});
	txtcancelTop.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		 Intent data = new Intent();
		setResult(Activity.RESULT_CANCELED, data);
		finish();
	    }
	});

	txtPost.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		final String Message = Aniways.encodeMessage(editDetail.getText());
		DebugReportOnLocat.e("################", "  Message : " + Message);

		if (Message.trim().length() >= 1)
		{

		    if (Network.isNetworkConnected(getApplicationContext()))
		    {
			// dialog.cancel();
			new SendComment(Message).execute();
			try
			{
			    KeyboardUtility.hideKeypad((Activity) mContext);
			    
			} catch (Exception e)
			{
			    DebugReportOnLocat.e(e);
			}

		    }
		    else
		    {
			AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
		    }

		}
		else if (editDetail.getText().toString().trim().length() == 0)
		{
		    // Bala Code
		    AlertUtility.showToast(getApplicationContext(), getString(R.string.enter_comment));

		}

	    }
	});
    }

    public class SendComment extends AsyncTask<Void, String, String>
    {

	ProgressDialog pDialog;
	String Title, Message, path;

	public SendComment(String Message)
	{
	    this.Message = Message;
	}

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {
		pDialog = new ProgressDialog(GroupDetailAddComment.this);
		pDialog.setMessage(getResources().getString(R.string.please_wait));
		pDialog.show();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }

	}

	@Override
	protected String doInBackground(Void... params)
	{

	    String respStr = null;

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/create_comment");

	    MultipartEntity reqEntity = new MultipartEntity();
	    try
	    {
		DebugReportOnLocat.e("##############", "  postID  -->  " + postID);
		SharedPreferences pref = getSharedPreferences("ID", 0);
		Constants.NxtAcId = pref.getString("nxtAcId", "0");

		// DebugReportOnLocat.e("Chat2", "Constants.NxtAcId: " +
		// Constants.NxtAcId);
		reqEntity.addPart("nxtID", new StringBody(Constants.NxtAcId));
		reqEntity.addPart("postID", new StringBody(postID));
		reqEntity.addPart("key", new StringBody(Constants.ParamKey));
		DebugReportOnLocat.e("CommentActivity", "  postID  -->  " + postID);
		String unicodeString = convertToUnicodeEscaped(Message);

		reqEntity.addPart("comment", new StringBody(unicodeString));

		httppost.setEntity(reqEntity);
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		DebugReportOnLocat.e("##############", "  responce from conversation  -->  " + respStr);
	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
	    }

	    return respStr;
	}

	@Override
	protected void onPostExecute(String result)
	{
	    super.onPostExecute(result);
	    try
	    {
		if (pDialog != null)
		    pDialog.dismiss();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);

	    }
	    ;

	    if (result != null && !result.equals("null"))
	    {
		try
		{
		    JSONObject jsonObject = new JSONObject(result);
		    boolean status = jsonObject.getBoolean("status");
		    if (status)
		    {
			Log.e("CommentActivity,status=>" + status);
			GroupDetailActivity.ComingFromCreatePost = true;
			
			 Intent data = new Intent();
				setResult(Activity.RESULT_OK, data);
			finish();
		    }

		} catch (JSONException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }

	}
    }

    private String unicodeEscaped(char ch)
    {
	if (ch < 0x10)
	{
	    return "\\u000" + Integer.toHexString(ch);
	}
	else if (ch < 0x100)
	{
	    return "\\u00" + Integer.toHexString(ch);
	}
	else if (ch < 0x1000)
	{
	    return "\\u0" + Integer.toHexString(ch);
	}
	return "\\u" + Integer.toHexString(ch);
    }

    private String convertToUnicodeEscaped(String str)
    {
	StringBuilder sbUnicodeBuilder = new StringBuilder();

	char[] chArr = str.toCharArray();
	for (char mChar : chArr)
	{
	    sbUnicodeBuilder.append(unicodeEscaped(mChar));
	}

	return sbUnicodeBuilder.toString();
    }

}
