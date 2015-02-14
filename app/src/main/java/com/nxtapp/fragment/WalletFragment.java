package com.nxtapp.fragment;

import java.io.File;
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

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nxtapp.adapter.TipListAdaptor;
import com.nxtapp.classes.TipModel;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.ExpandableHeightListView;
import com.nxtapp.utils.Network;
import com.nxtapp.utils.StaticUtility;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpClient;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;
import com.nxtty.main.R;

public class WalletFragment extends Fragment
{
    ArrayList<TipModel> arrlstTipModel = new ArrayList<TipModel>();
    TextView txtMainBal;
    ExpandableHeightListView lvTipList;
    TextView txtLeaderBoard;
    TipListAdaptor adaptor;
    ImageView lnMainLayout;
    int tipsCount = 0;
    ProgressDialog mProgressDialog;
    Context mContext;

    private static final Field sChildFragmentManagerField;
    private static final String LOGTAG = "GCheck";

    static
    {
	Field f = null;
	try
	{
	    f = Fragment.class.getDeclaredField("mChildFragmentManager");
	    f.setAccessible(true);
	} catch (NoSuchFieldException e)
	{
	    Log.e(LOGTAG, "Error getting mChildFragmentManager field", e);
	}
	sChildFragmentManagerField = f;
    }

    @Override
    public void onDetach()
    {
	super.onDetach();

	if (sChildFragmentManagerField != null)
	{
	    try
	    {
		sChildFragmentManagerField.set(this, null);
	    } catch (Exception e)
	    {
		Log.e(LOGTAG, "Error setting mChildFragmentManager field", e);
	    }
	}
    }

