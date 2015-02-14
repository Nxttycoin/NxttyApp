package com.nxtapp.fragment;

import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nxtapp.adapter.DiscoverAdapter;
import com.nxtapp.classes.Discover;
import com.nxtapp.classes.FriendRequest;
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
import com.nxtty.main.LoadMoreListView;
import com.nxtty.main.LoadMoreListView.OnLoadMoreListener;
import com.nxtty.main.R;

@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
public class DiscoverFragment extends Fragment implements OnClickListener
{

    private String TAG = "DiscoverFragment";
    DiscoverAdapter discoverAdap;
    ListView myListView;
    LoadMoreListView LoadList;
    Discover addListitems;
    LinearLayout lnMain, lnUserName, lnSchool, lnCity, lnSec;
    String searchText;
    int Offset = 0;
    boolean isDataCompleted = false;
    String SearchCriteria = "alias";
    FrameLayout frUser;
    LinearLayout lnFriendReq, lnInvites;
    InputMethodManager mgr;
    TextView txtCancel;
    TextView txtSearch;
    EditText editMainSearch, editUser, editSchool, editCity;

    private ArrayList<FriendRequest> DiscoverList = new ArrayList<FriendRequest>();

    
    @Override
    public void onPause()
    {
	try
	{
	    KeyboardUtility.hideKeypad(getActivity());
	} catch (Exception e)
	{
	   DebugReportOnLocat.e(e);
	}
       
        super.onPause();
    }
    
    
    
    
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub
	View view = inflater.inflate(R.layout.discoverlist, null, false);

	LoadList = (LoadMoreListView) view.findViewById(R.id.lv_loadMore_search);
	
	mContext=getActivity().getApplicationContext();
	getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

	LoadList.setOnLoadMoreListener(new OnLoadMoreListener()
	{

	    @Override
	    public void onLoadMore()
	    {

		if (!isDataCompleted)
		{
		    Offset = Offset + 25;
		 //   new GetDiscoverDataAsync().execute();
		    GetDiscoverData();
		}
		else
		{
		    LoadList.onLoadMoreComplete();
		}

	    }
	});

	lnMain = (LinearLayout) view.findViewById(R.id.ln_Mainlayout);
	lnMain.setVisibility(View.VISIBLE);

	lnSec = (LinearLayout) view.findViewById(R.id.ln_Secondlayout);
	lnSec.setVisibility(View.GONE);

	lnFriendReq = (LinearLayout) view.findViewById(R.id.ln_bottom_friends_req);
	lnFriendReq.setOnClickListener(this);
	lnInvites = (LinearLayout) view.findViewById(R.id.ln_bottom_invites);
	lnInvites.setOnClickListener(this);

