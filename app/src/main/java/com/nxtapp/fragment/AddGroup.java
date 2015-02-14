package com.nxtapp.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

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

public class AddGroup extends Activity
{
    ListView lvGroups;
    private ArrayList<GroupModel> GroupList = new ArrayList<GroupModel>();
    PublicChatAdapter publicChatAdapter;
    ProgressDialog mProgressDialog;

    // CallBackCLick backCLick;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.add_group);
	lvGroups = (ListView) findViewById(R.id.lv_AddGroup);
	// backCLick = (CallBackCLick)AddGroup.this ;

//	TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
//
//	try
//	{
//	    txtTitle.setText(getResources().getString(R.string.open_chat) + " " + getResources().getString(R.string.subscribe));
//
//	} catch (Exception e)
//	{
//	    // TODO: handle exception
//	}

//	RelativeLayout lnBack = (RelativeLayout) findViewById(R.id.ln_titleBackAdd);
//	lnBack.setOnClickListener(new OnClickListener()
//	{
//	    @Override
//	    public void onClick(View v)
//	    {
//
//		PublicChatFragment.isFlag = false;
//		// backCLick.OnBackPressed();
//		Intent intentCloseHome = new Intent("CLOSE_ALL");
//		sendBroadcast(intentCloseHome);
//		finish();
//	    }
//	});
	// new GetAsyncPublicChat().execute();

	if (Network.isNetworkConnected(getApplicationContext()))
	{

	    GetPublicChatData();
	}
	else
	{

	    AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
	}

    }

    @Override
    public void onBackPressed()
    {
	// backCLick.OnBackPressed();

	super.onBackPressed();
	Intent intentCloseHome = new Intent("CLOSE_ALL");
	sendBroadcast(intentCloseHome);

	finish();
    }

    void GetPublicChatData()
    {

	showProgress("Please Wait...");

	AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.ADD_GROUP);
	req.setBody(JSONBody.publicChat(Constants.NxtAcId));
	req.setUserInfo(HttpUri.ADD_GROUP);
	DebugReportOnLocat.ln("HttpUri.ADD_GROUP >>>" + HttpUri.ADD_GROUP);
	// final CategoryManager cManager = new CategoryManager(this);
	new AsyncHttpClient().executeJSONObject(getApplicationContext(), req, new ResultCallback()
	{

	    @Override
	    public void onCompleted(Exception e, String responseString, String methodInfo)
	    {

		PublicChat(responseString);

		publicChatAdapter = new PublicChatAdapter(AddGroup.this, GroupList);
		lvGroups.setAdapter(publicChatAdapter);

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
		    runOnUiThread(new Runnable()
		    {
			public void run()
			{

			    dismissProgress();

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
	if (mProgressDialog != null && mProgressDialog.isShowing())
	    dismissProgress();

	mProgressDialog = ProgressDialog.show(this, "", msg);
    }

    protected void dismissProgress()
    {
	if (mProgressDialog != null)
	{
	    mProgressDialog.dismiss();
	    mProgressDialog = null;
	}
    }

}