    public void changeWalletBackground(Uri backgroungUri)
    {
	if (backgroungUri != null)
	{
	    lnMainLayout.setImageURI(backgroungUri);
	}
	else
	{
	    lnMainLayout.setImageResource(R.drawable.menu_background_new);
	}
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

	View view = inflater.inflate(R.layout.wallet_main_screen, null, false);

	mContext = getActivity();

	lnMainLayout = (ImageView) view.findViewById(R.id.ln_walletMain);
	File background = StaticUtility.getResideMenuBackgroundImagePath(mContext);
	if (background != null && background.isFile())
	{
	    lnMainLayout.setImageURI(Uri.fromFile(background));
	}
	else
	{
	    lnMainLayout.setImageResource(R.drawable.menu_background_new);
	}

	txtMainBal = (TextView) view.findViewById(R.id.txt_mainBalance_nxt);
	// lvTipList = (ListView) view.findViewById(R.id.lv_walletList_tips);
	lvTipList = (ExpandableHeightListView) view.findViewById(R.id.lv_walletList_tips);
	lvTipList.setExpanded(true);

	// new GetTipListAsync().execute();

	txtLeaderBoard = (TextView) view.findViewById(R.id.txtLeaderBoard);
	txtLeaderBoard.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		startActivity(new Intent(mContext, LeaderBoardActivity.class));
	    }
	});

	txtLeaderBoard.setVisibility(View.GONE);

	/*
	 * SharedPreferences prefs = mContext.getSharedPreferences("PhpCall",
	 * 0); String FirstCallAns = prefs.getString("FirstCall", "No");
	 * 
	 * if (FirstCallAns.equalsIgnoreCase("No")) { AlertDialog.Builder
	 * alertDialogBuilder = new AlertDialog.Builder(mContext);
	 * alertDialogBuilder
	 * .setMessage(getResources().getString(R.string.wallet_notice
	 * )).setCancelable(false)
	 * .setPositiveButton(getResources().getString(R.string.yes), new
	 * DialogInterface.OnClickListener() { public void
	 * onClick(DialogInterface dialog, int id) { dialog.cancel(); new
	 * PhpCallAsync().execute();
	 * 
	 * } }).setNegativeButton(getResources().getString(R.string.no), new
	 * DialogInterface.OnClickListener() { public void
	 * onClick(DialogInterface dialog, int id) { dialog.cancel();
	 * SharedPreferences.Editor editor =
	 * mContext.getSharedPreferences("PhpCall", 0).edit();
	 * editor.putString("FirstCall", "Yes"); editor.commit(); new
	 * GetInitioalDataAsync().execute(); } }); AlertDialog alertDialog =
	 * alertDialogBuilder.create(); alertDialog.show(); } else { new
	 * GetInitioalDataAsync().execute(); }
	 */

	if (Network.isNetworkConnected(mContext.getApplicationContext()))
	{

	    new GetInitioalDataAsync().execute();

	    TipList();
	}
	else
	{
	    AlertUtility.showToast(mContext.getApplicationContext(), getString(R.string.networkIssues));
	}

	return view;
    }

    // ProgressDialog pDialog;

    public void TipList()
    {

	/*
	 * try {
	 * 
	 * mProgressDialog = new ProgressDialog(mContext);
	 * mProgressDialog.setMessage
	 * (getResources().getString(R.string.please_wait));
	 * mProgressDialog.show();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 */

	AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.GROUPS_GET_SUBSCRIBER_POST_TIPS);
	req.setBody(JSONBody.publicChat(Constants.NxtAcId));
	req.setUserInfo(HttpUri.GROUPS_GET_SUBSCRIBER_POST_TIPS);
	// final CategoryManager cManager = new CategoryManager(this);
	new AsyncHttpClient().executeJSONObject(mContext.getApplicationContext(), req, new ResultCallback()
	{

	    @Override
	    public void onCompleted(Exception e, String responseString, String methodInfo)
	    {

		GetContacts(responseString);

		adaptor = new TipListAdaptor(mContext, arrlstTipModel);
		lvTipList.setAdapter(adaptor);
		DebugReportOnLocat.ln("Tips count>>>" + lvTipList.getCount());
		tipsCount = lvTipList.getCount();
		// txtMainBal.setText(tipsCount + " NXTTY");
		// DebugReportOnLocat.ln("Tips count>>>" + txtMainBal);
		/*
		 * try { if (mProgressDialog != null) mProgressDialog.dismiss();
		 * 
		 * } catch (Exception e2) { e2.printStackTrace(); }
		 */

	    }

	    private void GetContacts(String respStr)
	    {

		if (respStr != null)
		{
		    
		    arrlstTipModel.clear();
		    
		    if (respStr.length() > 10)
		    {

			DebugReportOnLocat.println(" >>>> " + respStr);
			try
			{
			    JSONArray jArray = new JSONArray(respStr);
			    TipModel model;
			  
			    for (int i = 0; i < jArray.length(); i++)
			    {
				model = new TipModel();
				JSONObject jobj = jArray.getJSONObject(i);
				JSONObject jobjInner = jobj.getJSONObject("owner");
				model.setName(jobjInner.optString("nameAlias"));
				model.setId(jobjInner.optString("nxtAccountId"));

				if (jobjInner.optString("avatar").equals("null"))
				{

				    model.setImagePath(null);

				}
				else
				{

				    model.setImagePath(Constants.baseUrl_ImagesGroup + jobjInner.optString("avatar"));
				}

				model.setTipTimestamp(jobj.optString("created"));
				arrlstTipModel.add(model);
			    }
			} catch (JSONException e)
			{
			    e.printStackTrace();
			}

		    }

		}
	    }
	});

    }

    /*
     * public class GetTipListAsync extends AsyncTask<Void, String, Void> {
     * 
     * @Override protected void onPreExecute() { super.onPreExecute();
     * 
     * }
     * 
     * @Override protected Void doInBackground(Void... params) {
     * 
     * DebugReportOnLocat.e("#########", " Demo Req for TIP User  :  " +
     * Constants.NxtAcId);
     * 
     * String respStr = null;
     * 
     * HttpClient httpclient = new DefaultHttpClient();
     * HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
     * HttpPost httppost = new HttpPost(Constants.baseUrl_Group +
     * "groups/get_subscriber_posts_tips");
     * 
     * List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
     * 
     * try { nameValuePairs.add(new BasicNameValuePair("nxtID",
     * Constants.NxtAcId)); httppost.setEntity(new
     * UrlEncodedFormEntity(nameValuePairs)); HttpResponse response =
     * httpclient.execute(httppost); respStr =
     * EntityUtils.toString(response.getEntity());
     * 
     * DebugReportOnLocat.e("##############", "  responce from post -->  " +
     * respStr);
     * 
     * } catch (ClientProtocolException e) { } catch (IOException e) { }
     * 
     * try { JSONArray jArray = new JSONArray(respStr); TipModel model;
     * arrlstTipModel.clear(); for (int i = 0; i < jArray.length(); i++) { model
     * = new TipModel(); JSONObject jobj = jArray.getJSONObject(i); JSONObject
     * jobjInner = jobj.getJSONObject("owner");
     * model.setName(jobjInner.optString("nameAlias"));
     * model.setId(jobjInner.optString("nxtAccountId"));
     * model.setImagePath(Constants.baseUrl_ImagesGroup +
     * jobjInner.optString("avatar")); arrlstTipModel.add(model); } } catch
     * (JSONException e) { e.printStackTrace(); }
     * 
     * return null; }
     * 
     * @Override protected void onPostExecute(Void result) {
     * super.onPostExecute(result); adaptor = new TipListAdaptor(mContext,
     * arrlstTipModel); lvTipList.setAdapter(adaptor); }
     * 
     * }
     */
    public static WalletFragment newInstance()
    {
	WalletFragment fragment = new WalletFragment();
	return fragment;
    }

    /*
     * public class PhpCallAsync extends AsyncTask<Void, String, Void> {
     * 
     * @Override protected void onPreExecute() { super.onPreExecute(); }
     * 
     * @Override protected Void doInBackground(Void... params) { try {
     * 
     * DebugReportOnLocat.e("#########", " PHP call Wallet   :  " +
     * Constants.NxtAcId); DebugReportOnLocat.e("#########",
     * " PHP call Wallet   :  " + Constants.RegistationKeyGCM);
     * 
     * String respStr = null; HttpClient httpclient = new DefaultHttpClient();
     * HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
     * HttpPost httppost = new
     * HttpPost("http://128.199.211.69/2500nxttycoinsender.php");
     * 
     * List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); try
     * { nameValuePairs.add(new BasicNameValuePair("recipient",
     * Constants.NxtAcId)); nameValuePairs.add(new
     * BasicNameValuePair("deviceID", GetUniqueID())); // nameValuePairs.add(new
     * BasicNameValuePair("deviceID", Constants.RegistationKeyGCM));
     * nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));//
     * G httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
     * HttpResponse response = httpclient.execute(httppost); respStr =
     * EntityUtils.toString(response.getEntity());
     * DebugReportOnLocat.e("##############",
     * "  responce from  PHP call Wallet  -->  " + respStr);
     * 
     * } catch (ClientProtocolException e) { DebugReportOnLocat.e(e);
     * 
     * } catch (IOException e) { DebugReportOnLocat.e(e); }
     * 
     * } catch (Exception e) { DebugReportOnLocat.e(e); }
     * 
     * return null; }
     * 
     * @Override protected void onPostExecute(Void result) {
     * super.onPostExecute(result);
     * 
     * try { SharedPreferences.Editor editor =
     * mContext.getSharedPreferences("PhpCall", 0).edit();
     * editor.putString("FirstCall", "Yes"); editor.commit(); new
     * GetInitioalDataAsync().execute();
     * 
     * } catch (Exception e) { DebugReportOnLocat.e(e);
     * 
     * } }
     * 
     * }
     */
    public class GetInitioalDataAsync extends AsyncTask<Void, String, Void>
    {

	// ProgressDialog pDialog;
	String Balance = "";

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {
		// G code
		if (mProgressDialog != null)
		{

		    if (mProgressDialog.isShowing())
			dismissProgress();

		    // mProgressDialog = null;
		}
		showProgress(mContext.getResources().getString(R.string.please_wait));
		// pDialog = new
		// ProgressDialog(mContext.getApplicationContext());
		// pDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
		// pDialog.show();

	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}

	@Override
	protected Void doInBackground(Void... params)
	{

	    String respStr = null;

	    try

	    {
		DebugReportOnLocat.e("#########", " Demo Req for TIP User  :  " + Constants.NxtAcId);

		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
		HttpPost httppost = new HttpPost(Constants.baseUrl);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try
		{
		    nameValuePairs.add(new BasicNameValuePair("requestType", "getAccount"));
		    nameValuePairs.add(new BasicNameValuePair("account", Constants.NxtAcId));
		    nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));// G
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    HttpResponse response = httpclient.execute(httppost);
		    respStr = EntityUtils.toString(response.getEntity());

		    DebugReportOnLocat.e("##############", "  responce from post -->  " + respStr);

		} catch (ClientProtocolException e)
		{
		} catch (IOException e)
		{
		}

		// G code
		if (respStr != null)

		    if (respStr.length() > 5)
		    {

			try
			{
			    if (respStr != null)
			    {

				JSONObject jobj = new JSONObject(respStr);
				JSONArray jArray = jobj.getJSONArray("assetBalances");
				for (int i = 0; i < jArray.length(); i++)
				{
				    JSONObject jobj_innner = jArray.getJSONObject(i);
				    Balance = jobj_innner.optString("balanceQNT");
				    // Bala code
				    // Balance =
				    // String.valueOf(lvTipList.getCount());
				    DebugReportOnLocat.e("#############", "   Balance  " + Balance);

				}
			    }

			} catch (JSONException e)
			{
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
		    }

		return null;

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

	    try
	    {
		if (mProgressDialog != null)
		{

		    mProgressDialog.dismiss();
		}

	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }

	    // txtMainBal.setText(tipsCount + " NXTTY");
	    txtMainBal.setText(Balance + " NXTTY");

	}

    }

    protected void showProgress(String msg)
    {
	try
	{
	    // mContext
	    if (mProgressDialog != null)
	    {

		if (mProgressDialog.isShowing())
		    dismissProgress();
	    }

	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	    ;
	}

	try
	{

	    mProgressDialog = new ProgressDialog(mContext);
	    mProgressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
	    mProgressDialog.show();

	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	    ;
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
	    DebugReportOnLocat.e(e);
	}

    }

    /*
     * private String GetUniqueID() { final TelephonyManager tm =
     * (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
     * final String tmDevice, tmSerial, androidId; tmDevice = "" +
     * tm.getDeviceId(); tmSerial = "" + tm.getSimSerialNumber(); androidId = ""
     * +
     * android.provider.Settings.Secure.getString(mContext.getContentResolver(),
     * android.provider.Settings.Secure.ANDROID_ID); UUID deviceUuid = new
     * UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) |
     * tmSerial.hashCode()); String deviceId = deviceUuid.toString();
     * 
     * DebugReportOnLocat.e("#########", "  device ID ----->>  " + deviceId);
     * 
     * return deviceId;
     * 
     * }
     */

}
