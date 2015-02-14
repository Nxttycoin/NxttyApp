package com.nxtty.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aniways.Aniways;
import com.flurry.android.FlurryAgent;
import com.nextapp.sinch.SinchClientService;
import com.nextapp.tasks.CallPostWebseviceTask;
import com.nxtapp.fragment.AddGroup;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.fragment.ContactsFragment;
import com.nxtapp.fragment.DiscoverFragment;
import com.nxtapp.fragment.HomeFragment;
import com.nxtapp.fragment.PublicChatFragment;
import com.nxtapp.fragment.SettingFragment;
import com.nxtapp.fragment.WalletFragment;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.CropperImageActivity;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.KeyboardUtility;
import com.nxtapp.utils.StaticUtility;
import com.nxtapp.utils.Utils;
import com.nxtapp.utils.Utils.STORAGE;
import com.nxtapp.utils.WebDataManager;
import com.nxtty.openchat.OpenChatMainFragment;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class Dashboard extends FragmentActivity implements OnClickListener
{
     private static final int ACTION_REQUEST_CAMERA = 1001;
     private static final int ACTION_REQUEST_CROP = 1002;
     private static final int ACTION_REQUEST_GALLERY = 1003;
     
    public static final int SWITCH_FRAGMENT = 1004;
    // public static String ForPublicGroup = "Main";
    String OnCLickOption = "";

    // private String tempImagePath;
    Button btnheaderLast;
    Button btnheaderSearch;
   // Button btnGroupSettins;
    public static ResideMenu resideMenu;
    private static Activity mContext;
    private ResideMenuItem itemHome;
    String profileImagePath;

    Activity activity;
    // private ResideMenuItem itemProfile;
    // private ResideMenuItem itemCalendar;
    String imagePath = "";
    // ImageLoader_Nxtty imageloader_nxtty;
    File profileImage;
    // private static final int PICTURE_RESULT = 0;
    // private static final int SELECT_PICTURE = 1;
 
    

    private ResideMenuItem itemSettings, itemDiscover, itemPublicChat, itemContacts, itemWallet;//,itemGame;
    // private ResideMenuItem itemNewsfeed;
    private Fragment content;
    private boolean started = false;
    private boolean backStackEnabled = true;
    public HashMap<String, Stack<Fragment>> stacks;
    public String currentMenu;

    // private FrameLayout myFrame;

    TextView txt_navTitle;
    public HashMap<String, Fragment> allFragments;

    public WebDataManager mWebDataManager;

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
	super.onSaveInstanceState(outState);

    }

    @Override
    protected void onStart()
    {
	// TODO Auto-generated method stub
	super.onStart();

	FlurryAgent.onStartSession(this, Constants.FlurryKey);

    }

    @Override
    protected void onStop()
    {
	// TODO Auto-generated method stub
	super.onStop();
	FlurryAgent.onEndSession(this);
    }

    public static enum SliderMenu
    {
	CONVERSATION;

	@Override
	public String toString()
	{
	    return super.toString();
	}
    }

    public static enum FragmentAnimation
    {

	SLIDE_UP, SLIDE_DOWN, SLIDE_RIGHT, SLIDE_LEFT
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {

	boolean IsOpen = false;
	if (resideMenu.isOpened())
	{
//	    resideMenu.setShadowAdjustScaleXByOrientation();
	    KeyboardUtility.hideKeypad(Dashboard.this);
	    IsOpen = true;
	    File background = StaticUtility.getResideMenuBackgroundImagePath(Dashboard.this);
	    if (background != null && background.isFile())
	    {
//		resideMenu.setBackgroundUri(Uri.fromFile(background));
	    }
	    else
	    {
		resideMenu.setBackground(R.drawable.menu_background_new);
	    }
	    resideMenu.closeMenu();
	}
	super.onConfigurationChanged(newConfig);

	if (IsOpen)
	    resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
	File background = StaticUtility.getResideMenuBackgroundImagePath(mContext);
//	if (background != null && background.isFile())
//	{
//	    KeyboardUtility.hideKeypad(mContext);
//	    resideMenu.setBackgroundUri(Uri.fromFile(background));
//	}
//	else
//	{
	    resideMenu.setBackground(R.drawable.menu_background_new);
//	}

    }

    @Override
    protected void onCreate(Bundle arg0)
    {
	// TODO Auto-generated method stub
	super.onCreate(arg0);
	// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	Aniways.init(this);

	Constants.ISinChatScreen = false;
	//getting unique id for device
			String id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			
			
			DebugReportOnLocat.ln(" id>>>"+id);
			
			  final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
			    final String tmDevice, tmSerial, androidId;
			    tmDevice = "" + tm.getDeviceId();
			    tmSerial = "" + tm.getSimSerialNumber();
			    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
			    String deviceId = deviceUuid.toString();
			    
			    DebugReportOnLocat.ln(" id>>> ** "+deviceId);
			    
	setContentView(R.layout.dashboard);
	
	getDeviceId();
	
	mContext = this;
	activity = this;
	mWebDataManager = new WebDataManager(Dashboard.this);
	try
	{

	    setUpMenu();

	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}

	txt_navTitle = (TextView) findViewById(R.id.textview_navigationTitle);

	callWSGetSubscriberDetail();
	startApp();

	SharedPreferences pref = getSharedPreferences("DeviceType", 0);
	String DeviceType = pref.getString("update_deviceType", "NO");
	
	/*if (DeviceType.equalsIgnoreCase("NO"))
	{
*/
	    DebugReportOnLocat.e("########", "  Result :  INSIDE IF  ");

	    SharedPreferences.Editor edt_notifications = pref.edit();
	    edt_notifications.putString("update_deviceType", "YES");
	    edt_notifications.commit();

	    
	   
	   

	//}

	DebugReportOnLocat.e("########", "  RegistationKeyGCM   :" + Constants.RegistationKeyGCM);
	if (!Constants.RegistationKeyGCM.equalsIgnoreCase("") || Constants.RegistationKeyGCM != null)
	{
	    DebugReportOnLocat.e("########", "  Result :  INSIDE IF  FOR REG ID " + Constants.RegistationKeyGCM);
	    
	    WebCallUpdateDeviceDetails();
	  //  WebCallUpdateDeviceType();
	 //   WebCallRegistrationID();
	    
	    new DoInBackRegKey().execute();

	}

	FlurryAgent.onStartSession(mContext, Constants.FlurryKey);

	FlurryAgent.logEvent("Dashboard viewed");

	FlurryAgent.setUserId(Constants.NxtAcId);
	FlurryAgent.setLogEnabled(true);

	FlurryAgent.setContinueSessionMillis(5 * 10000);

	// changeFragment(new HomeFragment());
    }

    public void startService()
    {
	try
	{

	    if (Constants.AliasName == null || Constants.AliasName.equals(""))
	    {
		// AlertUtility.showToast(mContext, "Sinch not started!");
		return;
	    }
	    if (CurrentCall.mSinchClientService != null && CurrentCall.mSinchClientService.isStarted())
	    {
		// AlertUtility.showToast(mContext, "Sinch is already running");
		return;
	    }
	    CurrentCall.mSinchClientService = new SinchClientService();
	    CurrentCall.mSinchClientService.start(Dashboard.this, Constants.AliasName);
	    // AlertUtility.showToast(mContext, "Sinch is started!");

	} catch (ExceptionInInitializerError e)
	{
	    DebugReportOnLocat.e(e);
	}
	catch (UnsatisfiedLinkError e)
	{
	     DebugReportOnLocat.e(e);
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}

    }

    //
    // public void callFromSinch(String contactId)
    // {
    // CallClient callClient =
    // CurrentCall.mSinchClientService.getSinchClient().getCallClient();
    // Call call = callClient.callUser(contactId);
    // CurrentCall.currentCall = call;
    // startActivity(new Intent(this, CallScreenActivity.class));
    // }

    
    
   /* public static void stopSinchService()
    {

	try
	{

	    if (CurrentCall.mSinchClientService != null)
	    {
		if (CurrentCall.mSinchClientService.isStarted())
		{

		    CurrentCall.mSinchClientService.stop();
		    CurrentCall.mSinchClientService = null;
		    CurrentCall.currentCall = null;
		}

	    }
	    FlurryAgent.onEndSession(mContext);

	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
    }*/

    public void startApp()
    {
	started = true;
	allFragments = new HashMap<String, Fragment>();
	stacks = new HashMap<String, Stack<Fragment>>();
	for (SliderMenu menu : SliderMenu.values())
	{
	    stacks.put(menu.toString(), new Stack<Fragment>());
	    // Toast.makeText(MainActivity.this,
	    // "Menu : "+menu.toString(),Toast.LENGTH_LONG).show();
	}
	HomeFragment homeFrag = HomeFragment.newInstance();
	 txt_navTitle.setText(R.string.open_chat);
	/*
	 * SASignInFragment signInFragment = SASignInFragment.newInstance();
	 * MyHubFragment hubFragment = MyHubFragment.newInstance();
	 * TermsAndConditionFregment settingsFragment =
	 * TermsAndConditionFregment.newInstance(); AboutFragment
	 * friendsFragment = AboutFragment.newInstance(); MessageFragment
	 * messageFragment = MessageFragment.newInstance(); HistoryFragment
	 * notificationFragment = HistoryFragment.newInstance();
	 * MyBookingsFregment myGameFragment = MyBookingsFregment.newInstance();
	 * MyProfileFragment myProfileFragment =
	 * MyProfileFragment.newInstance(); LeaderBoardFragment
	 * leaderBoardFragment = LeaderBoardFragment.newInstance();
	 */
	allFragments.put(SliderMenu.CONVERSATION.toString() + homeFrag.getClass().getName().toString(), homeFrag);
	// menuFragment = SAMenuFragment.newInstance();
	int switchToFragment = 1;
	switch (switchToFragment)
	{
	case 1:
	    Constants.HomescreenDialog = false;
	    currentMenu = SliderMenu.CONVERSATION.toString();
	    content = allFragments.get(SliderMenu.CONVERSATION.toString() + homeFrag.getClass().getName().toString());
	    break;
	case 2:
	    /*
	     * currentMenu = SliderMenu.PROPOSALLIST.toString(); content =
	     * allFragments.get(SliderMenu.PROPOSALLIST.toString() +
	     * proposalListFragment.getClass().getName().toString());
	     */
	default:

	    break;
	}
	stacks.get(currentMenu).push(content);
	getSupportFragmentManager().beginTransaction().replace(R.id.fragment, content).commit();

    }

    @SuppressWarnings("unused")
    private void pushFragments(String tag, Fragment fragment, boolean shouldAdd, FragmentAnimation anim)
    {
	if (shouldAdd)
	{
	    stacks.get(tag).push(fragment);
	}
	try
	{
	    FragmentManager manager = getSupportFragmentManager();
	    FragmentTransaction ft = manager.beginTransaction();
	    // ft.setCustomAnimations(android.R.anim.fade_in,
	    // android.R.anim.fade_out);

	    if (anim == FragmentAnimation.SLIDE_LEFT)
	    {
		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left);
	    }
	    if (anim == FragmentAnimation.SLIDE_RIGHT)
	    {
		ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right);
	    }

	    Fragment oldFrag = manager.findFragmentById(R.id.fragment);
	    if (oldFrag != null)
	    {
		ft.remove(oldFrag);
	    }
	    ft.add(R.id.fragment, fragment);
	    ft.commit();

	} catch (Exception e)
	{
	    DebugReportOnLocat.e("######", "pushFragment() error", e);

	}

    }

    // @Override
    // public void onBackPressed()
    // {
    // /*
    // * if (!resideMenu.isOpened()) {
    // * resideMenu.openMenu(ResideMenu.DIRECTION_LEFT); } else {
    // *
    // * finish();
    // *
    // *
    // * }
    // */
    //
    // Log.d("CDA", "onBackPressed Called");
    // Intent setIntent = new Intent(Intent.ACTION_MAIN);
    // setIntent.addCategory(Intent.CATEGORY_HOME);
    // setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // startActivity(setIntent);
    //
    // }

    @Override
    public void onBackPressed()
    {
	try
	{
	    if(mContext!=null)
	    hideSoftKeyboard(mContext);
	    
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
	
	// resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
	if (!resideMenu.isOpened())
	{
	    resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
	}
	else
	{
	    SharedPreferences pref = mContext.getSharedPreferences("ID", 0);
	    

	/*    if(false){
		
		  SharedPreferences.Editor edt_notifications = pref.edit();
		    edt_notifications.putString("nxtAcId", "0");
		    edt_notifications.commit();
		    
	    }
	  */
	    
	    finish();

	}
    }

    public void popFragments()
    {

	try
	{

	    /*
	     * Select the second last fragment in current tab's stack.. which
	     * will be shown after the fragment transaction given below
	     */
	    Fragment fragment = stacks.get(currentMenu).elementAt(stacks.get(currentMenu).size() - 2);
	    // Fragment fragment = stacks.get(currentMenu).lastElement();
	    /* pop current fragment from stack.. */
	    stacks.get(currentMenu).pop();
	    /*
	     * We have the target fragment in hand.. Just show it.. Show a
	     * standard navigation animation
	     */
	    FragmentManager manager = getSupportFragmentManager();
	    FragmentTransaction ft = manager.beginTransaction();

	    /*
	     * int enter, int exit, int popEnter, int popExit
	     */
	    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right);

	    Fragment oldFrag = manager.findFragmentById(R.id.fragment);
	    if (oldFrag != null)
	    {
		ft.remove(oldFrag);
	    }

	    ft.add(R.id.fragment, fragment);
	    ft.commit();

	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
    }

    private void setUpMenu()
    {

	resideMenu = new ResideMenu(this);
	File background = StaticUtility.getResideMenuBackgroundImagePath(mContext);
//	if (background != null && background.isFile())
//	{
//	    resideMenu.setBackgroundUri(Uri.fromFile(background));
//	}
//	else
//	{
	    resideMenu.setBackground(R.drawable.menu_background_new);
//	}

	resideMenu.attachToActivity(this);
	resideMenu.setMenuListener(menuListener);
	// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
	// 150dip.
	resideMenu.setScaleValue(0.6f);
	// resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);
	resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
	resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
//	resideMenu.getChangeBackgroundButton().setImageResource(R.drawable.pick_image_ic);
//
//	resideMenu.getChangeBackgroundButton().setOnClickListener(new OnClickListener()
//	{
//	    @Override
//	    public void onClick(View v)
//	    {
//		// TODO Auto-generated method stub
//		openChangeBGOptionsDialog();
//	    }
//	});
	
	
	

	// create menu items;
	itemHome = new ResideMenuItem(this, R.drawable.conversation, R.string.conversations);
	itemContacts = new ResideMenuItem(this, R.drawable.public_chat, R.string.contacts);
	// itemNewsfeed = new ResideMenuItem(this, R.drawable.newsfeed,
	// R.string.newsfeed);
	itemDiscover = new ResideMenuItem(this, R.drawable.discover, R.string.find_users);
	itemPublicChat = new ResideMenuItem(this, R.drawable.groupchaticon, R.string.open_chat);
	itemWallet = new ResideMenuItem(this, R.drawable.wallet_icon, R.string.wallet);
	itemSettings = new ResideMenuItem(this, R.drawable.setting, R.string.settings);

	//itemGame= new ResideMenuItem(this, R.drawable.app_frozen_bubble, R.string.game);
	
	resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
	resideMenu.addMenuItem(itemPublicChat, ResideMenu.DIRECTION_LEFT);
	resideMenu.addMenuItem(itemContacts, ResideMenu.DIRECTION_LEFT);
	// resideMenu.addMenuItem(itemNewsfeed, ResideMenu.DIRECTION_LEFT);
	resideMenu.addMenuItem(itemDiscover, ResideMenu.DIRECTION_LEFT);
	resideMenu.addMenuItem(itemWallet, ResideMenu.DIRECTION_LEFT);
	//resideMenu.addMenuItem(itemGame, ResideMenu.DIRECTION_LEFT);
	resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);
	
	

	itemHome.setOnClickListener(this);
	itemContacts.setOnClickListener(this);
	// itemNewsfeed.setOnClickListener(this);
	itemDiscover.setOnClickListener(this);
	itemPublicChat.setOnClickListener(this);
	itemSettings.setOnClickListener(this);
	itemWallet.setOnClickListener(this);
	//itemGame.setOnClickListener(this);
	

	if ((findViewById(R.id.ln_titleBack) instanceof LinearLayout))
	{

	    findViewById(R.id.ln_titleBack).setOnTouchListener(new OnTouchListener()
	    {

		public boolean onTouch(View v, MotionEvent event)
		{
		    hideSoftKeyboard(mContext);
		    return false;
		}

	    });
	}
	findViewById(R.id.ln_titleBack).setOnClickListener(new View.OnClickListener()
	{
	    @Override
	    public void onClick(View view)
	    {

		resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);

	    }
	});
	
