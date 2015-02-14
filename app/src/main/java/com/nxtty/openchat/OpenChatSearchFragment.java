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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nxtapp.adapter.GroupDetailAdapter;
import com.nxtapp.adapter.GroupDetailAdapter.OnEditPost;
import com.nxtapp.classes.GroupDetailModel;
import com.nxtapp.classes.GroupModel;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.Network;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpClient;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;
import com.nxtty.main.GroupDetailCreatePost;
import com.nxtty.main.LoadMoreListView;
import com.nxtty.main.LoadMoreListView.OnLoadMoreListener;
import com.nxtty.main.R;

public class OpenChatSearchFragment extends Fragment implements OnEditPost
{

    Spinner SpinGroup;
    LoadMoreListView lvSearchFeeds;
    EditText editSearch;
    TextView txtSearch;
    RelativeLayout root;
    GroupDetailAdapter groupDetailAdapterSearch;
    public static ArrayList<GroupDetailModel> GROUPDETAILS_LIST = new ArrayList<GroupDetailModel>();
    private ArrayList<GroupModel> GroupList = new ArrayList<GroupModel>();
    List<String> list_name = new ArrayList<String>();
    List<String> list_ID = new ArrayList<String>();
    int Offset = 0;
    String query, groupID = "-99";
    public static boolean isDataCompleted = false;

    public static Fragment newInstance(Context context)
    {
	OpenChatSearchFragment f = new OpenChatSearchFragment();
	return f;
    }

    FragmentActivity listener;

    /*
     * @Override public void onAttach(Activity activity) {
     * super.onAttach(activity); this.listener = (FragmentActivity) activity; }
     */

    /*
     * private static final Field sChildFragmentManagerField; private static
     * final String LOGTAG = "GCheck";
     * 
     * static { Field f = null; try { f =
     * Fragment.class.getDeclaredField("mChildFragmentManager");
     * f.setAccessible(true); } catch (NoSuchFieldException e) { Log.e(LOGTAG,
     * "Error getting mChildFragmentManager field", e); }
     * sChildFragmentManagerField = f; }
     * 
     * @Override public void onDetach() { super.onDetach();
     * 
     * if (sChildFragmentManagerField != null) { try {
     * sChildFragmentManagerField.set(this, null); } catch (Exception e) {
     * Log.e(LOGTAG, "Error setting mChildFragmentManager field", e); } } }
     */

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
	View view = inflater.inflate(R.layout.group_search, container, false);

	getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

	GroupList.addAll(OpenChatListFragment.GroupList);

	((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);

	for (int i = 0; i < GroupList.size(); i++)
	{
	    list_name.add(GroupList.get(i).getTitle());
	    list_ID.add(GroupList.get(i).getId());
	}

	root = (RelativeLayout) view.findViewById(R.id.root);

	txtSearch = (TextView) view.findViewById(R.id.txt_Search_group);
	lvSearchFeeds = (LoadMoreListView) view.findViewById(R.id.lv_loadMore_GroupDetail_search);

	// editSearch.setText("");
	SpinGroup = (Spinner) view.findViewById(R.id.spin_group);

	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list_name);
	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	editSearch = (EditText) view.findViewById(R.id.txtMainSearch);

	new Runnable()
	{
	    public void run()
	    {
		InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(editSearch, 0);
		editSearch.requestFocus();
	    }
	};

