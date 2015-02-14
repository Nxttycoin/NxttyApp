package com.nxtapp.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nxtapp.adapter.ContactAdapter;
import com.nxtapp.classes.FriendRequest;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.Network;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpClient;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;
import com.nxtty.main.LoadMoreListView;
import com.nxtty.main.LoadMoreListView.OnLoadMoreListener;
import com.nxtty.main.R;

@SuppressLint("CutPasteId")
public class ContactsFragment extends Fragment
{

    ContactAdapter contactAdap;
    ListView myListView;
    private ArrayList<FriendRequest> ContactList = new ArrayList<FriendRequest>();
    // LinearLayout lnContacts, lnMessage, lnSettings;
    TextView txt_navTitle;
    LoadMoreListView LoadList;
    int Offset = 0;
    TextView txtAddFriends;
    boolean isDataCompleted = false;
    ImageView ivMessage, ivContact, ivSettings;
    
    
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
	
    

    @SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub
	View view = inflater.inflate(R.layout.contactlist, null, false);
	myListView = (ListView) view.findViewById(R.id.lv_contactsLoadMore);


	ContactList = new ArrayList<FriendRequest>();
	
	txt_navTitle = (TextView) getActivity().findViewById(R.id.textview_navigationTitle);
	txt_navTitle.setText("Contacts");

	txtAddFriends = (TextView) view.findViewById(R.id.txt_AddFriends);
	txtAddFriends.setVisibility(View.GONE);
	
	txtAddFriends.setOnClickListener(new OnClickListener()
	{
	    
	    @Override
	    public void onClick(View v)
	    {
		 changeFragment(new DiscoverFragment());
	    }
	});

	LoadList = (LoadMoreListView) view.findViewById(R.id.lv_contactsLoadMore);

	LoadList.setOnLoadMoreListener(new OnLoadMoreListener()
	{

	    @Override
	    public void onLoadMore()
	    {

		if (!isDataCompleted)
		{
		    Offset = Offset + 25;
		    
		    if (Network.isNetworkConnected(getActivity().getApplicationContext()))
		    {
			//new GetAsync().execute();
			    SubcriberContact();
		    }
		    else{
			
			AlertUtility.showToast(getActivity().getApplicationContext(),  getString(R.string.networkIssues));
		    }
		}
		else{
		    LoadList.onLoadMoreComplete();
		}

	    }
	});

	if (Network.isNetworkConnected(getActivity().getApplicationContext()))
	{
	  //  new GetAsync().execute();
	    SubcriberContact();
	}
	else{
	    AlertUtility.showToast(getActivity().getApplicationContext(),  getString(R.string.networkIssues));
	}

	// new GetAsync().execute();

	return view;
    }
    
    ProgressDialog pDialog = null;
    
