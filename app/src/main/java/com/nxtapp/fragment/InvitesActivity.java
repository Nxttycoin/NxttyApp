package com.nxtapp.fragment;

import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.nxtapp.adapter.InvitesAdapter;
import com.nxtapp.classes.Invites;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.KeyboardUtility;
import com.nxtapp.utils.Network;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpClient;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;
import com.nxtty.main.R;

public class InvitesActivity extends Activity
{

    InvitesAdapter discoverAdap;
    ListView myListView;
    Invites addListitems;
    private ArrayList<Invites> InvitesList = new ArrayList<Invites>();
    ImageView ivBack;
    ProgressDialog mProgressDialog;
   
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.inviteslist_new);
	myListView = (ListView) findViewById(android.R.id.list);
	
	ivBack = (ImageView) findViewById(R.id.iv_invitesBack);
	ivBack.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {
         
		finish();

	    }
	});

	//new GetDataInvites().execute();


if(Network.isNetworkConnected(getApplicationContext())){
    DataInvites();
	}
	else{
	    AlertUtility.showToast(getApplicationContext(),  getString(R.string.networkIssues));
	}
	

    }
public void DataInvites(){



    showProgress("Please Wait...");

	AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.SUBSCRIBER_CONFIRM_PENDING_REQUESTS);
	req.setBody(JSONBody.publicChat(Constants.NxtAcId));
	req.setUserInfo(HttpUri.SUBSCRIBER_CONFIRM_PENDING_REQUESTS);
	//final CategoryManager cManager = new CategoryManager(this);
	new AsyncHttpClient().executeJSONObject(getApplicationContext(),req,
			new ResultCallback() {

				@Override
				public void onCompleted(Exception e, String responseString, String methodInfo) {
				    
				    PublicChat(responseString);
				    discoverAdap = new InvitesAdapter(InvitesActivity.this, InvitesList);
				    myListView.setAdapter(discoverAdap);

				}

				private void PublicChat(String respStr)
				{
				    // parsing
				    try
				    {
					JSONArray jArray = new JSONArray(respStr);
					Invites model;
					for (int i = 0; i < jArray.length(); i++)
					{

					    JSONObject jobj = jArray.getJSONObject(i);
					    model = new Invites();
					    model.setUserId(jobj.optString("nxtAccountId"));
					    model.setUserName(jobj.optString("nameAlias"));
					    model.setDeletPlanID(jobj.optString("deletePlanId"));
					    model.setRegistrationDate(jobj.optString("registrationDate"));
					    model.setAvatarImage(Constants.baseUrl_Images + jobj.optString("avatar"));
					    model.setUserStatus(StringEscapeUtils.unescapeJava(jobj.optString("status")));
					    //model.setUserStatus(jobj.optString("status"));
					   
					    model.setSchool(jobj.optString("school"));

					    InvitesList.add(model);

					}

				    } catch (JSONException e)
				    {
					 DebugReportOnLocat.e(e);
				    }finally {
					runOnUiThread(new Runnable() {
						public void run() {

						 
							dismissProgress();

						}
					});
				}

				    
				}
			});  



}
    /*private class GetDataInvites extends AsyncTask<Void, String, Void>
    {
	ProgressDialog pDialog;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();
	    pDialog = new ProgressDialog(InvitesActivity.this);
	    pDialog.setMessage(getResources().getString(R.string.please_wait));
	    pDialog.show();

	}

	@Override
	protected Void doInBackground(Void... params)
	{
	    String respStr = null;

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_new + "subscriber/confirm_pending_requests");

	     DebugReportOnLocat.e("##############", "  Constants.NxtAcId -->  " + Constants.NxtAcId);

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {

		nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));

		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		 DebugReportOnLocat.e("##############", "  responce from post -->  " + respStr);

	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
	    }

	    // parsing
	    try
	    {
		JSONArray jArray = new JSONArray(respStr);
		Invites model;
		for (int i = 0; i < jArray.length(); i++)
		{

		    JSONObject jobj = jArray.getJSONObject(i);
		    model = new Invites();
		    model.setUserId(jobj.optString("nxtAccountId"));
		    model.setUserName(jobj.optString("nameAlias"));
		    model.setDeletPlanID(jobj.optString("deletePlanId"));
		    model.setRegistrationDate(jobj.optString("registrationDate"));
		    model.setAvatarImage(Constants.baseUrl_Images + jobj.optString("avatar"));
		    model.setUserStatus(jobj.optString("status"));
		    model.setSchool(jobj.optString("school"));

		    InvitesList.add(model);

		}

	    } catch (JSONException e)
	    {
		 DebugReportOnLocat.e(e);
	    }

	    return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
	    super.onPostExecute(result);

	        try
					    {
						if(pDialog!=null)
						  pDialog.dismiss();
						  
					    } catch (Exception err)
					    {
						DebugReportOnLocat.e(err);
						
					    };

	    discoverAdap = new InvitesAdapter(InvitesActivity.this, InvitesList);
	    myListView.setAdapter(discoverAdap);
	}

    }*/
    protected void showProgress(String msg) {
   	if (mProgressDialog != null && mProgressDialog.isShowing())
   		dismissProgress();

   	mProgressDialog = ProgressDialog.show(this, "", msg);
   }

   protected void dismissProgress() {
   	if (mProgressDialog != null) {
   		mProgressDialog.dismiss();
   		mProgressDialog = null;
   	}
   }
}