	/*
	 * editSearch.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * System.out.println("setOnClickListener ");
	 * getActivity().getWindow().setSoftInputMode
	 * (WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	 * 
	 * // ((InputMethodManager)getActivity().getSystemService(Context.
	 * INPUT_METHOD_SERVICE
	 * )).toggleSoftInput(InputMethodManager.SHOW_FORCED,
	 * InputMethodManager.SHOW_IMPLICIT);
	 * 
	 * InputMethodManager inputMethodManager =
	 * (InputMethodManager)getActivity
	 * ().getApplicationContext().getSystemService
	 * (Context.INPUT_METHOD_SERVICE);
	 * inputMethodManager.showSoftInput(editSearch, 0);
	 * editSearch.requestFocus();
	 * 
	 * } });
	 */
	editSearch.setOnTouchListener(new OnTouchListener()
	{

	    @Override
	    public boolean onTouch(View v, MotionEvent event)
	    {
		v.onTouchEvent(event);

		DebugReportOnLocat.println("setOnTouchListener ");

		int action = event.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
		    DebugReportOnLocat.ln("  >>> ACTION_DOWN ");
		    break;
		case MotionEvent.ACTION_UP:
		    DebugReportOnLocat.ln("  >>> ACTION_UP ");

		  
		    break;
		case MotionEvent.ACTION_MASK:
		    DebugReportOnLocat.ln("  >>> ACTION_MASK ");

		    break;
		case MotionEvent.ACTION_POINTER_DOWN:
		    DebugReportOnLocat.ln("  >>> ACTION_POINTER_DOWN ");

		    break;
		    
		
	

		default:
		    DebugReportOnLocat.ln("  >>> default "+action);
		    break;
		}

		  getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
         	    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		    inputMethodManager.showSoftInput(editSearch, 0);
		    editSearch.requestFocus();

		    
		return false;
	    }

	});

	SpinGroup.setAdapter(dataAdapter);
	SpinGroup.setOnItemSelectedListener(new OnItemSelectedListener()
	{
	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	    {
		groupID = list_ID.get(position);
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> parent)
	    {

	    }
	});

	txtSearch.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		try
		{
		    DebugReportOnLocat.ln(" Root touch ");
		    hideSoftKeyboard(getActivity());

		} catch (Exception e)
		{
		    e.printStackTrace();
		}

		if (editSearch.getText().toString().trim().length() > 2 && !groupID.equalsIgnoreCase("-99"))
		{
		    Offset = 0;
		    query = editSearch.getText().toString().trim();

		    try
		    {
			if (GROUPDETAILS_LIST != null)
			{

			    GROUPDETAILS_LIST.clear();

			    if (groupDetailAdapterSearch != null)
			    {
				groupDetailAdapterSearch.notifyDataSetChanged();
			    }

			}

		    } catch (Exception e)
		    {
			e.printStackTrace();
		    }

		    if (Network.isNetworkConnected(getActivity()))
		    {

			new GetSearcData().execute();

		    }
		    else
		    {

			Toast.makeText(getActivity(), getResources().getString(R.string.networkIssues), Toast.LENGTH_SHORT).show();

		    }

		}
		else
		{
		    Toast.makeText(getActivity(), getResources().getString(R.string.search_instuction_edit_text), Toast.LENGTH_SHORT).show();
		}
	    }
	});

	lvSearchFeeds.setOnLoadMoreListener(new OnLoadMoreListener()
	{

	    @Override
	    public void onLoadMore()
	    {

		try
		{
		    DebugReportOnLocat.ln(" Root touch ");
		    hideSoftKeyboard(getActivity());

		} catch (Exception e)
		{
		    e.printStackTrace();
		}

		if (!isDataCompleted)
		{
		    Offset = Offset + 25;
		    new GetSearcData().execute();

		}
		else
		{
		    lvSearchFeeds.onLoadMoreComplete();
		}

	    }
	});

	// groupDetailAdapter = new GroupDetailAdapter(getActivity(),
	// GROUPDETAILS_LIST, "1");
	// lvSearchFeeds.setAdapter(groupDetailAdapter);

	return view;
    }

    public class GetSearcData extends AsyncTask<Void, String, Void>
    {
	// ProgressDialog pDialog;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    /*
	     * pDialog = new ProgressDialog(getActivity());
	     * pDialog.setMessage(getResources
	     * ().getString(R.string.please_wait)); pDialog.show();
	     */

	}

	String respStr = null;

	@Override
	protected Void doInBackground(Void... params)
	{

	    DebugReportOnLocat.e("#########", " FR Constants.NxtAcId  :  " + Constants.NxtAcId);

	    if (Network.isNetworkConnected(getActivity()))
	    {

		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
		HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/fetch_posts");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try
		{
		    nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		    nameValuePairs.add(new BasicNameValuePair("groupID", groupID));
		    nameValuePairs.add(new BasicNameValuePair("type", "2"));
		    nameValuePairs.add(new BasicNameValuePair("query", query));
		    nameValuePairs.add(new BasicNameValuePair("offset", "" + Offset));
		    nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    HttpResponse response = httpclient.execute(httppost);
		    respStr = EntityUtils.toString(response.getEntity());

		    DebugReportOnLocat.e("##############", "  responce from post -->  " + respStr);

		} catch (ClientProtocolException e)
		{
		} catch (IOException e)
		{
		}

	    }

	    return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{

	    super.onPostExecute(result);

	    DebugReportOnLocat.ln(" String called ");

	    /*
	     * try { if (pDialog != null) pDialog.dismiss();
	     * 
	     * } catch (Exception err) { DebugReportOnLocat.e(err); }
	     */

	    if (respStr != null)
	    {

		if (respStr.length() < 10)
		{
		    isDataCompleted = true;
		}

		try
		{
		    Log.e("GroupDetailActivity", "respStr for comment=>" + respStr);
		    JSONArray jArray = new JSONArray(respStr);
		    GroupDetailModel model;
		    for (int i = 0; i < jArray.length(); i++)
		    {

			JSONObject jobj = jArray.getJSONObject(i);
			model = new GroupDetailModel();
			model.setId(jobj.optString("id"));
			model.setTitle(jobj.optString("title"));
			model.setBody(jobj.optString("body"));
			model.setUrl(jobj.optString("url"));
			model.setSource(jobj.optString("source"));
			model.setTipCount(jobj.optString("tipCount"));
			if (jobj.optString("image").length() > 5)
			{
			    model.setImage(Constants.baseUrl_ImagesGroup + jobj.optString("image"));
			}
			else
			{
			    model.setImage("null");
			}
			model.setSpamCount(jobj.optString("spamCount"));
			model.setDeleted(jobj.optString("deleted"));
			model.setCreated(jobj.optString("created"));
			model.setModified(jobj.optString("modified"));

			model.setCommentCount(jobj.optString("commentCount"));

			JSONObject jobj1 = jobj.getJSONObject("owner");

			model.setNxtAccountId(jobj1.optString("nxtAccountId"));
			model.setNameAlias(jobj1.optString("nameAlias"));
			model.setAvatar(Constants.baseUrl_ImagesGroup + jobj1.optString("avatar"));
			model.setRole(jobj1.optString("role"));
			model.setBlocked(jobj1.optString("blocked"));
			model.setSpamCount_owner(jobj1.optString("spamCount"));
			model.setCreated_owner(jobj1.optString("created"));
			model.setBlockedDate(jobj1.optString("blockedDate"));

			if (jobj1.optString("nxtAccountId").equalsIgnoreCase(Constants.NxtAcId))
			{
			    model.setOwner(true);
			}
			else
			{
			    model.setOwner(false);
			}

			if (jobj.optString("image").length() > 5)
			{

			    model.setImagePath(Constants.baseUrl_ImagesGroup + jobj.optString("image"));

			}
			else
			{
			    model.setImagePath("null");
			}

			GROUPDETAILS_LIST.add(model);
			try
			{

			    if (Offset < 25)
			    {

				if (GROUPDETAILS_LIST.size() == 0)
				{

				    AlertUtility.showToast(getActivity(), "There is no record found!");
				}
				groupDetailAdapterSearch = new GroupDetailAdapter(getActivity(), GROUPDETAILS_LIST, groupID, "Search");
				lvSearchFeeds.setAdapter(groupDetailAdapterSearch);
			    }
			    else
			    {
				groupDetailAdapterSearch.notifyDataSetChanged();
			    }

			    lvSearchFeeds.onLoadMoreComplete();

			} catch (Exception e)
			{
			    DebugReportOnLocat.e(e);
			}

		    }

		} catch (JSONException e)
		{
		    DebugReportOnLocat.e(e);
		} finally
		{
		    getActivity().runOnUiThread(new Runnable()
		    {
			public void run()
			{
			}
		    });
		}

	    }
	}

    }

    /*
     * private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
     * public void onReceive(Context context, Intent intent) {
     * 
     * boolean noConnectivity =
     * intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
     * String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
     * boolean isFailover =
     * intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
     * 
     * NetworkInfo currentNetworkInfo = (NetworkInfo)
     * intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
     * NetworkInfo otherNetworkInfo = (NetworkInfo)
     * intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
     * 
     * if (currentNetworkInfo.isConnected()) {
     * 
     * getGrouplist();
     * 
     * 
     * Toast.makeText(getApplicationContext(), "Network Connected",
     * Toast.LENGTH_LONG).show();
     * 
     * } else { Toast.makeText(getActivity(), "Network Not Connected",
     * Toast.LENGTH_LONG).show(); } } };
     */

    void getGrouplist()
    {

	if (GroupList != null)
	{

	    if (GroupList.size() > 0)
	    {

		return;
	    }
	}

	try
	{

	    AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.GROUPS_CHAT_KEY);
	    req.setBody(JSONBody.publicChat(Constants.NxtAcId));
	    req.setUserInfo(HttpUri.GROUPS_CHAT_KEY);
	    DebugReportOnLocat.ln(" HttpUri.GROUPS_CHAT_KEY >> " + HttpUri.GROUPS_CHAT_KEY);
	    // final CategoryManager cManager = new CategoryManager(this);
	    new AsyncHttpClient().executeJSONObject(getActivity(), req, new ResultCallback()
	    {

		@Override
		public void onCompleted(Exception e, String responseString, String methodInfo)
		{

		    GetContacts(responseString);

		    try
		    {

			for (int i = 0; i < GroupList.size(); i++)
			{
			    list_name.add(GroupList.get(i).getTitle());
			    list_ID.add(GroupList.get(i).getId());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list_name);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			SpinGroup.setAdapter(dataAdapter);

		    } catch (Exception e2)
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

				/*
				 * if (Key.equalsIgnoreCase("user_groups")) {
				 */
				model.setOwner(true);
				/*
				 * } else { model.setOwner(false); }
				 */

				GroupList.add(model);

			    }

			}
		    } catch (JSONException e)
		    {
			DebugReportOnLocat.e(e);
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

    private static int EDIT_POST = 0x1000;

    String Title = "", Body = "", PostID = "", attachedImage = "";
    boolean isEdit = false;
    int EditPos = 0;

    @Override
    public void OnEditClick(int pos, String Title, String body, String PostID, String attachedImage)
    {
	DebugReportOnLocat.e("#############", "   position :  " + pos);

	EditPos = pos;
	isEdit = true;
	this.Title = Title;
	this.Body = body;
	this.PostID = PostID;
	this.attachedImage = attachedImage;
	// openDialogCreatePost();

	Intent intent = new Intent(getActivity(), GroupDetailCreatePost.class);
	intent.putExtra("IsEdit", isEdit);
	intent.putExtra("gID", groupID);
	intent.putExtra("Title", Title);
	intent.putExtra("Body", Body);
	intent.putExtra("AttachedImage", attachedImage);
	intent.putExtra("PostID", PostID);
	startActivityForResult(intent, EDIT_POST);

    }

    void refresh()
    {

	if (editSearch.getText().toString().trim().length() > 0 && !groupID.equalsIgnoreCase("-99"))
	{
	    Offset = 0;
	    query = editSearch.getText().toString().trim();

	    try
	    {
		if (GROUPDETAILS_LIST != null)
		{

		    GROUPDETAILS_LIST.clear();

		    if (groupDetailAdapterSearch != null)
		    {
			groupDetailAdapterSearch.notifyDataSetChanged();
		    }

		}

	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }

	    if (Network.isNetworkConnected(getActivity()))
	    {

		new GetSearcData().execute();

	    }
	    else
	    {

		Toast.makeText(getActivity(), getResources().getString(R.string.networkIssues), Toast.LENGTH_SHORT).show();

	    }

	}
	else
	{
	    Toast.makeText(getActivity(), getResources().getString(R.string.search_instuction_edit_text), Toast.LENGTH_SHORT).show();
	}

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

	super.onActivityResult(requestCode, resultCode, data);

	if (requestCode == EDIT_POST)
	{

	    if (resultCode == Activity.RESULT_OK)
	    {

		refresh();

	    }
	}
    }

    public static void hideSoftKeyboard(Activity activity)
    {

	try
	{
	    if (activity == null)
		return;

	    /*
	     * InputMethodManager inputMethodManager = (InputMethodManager)
	     * activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	     * inputMethodManager
	     * .hideSoftInputFromWindow(activity.getCurrentFocus
	     * ().getWindowToken(), 0);
	     */

	    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

	} catch (NullPointerException e)
	{
	    DebugReportOnLocat.e(e);
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
    }

    /*
     * @Override public void onClick(View v) { // TODO Auto-generated method
     * stub
     * 
     * }
     */

}
