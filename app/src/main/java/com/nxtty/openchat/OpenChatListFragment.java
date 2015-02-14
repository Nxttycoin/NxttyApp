package com.nxtty.openchat;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nextapp.data.ChatInfoManager;
import com.nxtapp.adapter.PublicChatAdapter;
import com.nxtapp.classes.GroupModel;
import com.nxtapp.fragment.GroupSearchActivity;
import com.nxtapp.fragment.GroupSettingsActivity;
import com.nxtapp.fragment.PublicChatFragment;
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

public class OpenChatListFragment extends Fragment
{

    PublicChatAdapter publicChatAdapter;
    ListView myListView;
    // TextView txt_mainTextPublic;
    BroadcastReceiver broadcastReceiver;
    // LinearLayout lnAddChat, lnSettings;
    LinearLayout lnSearch, lnSettings;
    String Key;

    public static boolean isFlag = true;
    public static ArrayList<GroupModel> GroupList = new ArrayList<GroupModel>();
    // LinearLayout lnSubScribeLayout, lnSunscribe;
    boolean IsOwner = true;
    public static boolean IsSubscribeVisible = true;
//    boolean isVisible;

    public static Fragment newInstance(Context context)
    {
	OpenChatListFragment f = new OpenChatListFragment();
	return f;
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser)
//    {
//
//	isVisible = isVisibleToUser;
//	super.setUserVisibleHint(isVisibleToUser);
//    }

    public  SharedPreferences sharedPreferences;
    
    ChatInfoManager chatInfoManager;
    
    private static final Field sChildFragmentManagerField;
	private static final String LOGTAG = "GCheck";

	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} catch (NoSuchFieldException e) {
			Log.e(LOGTAG, "Error getting mChildFragmentManager field", e);
		}
		sChildFragmentManagerField = f;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} catch (Exception e) {
				Log.e(LOGTAG, "Error setting mChildFragmentManager field", e);
			}
		}
	}
	
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

	View view = inflater.inflate(R.layout.public_chat, container, false);

	
	sharedPreferences = getActivity().getSharedPreferences("PublicChatSubscribeDetails", Context.MODE_PRIVATE);
	

	myListView = (ListView) view.findViewById(android.R.id.list);
	myListView.setVisibility(View.VISIBLE);
	
	
	// new GetAsyncPublicChat("user_groups").execute();
	// Bala Code

