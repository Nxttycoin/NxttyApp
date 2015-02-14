package com.nxtapp.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nextapp.data.ChatInfoManager;
import com.nextapp.tasks.CallPostWebseviceTask;
import com.nextapp.webservice.JsonParserUtility;
import com.nxtapp.adapter.HomeAdapter;
import com.nxtapp.classes.ChatModel;
import com.nxtapp.classes.UserModel;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.Dashboard;
import com.nxtty.main.R;
import com.special.ResideMenu.ResideMenu;

public class HomeFragment extends BaseListFragment
{

    private final int ACTION_CHAT_WINDOW = 1111;
    @SuppressWarnings("unused")
    private ResideMenu resideMenu;
    ListView myListView;
    HomeAdapter homeAdapter;
    public static ArrayList<UserModel> HomeScreenList = new ArrayList<UserModel>();
    // LinearLayout lnContacts, lnMessage, lnSettings;
    TextView txt_navTitle;
    ImageView ivMessage, ivContact, ivSettings;
    static String TAG = "HomeFragment";

    ChatInfoManager chatInfoManager;
    Context mContext;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
	setUpViews();

	View view = inflater.inflate(R.layout.home, container, false);
	myListView = (ListView) view.findViewById(android.R.id.list);
	
	mContext=getActivity();
	HomeScreenList = new ArrayList<UserModel>();
	
	try
	{
	    
	    chatInfoManager = new ChatInfoManager(getActivity());
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	/*try
	{
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
		    }
		
		
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}*/

	Constants.sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

	// ivMessage = (ImageView) view.findViewById(R.id.iv_message);
	// ivMessage.setBackgroundResource(R.drawable.message_selector);
	// ivContact = (ImageView) view.findViewById(R.id.iv_contact);
	// ivContact.setBackgroundResource(R.drawable.contacts);
	// ivSettings = (ImageView) view.findViewById(R.id.iv_settings);
	// ivSettings.setBackgroundResource(R.drawable.settings_bottom);
	// lnContacts = (LinearLayout)
	// view.findViewById(R.id.ln_bottom_contacts_conversation);
	// lnContacts.setOnClickListener(this);
	// lnMessage = (LinearLayout)
	// view.findViewById(R.id.ln_bottom_message_conversation);
	// lnSettings = (LinearLayout)
	// view.findViewById(R.id.ln_bottom_setting_conversation);
	// lnSettings.setOnClickListener(this);

	txt_navTitle = (TextView) getActivity().findViewById(R.id.textview_navigationTitle);
	txt_navTitle.setText(R.string.conversations);

	if (isNetworkConnected())
	{

	    new GetHomeDataAsync().execute();
	  //  HomeData();
	}else{


          AlertUtility.showToast(getActivity().getApplicationContext(),  getString(R.string.networkIssues));
	}

	// new GetHomeDataAsync().execute();

	return view;
    }

