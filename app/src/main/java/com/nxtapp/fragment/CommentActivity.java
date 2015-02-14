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
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import aniways.com.google.analytics.tracking.android.Log;

import com.aniways.Aniways;
import com.nxtapp.adapter.CommentAdapter;
import com.nxtapp.adapter.CommentAdapter.OnEditcommentPost;
import com.nxtapp.adapter.GroupDetailAdapter;
import com.nxtapp.classes.CommentModel;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.KeyboardUtility;
import com.nxtapp.utils.Network;
import com.nxtty.main.R;

public class CommentActivity extends Activity implements OnEditcommentPost
{

    public static final int ADD_COMMMENTS = 0x10000;
    String postID;
    
    String CommentID;
    ArrayList<CommentModel> CommentList = new ArrayList<CommentModel>();
    ListView lvCommentList;

    private EditText textEditor;

    CommentAdapter adapter;
    ImageView ivBack;
    public static String GroupId, GroupName;

    GroupDetailAdapter groupDetailAdapter;

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
	// TODO Auto-generated method stub
	super.onConfigurationChanged(newConfig);

	
	if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
	{
	    // Log.e(TAG, "max line 1");
	    textEditor.setMaxLines(1);
	}
	else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
	{
	    // Log.e(TAG, "max line 5");
	    textEditor.setMaxLines(5);
	}
    }

    Context mContext;
    private boolean isEdit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	Aniways.init(this);
	mContext = this;
	CommentList = new ArrayList<CommentModel>();
	
	getWindow().setSoftInputMode(
		    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.comment_layoutscreen);

	postID = getIntent().getExtras().getString("PostID");
	LoadUI();
	isEdit=false;
    }

    private void LoadUI()
    {

	lvCommentList = (ListView) findViewById(R.id.lvCommentList);
	new GetCommentDataAsync().execute();

	ivBack = (ImageView) findViewById(R.id.iv_CommnetBack);
	ivBack.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {


		finish();
	    }
	});

	final ImageView btn = (ImageView) findViewById(R.id.chat_send);
	textEditor = (EditText) findViewById(R.id.chat_input);
	textEditor.addTextChangedListener(new TextWatcher()
	{

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count)
	    {
		if (s.toString().startsWith(" "))
		{

		    AlertUtility.showToast(getApplicationContext(), "Empty start up spaces not allowed.");
		    textEditor.setText("");
		    return;
		}
		else if (s.toString().startsWith("\n"))
		{
		    AlertUtility.showToast(getApplicationContext(), "Enter not allowed in start up.");
		    textEditor.setText("");
		    return;
		}

	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after)
	    {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void afterTextChanged(Editable s)
	    {
		// TODO Auto-generated method stub
		btn.setEnabled(s != null && s.length() > 0);
	    }
	});

	btn.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		final String Message = Aniways.encodeMessage(textEditor.getText());
		DebugReportOnLocat.e("################", "  Message : " + Message);

		if (Network.isNetworkConnected(getApplicationContext()))
		{

		    if (Message.trim().length() >= 1)
		    {

			textEditor.setText("");
			// dialog.cancel();
			

			if(isEdit){
			    
			    new UpdateComment(Message,CommentID).execute();
			    
			   
			}else{
			    
			    new SendComment(Message).execute();
			 
			}
			
			//new SendComment(Message).execute();

			try
			{
			    KeyboardUtility.hideKeypad((Activity) mContext);

			} catch (Exception e)
			{
			    DebugReportOnLocat.e(e);
			}

		    }

		}
		else if (textEditor.getText().toString().trim().length() == 0)
		{
		    // Bala Code
		    AlertUtility.showToast(getApplicationContext(), getString(R.string.enter_comment));

		}

		else
		{
		    AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
		}

	    }
	});

	/*
	 * TextView txt = (TextView) findViewById(R.id.txt_addComment);
	 * txt.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { //openDialogCreatePost();
	 * Intent intent = new Intent(CommentActivity.this,
	 * GroupDetailAddComment.class); intent.putExtra("PostID", postID);
	 * startActivityForResult(intent, ADD_COMMMENTS);
	 * 
	 * 
	 * } });
	 */
    }

    public void openDialogCreatePost()
    {
	
	try
	{
	    


		final Dialog dialog = new Dialog(CommentActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.create_comment);
		dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

		final EditText editDetail = (EditText) dialog.findViewById(R.id.edit_CreateDetail);
		TextView txtPost = (TextView) dialog.findViewById(R.id.txt_create_post);
		TextView txtcancelBottom = (TextView) dialog.findViewById(R.id.txt_cancelBottom);
		TextView txtcancelTop = (TextView) dialog.findViewById(R.id.txt_create_cancel);

		txtcancelBottom.setOnClickListener(new OnClickListener()
		{
		    @Override
		    public void onClick(View v)
		    {
			dialog.cancel();
		    }
		});
		txtcancelTop.setOnClickListener(new OnClickListener()
		{
		    @Override
		    public void onClick(View v)
		    {
			dialog.cancel();
		    }
		});

		txtPost.setOnClickListener(new OnClickListener()
		{
		    @Override
		    public void onClick(View v)
		    {
			final String Message = Aniways.encodeMessage(editDetail.getText());
			DebugReportOnLocat.e("################", "  Message : " + Message);

			if (Message.trim().length() >= 1)
			{

			    if (Network.isNetworkConnected(getApplicationContext()))
			    {
				dialog.cancel();
				
				if(isEdit){
				    
				    new UpdateComment(Message,CommentID).execute();
				    
				}else{
				    
				    new SendComment(Message).execute();
				   
				}
				

			    }
			    else
			    {
				AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
			    }

			}
			else if (editDetail.getText().toString().trim().length() == 0)
			{
			    // Bala Code
			    AlertUtility.showToast(getApplicationContext(), getString(R.string.enter_comment));

			}

		    }
		});

		dialog.show();
	    
		
	    
	} catch (Exception e)
	{
	    // TODO: handle exception
	}
    }

    public class SendComment extends AsyncTask<Void, String, String>
    {

	ProgressDialog pDialog;
	String Title, Message, path;

	public SendComment(String Message)
	{
	    this.Message = Message;
	}

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {
		pDialog = new ProgressDialog(CommentActivity.this);
		pDialog.setMessage(getResources().getString(R.string.please_wait));
		pDialog.show();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }

	}

	@Override
	protected String doInBackground(Void... params)
	{
	    
	    String respStr = null;
	    if(Network.isNetworkConnected(mContext)){
		

		    HttpClient httpclient = new DefaultHttpClient();
		    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
		    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/create_comment");

		    MultipartEntity reqEntity = new MultipartEntity();
		    try
		    {
			DebugReportOnLocat.e("##############", "  postID  -->  " + postID);
			SharedPreferences pref = getSharedPreferences("ID", 0);
			Constants.NxtAcId = pref.getString("nxtAcId", "0");

			
			reqEntity.addPart("nxtID", new StringBody(Constants.NxtAcId));
			reqEntity.addPart("postID", new StringBody(postID));
			reqEntity.addPart("key", new StringBody(Constants.ParamKey));
			DebugReportOnLocat.e("CommentActivity", "  postID  -->  " + postID);
			String unicodeString = convertToUnicodeEscaped(Message);

			reqEntity.addPart("comment", new StringBody(unicodeString));

			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			respStr = EntityUtils.toString(response.getEntity());

			DebugReportOnLocat.e("##############", "  responce from conversation  -->  " + respStr);
			
		    } catch (ClientProtocolException e)
		    {
			DebugReportOnLocat.e(e);
		    } catch (IOException e)
		    {
			
			DebugReportOnLocat.e(e);
		    }
		    
		    
	    }

		    return respStr;
		
	    
	}

	@Override
	protected void onPostExecute(String result)
	{
	    super.onPostExecute(result);
	    try
	    {
		if (pDialog != null)
		    pDialog.dismiss();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);

	    }
	    ;

	    if (result != null )
	    {
		
		if(!result.equals("null")){
		    
		    
		    try
			{
			    JSONObject jsonObject = new JSONObject(result);
			    boolean status = jsonObject.getBoolean("status");
			    if (status)
			    {
				Log.e("CommentActivity,status=>" + status);
				GroupDetailActivity.ComingFromCreatePost = true;
			    }

			} catch (JSONException e)
			{
			    // TODO Auto-generated catch block
			    DebugReportOnLocat.e(e);
			}
		    
		}
		
	    }
	    
	    if(Network.isNetworkConnected(mContext)){
		
		
		    new GetCommentDataAsync().execute();
		    
	    }

	

	}
    }

    private String unicodeEscaped(char ch)
    {
	if (ch < 0x10)
	{
	    return "\\u000" + Integer.toHexString(ch);
	}
	else if (ch < 0x100)
	{
	    return "\\u00" + Integer.toHexString(ch);
	}
	else if (ch < 0x1000)
	{
	    return "\\u0" + Integer.toHexString(ch);
	}
	return "\\u" + Integer.toHexString(ch);
    }

    private String convertToUnicodeEscaped(String str)
    {
	StringBuilder sbUnicodeBuilder = new StringBuilder();

	char[] chArr = str.toCharArray();
	for (char mChar : chArr)
	{
	    sbUnicodeBuilder.append(unicodeEscaped(mChar));
	}

	return sbUnicodeBuilder.toString();
    }

    public class GetCommentDataAsync extends AsyncTask<Void, String, Void>
    {

	ProgressDialog pDialog;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {

		pDialog = new ProgressDialog(CommentActivity.this);
		pDialog.setMessage(getResources().getString(R.string.please_wait));
		pDialog.show();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }

	}

	@Override
	protected Void doInBackground(Void... params)
	{
	    
	    
	    if(Network.isNetworkConnected(mContext)){
		


		    DebugReportOnLocat.e("#########", " FR Constants.NxtAcId  :  " + Constants.NxtAcId);

		    String respStr = null;

		    HttpClient httpclient = new DefaultHttpClient();
		    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
		    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/get_post");

		    DebugReportOnLocat.e("##############", "  Constants.NxtAcId -->  " + Constants.NxtAcId);

		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		    try
		    {
			nameValuePairs.add(new BasicNameValuePair("postID", postID));
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
		    try
		    {
			
			
			if(CommentList!=null){
			    
			    CommentList.clear();
			}
			

			if (respStr != null)
			{

			    JSONObject Jobj = new JSONObject(respStr);
			    CommentModel model;
			    JSONArray jArray = Jobj.getJSONArray("comments");
			    for (int i = 0; i < jArray.length(); i++)
			    {
				model = new CommentModel();
				JSONObject jobj_new = jArray.getJSONObject(i);

				model.setTimestamp(jobj_new.optString("created"));
				model.setCommentID(jobj_new.optString("id"));
				model.setBody(jobj_new.optString("comment"));
				JSONObject jobjInner = jobj_new.getJSONObject("owner");
				model.setName(jobjInner.optString("nameAlias"));
				model.setId(jobjInner.optString("nxtAccountId"));
				model.setImage(Constants.baseUrl_ImagesGroup + jobjInner.optString("avatar"));

				CommentList.add(model);
			    }
			}

		    } catch (JSONException e)
		    {
			DebugReportOnLocat.e(e);
		    }

		 
		
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

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);

	    }
	    ;
	    adapter = new CommentAdapter(CommentActivity.this, CommentList);
	    lvCommentList.setAdapter(adapter);
	    // Constants.comments= lvCommentList.getCount();
	}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);

	if (resultCode == Activity.RESULT_OK)
	{
	    switch (requestCode)
	    {
	    case ADD_COMMMENTS:

		try
		{
		    GroupDetailActivity.ComingFromCreatePost = true;
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		
		 if(Network.isNetworkConnected(mContext)){
		     
		     
		     new GetCommentDataAsync().execute();
		     
		 }else{
		     
		     AlertUtility.showToast(getApplicationContext(),  getString(R.string.networkIssues));
		 }

		
		break;

	    }
	}

    }

    @Override
    public void OnEditClick(final int position,String msg,String commentID,boolean isedit)
    {
	  
	if(isedit){
	    
	        textEditor.setText(msg);
		CommentID=commentID;
		this.isEdit=true;
		
	}else{
	    
	    if(isEdit){
		
		 new AlertDialog.Builder(this)
		    
		    .setMessage(R.string.cancelUpdate)
		    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
		            
		                textEditor.setText("");
				CommentID="";
				isEdit=false;
				
			try
			{
			    if(CommentList!=null){
				
				   adapter = new CommentAdapter(CommentActivity.this, CommentList);
				    lvCommentList.setAdapter(adapter);
				    lvCommentList.setSelection(position);
			    }
				 
				    
			    
			} catch (Exception e)
			{
			   DebugReportOnLocat.e(e);
			}
				
				
		        }
		     })
		    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();
		
	    }
	    
	   
	    
	    
	       
	}
	
	
	DebugReportOnLocat.ln(" msg "+msg+" CommentID>> "+CommentID);
	
    }
    
    
    //
    
    
    
    public class UpdateComment extends AsyncTask<Void, String, String>
    {

	ProgressDialog pDialog;
	String Title, Message, path,commentID ;

	public UpdateComment(String Message,String commentID)
	{
	    this.Message = Message;
	    this.commentID=commentID ;
	}

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {
		pDialog = new ProgressDialog(CommentActivity.this);
		pDialog.setMessage(getResources().getString(R.string.please_wait));
		pDialog.show();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }

	}

	@Override
	protected String doInBackground(Void... params)
	{

	    String respStr = null;

	    if(Network.isNetworkConnected(mContext)){
		
		
		HttpClient httpclient = new DefaultHttpClient();
		    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
		    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/update_comment");

		    MultipartEntity reqEntity = new MultipartEntity();
		    try
		    {
			DebugReportOnLocat.e("##############", "  update_comment commentID  -->  " + commentID);
			
			DebugReportOnLocat.e("CommentActivity ", "  commentID  -->  " + commentID);
			
			
			reqEntity.addPart("commentID", new StringBody(commentID));
			reqEntity.addPart("key", new StringBody(Constants.ParamKey));
			
			String unicodeString = convertToUnicodeEscaped(Message);

			reqEntity.addPart("comment", new StringBody(unicodeString));

			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			respStr = EntityUtils.toString(response.getEntity());

			DebugReportOnLocat.e("##############", "  responce from conversation  -->  " + respStr);
		   
		    } catch (ClientProtocolException e)
		    {
			DebugReportOnLocat.e(e);
			
		    } catch (IOException e)
		    {
			DebugReportOnLocat.e(e);
		    }
		
	    }
	    

	    return respStr;
	}

	@Override
	protected void onPostExecute(String result)
	{
	    super.onPostExecute(result);
	    
	    isEdit=false;
	    
	    try
	    {
		if (pDialog != null)
		    pDialog.dismiss();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);

	    }
	    ;

	    if (result != null ){
		
		

		if( !result.equals("null"))
	    {
		try
		{
		    JSONObject jsonObject = new JSONObject(result);
		    boolean status = jsonObject.getBoolean("status");
		    if (status)
		    {
			Log.e("CommentActivity,status=>" + status);
			//GroupDetailActivity.ComingFromCreatePost = true;
		    }

		} catch (JSONException e)
		{
		    // TODO Auto-generated catch block
		    DebugReportOnLocat.e(e);
		}
	    }
		
	    }
		
	    
	    

		 if(Network.isNetworkConnected(mContext)){
		     
		     
		     new GetCommentDataAsync().execute();
		     
		 }else{
		     
		     AlertUtility.showToast(getApplicationContext(),  getString(R.string.networkIssues));
		 }


	   // new GetCommentDataAsync().execute();

	}
    }
}
