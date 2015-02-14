package com.nxtapp.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.nextapp.tasks.CallPostWebseviceTask;
import com.nxtapp.adapter.BlockContactAdapter;
import com.nxtapp.classes.FriendRequest;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

public class BlockListActivity extends Activity implements OnClickListener
{
    private ArrayList<FriendRequest> blockContactList = new ArrayList<FriendRequest>();
    private ListView lvBlockList;
    private BlockContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_block_list);

	initObjects();

	callWSBlockContacts();

    }

    private void initObjects()
    {
	// TODO Auto-generated method stub
	lvBlockList = (ListView) findViewById(R.id.lv_blocklist);
	findViewById(R.id.iv_chatBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
	// TODO Auto-generated method stub
	switch (v.getId())
	{
	case R.id.iv_chatBack:
	    backManage();
	    break;

	default:
	    break;
	}
    }

    @Override
    public void onBackPressed()
    {
	// TODO Auto-generated method stub
	backManage();
    }

    private void backManage()
    {
	finish();
    }

    private void callWSBlockContacts()
    {
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

	CallPostWebseviceTask task = new CallPostWebseviceTask(BlockListActivity.this, true, "blocked_contacts", nameValuePairs)
	{

	    @Override
	    protected void parseResponse(String response)
	    {
		// TODO Auto-generated method stub
		try
		{
		    JSONArray jArray = new JSONArray(response);
		    FriendRequest model;
		    for (int i = 0; i < jArray.length(); i++)
		    {

			JSONObject jobj = jArray.getJSONObject(i);
			model = new FriendRequest();
			model.setUserId(jobj.optString("nxtAccountId"));
			model.setUserName(jobj.optString("nameAlias"));
			model.setDeletPlanID(jobj.optString("deletePlanId"));
			model.setRegistrationDate(jobj.optString("registrationDate"));
			
			if(jobj.optString("avatar").equals("null")){
			    
			    model.setAvatarImage(null);
			    
			}else{
			    
			    model.setAvatarImage(Constants.baseUrl_Images + jobj.optString("avatar"));
			}
			
			

			model.setUserStatus(StringEscapeUtils.unescapeJava(jobj.optString("status")));

			// model.setUserStatus(jobj.optString("status"));

			model.setSchool(jobj.optString("school"));

			blockContactList.add(model);

		    }

		} catch (JSONException e)
		{
		    DebugReportOnLocat.e(e);
		}
	    }

	    @Override
	    protected void onPostExecute(String result)
	    {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		if (result != null)
		{
		    updateListData();
		}

	    }
	};

	task.execute();
    }

    protected void updateListData()
    {
	// TODO Auto-generated method stub
	if (adapter == null)
	{
	    adapter = new BlockContactAdapter(BlockListActivity.this, blockContactList);
	    lvBlockList.setAdapter(adapter);
	}
	else
	{
	    adapter.notifyDataSetChanged();
	}
    }
}
