package com.nxtapp.fragment;

import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nxtapp.adapter.InvitesAdapter;
import com.nxtapp.classes.Invites;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.Network;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpClient;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;
import com.nxtty.main.R;

public class InvitesFragment extends BaseListFragment
{

    InvitesAdapter discoverAdap;
    ListView myListView;
    Invites addListitems;
    private ArrayList<Invites> InvitesList = new ArrayList<Invites>();

    @SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
	View view = inflater.inflate(R.layout.inviteslist, null, false);
	myListView = (ListView) view.findViewById(android.R.id.list);

	//new GetDataInvites().execute();
	if(Network.isNetworkConnected(getActivity().getApplicationContext())){
	    
	      DataInvites();
	}
	else{
	    AlertUtility.showToast(getActivity().getApplicationContext(),  getString(R.string.networkIssues));
	}
  
	return view;
    }
    
    ProgressDialog pDialog;
    public void DataInvites(){


	
	    
	
	                 try
	                 {
	                     
	                     pDialog = new ProgressDialog(getActivity());
	                     pDialog.setMessage(getResources().getString(R.string.please_wait));
	                     pDialog.show();
	                 
	                     
	                 } catch (Exception e)
	                 {
	                      DebugReportOnLocat.e(e);
	                 }


		AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.SUBSCRIBER_CONFIRM_PENDING_REQUESTS);
		req.setBody(JSONBody.publicChat(Constants.NxtAcId));
		req.setUserInfo(HttpUri.SUBSCRIBER_CONFIRM_PENDING_REQUESTS);
		//final CategoryManager cManager = new CategoryManager(this);
		new AsyncHttpClient().executeJSONObject(getActivity().getApplicationContext() ,req,
				new ResultCallback() {

					@Override
					public void onCompleted(Exception e, String responseString, String methodInfo) {
					    
					    GetContacts(responseString);

					 
					    discoverAdap = new InvitesAdapter(getActivity(), InvitesList);
					    myListView.setAdapter(discoverAdap);

					}

					private void GetContacts(String respStr)
					{     // parsing
					    try
					    {
						if (respStr != null)
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
							model.setAvatarImage(jobj.optString("avatar"));
							 model.setUserStatus(StringEscapeUtils.unescapeJava(jobj.optString("status")));
							//model.setUserStatus(jobj.optString("status"));
							
							model.setSchool(jobj.optString("school"));

							InvitesList.add(model);

						    }
						}

					    } catch (JSONException e)
					    {
						 DebugReportOnLocat.e(e);
					    }finally{
						
						try
						{
						    if(pDialog!=null){
							   pDialog.dismiss();
						    }
						} catch (Exception e2)
						{
						   e2.printStackTrace();
						}
					    }
					}
				});  



    }
  /*  private class GetDataInvites extends AsyncTask<Void, String, Void>
    {
	ProgressDialog pDialog;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();
	    pDialog = new ProgressDialog(getActivity());
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
		if (respStr != null)
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
			model.setAvatarImage(jobj.optString("avatar"));
			model.setUserStatus(jobj.optString("status"));
			model.setSchool(jobj.optString("school"));

			InvitesList.add(model);

		    }
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

	    pDialog.dismiss();

	    discoverAdap = new InvitesAdapter(getActivity(), InvitesList);
	    myListView.setAdapter(discoverAdap);
	}

    }
*/
    public static InvitesFragment newInstance()
    {
	InvitesFragment fragment = new InvitesFragment();
	return fragment;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);

    }

}
