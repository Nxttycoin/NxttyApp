package com.nxtty.openchat;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nxtapp.adapter.AddGroupAdapter;
import com.nxtapp.adapter.PublicChatAdapter;
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
import com.nxtty.main.R;

public class OpenChatAddFragment extends Fragment
{

    ListView lvGroups;
    private ArrayList<GroupModel> GroupList = new ArrayList<GroupModel>();
    AddGroupAdapter addGroupAdapter;
    ProgressDialog mProgressDialog;

    public static Fragment newInstance(Context context)
    {
	OpenChatAddFragment f = new OpenChatAddFragment();
	return f;
    }
    
    
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
	View view = inflater.inflate(R.layout.add_group, container, false);
	lvGroups = (ListView) view.findViewById(R.id.lv_AddGroup);
	// backCLick = (CallBackCLick)AddGroup.this ;

	// TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);

	// try
	// {
	// txtTitle.setText(getResources().getString(R.string.open_chat) + " " +
	// getResources().getString(R.string.subscribe));
	//
	// } catch (Exception e)
	// {
	// // TODO: handle exception
	// }

	// RelativeLayout lnBack = (RelativeLayout)
	// findViewById(R.id.ln_titleBackAdd);
	// lnBack.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	//
	// PublicChatFragment.isFlag = false;
	// // backCLick.OnBackPressed();
	// Intent intentCloseHome = new Intent("CLOSE_ALL");
	// sendBroadcast(intentCloseHome);
	// finish();
	// }
	// });
	// new GetAsyncPublicChat().execute();

	if (Network.isNetworkConnected(getActivity()))
	{

	    GetPublicChatData();
	}
	else
	{

	    AlertUtility.showToast(getActivity(), getString(R.string.networkIssues));
	}

	return view;
    }

    void GetPublicChatData()
    {

	//showProgress("Please Wait...");

	AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.ADD_GROUP);
	req.setBody(JSONBody.publicChat(Constants.NxtAcId));
	req.setUserInfo(HttpUri.ADD_GROUP);
	DebugReportOnLocat.ln("HttpUri.ADD_GROUP >>>" + HttpUri.ADD_GROUP);
	// final CategoryManager cManager = new CategoryManager(this);
	new AsyncHttpClient().executeJSONObject(getActivity(), req, new ResultCallback()
	{

	    @Override
	    public void onCompleted(Exception e, String responseString, String methodInfo)
	    {

		PublicChat(responseString);

		addGroupAdapter = new AddGroupAdapter(getActivity(), GroupList);
		lvGroups.setAdapter(addGroupAdapter);

	    }

	    private void PublicChat(String respStr)
	    {
		try
		{
		    if (respStr != null)
		    {

			DebugReportOnLocat.ln(respStr);

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

			    model.setOwner(false);

			    GroupList.add(model);

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

			  //  dismissProgress();

			}
		    });
		}

	    }
	});
    }

    public interface CallBackCLick
    {
	public void OnBackPressed();
    }

    protected void showProgress(String msg)
    {
	
	try
	{

		if (mProgressDialog != null && mProgressDialog.isShowing())
		    dismissProgress();

		mProgressDialog = ProgressDialog.show(getActivity(), "", msg);
	    
		
	} catch (Exception e)
	{
	   e.printStackTrace();
	}
    }

    protected void dismissProgress()
    {
	try
	{
	    if (mProgressDialog != null)
		{
		    mProgressDialog.dismiss();
		    mProgressDialog = null;
		}
	    
	} catch (Exception e)
	{
	   e.printStackTrace();
	}
	
    }

}