    private void setUpViews()
    {
	Dashboard parentActivity = (Dashboard) getActivity();
	resideMenu = parentActivity.getResideMenu();

	// FrameLayout ignored_view = (FrameLayout) parentView
	// .findViewById(R.id.ignored_view);
	// resideMenu.addIgnoredView(ignored_view);
    }
    
    
//    public void HomeData(){
//
//	    final ProgressDialog pDialog;
//	    pDialog = new ProgressDialog(getActivity());
//	    pDialog.setMessage(getResources().getString(R.string.please_wait));
//	    pDialog.show();
//
//
//		AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.SUBSCRIBER_ACTIVE_CONVERSATION);
//		req.setBody(JSONBody.publicChat(Constants.NxtAcId));
//		req.setUserInfo(HttpUri.SUBSCRIBER_ACTIVE_CONVERSATION);
//		//final CategoryManager cManager = new CategoryManager(this);
//		AsyncHttpClient.getDefaultInstance().executeJSONObject(getActivity().getApplicationContext(),req,
//				new ResultCallback() {
//
//					@Override
//					public void onCompleted(Exception e, String responseString, String methodInfo) {
//					    
//					    GetContacts(responseString);
//
//					    pDialog.dismiss();
//					    updateList();
//
//					}
//
//					private void GetContacts(String respStr)
//					{   try
//					    {
//					    
//					    if(respStr!=null){
//						
//
//						JSONArray jArray = new JSONArray(respStr);
//
//						JsonParserUtility jsonUtility = new JsonParserUtility();
//						
//						
//						
//						
//						//The following code null pointer exception is raising
//						File imagesFolderPath = getActivity().getApplicationContext().getExternalFilesDir("images");
//						
//						for (int i = 0; i < jArray.length(); i++)
//						{
//						    JSONObject jobj = jArray.getJSONObject(i);
//						    JSONObject jobjIn = jobj.getJSONObject("user");
//						    UserModel model = new UserModel();
//						    model.setNxtAccountId(jobjIn.optString("nxtAccountId"));
//						    model.setNameAlias(jobjIn.optString("nameAlias"));
//						    model.setDeletePlanId(jobjIn.optString("deletePlanId"));
//						    model.setRegistrationDate(jobj.optString("registrationDate"));
//						    model.setStatus(jobjIn.optString("status"));
//						    model.setSchool(jobjIn.optString("school"));
//
//						    model.setAvatar(Constants.baseUrl_Images + jobjIn.optString("avatar"));
//						    JSONObject jobjLastMessage = jobj.getJSONObject("lastMessage");
//
//						    
//						    // G code modified 
//						    String msg = checkForDecryptChat(imagesFolderPath, jobjLastMessage);
//						 
//						    
//						    model.setLastMessage(jsonUtility.parseChat(imagesFolderPath, jobjLastMessage, msg));
//						    HomeScreenList.add(model);
//
//						}
//
//						reorderConversationThreads();
//					    
//					    }
//					    
//					    } catch (JSONException e)
//					    {
//						// TODO Auto-generated catch block
//						 DebugReportOnLocat.e(e);
//					    }catch (NullPointerException e) {
//						 DebugReportOnLocat.e(e);
//					
//					    }
//					}
//				});  
//
//
//    }
//    
    
    public class reorderConversationAsync  extends AsyncTask<Void, String, Void>{

	

	ProgressDialog pDialog;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {
		
		   pDialog = new ProgressDialog(getActivity());
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
	    // TODO Auto-generated method stub
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

	    updateList();
	}
	
	
    }
    
    public class GetHomeDataAsync extends AsyncTask<Void, String, Void>
    {