//	if (isVisible)
//	{
	
	try
	{
	    
	    chatInfoManager = new ChatInfoManager(getActivity().getApplicationContext());
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	

	    if (Network.isNetworkConnected(getActivity().getApplicationContext()))
	    {
		try
		{
		    

			
			boolean b=sharedPreferences.getBoolean("isSubscribed", false);
			//("isSubscribed", true);
			
			if(b){
			    
			    IsSubscribeVisible = false;
				
				myListView.setVisibility(View.VISIBLE);
				PublicChat("user_groups");
				
			}else{
			    
			    new SunScribeAsync().execute();
			}
			  
			
		    
			
		    //
		    //new SunScribeAsync().execute();

		} catch (Exception e)
		{
		    DebugReportOnLocat.e(e);
		}

		try
		{
		    new DoInBackRegKey().execute();

		} catch (Exception e)
		{
		    DebugReportOnLocat.e(e);
		}

	    }
	    else
	    {
		AlertUtility.showToast(getActivity().getApplicationContext(), getString(R.string.networkIssues));
	    }
//	}

	lnSettings = (LinearLayout) view.findViewById(R.id.ln_bottom_Settings);
	lnSettings.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		startActivity(new Intent(getActivity(), GroupSettingsActivity.class));
	    }
	});

	lnSearch = (LinearLayout) view.findViewById(R.id.ln_bottom_Search);
	lnSearch.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		startActivity(new Intent(getActivity(), GroupSearchActivity.class).putParcelableArrayListExtra("GroupNames", GroupList));
	    }
	});

	//myListView = (ListView) view.findViewById(android.R.id.list);
	//myListView.setVisibility(View.VISIBLE);

	IntentFilter intentFilter = new IntentFilter();
	intentFilter.addAction("CLOSE_ALL");
	broadcastReceiver = new BroadcastReceiver()
	{

	    @Override
	    public void onReceive(Context context, Intent intent)
	    {
		// new GetAsyncPublicChat("user_groups").execute();

		if (Network.isNetworkConnected(getActivity().getApplicationContext()))
		{
		    PublicChat("user_groups");
		}
		else
		{
		    AlertUtility.showToast(getActivity().getApplicationContext(), getString(R.string.networkIssues));
		}

	    }
	};

	getActivity().registerReceiver(broadcastReceiver, intentFilter);

	return view;
    }

    @Override
    public void onDestroy()
    {
	super.onDestroy();

	getActivity().unregisterReceiver(broadcastReceiver);
    }

    ProgressDialog pDialog = null;

    public void PublicChat(String KeyStr)
    {

	try
	{

	    Key = KeyStr;

	    try
	    {/*

		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage(getResources().getString(R.string.please_wait));
		pDialog.show();

	    */} catch (NullPointerException e)
	    {
		DebugReportOnLocat.e(e);
	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }

	    AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.GROUPS_CHAT_KEY);
	    req.setBody(JSONBody.publicChat(Constants.NxtAcId));
	    req.setUserInfo(HttpUri.GROUPS_CHAT_KEY);
	    DebugReportOnLocat.ln(" HttpUri.GROUPS_CHAT_KEY >> " + HttpUri.GROUPS_CHAT_KEY);
	    // final CategoryManager cManager = new CategoryManager(this);
	    new AsyncHttpClient().executeJSONObject(getActivity().getApplicationContext(), req, new ResultCallback()
	    {

		@Override
		public void onCompleted(Exception e, String responseString, String methodInfo)
		{

		    GetContacts(responseString);

		    publicChatAdapter = new PublicChatAdapter(getActivity(), GroupList);
		    myListView.setAdapter(publicChatAdapter);

		    try
		    {/*
			if (pDialog != null)
			    pDialog.dismiss();

		    */} catch (Exception e2)
		    {
			DebugReportOnLocat.e(e2);
		    }

		}

		private void GetContacts(String respStr)
		{// Parsing
		    try
		    {
			// G code
			if (respStr != null)
			{

			    DebugReportOnLocat.println(" nxtty >>>>> " + respStr);

			    GroupList.clear();
			    JSONArray jArray = new JSONArray(respStr);
			    GroupModel model;
			    for (int i = 0; i < jArray.length(); i++)
			    {

				JSONObject jobj = jArray.getJSONObject(i);
				model = new GroupModel();
				model.setId(jobj.optString("id"));
				model.setTitle(jobj.optString("title"));
				model.setDescription(jobj.optString("description"));
				model.setCategory(jobj.optString("category"));
				model.setAvatar(jobj.optString("avatar"));
				model.setCreated(jobj.optString("created"));
				model.setModified(jobj.optString("modified"));
				model.setPosts(jobj.optString("posts"));
				model.setMembers(jobj.optString("members"));

				if (Key.equalsIgnoreCase("user_groups"))
				{
				    model.setOwner(true);
				}
				else
				{
				    model.setOwner(false);
				}

				
				try
				{
				    
				/*    
				    if(chatInfoManager.isExists(jobj.optString("id"))){
					    
					model=chatInfoManager.selectPublicChatTopic(jobj.optString("id"));
					
					    
					}*/
					 
				    
				} catch (Exception e)
				{
				    e.printStackTrace();
				}
				
				GroupList.add(model);

			    }

			}
		    } catch (JSONException e)
		    {
			DebugReportOnLocat.e(e);
		    } finally
		    {

			//

			/*try
			{
			    if (isFlag == true)
			    {
				if (GroupList != null)
				    if (GroupList.size() == 0)
				    {
					isFlag = false;
					startActivity(new Intent(getActivity(), AddGroup.class));
					
				    }
			    }

			} catch (Exception e2)
			{
			    // TODO: handle exception
			}*/

		    }
		}
	    });

	} catch (NullPointerException e)
	{
	    DebugReportOnLocat.e(e);
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
    }

    public class SunScribeAsync extends AsyncTask<Void, String, Void>
    {

	String respStr = null;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	}

	@Override
	protected Void doInBackground(Void... params)
	{

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "subscriber/issubscribed");

	    DebugReportOnLocat.e("##############", "  Constants.NxtAcId -->  " + Constants.NxtAcId);

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {

		nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		DebugReportOnLocat.e("##############", "  responce from post subscribe -->  " + respStr);

	    } catch (RuntimeException e)
	    {
		DebugReportOnLocat.e(e);
	    } catch (ClientProtocolException e)
	    {
		DebugReportOnLocat.e(e);
	    } catch (IOException e)
	    {
		DebugReportOnLocat.e(e);
	    }

	    return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
	    super.onPostExecute(result);

	    if (respStr != null)
	    {
		try
		{
		    JSONObject jobj = new JSONObject(respStr);
		    if (jobj.optString("status").equalsIgnoreCase("true"))
		    {

			
			  SharedPreferences.Editor edt_notifications = sharedPreferences.edit();
			    edt_notifications.putString("nxtAcId", Constants.NxtAcId);
			    edt_notifications.putBoolean("isSubscribed", true);
			    edt_notifications.commit();
			    
			    
			myListView.setVisibility(View.VISIBLE);

			IsSubscribeVisible = false;

			PublicChat("user_groups");
		    }
		    else
		    {
			new SunScribeDetailAsync().execute();

			IsSubscribeVisible = true;
		    }
		} catch (JSONException e)
		{
		    DebugReportOnLocat.e(e);
		}
	    }

	}

    }

    public class SunScribeDetailAsync extends AsyncTask<Void, String, Void>
    {

	ProgressDialog pDialog;
	String respStr = null;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {
		if (getActivity() != null)
		{

		    pDialog = new ProgressDialog(getActivity());
		    pDialog.setMessage(getResources().getString(R.string.please_wait));
		    pDialog.show();

		}

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }

	}

	@Override
	protected Void doInBackground(Void... params)
	{

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "subscriber/subscribe");

	    DebugReportOnLocat.e("##############", "  Constants.NxtAcId -->  " + Constants.NxtAcId);

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {
		nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		nameValuePairs.add(new BasicNameValuePair("name_alias", Constants.AliasName));
		nameValuePairs.add(new BasicNameValuePair("appleDeviceID", Constants.RegistationKeyGCM));// device
													 // id
													 // blank
													 // update
		nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		DebugReportOnLocat.e("##############", "  responce from post subscribe -->  " + respStr);

	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
	    }

	    return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
	    super.onPostExecute(result);

	    try
	    {
		if (pDialog != null)
		    pDialog.dismiss();

	    } catch (Exception err)
	    {
		DebugReportOnLocat.e(err);

	    }
	    ;
	    if (respStr != null)
	    {
		try
		{
		    JSONObject jobj = new JSONObject(respStr);
		    if (jobj.optString("status").equalsIgnoreCase("true"))
		    {
			
			

			   SharedPreferences.Editor edt_notifications = sharedPreferences.edit();
			    edt_notifications.putString("nxtAcId", Constants.NxtAcId);
			    edt_notifications.putBoolean("isSubscribed", true);
			    edt_notifications.commit();
			    
			IsSubscribeVisible = false;
			// lnSubScribeLayout.setVisibility(View.GONE);
			myListView.setVisibility(View.VISIBLE);
			// lvViewGroup.setVisibility(View.VISIBLE);
			// new GetAsyncPublicChat("user_groups").execute();
			PublicChat("user_groups");
		    }
		} catch (JSONException e)
		{
		    DebugReportOnLocat.e(e);
		}
	    }

	}

    }

    public class DoInBackRegKey extends AsyncTask<Void, String, Void>
    {
	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params)
	{
	    try
	    {
		SharedPreferences pref = getActivity().getSharedPreferences("ID", 0);
		Constants.NxtAcId = pref.getString("nxtAcId", "0");

		DebugReportOnLocat.e("#########", " NxtAcId  :  " + Constants.NxtAcId);
		DebugReportOnLocat.e("#########", " RegistationKeyGCM  :  " + Constants.RegistationKeyGCM);
		String respStr = null;

		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
		HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "subscriber/update_subscriber_settings");

		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("nxtID", new StringBody(Constants.NxtAcId));
		// reqEntity.addPart("file", new FileBody(new File("")));
		reqEntity.addPart("deviceID", new StringBody(Constants.RegistationKeyGCM));
		reqEntity.addPart("deviceType", new StringBody("Android"));
		reqEntity.addPart("key", new StringBody(Constants.ParamKey));

		httppost.setEntity(reqEntity);
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		DebugReportOnLocat.e("##############", "  responce from Save CHanges  -->  " + respStr);

	    } catch (NullPointerException e)
	    {
		DebugReportOnLocat.e(e);
	    } catch (RuntimeException e)
	    {
		DebugReportOnLocat.e(e);
	    } catch (ClientProtocolException e)
	    {
		DebugReportOnLocat.e(e);
	    } catch (IOException e)
	    {
		DebugReportOnLocat.e(e);
	    }

	    return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
	    super.onPostExecute(result);
	}
    }

    public static PublicChatFragment newInstance()
    {
	PublicChatFragment fragment = new PublicChatFragment();
	return fragment;

    }

    // @Override
    // public void OnBackPressed()
    // {
    // // new GetAsyncPublicChat("user_groups").execute();
    // PublicChat("user_groups");
    // }

}