	editMainSearch = (EditText) view.findViewById(R.id.edittext_main_search);
	 // Request focus and show soft keyboard automatically
	editMainSearch.requestFocus();
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	
	editMainSearch.addTextChangedListener(new TextWatcher()
	{
	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count)
	    {
		Offset = 0;
	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after)
	    {
		
	    }

	    @Override
	    public void afterTextChanged(Editable s)
	    {
		
//		try
//		{
//		    
//
//
//			if (s.length() > 2)
//			{
//			   
//			    searchText = s.toString();
//			    DiscoverList.clear();
//			   // new GetDiscoverDataAsync().execute();
//			    
//			    //G Code 
//			    if(Network.isNetworkConnected(mContext)){
//				
//				 GetDiscoverData();
//				 
//			    }else{
//				
//				AlertUtility.showToast(getActivity().getApplicationContext(),  getString(R.string.networkIssues));
//			    }
//			   
//			}
//
//		    
//		} catch (NullPointerException e)
//		{
//		   DebugReportOnLocat.e(e);
//		   
//		}catch (Exception e) {
//		    
//		    DebugReportOnLocat.e(e);
//		}
	    }
	});

	editUser = (EditText) view.findViewById(R.id.edittext_username_search);
	editUser.setOnClickListener(this);
	editUser.setKeyListener(null);
	editUser.setFocusable(false);

	editCity = (EditText) view.findViewById(R.id.edittext_city_search);
	editCity.setOnClickListener(this);
	editCity.setKeyListener(null);
	editCity.setFocusable(false);

	editSchool = (EditText) view.findViewById(R.id.edittext_school_search);
	editSchool.setOnClickListener(this);
	editSchool.setKeyListener(null);
	editSchool.setFocusable(false);

	txtCancel = (TextView) view.findViewById(R.id.txt_cancel);
	txtCancel.setOnClickListener(this);

	txtSearch = (TextView) view.findViewById(R.id.txt_Search);
	txtSearch.setOnClickListener(this);
	
	if (getArguments() != null)
	{

	    String comingFrom = getArguments().getString("comingFrom");
	    String nameAlias = getArguments().getString("NameAlias");

	    if (comingFrom.equalsIgnoreCase("openChat"))
	    {
		Log.e(TAG, "comingFrom openChat=>" + nameAlias);

		SearchCriteria = "alias";
		lnMain.setVisibility(View.GONE);
		lnSec.setVisibility(View.VISIBLE);
		editMainSearch.setText(nameAlias);

	    }
	}
	
	return view;
    }
   
    ProgressDialog pDialog = null;
    
    public void GetDiscoverData(){

	
	 
	try
	{
	    pDialog = new ProgressDialog(getActivity());
	    pDialog.setMessage(getResources().getString(R.string.please_wait));
	    pDialog.show();

	    
	} catch (Exception e)
	{
	   DebugReportOnLocat.e(e);
	}
	   
	  

		AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.SUBSCRIBER_PROSPECTIVE_CONTACTS);
		req.setBody(JSONBody.discoverContacts(Constants.NxtAcId,searchText,Offset,SearchCriteria));
		req.setUserInfo(HttpUri.SUBSCRIBER_PROSPECTIVE_CONTACTS);
		//final CategoryManager cManager = new CategoryManager(this);
		new AsyncHttpClient().executeJSONObject(getActivity().getApplicationContext(),req,
				new ResultCallback() {

					@Override
					public void onCompleted(Exception e, String responseString, String methodInfo) {
					    DebugReportOnLocat.e(TAG, " responseString-> " + responseString);
					    GetDiscoverListContacts(responseString);
					    try
					    {
						if(pDialog!=null)
						  pDialog.dismiss();
						  
					    } catch (Exception err)
					    {
						DebugReportOnLocat.e(err);
						
					    }

					     DebugReportOnLocat.e(TAG, "  DiscoverList.size   " + DiscoverList.size());

					     if(DiscoverList.size() ==0){
						 
						 try
						{
						       
						     if(SearchCriteria.equals("alias")){
							 
							 AlertUtility.showToast(mContext, "Search name not matched with any user");
							 
						     }else if(SearchCriteria.equals("city")){
							 
							 AlertUtility.showToast(mContext, "City name not matched with any user");
							 
						     }
						     else if(SearchCriteria.equals("school")){
							 
							 AlertUtility.showToast(mContext, "school name not matched with any user");
						     }
						    
						} catch (Exception e2)
						{
						    e2.printStackTrace();
						}
						
					     }
					    if (Offset < 25)
					    {
						discoverAdap = new DiscoverAdapter(getActivity(), DiscoverList);
						LoadList.setAdapter(discoverAdap);
					    }
					    else
					    {
						discoverAdap.notifyDataSetChanged();
					    }

					    LoadList.onLoadMoreComplete();

					}

					private void GetDiscoverListContacts(String respStr)
					{

						if (respStr.length() < 10)
						{
						    isDataCompleted = true;
						}else{
						    isDataCompleted = false;
						}
						  // Parsing
						    try
						    {
							if (respStr != null)
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
							//	model.setUserStatus(jobj.optString("status"));
								model.setUserStatus(StringEscapeUtils.unescapeJava(jobj.optString("status")));
								model.setSchool(jobj.optString("school"));
								model.setGender(jobj.optString("gender"));

								DiscoverList.add(model);

							    }
							}

						    } catch (JSONException e)
						    {
							 DebugReportOnLocat.e(e);
						    }

	                                    finally {
	                                	
	                                	
	                                    }

					    
					}
				});  


    }
  /*  public class GetDiscoverDataAsync extends AsyncTask<Void, String, Void>
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

	    SharedPreferences pref = getActivity().getSharedPreferences("ID", 0);
	    Constants.NxtAcId = pref.getString("nxtAcId", "0");
	     DebugReportOnLocat.e(TAG, " FR Constants.NxtAcId  :  " + Constants.NxtAcId);

	    String respStr = null;

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_new + "subscriber/prospective_contacts");

	     DebugReportOnLocat.e(TAG, "  Constants.NxtAcId -->  " + Constants.NxtAcId);

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {

		 DebugReportOnLocat.e(TAG, "  searchText -->  " + searchText + "  " + SearchCriteria);

		nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		nameValuePairs.add(new BasicNameValuePair(SearchCriteria, searchText));
		nameValuePairs.add(new BasicNameValuePair("offset", "" + Offset));

		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		 DebugReportOnLocat.e(TAG, "  responce from post -->  " + respStr);

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
		if (respStr != null)
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
			model.setGender(jobj.optString("gender"));

			DiscoverList.add(model);

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

	        try
					    {
						if(pDialog!=null)
						  pDialog.dismiss();
						  
					    } catch (Exception err)
					    {
						DebugReportOnLocat.e(err);
						
					    };

	     DebugReportOnLocat.e(TAG, "  DiscoverList.size   " + DiscoverList.size());

	    if (Offset < 25)
	    {
		discoverAdap = new DiscoverAdapter(getActivity(), DiscoverList);
		LoadList.setAdapter(discoverAdap);
	    }
	    else
	    {
		discoverAdap.notifyDataSetChanged();
	    }

	    LoadList.onLoadMoreComplete();

	}

    }
*/
    public static DiscoverFragment newInstance()
    {
	DiscoverFragment fragment = new DiscoverFragment();
	return fragment;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
	super.onActivityCreated(savedInstanceState);

    }
    private void showKeyBoard(){
   	InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
   	inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
       }
       
    @Override
    public void onClick(View v)
    {

	switch (v.getId())
	{
	case R.id.edittext_username_search:
	    SearchCriteria = "alias";
	    lnMain.setVisibility(View.GONE);
	    lnSec.setVisibility(View.VISIBLE);
	    showKeyBoard();
	    break;
	case R.id.edittext_city_search:
	    SearchCriteria = "city";
	    lnMain.setVisibility(View.GONE);
	    lnSec.setVisibility(View.VISIBLE);
	    showKeyBoard();
	    break;
	case R.id.edittext_school_search:
	    SearchCriteria = "school";
	    lnMain.setVisibility(View.GONE);
	    lnSec.setVisibility(View.VISIBLE);
	    showKeyBoard();
	    break;
	case R.id.txt_cancel:
	    KeyboardUtility.hideKeypad(getActivity());
	    DiscoverList.clear();
	    editMainSearch.setText("");
	    
	    if (discoverAdap != null)
		discoverAdap.notifyDataSetChanged();
	    lnMain.setVisibility(View.VISIBLE);
	    lnSec.setVisibility(View.GONE);
	    break;
	case R.id.ln_bottom_friends_req:

	    startActivity(new Intent(getActivity(), FriendsReqActivity.class));

	    break;
	case R.id.ln_bottom_invites:

	    startActivity(new Intent(getActivity(), InvitesActivity.class));

	    break;
	    
	case R.id.txt_Search:
	    
	    Offset = 0;
	    
	    KeyboardUtility.hideKeypad(getActivity());
	    searchText = editMainSearch.getText().toString().trim();
	    if (searchText.length() > 0)
	    {
		DiscoverList.clear();
		if (Network.isNetworkConnected(getActivity().getApplicationContext()))
		{
		    GetDiscoverData();
		}
		else
		{
		    AlertUtility.showToast(getActivity().getApplicationContext(), getString(R.string.networkIssues));
		}
	    }

	    break;
	    

	default:
	    break;
	}

    }

}