public void SubcriberContact(){
    
    
 
    
    try
    {
	    pDialog = new ProgressDialog(getActivity());
	    pDialog.setMessage(getResources().getString(R.string.please_wait));
	    pDialog.show();
	    
    } catch (Exception e)
    {
	 DebugReportOnLocat.e(e);
    }


    try
    {
	

	AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.SUBSCRIBER_CONTACTS);
	req.setBody(JSONBody.contacts(Constants.NxtAcId,Offset));
	req.setUserInfo(HttpUri.SUBSCRIBER_CONTACTS);
	//final CategoryManager cManager = new CategoryManager(this);
	new AsyncHttpClient().executeJSONObject(getActivity().getApplicationContext(),req,
			new ResultCallback() {

				@Override
				public void onCompleted(Exception e, String responseString, String methodInfo) {
				    
				    GetContacts(responseString);
				    
				    if (Offset == 0 && responseString.length() < 10)
				    {
					txtAddFriends.setVisibility(View.VISIBLE);
				    }
				    else
				    {
					txtAddFriends.setVisibility(View.GONE);
				    }

				    if (Offset < 25)
				    {
					contactAdap = new ContactAdapter(getActivity(), ContactList);
					myListView.setAdapter(contactAdap);
				    }
				    else
				    {
					contactAdap.notifyDataSetChanged();
				    }

				    LoadList.onLoadMoreComplete();

				    
				}

				private void GetContacts(String respStr)
				{
				    
				    
				    
				    if(respStr!=null){
					
					
					if (respStr.length() < 10)
					{
					    isDataCompleted = true;
					}
				
				    try
				    {
					JSONArray jArray = new JSONArray(respStr);
					FriendRequest model;
					for (int i = 0; i < jArray.length(); i++)
					{

					    JSONObject jobj = jArray.getJSONObject(i);
					    model = new FriendRequest();
					    model.setUserId(jobj.optString("nxtAccountId"));
					    model.setUserName(jobj.optString("nameAlias"));
					    model.setDeletPlanID(jobj.optString("deletePlanId"));
					    model.setRegistrationDate(jobj.optString("registrationDate"));
					    
					    if (jobj.optString("avatar").length() > 5)
						{
						  model.setAvatarImage(Constants.baseUrl_Images + jobj.optString("avatar"));
						}else{
						    model.setAvatarImage(null);
						}
					  
					
					    model.setUserStatus(StringEscapeUtils.unescapeJava(jobj.optString("status")));
					    model.setSchool(jobj.optString("school"));

					    ContactList.add(model);

					}

				    } catch (JSONException e)
				    {
					 DebugReportOnLocat.e(e);
					 
				    }catch (IndexOutOfBoundsException e) {

					DebugReportOnLocat.e(e);
					
				    }
                                    finally {
                                	
                                	
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
				    
				    }

					
			});  

	
	
    } catch (Exception e)
    {
	DebugReportOnLocat.e(e);
    }
}
   /* public class GetAsync extends AsyncTask<Void, String, Void>
    {

	ProgressDialog pDialog;
	String respStr = null;

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

	     DebugReportOnLocat.e("#########", " FR Constants.NxtAcId  :  " + Constants.NxtAcId);

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_new + "subscriber/contacts");

	     DebugReportOnLocat.e("##############", "  Constants.NxtAcId -->  " + Constants.NxtAcId);

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {

		nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		nameValuePairs.add(new BasicNameValuePair("offset", "" + Offset));

		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		 DebugReportOnLocat.e("##############", "  responce from post -->  " + respStr);

		if (respStr.length() < 10)
		{
		    isDataCompleted = true;
		}

	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
	    }

	    // Parsing
	    try
	    {
		JSONArray jArray = new JSONArray(respStr);
		FriendRequest model;
		for (int i = 0; i < jArray.length(); i++)
		{

		    JSONObject jobj = jArray.getJSONObject(i);
		    model = new FriendRequest();
		    model.setUserId(jobj.optString("nxtAccountId"));
		    model.setUserName(jobj.optString("nameAlias"));
		    model.setDeletPlanID(jobj.optString("deletePlanId"));
		    model.setRegistrationDate(jobj.optString("registrationDate"));
		    model.setAvatarImage(Constants.baseUrl_Images + jobj.optString("avatar"));
		    model.setUserStatus(jobj.optString("status"));
		    model.setSchool(jobj.optString("school"));

		    ContactList.add(model);

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
		  
	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
		
	    };

	    if (Offset == 0 && respStr.length() < 10)
	    {
		txtAddFriends.setVisibility(View.VISIBLE);
	    }
	    else
	    {
		txtAddFriends.setVisibility(View.GONE);
	    }

	    if (Offset < 25)
	    {
		contactAdap = new ContactAdapter(getActivity(), ContactList);
		myListView.setAdapter(contactAdap);
	    }
	    else
	    {
		contactAdap.notifyDataSetChanged();
	    }

	    LoadList.onLoadMoreComplete();

	}

    }*/

    public static ContactsFragment newInstance()
    {
	ContactsFragment fragment = new ContactsFragment();
	return fragment;

    }
    private void changeFragment(Fragment targetFragment)
    {
	getActivity().sendBroadcast(new Intent("Title"));
	getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, targetFragment, "fragment").setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    // @Override
    // public void onClick(View v)
    // {
    //
    // switch (v.getId())
    // {
    // case R.id.ln_bottom_message_contacts:
    //
    // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
    // new HomeFragment(), "fragment")
    // .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    //
    // txt_navTitle.setText("Contacts");
    //
    // break;
    // case R.id.ln_bottom_setting_contacts:
    //
    // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
    // new SettingFragment(), "fragment")
    // .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    //
    // txt_navTitle.setText("Conversation");
    //
    // break;
    //
    // default:
    // break;
    // }
    //
    // }

}