	ProgressDialog pDialog;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    if (Constants.HomescreenDialog)
	    {
		
	 
	    try
	    {
		if(pDialog!=null){
		    
		    if(pDialog.isShowing()) pDialog.dismiss();
		    
		    pDialog=null;
		}
		
		    pDialog = new ProgressDialog(getActivity());
		    pDialog.setMessage(getResources().getString(R.string.please_wait));
		    pDialog.show();
		    
		
	    } catch (RuntimeException e)
	    {
		DebugReportOnLocat.e(e);
	    }
	    catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }
	  
	    }

	}

	@Override
	protected Void doInBackground(Void... params)
	{

	     DebugReportOnLocat.e("web_service", " FR Constants.NxtAcId  :  " + Constants.NxtAcId);

	    String respStr = null;

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_new + "subscriber/active_conversations");

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {

		nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		DebugReportOnLocat.i("web_service", "GetHomeDataAsync result " + respStr);

	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
	    }

	    try
	    {
		
		if(respStr!=null){
		    

			JSONArray jArray = new JSONArray(respStr);

			JsonParserUtility jsonUtility = new JsonParserUtility();
			
			File imagesFolderPath = mContext.getApplicationContext().getExternalFilesDir("images");
			
			for (int i = 0; i < jArray.length(); i++)
			{
			    JSONObject jobj = jArray.getJSONObject(i);
			    JSONObject jobjIn = jobj.getJSONObject("user");
			    UserModel model = new UserModel();
			    model.setNxtAccountId(jobjIn.optString("nxtAccountId"));
			    model.setNameAlias(jobjIn.optString("nameAlias"));
			    model.setDeletePlanId(jobjIn.optString("deletePlanId"));
			    model.setRegistrationDate(jobj.optString("registrationDate"));
			    model.setStatus(jobjIn.optString("status"));
			    model.setSchool(jobjIn.optString("school"));

			 try
			{
			     
			     if (jobj.optString("avatar").length() > 5){
				    model.setAvatar(Constants.baseUrl_Images + jobjIn.optString("avatar"));
				    	}else{
				    model.setAvatar(null);
				    	}
			     
			    
			} catch (Exception e)
			{
			  DebugReportOnLocat.e(e);
			}
			    
			    JSONObject jobjLastMessage = jobj.getJSONObject("lastMessage");

			    String msg = checkForDecryptChat(jobjLastMessage.optString("id"),imagesFolderPath, jobjLastMessage);
			    // model.setLastMessage(jsonUtility.parseChat(imagesFolderPath,
			    // jobjLastMessage));
			    model.setLastMessage(jsonUtility.parseChat(imagesFolderPath, jobjLastMessage, msg));
			    HomeScreenList.add(model);

			}

			reorderConversationThreads();
		    
		}
	    } catch (JSONException e)
	    {
		// TODO Auto-generated catch block
		 DebugReportOnLocat.e(e);
	    }catch (NullPointerException e) {
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

	    updateList();
	}

    }

    private String checkForDecryptChat(String id,File imagesFolderPath, JSONObject jobj)
    {

	String senderId = "", chatMsg = "";
	
	if(jobj==null) return "";

	if (jobj.optString("body").contains("nonce"))
	{
	     DebugReportOnLocat.e(TAG, "checkForDecryptChat contains nonce");
	    JSONObject jsonObject;
	    try
	    {
		jsonObject = new JSONObject(jobj.optString("body"));
		String nonce = jsonObject.getString("nonce");
		String data = jsonObject.getString("data");

		// new Chat2Activity.decryptAsynk(nonce, data,
		// jobj.optString("senderId"), imagesFolderPath, jobj,
		// comingFrom).execute();

		if (Constants.NxtAcId.equalsIgnoreCase(jobj.optString("senderId")))
		{
		    senderId = jobj.optString("receiverId");
		}
		else
		{
		    senderId = jobj.optString("senderId");
		}

		
		

		if(chatInfoManager.isExists(id)){
		    
		    ChatModel chatModel=chatInfoManager.selectMsg(id);
		    chatMsg=chatModel.getBody();
		    
		}else{
		    
		    chatMsg = decryptMessage(nonce, data, senderId);
		    
		  //  ChatModel chatModel=new ChatModel();
		    
		    JsonParserUtility jsonUtility = new JsonParserUtility();
		    
		    ChatModel chat=   jsonUtility.parseChat(imagesFolderPath, jobj, chatMsg);
		    
		    chatInfoManager.add(chat);
		    
		}
		
		//chatMsg = decryptMessage(nonce, data, senderId);

	    } catch (JSONException e)
	    {
		// TODO Auto-generated catch block
		 DebugReportOnLocat.e(e);
	    }

	}
	else
	{
	    // decryptMsg = jobj.optString("body");
	}
	return chatMsg;
    }

    private static String decryptMessage(String nonce, String data, String senderId)
    {
	
	String decryptMsg = "";
	
	try
	{
	    

		

		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 15000);
		String url = Constants.baseUrl + "?";
		HttpPost httppost = new HttpPost(url);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try
		{

		    String secretPhrase = Constants.sharedPreferences.getString("SecretPhrase", "");
		     DebugReportOnLocat.e(TAG, "decrypt,secretPhrase=>" + secretPhrase);

		    nameValuePairs.add(new BasicNameValuePair("requestType", "decryptFrom"));
		    nameValuePairs.add(new BasicNameValuePair("secretPhrase", secretPhrase));
		    nameValuePairs.add(new BasicNameValuePair("account", senderId));

		     DebugReportOnLocat.e(TAG, "decrypt,nonce=>" + nonce);
		     DebugReportOnLocat.e(TAG, "decrypt,data=>" + data);
		     DebugReportOnLocat.e(TAG, "decrypt,senderId=>" + senderId);

		    // nameValuePairs.add(new BasicNameValuePair("account",
		    // Constants.recipient));
		    nameValuePairs.add(new BasicNameValuePair("data", data));
		    nameValuePairs.add(new BasicNameValuePair("decryptedMessageIsText", "true"));
		    nameValuePairs.add(new BasicNameValuePair("nonce", nonce));

		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    HttpResponse response = httpclient.execute(httppost);
		    String res = EntityUtils.toString(response.getEntity());
		     DebugReportOnLocat.e(TAG, "  responce for decrypt,res-->  " + res);
		    try
		    {
			JSONObject jsonObject = new JSONObject(res);
			if (jsonObject.has("decryptedMessage"))
			{

			    String decryptMsgFromRes = jsonObject.getString("decryptedMessage");

			    decryptMsg = StringEscapeUtils.unescapeJava(decryptMsgFromRes);
			     DebugReportOnLocat.e(TAG, "decrypt msg escape=>" + decryptMsg);
			}
		    } catch (JSONException e)
		    {
			// TODO Auto-generated catch block
			 DebugReportOnLocat.e(e);
			decryptMsg = "";
		    }

		    //  DebugReportOnLocat.e(TAG, "  responce for decrypt-->  " + decryptMsg);

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
	return decryptMsg;
    }

    private void updateList()
    {
	if (homeAdapter != null)
	{
	    homeAdapter.notifyDataSetChanged();
	}
	else
	{
	    homeAdapter = new HomeAdapter(getActivity(), HomeScreenList);
	    myListView.setAdapter(homeAdapter);
	    myListView.setOnItemClickListener(onListItemClick);
	    myListView.setOnItemLongClickListener(onListItemLongClick);
	}
    }

    private OnItemLongClickListener onListItemLongClick = new OnItemLongClickListener()
    {

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
	{
	   //bala code
	    AlertUtility.showConfirmDialog(getActivity(), "Are you sure you want to hide conversation with " + HomeScreenList.get(position).getNameAlias()+ " ?",
		    new DialogInterface.OnClickListener()
		    {

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			   
			    callWSMuteConversation(position);
			}

		    });
	    return true;
	}
    };

    private OnItemClickListener onListItemClick = new OnItemClickListener()
    {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
	   
	    startActivityForResult(
		    new Intent(getActivity(), Chat2Activity.class).putExtra("contactID", HomeScreenList.get(position).getNxtAccountId())
			    .putExtra("Name", HomeScreenList.get(position).getNameAlias()).putExtra("Avtar", HomeScreenList.get(position).getAvatar()), ACTION_CHAT_WINDOW);
	}
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
	if (requestCode == ACTION_CHAT_WINDOW && resultCode == Activity.RESULT_OK)
	{
	    Bundle bundle = data.getExtras();
	    ChatModel chatModel = (ChatModel) bundle.getParcelable("last_message");
	    String contact_id = bundle.getString("contact_id");
	    
	    for (UserModel userModel : HomeScreenList)
	    {
		if (userModel.getNxtAccountId().equalsIgnoreCase(contact_id))
		{
		    userModel.setLastMessage(chatModel);
		}
	    }
	    reorderConversationThreads();

	    updateList();

	}
    };

    public void reorderConversationThreads()
    {
	// TODO Auto-generated method stub
	if (HomeScreenList != null && HomeScreenList.size() > 0)
	{
	    Collections.sort(HomeScreenList, new Comparator<UserModel>()
	    {

		@Override
		public int compare(UserModel lhs, UserModel rhs)
		{
		   
		    return rhs.getLastMessage().getSeenDateLong().compareTo(lhs.getLastMessage().getSeenDateLong());
		}
	    });
	}
    }

    public static HomeFragment newInstance()
    {
	HomeFragment newHome = new HomeFragment();
	return newHome;
    }

    // @Override
    // public void onClick(View v)
    // {
    // switch (v.getId())
    // {
    // case R.id.ln_bottom_contacts_conversation:
    //
    // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
    // new ContactsFragment(), "fragment")
    // .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    //
    // txt_navTitle.setText("Contacts");
    //
    // break;
    // case R.id.ln_bottom_setting_conversation:
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
    // }

    private void callWSMuteConversation(final int position)
    {
	// TODO Auto-generated method stub
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
	nameValuePairs.add(new BasicNameValuePair("contactID", HomeScreenList.get(position).getNxtAccountId()));

	CallPostWebseviceTask task = new CallPostWebseviceTask(getActivity(), true, "mute_conversation", nameValuePairs)
	{
	    @Override
	    protected void onPostExecute(String result)
	    {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		try
		{
		    JSONObject jo = new JSONObject(result);
		    if (jo.getBoolean(Constants.STATUS_WKEY))
		    {
			
			if(HomeScreenList.size()!=position && HomeScreenList.size()>position){
			    
			    HomeScreenList.remove(position);
			}
			
			updateList();
		    }
		    else
		    {
			AlertUtility.showToast(getActivity(), getString(R.string.err_unknown));
		    }

		} catch (Exception e)
		{
		     DebugReportOnLocat.e(e);
		    AlertUtility.showToast(getActivity(), getString(R.string.err_unknown));
		}

	    }
	};

	task.execute();
    }

    public boolean isNetworkConnected()
    {
	ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo ni = cm.getActiveNetworkInfo();
	if (ni == null)
	{
	    // There are no active networks.
	    return false;
	}
	else
	    return true;
    }

    // private String checkForDecryptChat(File imagesFolderPath, JSONObject
    // jobj)
    // {
    //
    // String senderId = "", chatMsg = "";
    //
    // if (jobj.optString("body").contains("nonce"))
    // {
    //  DebugReportOnLocat.e(TAG, "checkForDecryptChat contains nonce");
    // JSONObject jsonObject;
    // try
    // {
    // jsonObject = new JSONObject(jobj.optString("body"));
    // String nonce = jsonObject.getString("nonce");
    // String data = jsonObject.getString("data");
    //
    // // new Chat2Activity.decryptAsynk(nonce, data,
    // // jobj.optString("senderId"), imagesFolderPath, jobj,
    // // comingFrom).execute();
    //
    // if (Constants.NxtAcId.equalsIgnoreCase(jobj.optString("senderId")))
    // {
    // senderId = jobj.optString("receiverId");
    // }
    // else
    // {
    // senderId = jobj.optString("senderId");
    // }
    //
    // chatMsg = decryptMessage(nonce, data, senderId);
    //
    // } catch (JSONException e)
    // {
    // // TODO Auto-generated catch block
    //  DebugReportOnLocat.e(e);
    // }
    //
    // }
    // else
    // {
    // // decryptMsg = jobj.optString("body");
    // }
    // return chatMsg;
    // }
    //
    // private static String decryptMessage(String nonce, String data, String
    // senderId)
    // {
    // String decryptMsg = "";
    //
    // HttpClient httpclient = new DefaultHttpClient();
    // HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 15000);
    // String url = Constants.baseUrl + "?";
    // HttpPost httppost = new HttpPost(url);
    //
    // List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    //
    // try
    // {
    //
    // String secretPhrase =
    // Constants.sharedPreferences.getString("SecretPhrase", "");
    //  DebugReportOnLocat.e(TAG, "decrypt,secretPhrase=>" + secretPhrase);
    //
    // nameValuePairs.add(new BasicNameValuePair("requestType", "decryptFrom"));
    // nameValuePairs.add(new BasicNameValuePair("secretPhrase", secretPhrase));
    // nameValuePairs.add(new BasicNameValuePair("account", senderId));
    //
    //  DebugReportOnLocat.e(TAG, "decrypt,nonce=>" + nonce);
    //  DebugReportOnLocat.e(TAG, "decrypt,data=>" + data);
    //  DebugReportOnLocat.e(TAG, "decrypt,senderId=>" + senderId);
    //
    // // nameValuePairs.add(new BasicNameValuePair("account",
    // // Constants.recipient));
    // nameValuePairs.add(new BasicNameValuePair("data", data));
    // nameValuePairs.add(new BasicNameValuePair("decryptedMessageIsText",
    // "true"));
    // nameValuePairs.add(new BasicNameValuePair("nonce", nonce));
    //
    // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    // HttpResponse response = httpclient.execute(httppost);
    // String res = EntityUtils.toString(response.getEntity());
    //  DebugReportOnLocat.e(TAG, "  responce for decrypt,res-->  " + res);
    // try
    // {
    // JSONObject jsonObject = new JSONObject(res);
    // if (jsonObject.has("decryptedMessage"))
    // {
    //
    // String decryptMsgFromRes = jsonObject.getString("decryptedMessage");
    //
    // decryptMsg = StringEscapeUtils.unescapeJava(decryptMsgFromRes);
    //  DebugReportOnLocat.e(TAG, "decrypt msg escape=>" + decryptMsg);
    // }
    // } catch (JSONException e)
    // {
    // // TODO Auto-generated catch block
    //  DebugReportOnLocat.e(e);
    // decryptMsg = "";
    // }
    //
    // //  DebugReportOnLocat.e(TAG, "  responce for decrypt-->  " + decryptMsg);
    //
    // } catch (ClientProtocolException e)
    // {
    // } catch (IOException e)
    // {
    // }
    // return decryptMsg;
    // }
}