//	btnGroupSettins = (Button) findViewById(R.id.btn_Settings_Group);
//	btnGroupSettins.setVisibility(View.GONE);
//	btnGroupSettins.setOnClickListener(new OnClickListener()
//	{
//	    @Override
//	    public void onClick(View v)
//	    {
//		startActivity(new Intent(Dashboard.this, GroupSettingsActivity.class));
//	    }
//	});

	btnheaderLast = (Button) findViewById(R.id.btn_Settings_head);
	btnheaderLast.setVisibility(View.GONE);
	btnheaderLast.setOnClickListener(new View.OnClickListener()
	{
	    @Override
	    public void onClick(View view)
	    {
		if (OnCLickOption.equalsIgnoreCase("Public Chat"))
		{
		    startActivity(new Intent(Dashboard.this, AddGroup.class));
		    // if (!PublicChatFragment.IsSubscribeVisible)
		}
		else
		{
		    btnheaderLast.setVisibility(View.GONE);
		    btnheaderSearch.setVisibility(View.GONE);
		    changeFragment(new SettingFragment());
		    txt_navTitle.setText(R.string.settings);
		}
	    }
	});

	btnheaderSearch = (Button) findViewById(R.id.btn_search_head);
	btnheaderSearch.setVisibility(View.GONE);
	btnheaderSearch.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {
		btnheaderLast.setVisibility(View.GONE);
		btnheaderSearch.setVisibility(View.GONE);
		changeFragment(new DiscoverFragment());
		txt_navTitle.setText(R.string.discover);

	    }
	});

    }

    public static void hideSoftKeyboard(Activity activity)
    {

	try
	{
	    if (activity == null)
		return;

	    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    
	    if(activity.getCurrentFocus()==null)return;
	    
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

	} catch (NullPointerException e)
	{
	    DebugReportOnLocat.e(e);
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
    }

    public int stackSize()
    {
	if (stacks.get(currentMenu) != null)
	{
	    return stacks.get(currentMenu).size();
	}
	return 0;
    }

    public boolean isBackStackEnabled()
    {
	return backStackEnabled;
    }

    public void enableBackStack()
    {
	this.backStackEnabled = true;
    }

    public void disableBackStack()
    {
	this.backStackEnabled = false;
    }

    @Override
    protected void onResumeFragments()
    {

	super.onResumeFragments();

	try
	{
            if(activity!=null)
	    KeyboardUtility.hideKeypad(activity);

	} catch (NullPointerException e)
	{
	     DebugReportOnLocat.e(e);
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}

    }

    @Override
    protected void onResume()
    {
	// TODO Auto-generated method stub
	super.onResume();
	DebugReportOnLocat.e("", "onResume==>");
	try
	{

	    if (getIntent().getExtras() != null)
	    {

		String comingFromNoti = getIntent().getExtras().getString("comingFromNoti", "false");

		if (comingFromNoti.equalsIgnoreCase("true"))
		{
		    DebugReportOnLocat.e("", "onResume==>" + comingFromNoti);
		    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		    DebugReportOnLocat.e("", "notificationId==>" + SplashScreen.notificationId);
		    notificationManager.cancel(SplashScreen.notificationId);
		    IncomingCallScreenActivity.numMessages = -1;
		    // IncomingCallScreenActivity.notificationId ++;
		    SplashScreen.notificationId = (int) System.currentTimeMillis();
		}

	    }

	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}

	if (started)
	{
	    return;
	}
	// animation = FragmentAnimation.PUSH_DOWN_IN.ordinal();

	startApp();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
	return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view)
    {

	btnheaderLast.setVisibility(View.GONE);
	btnheaderSearch.setVisibility(View.GONE);
	//btnGroupSettins.setVisibility(View.GONE);
	if (view == itemHome)
	{
	    changeFragment(new HomeFragment());
	}
	else if (view == itemSettings)
	{
	    changeFragment(new SettingFragment());
	    txt_navTitle.setText(R.string.settings);
	}
	else if (view == itemDiscover)
	{
	    changeFragment(new DiscoverFragment());
	    txt_navTitle.setText(R.string.find_users);
	}
	// else if (view == itemNewsfeed)
	// {
	// changeFragment(new NewsFeedFragment());
	// txt_navTitle.setText(R.string.newsfeed);
	// }
	else if (view == itemPublicChat)
	{
	   // btnGroupSettins.setVisibility(View.VISIBLE);
	    
	    PublicChatFragment.isFlag=true;
	    btnheaderLast.setVisibility(View.INVISIBLE);
	 //   btnheaderLast.setBackgroundResource(R.drawable.add_white_header);
	    OnCLickOption = "Public Chat";
	    changeFragment(new OpenChatMainFragment());
	    txt_navTitle.setText(R.string.open_chat);
	    
	    //Current live
	  //  changeFragment(new PublicChatFragment());
	  //  txt_navTitle.setText(R.string.open_chat);
	}
	else if (view == itemContacts)
	{
	    OnCLickOption = "Contacts";
	    btnheaderSearch.setVisibility(View.VISIBLE);
	    // btnheaderLast.setVisibility(View.VISIBLE);
	    // btnheaderLast.setBackgroundResource(R.drawable.settings_topbar);
	    changeFragment(new ContactsFragment());
	    txt_navTitle.setText(R.string.contacts);
	}
	else if (view == itemWallet)
	{
	    changeFragment(new WalletFragment());
	    txt_navTitle.setText(R.string.wallet);
	}
	/*else if(view ==itemGame){
	    
	    Intent intent=new Intent(activity,HomeScreen.class);
	    intent.putExtra("playerName", Constants.AliasName);
	    intent.putExtra("playerId", Constants.NxtAcId);
	    
	    
	    startActivity(intent);
	    
	}*/

	resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener()
    {
	@Override
	public void openMenu()
	{
	    // Toast.makeText(mContext, "Menu is opened!",
	    // Toast.LENGTH_SHORT).show();
	}

	@Override
	public void closeMenu()
	{
	    // Toast.makeText(mContext, "Menu is closed!",
	    // Toast.LENGTH_SHORT).show();
	}
    };

    private void changeFragment(Fragment targetFragment)
    {
	resideMenu.clearIgnoredViewList();
	getSupportFragmentManager().beginTransaction().replace(R.id.fragment, targetFragment, "fragment").setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu()
    {
	return resideMenu;
    }

    private void callWSGetSubscriberDetail()
    {
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
	   nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));//G
	   
	CallPostWebseviceTask task = new CallPostWebseviceTask(Dashboard.this, false, "get_subscriber", nameValuePairs)
	{
	    @Override
	    protected void onPostExecute(String result)
	    {

		super.onPostExecute(result);
		try
		{

		    // G code
		    if (result != null)
		    {
			DebugReportOnLocat.ln(result);

			if(result.trim().length()>0){
			    
			    JSONObject jo = new JSONObject(result);
				Constants.AliasName = jo.getString("nameAlias");

				
				
				Constants.ProfImageBitmapSmall = BitmapUtility.decodeBitmapFromBase64(jo.getString("avatar"), 100, 100);

				
				try
				{
				    SharedPreferences.Editor editor = getSharedPreferences("nxttyPlayerName", Context.MODE_PRIVATE).edit();
				    editor.putString("name", Constants.AliasName);
				    editor.commit();
				    
				} catch (Exception e)
				{
				    // TODO: handle exception
				}
				try
				{
				    
				    startService();
				    
				} catch (ExceptionInInitializerError e)
				{
				    DebugReportOnLocat.e(e);
				}
				
			    
			}
			

		    }

		} catch (JSONException e)
		{

		    e.printStackTrace();
		} catch (Exception e)
		{

		    DebugReportOnLocat.e(e);
		    
		}
	    }
	};

	try
	{
	    if (task != null)
		task.execute();
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}

    }

    private void WebCallUpdateDeviceDetails()
    {
	
	
	/*java.lang.String subscriberID,
        java.lang.String deviceID,
        java.lang.String deviceType,
        java.lang.String key*/
        
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("subscriberID", Constants.NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("deviceType", "Android"));
	nameValuePairs.add(new BasicNameValuePair("deviceID", Constants.RegistationKeyGCM));
	
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));//G
	
	 DebugReportOnLocat.ln("subscriberID >> "+ Constants.NxtAcId);
	 DebugReportOnLocat.ln("deviceType >> "+ "Android");
	 DebugReportOnLocat.ln("key >> "+ Constants.ParamKey);
	
	CallPostWebseviceTask task = new CallPostWebseviceTask(Dashboard.this, false, "update_device_details", nameValuePairs)
	{
	    @Override
	    protected void onPostExecute(String result)
	    {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		try
		{
		    DebugReportOnLocat.e("########", "  Result :   " + result);

		} catch (Exception e)
		{
		    // TODO Auto-generated catch block
		     DebugReportOnLocat.e(e);
		}
	    }
	};
	task.execute();
    }

    
    
    private void WebCallUpdateDeviceTypeOLD()
    {
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("subscriberID", Constants.NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("deviceType", "Android"));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));//G
	
	 DebugReportOnLocat.ln("subscriberID >> "+ Constants.NxtAcId);
	 DebugReportOnLocat.ln("deviceType >> "+ "Android");
	 DebugReportOnLocat.ln("key >> "+ Constants.ParamKey);
	
	CallPostWebseviceTask task = new CallPostWebseviceTask(Dashboard.this, false, "update_device_type", nameValuePairs)
	{
	    @Override
	    protected void onPostExecute(String result)
	    {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		try
		{
		    DebugReportOnLocat.e("########", "  Result :   " + result);

		} catch (Exception e)
		{
		    // TODO Auto-generated catch block
		     DebugReportOnLocat.e(e);
		}
	    }
	};
	task.execute();
    }

    
    private void WebCallRegistrationIDOLD()
    {
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("subscriberID", Constants.NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("appleDeviceID", Constants.RegistationKeyGCM));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));//G
	
	CallPostWebseviceTask task = new CallPostWebseviceTask(Dashboard.this, false, "update_apple_device_id", nameValuePairs)
	{
	    @Override
	    protected void onPostExecute(String result)
	    {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		try
		{
		    DebugReportOnLocat.e("########", "  Result for Reg ID:   " + result);

		} catch (Exception e)
		{
		    // TODO Auto-generated catch block
		     DebugReportOnLocat.e(e);
		}
	    }
	};
	task.execute();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void openChangeBGOptionsDialog()
    {
	final Dialog dialog = new Dialog(Dashboard.this);
	// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.setContentView(R.layout.list_dialog);
	dialog.setTitle("Change Background");
	ListView lv = (ListView) dialog.findViewById(R.id.dialog_list);
	int resArrayId = 0;
	File background = StaticUtility.getResideMenuBackgroundImagePath(mContext);
	if (background != null && background.isFile())
	{
	    resArrayId = R.array.options_with_default;
	}
	else
	{
	    resArrayId = R.array.uploadpicoption;
	}
	lv.setAdapter(new ArrayAdapter(Dashboard.this, R.layout.row, getResources().getStringArray(resArrayId)));
	lv.setOnItemClickListener(new OnItemClickListener()
	{

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	    {
		// TODO Auto-generated method stub
		dialog.dismiss();
		if (arg2 == 0)
		{
		    initCamera();
		}
		else if (arg2 == 1)
		{
		    initGallery();
		}
		else if (arg2 == 2)
		{
		    StaticUtility.getResideMenuBackgroundImagePath(mContext).delete();
		    resideMenu.setBackground(R.drawable.menu_background_new);
		}
	    }
	});
	dialog.show();

    }

    @SuppressLint("NewApi")
    public static float rotationForImage(String imagePath)
    {
	try
	{
	    ExifInterface exif = new ExifInterface(imagePath);
	    int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
	    return rotation;
	} catch (IOException e)
	{
	    // Log.e(TAG, "Error checking exif", e);
	}
	return 0f;
    }

    private static float exifOrientationToDegrees(int exifOrientation)
    {
	if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
	{
	    return 90;
	}
	else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
	{
	    return 180;
	}
	else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
	{
	    return 270;
	}
	return 0;
    }

    public String getPath(Uri uri)
    {

	String StringPath;
	String[] projection = { MediaStore.Images.Media.DATA };
	Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
	if (cursor != null)
	{

	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();

	    StringPath = cursor.getString(column_index);
	}
	else
	{
	    StringPath = null;
	}

	if (StringPath == null)
	{
	    StringPath = uri.getPath();
	}
	else
	{
	    return StringPath;
	}

	if (StringPath == null)
	{
	    return null;
	}
	else
	{
	    return StringPath;
	}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
	
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode == Activity.RESULT_OK)
	{
	    switch (requestCode)
	    {

	    case ACTION_REQUEST_GALLERY:
		try
		{
		    Uri mImageUri = data.getData();
		    STORAGE available_storage = Utils.getStorageWithFreeSpace(Dashboard.this);
		    File folder = new File(Utils.getRootPath(Dashboard.this, available_storage));
		    if (!folder.isDirectory())
		    {
			folder.mkdir();
		    }
		    File imageFile = new File(Utils.getImagePath(Dashboard.this, available_storage, true));
		    if (!imageFile.exists())
		    {
			imageFile.createNewFile();
		    }

		    if (imageFile.exists())
		    {
			Utils.getImagePathFromURI(Dashboard.this, mImageUri, imageFile);

			profileImagePath = imageFile.getPath();
			startActivityForResult(new Intent(Dashboard.this, CropperImageActivity.class).putExtra("Path", profileImagePath), ACTION_REQUEST_CROP);
		    }
		    else
		    {
			Toast.makeText(Dashboard.this, getResources().getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
		    }

		} catch (Exception e)
		{
		    Toast.makeText(Dashboard.this, getResources().getString(R.string.image_invalid), Toast.LENGTH_LONG).show();
		     DebugReportOnLocat.e(e);
		}

		break;
	    case ACTION_REQUEST_CAMERA:
		try
		{
		    startActivityForResult(new Intent(Dashboard.this, CropperImageActivity.class).putExtra("Path", profileImagePath), ACTION_REQUEST_CROP);

		} catch (Exception e)
		{
		}

		break;
	 
	    
		
	    case ACTION_REQUEST_CROP:

		String path = data.getStringExtra("picture_path");
		if (path != null && !path.equals(""))
		{
		    
		    try
		    {

//			    resideMenu.setBackgroundUri(Uri.parse(path));
			    changeWalletFragmentBackground(Uri.parse(path));
			    File f = StaticUtility.getResideMenuBackgroundImagePath(Dashboard.this);
			    new File(path).renameTo(f);
			
		    } catch (Exception e)
		    {
			 DebugReportOnLocat.e(e);
		    }
		}
		break;
	    }
	}
    }

