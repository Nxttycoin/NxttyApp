package com.nxtapp.fragment;

import java.util.ArrayList;
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
import com.nxtapp.adapter.NewsFeedAdapter;
import com.nxtapp.classes.NewsFeed;
import com.nxtapp.classes.UserModel;
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

@SuppressLint("InflateParams")
public class NewsFeedFragment extends BaseListFragment {

	NewsFeedAdapter newsfeedAdap;
	ListView myListView;
	private ArrayList<NewsFeed> NewsFeedList = new ArrayList<NewsFeed>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    
	    NewsFeedList = new ArrayList<NewsFeed>();
	    
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.newsfeedlist, null, false);
		myListView = (ListView) view.findViewById(android.R.id.list);

		 DebugReportOnLocat.e("#########", "  Inside OnCreate  :  ");

		//new GetAsyncNewsFeed().execute();
		
		
		newsfeedAdap = new NewsFeedAdapter(getActivity(), NewsFeedList);
		myListView.setAdapter(newsfeedAdap);


if(Network.isNetworkConnected(getActivity().getApplicationContext())){
    		NewsFeed();
	}
	else{
	    AlertUtility.showToast(getActivity().getApplicationContext(),  getString(R.string.networkIssues));
	}

		
		
		
		return view;
	}
	
	    ProgressDialog pDialog=null;
        public void NewsFeed(){



	 
	    
	    try
	    {
		
		  pDialog = new ProgressDialog(getActivity());
		    pDialog.setMessage(getResources().getString(R.string.please_wait));
		    pDialog.show();
		
	    } catch (Exception e)
	    {
		 DebugReportOnLocat.e(e);
	    }
	  


		AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.SUBSCRIBER_ACTIVITIES);
		req.setBody(JSONBody.publicChat(Constants.NxtAcId));
		req.setUserInfo(HttpUri.SUBSCRIBER_ACTIVITIES);
		//final CategoryManager cManager = new CategoryManager(this);
		new AsyncHttpClient().executeJSONObject(getActivity().getApplicationContext(),req,
				new ResultCallback() {

					@Override
					public void onCompleted(Exception e, String responseString, String methodInfo) {
					    
					    GetContacts(responseString);

					  
					    newsfeedAdap = new NewsFeedAdapter(getActivity(), NewsFeedList);
					    myListView.setAdapter(newsfeedAdap);
					    
					    try
					    {
						if(pDialog!=null)						
						  pDialog.dismiss();
						  
						
					    } catch (Exception e2)
					    {
						e2.printStackTrace();
					    }

					}

					private void GetContacts(String respStr)
					{// Parsing
						try {
							JSONArray jArray = new JSONArray(respStr);
							NewsFeed model;
							for (int i = 0; i < jArray.length(); i++) {

								JSONObject jobj = jArray.getJSONObject(i);
								model = new NewsFeed();
								model.setActivityType(jobj.optString("activityType"));
								model.setBody(jobj.optString("body"));
								model.setCreated(jobj.optString("created"));
								model.setId(jobj.optString("id"));
								model.setImage(Constants.baseUrl_Images+jobj.optString("image"));

								JSONObject jobjUser = jobj.getJSONObject("user");
								UserModel Umodel = new UserModel();
								Umodel.setAvatar(Constants.baseUrl_Images+jobjUser.optString("avatar"));
								Umodel.setCity(jobjUser.optString("city"));
								Umodel.setDeletePlanId(jobjUser.optString("deletePlanId"));
								Umodel.setGender(jobjUser.optString("gender"));
								Umodel.setNameAlias(jobjUser.optString("nameAlias"));
								Umodel.setNxtAccountId(jobjUser.optString("nxtAccountId"));
								Umodel.setRegistrationDate(jobjUser
										.optString("registrationDate"));
								Umodel.setSchool(jobjUser.optString("school"));
								Umodel.setStatus(jobjUser.optString("status"));

								ArrayList<UserModel> Temp = new ArrayList<UserModel>();
								Temp.add(Umodel);

								model.setUserDetail(Temp);

								NewsFeedList.add(model);

							}

						} catch (JSONException e) {
							 DebugReportOnLocat.e(e);
						}
					}
				});  



    
        }
	/*public class GetAsyncNewsFeed extends AsyncTask<Void, String, Void> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage(getResources().getString(R.string.please_wait));
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String respStr = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
					10000);
			HttpPost httppost = new HttpPost(Constants.baseUrl_new
					+ "subscriber/activities");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			try {

				nameValuePairs.add(new BasicNameValuePair("nxtID",
						Constants.NxtAcId));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				respStr = EntityUtils.toString(response.getEntity());

				 DebugReportOnLocat.e("##############", "  responce from post -->  " + respStr);

			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}

			// Parsing
			try {
				JSONArray jArray = new JSONArray(respStr);
				NewsFeed model;
				for (int i = 0; i < jArray.length(); i++) {

					JSONObject jobj = jArray.getJSONObject(i);
					model = new NewsFeed();
					model.setActivityType(jobj.optString("activityType"));
					model.setBody(jobj.optString("body"));
					model.setCreated(jobj.optString("created"));
					model.setId(jobj.optString("id"));
					model.setImage(Constants.baseUrl_Images+jobj.optString("image"));

					JSONObject jobjUser = jobj.getJSONObject("user");
					UserModel Umodel = new UserModel();
					Umodel.setAvatar(Constants.baseUrl_Images+jobjUser.optString("avatar"));
					Umodel.setCity(jobjUser.optString("city"));
					Umodel.setDeletePlanId(jobjUser.optString("deletePlanId"));
					Umodel.setGender(jobjUser.optString("gender"));
					Umodel.setNameAlias(jobjUser.optString("nameAlias"));
					Umodel.setNxtAccountId(jobjUser.optString("nxtAccountId"));
					Umodel.setRegistrationDate(jobjUser
							.optString("registrationDate"));
					Umodel.setSchool(jobjUser.optString("school"));
					Umodel.setStatus(jobjUser.optString("status"));

					ArrayList<UserModel> Temp = new ArrayList<UserModel>();
					Temp.add(Umodel);

					model.setUserDetail(Temp);

					NewsFeedList.add(model);

				}

			} catch (JSONException e) {
				 DebugReportOnLocat.e(e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			pDialog.dismiss();

			newsfeedAdap = new NewsFeedAdapter(getActivity(), NewsFeedList);
			myListView.setAdapter(newsfeedAdap);
		}

	}
*/
	public static DiscoverFragment newInstance() {
		DiscoverFragment fragment = new DiscoverFragment();
		return fragment;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

}
