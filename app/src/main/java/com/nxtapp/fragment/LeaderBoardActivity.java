package com.nxtapp.fragment;

import java.io.IOException;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.nxtapp.adapter.LeaderBoardAdaptor;
import com.nxtapp.classes.TipModel;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.http.HttpUri;
import com.nxtty.main.R;

public class LeaderBoardActivity extends Activity
{
    ListView lvListLeader;
    LeaderBoardAdaptor adaptor;
    ArrayList<TipModel> LeaderList = new ArrayList<TipModel>();
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.leaderboard_list);
	LoadUI();

    }

    private void LoadUI()
    {
	lvListLeader = (ListView) findViewById(R.id.lv_leaderBoard);
	new LeaderAsync().execute();

	ivBack = (ImageView) findViewById(R.id.iv_leaderBack);
	ivBack.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		finish();
	    }
	});
    }

    public class LeaderAsync extends AsyncTask<Void, String, Void>
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
	    try
	    {

		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
		HttpPost httppost = new HttpPost(HttpUri.GROUPS_LEADERBOARD);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try
		{
		    nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));// G
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    HttpResponse response = httpclient.execute(httppost);
		    respStr = EntityUtils.toString(response.getEntity());
		    DebugReportOnLocat.e("##############", "  responce from Leader  -->  " + respStr);

		} catch (ClientProtocolException e)
		{
		    DebugReportOnLocat.e(e);

		} catch (IOException e)
		{
		    DebugReportOnLocat.e(e);
		}

	    } catch (Exception e)
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
		    JSONArray jArray = new JSONArray(respStr);
		    DebugReportOnLocat.ln("     " + jArray.length());
		    TipModel model;
		    LeaderList.clear();
		    for (int i = 0; i < jArray.length(); i++)
		    {
			try
			{

				model = new TipModel();
				JSONObject jobj = jArray.getJSONObject(i);
				JSONObject jobjInner = jobj.getJSONObject("owner");
				model.setName(jobjInner.optString("nameAlias"));
				DebugReportOnLocat.ln("     " + jobjInner.optString("nameAlias"));
				model.setId(jobjInner.optString("nxtAccountId"));
				
				 if(jobjInner.optString("avatar").equals("null")){
				     
				     model.setImagePath(null);
				     
				 }else{
				     
				     model.setImagePath(Constants.baseUrl_ImagesGroup + jobjInner.optString("avatar"));
				 }
				 
				
				model.setTipCount(jobj.optString("tipCount"));
				LeaderList.add(model);
			    
			    
			} catch (Exception e)
			{
			    e.printStackTrace();
			}
			
			
		    }
		} catch (JSONException e)
		{
		    e.printStackTrace();
		}

		adaptor = new LeaderBoardAdaptor(LeaderBoardActivity.this, LeaderList);
		lvListLeader.setAdapter(adaptor);
	    }

	}

    }

}