/*    private void changeWalletFragmentBackground(Uri backgroungUri)
    {
	WalletFragment walletFragment = (WalletFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

	if (walletFragment != null)
	{
	    walletFragment.changeWalletBackground(backgroungUri);
	}
    }*/
    
    
    
//    public String getPath(Uri uri)
//    {
//	String[] projection = { MediaStore.Images.Media.DATA };
//	@SuppressWarnings("deprecation")
//	Cursor cursor = managedQuery(uri, projection, null, null, null);
//	if (cursor != null)
//	{
//	    // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
//	    // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
//	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//	    cursor.moveToFirst();
//	    return cursor.getString(column_index);
//	}
//	else
//	    return null;
//    }
    
    private void changeWalletFragmentBackground(Uri backgroungUri)
    {
	Fragment fragmentF =  getSupportFragmentManager().findFragmentById(R.id.fragment);

	if (fragmentF instanceof WalletFragment)
	{
	    ((WalletFragment) fragmentF).changeWalletBackground(backgroungUri);
	}
    }

    private void initGallery()
    {
	startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), ACTION_REQUEST_GALLERY);
    }

    private void initCamera()
    {

	STORAGE availableStorage = Utils.getStorageWithFreeSpace(Dashboard.this);
	String rootPath = Utils.getRootPath(Dashboard.this, availableStorage);

	File folder = new File(rootPath);

	if (!folder.isDirectory())
	{
	    folder.mkdir();
	}

	File fileCamera = new File(Utils.getImagePath(Dashboard.this, availableStorage, true));
	profileImagePath = fileCamera.getPath();
	Log.d("log_tag", "uri: " + profileImagePath);

	if (!fileCamera.exists())
	    try
	    {
		fileCamera.createNewFile();
	    } catch (IOException e)
	    {
		 DebugReportOnLocat.e(e);
	    }

	Uri mImageUri = Uri.fromFile(fileCamera);

	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
	startActivityForResult(intent, ACTION_REQUEST_CAMERA);
    }

    
    
    
    public void getDeviceId(){
	
	
	String m_szDevIDShort = "35" + //we make this look like a valid IMEI
	            Build.BOARD.length()%10+ Build.BRAND.length()%10 +
	            Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
	            Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
	            Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
	            Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
	            Build.TAGS.length()%10 + Build.TYPE.length()%10 +
	            Build.USER.length()%10 ; //13 digits
	
	
	
	final TelephonyManager tm = (TelephonyManager) getBaseContext()
	        .getSystemService(Dashboard.TELEPHONY_SERVICE);
	final String tmDevice, tmSerial, androidId;
	tmDevice = "" + tm.getDeviceId();
	Log.v("DeviceIMEI", "" + tmDevice);
	tmSerial = "" + tm.getSimSerialNumber();
	Log.v("GSM devices Serial Number[simcard] ", "" + tmSerial);
	androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),
	        android.provider.Settings.Secure.ANDROID_ID);
	Log.v("androidId CDMA devices", "" + androidId);
	UUID deviceUuid = new UUID(androidId.hashCode(),
	        ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
	String deviceId = deviceUuid.toString();
	Log.v("deviceIdUUID universally unique identifier", "" + deviceId);
	String deviceModelName = android.os.Build.MODEL;
	Log.v("Model Name", "" + deviceModelName);
	String deviceUSER = android.os.Build.USER;
	Log.v("Name USER", "" + deviceUSER);
	String devicePRODUCT = android.os.Build.PRODUCT;
	Log.v("PRODUCT", "" + devicePRODUCT);
	String deviceHARDWARE = android.os.Build.HARDWARE;
	Log.v("HARDWARE", "" + deviceHARDWARE);
	String deviceBRAND = android.os.Build.BRAND;
	Log.v("BRAND", "" + deviceBRAND);
	String myVersion = android.os.Build.VERSION.RELEASE;
	Log.v("VERSION.RELEASE", "" + myVersion);
	int sdkVersion = android.os.Build.VERSION.SDK_INT;
	Log.v("VERSION.SDK_INT", "" + sdkVersion);
	
    }
    
    
    
    
    
    
    
    public class DoInBackRegKey extends AsyncTask<Void, String, Void>
    {
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
		SharedPreferences pref = activity.getSharedPreferences("ID", 0);
		Constants.NxtAcId = pref.getString("nxtAcId", "0");

		DebugReportOnLocat.e("#########", " NxtAcId  :  " + Constants.NxtAcId);
		DebugReportOnLocat.e("#########", " RegistationKeyGCM  :  " + Constants.RegistationKeyGCM);
		String respStr = null;

		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
		HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "subscriber/update_subscriber_settings");

		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("nxtID", new StringBody(Constants.NxtAcId));
		//reqEntity.addPart("file", new FileBody(new File("")));
		reqEntity.addPart("deviceID", new StringBody(Constants.RegistationKeyGCM));
		reqEntity.addPart("deviceType", new StringBody("Android"));
		reqEntity.addPart("key", new StringBody(Constants.ParamKey));

		httppost.setEntity(reqEntity);
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		DebugReportOnLocat.e("##############", "  responce from Save CHanges  -->  " + respStr);

	    }catch (NullPointerException e)
	    {
		DebugReportOnLocat.e(e);
	    }catch (RuntimeException e)
	    {
		DebugReportOnLocat.e(e);
	    } 
	     catch (ClientProtocolException e)
	    {
		DebugReportOnLocat.e(e);
	    } catch (IOException e)
	    {
		DebugReportOnLocat.e(e);
	    }

	    return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
	    super.onPostExecute(result);
	}
    }

  
}
