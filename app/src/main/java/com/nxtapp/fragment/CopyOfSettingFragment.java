package com.nxtapp.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.nextapp.tasks.CallPostWebseviceTaskSetting;
import com.nextapp.tasks.DecryptImageAsynk;
import com.nxtapp.adapter.CityAutoCompleteAdapter;
import com.nxtapp.adapter.SchoolUniAutoCompleteAdapter;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.CropperImageActivity;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.KeyboardUtility;
import com.nxtapp.utils.Network;
import com.nxtapp.utils.Utils;
import com.nxtapp.utils.Utils.STORAGE;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpClient;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;
import com.nxtty.main.CommonShare;
import com.nxtty.main.Dashboard;
import com.nxtty.main.MainActivity;
import com.nxtty.main.R;

public class CopyOfSettingFragment extends BaseFragment implements OnClickListener
{
    private static final int ACTION_REQUEST_CAMERA = 1001;
    private static final int ACTION_REQUEST_CROP = 1002;
    private static final int ACTION_REQUEST_GALLERY = 1003;
    String profileImagePath;

    private String tempImagePath, imagePath;
    RelativeLayout rlSupport;
    private String[] gender = { "Male", "Female" };
    private String[] TextSize = { "12", "13", "14", "15", "16", "17", "18", "19", "20" };
    private Spinner genderOption, TextOption;
    private Button Logout;
    private SharedPreferences pref;
    @SuppressWarnings("unused")
    private TextView txt_navTitle, txt_username, version_name;
    private EditText edtStatus;
    private AutoCompleteTextView edtSchool, edtCity;
    // private LinearLayout lnContacts, lnMessage;
    private ImageView ivProfilePic, ivToggleNotification, ivToggleAutomaticlogout;

    private SharedPreferences mNotiPref;

    private RadioButton[] radioDeletePlans;

/*    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Uri fileUri;*/
    
    int textSizeOfSpinner;    
    Context mContext;

    private void setSelecteDeletePlans(int position)
    {
	radioDeletePlans[position].setChecked(true);
    }

    private int getSelectedDeletePlanPosition()
    {
	for (int i = 0; i < radioDeletePlans.length; i++)
	{
	    if (radioDeletePlans[i].isChecked())
	    {
		return i;
	    }
	}
	return 0;
    }
    
    @Override
    public void onStart()
    {
	// TODO Auto-generated method stub
	super.onStart();

	try
	{
	    FlurryAgent.onStartSession(getActivity(), Constants.FlurryKey);
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
	

    }

    @Override
    public void onStop()
    {
	// TODO Auto-generated method stub
	super.onStop();
	try
	{
	    FlurryAgent.onEndSession(getActivity());
	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}
	
    }
    

    private String blockCharacterSet = "~!@#$%^&*()_+=-{}[]<>?/:;\"\"₱£₤₵¥°c/o™®©π√Δ∏";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub

	View view = inflater.inflate(R.layout.setting, container, false);

	initObjects(view);

	mContext=getActivity();
	// bala code
	getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

	// getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	ivToggleNotification.setSelected(mNotiPref.getBoolean("noty_enabled", true));
	SharedPreferences preferences_user = getActivity().getSharedPreferences("USER_DATA", 0);
	// Boolean isAutoLogout
	// =preferences_user.getBoolean("isAutoLogout",false);

	ivToggleAutomaticlogout.setSelected(preferences_user.getBoolean("isAutoLogout", false));
	// Bala Code
	if (Network.isNetworkConnected(getActivity().getApplicationContext()))
	{
	    callWSGetSubscriberDetail();
	}
	else
	{
	    AlertUtility.showToast(getActivity().getApplicationContext(), getString(R.string.networkIssues));
	}

	return view;
    }

    private void initObjects(View view)
    {
	// TODO Auto-generated method stub
	// ivMessage = (ImageView) view.findViewById(R.id.iv_message);
	// ivMessage.setBackgroundResource(R.drawable.message);
	// ivContact = (ImageView) view.findViewById(R.id.iv_contact);
	// ivContact.setBackgroundResource(R.drawable.contacts);
	// ivSettings = (ImageView) view.findViewById(R.id.iv_settings);
	// ivSettings.setBackgroundResource(R.drawable.settings_bottom_selector);

/*	imageLoader = ImageLoader.getInstance();
	options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY).showImageOnLoading(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.loading).showImageOnFail(R.drawable.loading).build();*/

	rlSupport = (RelativeLayout) view.findViewById(R.id.rl_setting_support);
	rlSupport.setOnClickListener(this);

	edtSchool = (AutoCompleteTextView) view.findViewById(R.id.edtSchool);

	edtSchool.setThreshold(1);
	edtSchool.setAdapter(new SchoolUniAutoCompleteAdapter(getActivity(), R.layout.list_item));
	edtSchool.setOnItemClickListener(new OnItemClickListener()
	{
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	    {
		edtSchool.setSelection(0);
	    }
	});

	edtCity = (AutoCompleteTextView) view.findViewById(R.id.edtCity);

	edtCity.setThreshold(1);
	edtCity.setAdapter(new CityAutoCompleteAdapter(getActivity(), R.layout.list_item));

	edtCity.setOnItemClickListener(new OnItemClickListener()
	{

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	    {
		edtCity.setSelection(0);
	    }
	});

	version_name = (TextView) view.findViewById(R.id.version_name);
	PackageInfo packageInfo;
	try
	{
	    packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
	    String version = packageInfo.versionName;
	    Log.e("", "version=>" + version);
	    version_name.setText("v" + version);
	} catch (NameNotFoundException e)
	{
	    // TODO Auto-generated catch block
	     DebugReportOnLocat.e(e);
	}

	edtStatus = (EditText) view.findViewById(R.id.edtStatus);

	ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
	ivProfilePic.setOnClickListener(this);

	ivToggleNotification = (ImageView) view.findViewById(R.id.ivToggleNotification);
	ivToggleNotification.setOnClickListener(this);

	ivToggleAutomaticlogout = (ImageView) view.findViewById(R.id.ivToggleautomaticlogout);
	ivToggleAutomaticlogout.setOnClickListener(this);

	genderOption = (Spinner) view.findViewById(R.id.spinnerstate);
	ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, gender);
	adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	genderOption.setAdapter(adapter_state);

	TextOption = (Spinner) view.findViewById(R.id.spinnerTextSize);
	ArrayAdapter<String> TextAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, TextSize);
	TextAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	TextOption.setAdapter(TextAdapter);
	
	SharedPreferences prefs = getActivity().getSharedPreferences("TextSize", 0);
	int prefInt = prefs.getInt("Size", 0);
	if(prefInt != 0)
	{
	   Constants.TextSize = prefInt;
	}
	
	TextOption.setSelection(Constants.TextSize - 12);

	TextOption.setOnItemSelectedListener(new OnItemSelectedListener()
	{
	    @Override
	    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	    {
		//Constants.TextSize = Integer.parseInt(TextOption.getSelectedItem().toString());
		textSizeOfSpinner = Integer.parseInt(TextOption.getSelectedItem().toString());
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> parent)
	    {

	    }
	});

	txt_navTitle = (TextView) getActivity().findViewById(R.id.textview_navigationTitle);
	// txt_navTitle.setText("Settings");

	RadioGroup radioGroupDeletePlan = (RadioGroup) view.findViewById(R.id.radioGroupDeletePlan);
	int count = radioGroupDeletePlan.getChildCount();

	radioDeletePlans = new RadioButton[count];
	for (int i = 0; i < count; i++)
	{
	    radioDeletePlans[i] = (RadioButton) radioGroupDeletePlan.getChildAt(i);
	}

	txt_username = (TextView) view.findViewById(R.id.txt_username);

	pref = getActivity().getSharedPreferences("ID", 0);

	mNotiPref = getActivity().getSharedPreferences("noty_settings", 0);

	// lnContacts = (LinearLayout)
	// view.findViewById(R.id.ln_bottom_contacts);
	// lnContacts.setOnClickListener(this);
	// lnMessage = (LinearLayout) view.findViewById(R.id.ln_bottom_message);
	// lnMessage.setOnClickListener(this);

	Logout = (Button) view.findViewById(R.id.button_logout);
	Logout.setOnClickListener(this);

	view.findViewById(R.id.button_savechanges).setOnClickListener(this);
	view.findViewById(R.id.rlBlockList).setOnClickListener(this);
	
	
	
	view.findViewById(R.id.rl_Recommend_to_Friend).setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
		    
		    try
		    {
			FlurryAgent.onStartSession(mContext, Constants.FlurryKey);
			

			FlurryAgent.logEvent("Share button");

			FlurryAgent.setUserId(Constants.NxtAcId);
			FlurryAgent.setLogEnabled(true);

			//FlurryAgent.setContinueSessionMillis(5 * 10000);
			
		    } catch (Exception e)
		    {
			DebugReportOnLocat.e(e);
		    }
		    
		    CommonShare commonShare=new CommonShare();
		    commonShare.share(mContext);
	    

		}

	});
    }

    public static CopyOfSettingFragment newInstance()
    {
	CopyOfSettingFragment fragment = new CopyOfSettingFragment();
	return fragment;

    }

    @Override
    public void onClick(View v)
    {
	switch (v.getId())
	{
	case R.id.button_logout:
	    SharedPreferences.Editor edt_notifications = pref.edit();
	    edt_notifications.putString("nxtAcId", "0");
	    edt_notifications.commit();

	    try
	    {
		// StaticUtility.getResideMenuBackgroundImagePath(getActivity().getApplicationContext()).delete();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e); 
		}
	    
	   // Dashboard.stopSinchService();

	    startActivity(new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    getActivity().finish();
	    break;

	case R.id.ivToggleNotification:
	    ivToggleNotification.setSelected(!ivToggleNotification.isSelected());
	    break;

	case R.id.ivToggleautomaticlogout:
	    ivToggleAutomaticlogout.setSelected(!ivToggleAutomaticlogout.isSelected());
	    break;

	case R.id.button_savechanges:
	    try
	    {
//		if (edtCity.getText().toString().trim().length() == 0)
//		    AlertUtility.showToast(getActivity().getApplicationContext(), getString(R.string.validate_city));
//		else if (edtSchool.getText().toString().length() == 0)
//		    AlertUtility.showToast(getActivity().getApplicationContext(), getString(R.string.validate_school_univercity));
//		else
//		{
		KeyboardUtility.hideKeypad(getActivity());
		
		  if (imagePath != null && !imagePath.equals(""))
		    {
			    try
			    {
				Bitmap bitmap = null;
				
				File f=new File(imagePath);
				
				bitmap = decodeFile(f);
				
						
				String message = "";
				if (bitmap != null)
				{
				    
				    try
				    {
					// Image to Hex binary string conversion
					byte[] bytevalue=getBytesFromBitmap(bitmap);
					
					 message=new String(Hex.encodeHex(bytevalue));
					 
				    }catch (Throwable e) {
				
					 DebugReportOnLocat.e(e);
					
					}
				    				  			   
				   
				new encryptAsynk(message, true).execute();
				}
				
				
				
				
			    } catch (Exception e)
			    {

				 DebugReportOnLocat.e(e);
				AlertUtility.showToast(mContext, getString(R.string.err_unknown));
			    }
			    
		    
		    }else{
			
			 callWSUpdateSubscriberDetail("");
			
		    }
		   
		    SharedPreferences.Editor editor = getActivity().getSharedPreferences("TextSize", 0).edit();
		    editor.putInt("Size", textSizeOfSpinner);
		    editor.commit();
		    SharedPreferences preferences;
		    preferences = getActivity().getSharedPreferences("USER_DATA", 0);
		    SharedPreferences.Editor edt = preferences.edit();

		    if (ivToggleAutomaticlogout.isSelected())
		    {

			edt.putBoolean("isAutoLogout", true);
			edt.commit();
		    }
		    else
		    {
			edt.putBoolean("isAutoLogout", false);
			edt.commit();
		    }
		//}
	    } catch (Exception e)
	    {
		// TODO: handle exception
		 DebugReportOnLocat.e(e);
	    }

	    break;
	case R.id.ivProfilePic:
	    openUploadoption();
	    break;
	case R.id.rlBlockList:
	    getActivity().startActivity(new Intent(getActivity(), BlockListActivity.class));
	    break;
	case R.id.rl_setting_support:

	    // new SupportAsync().execute();

	    if (Network.isNetworkConnected(getActivity().getApplicationContext()))
	    {

		Support();
	    }
	    else
	    {

		AlertUtility.showToast(getActivity().getApplicationContext(), getString(R.string.networkIssues));
	    }

	    break;
	}
    }

    private void openUploadoption()
    {

	final Dialog dialog = new Dialog(getActivity());
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.setContentView(R.layout.list_dialog);
	ListView lv = (ListView) dialog.findViewById(R.id.dialog_list);
	lv.setAdapter(new ArrayAdapter(getActivity(), R.layout.row, getResources().getStringArray(R.array.uploadpicoption)));
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
	    }
	});
	dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode == Activity.RESULT_OK)
	{
	    switch (requestCode)
	    {
	    case ACTION_REQUEST_GALLERY:
		try
		{
		    Uri mImageUri = data.getData();
		    STORAGE available_storage = Utils.getStorageWithFreeSpace(getActivity());
		    File folder = new File(Utils.getRootPath(getActivity(), available_storage));
		    if (!folder.isDirectory())
		    {
			folder.mkdir();
		    }
		    File imageFile = new File(Utils.getImagePath(getActivity(), available_storage, true));
		    if (!imageFile.exists())
		    {
			imageFile.createNewFile();
		    }

		    if (imageFile.exists())
		    {
			Utils.getImagePathFromURI(getActivity(), mImageUri, imageFile);

			profileImagePath = imageFile.getPath();
			startActivityForResult(new Intent(getActivity(), CropperImageActivity.class).putExtra("Path", profileImagePath), ACTION_REQUEST_CROP);
		    }
		    else
		    {
			Toast.makeText(getActivity(), getResources().getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
		    }

		} catch (Exception e)
		{
		    Toast.makeText(getActivity(), getResources().getString(R.string.image_invalid), Toast.LENGTH_LONG).show();
		     DebugReportOnLocat.e(e);
		}

		break;
	    case ACTION_REQUEST_CAMERA:
		try
		{
		    startActivityForResult(new Intent(getActivity(), CropperImageActivity.class).putExtra("Path", profileImagePath), ACTION_REQUEST_CROP);

		} catch (Exception e)
		{
		}

		break;
	    case ACTION_REQUEST_CROP:
		imagePath = data.getStringExtra("picture_path");
		if (imagePath != null && !imagePath.equals(""))
		{
		    ivProfilePic.requestFocus();
		    ivProfilePic.setImageURI(Uri.parse(imagePath));
		}
		break;
	    }
	}
	else if (requestCode == ACTION_REQUEST_CROP)
	{
	    imagePath = tempImagePath;
	    if (imagePath != null && !imagePath.equals(""))
	    {
		ivProfilePic.requestFocus();
		ivProfilePic.setImageURI(Uri.parse(imagePath));
	    }
	}
    }

    // Before Deprecation of managed Query
    public static String getRealPathFromURI(Uri contentUri, Activity mContext)
    {

	String StringPath;
	DebugReportOnLocat.e("Installed Path", " " + contentUri.getPath().toString());
	String[] proj = { MediaStore.Images.Media.DATA };
	@SuppressWarnings("deprecation")
	Cursor cursor = mContext.managedQuery(contentUri, proj, null, null, null);
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
	    StringPath = contentUri.getPath();
	}

	if (StringPath.equals(""))
	{
	    return null;
	}
	else
	{
	    return StringPath;
	}
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     * 
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @author paulburke
     */

   
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri)
    {

	final boolean isKitKat = Build.VERSION.SDK_INT >= 19;

	// DocumentProvider
	if (isKitKat )
	{
	    
	    if(DocumentsContract.isDocumentUri(context, uri)){
		
		
		  // ExternalStorageProvider
		    if (isExternalStorageDocument(uri))
		    {
			final String docId = DocumentsContract.getDocumentId(uri);
			final String[] split = docId.split(":");
			final String type = split[0];

			if ("primary".equalsIgnoreCase(type))
			{
			    return Environment.getExternalStorageDirectory() + "/" + split[1];
			}

			// TODO handle non-primary volumes
		    }
		    // DownloadsProvider
		    else if (isDownloadsDocument(uri))
		    {

			final String id = DocumentsContract.getDocumentId(uri);
			final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

			return getDataColumn(context, contentUri, null, null);
		    }
		    // MediaProvider
		    else if (isMediaDocument(uri))
		    {
			final String docId = DocumentsContract.getDocumentId(uri);
			final String[] split = docId.split(":");
			final String type = split[0];

			Uri contentUri = null;
			if ("image".equals(type))
			{
			    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			}
			else if ("video".equals(type))
			{
			    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
			}
			else if ("audio".equals(type))
			{
			    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			}

			final String selection = "_id=?";
			final String[] selectionArgs = new String[] { split[1] };

			return getDataColumn(context, contentUri, selection, selectionArgs);
		    }
		    
	    }// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme()))
		{
		    return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme()))
		{
		    return uri.getPath();
		}
	  
	}
	// MediaStore (and general)
	else if ("content".equalsIgnoreCase(uri.getScheme()))
	{
	    return getDataColumn(context, uri, null, null);
	}
	// File
	else if ("file".equalsIgnoreCase(uri.getScheme()))
	{
	    return uri.getPath();
	}

	return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     * 
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs)
    {

	Cursor cursor = null;
	final String column = "_data";
	final String[] projection = { column };

	try
	{
	    cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
	    if (cursor != null && cursor.moveToFirst())
	    {
		final int column_index = cursor.getColumnIndexOrThrow(column);
		return cursor.getString(column_index);
	    }
	} finally
	{
	    if (cursor != null)
		cursor.close();
	}
	return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri)
    {
	return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri)
    {
	return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri)
    {
	return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void callWSGetSubscriberDetail()
    {

	try
	{

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
	    nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));//G
		
	    // CallPostWebseviceTask task = new
	    // CallPostWebseviceTask(getActivity(), true, "get_subscriber",
	    // nameValuePairs)
	    CallPostWebseviceTaskSetting task = new CallPostWebseviceTaskSetting(getActivity(), true, "get_subscriber", nameValuePairs)
	    {
		@Override
		protected void onPostExecute(String result)
		{
		    // TODO Auto-generated method stub
		    super.onPostExecute(result);
		    try
		    {
			
			DebugReportOnLocat.ln(" >>>>>> "+result);
			
			JSONObject jo = new JSONObject(result);

			String text_user = jo.getString("nameAlias");
			text_user = text_user.replaceAll(" ", "\u00A0");
			txt_username.setText(text_user);

			if(jo.getInt("deletePlanId") >0){
			    setSelecteDeletePlans(jo.getInt("deletePlanId") - 1);
			}
			
			String gender = jo.getString("gender");
			if (gender != null && gender.equalsIgnoreCase("female"))
			{
			    genderOption.setSelection(1);
			}
			else
			{
			    genderOption.setSelection(0);
			}
			String city = jo.getString("city");
			if (city != null && !city.equalsIgnoreCase("null"))
			{
			    edtCity.setText(city);
			}
			String school = jo.getString("school");
			if (school != null && !school.equalsIgnoreCase("null"))
			{
			    edtSchool.setText(school);
			}

			String unicodedMsg = jo.getString("status");
			String status = StringEscapeUtils.unescapeJava(unicodedMsg);
			// Log.e("SettingFragment", "status=>" + status);
			if (status != null && !status.equalsIgnoreCase("null"))
			{
			    edtStatus.setText(status);
			}
			String avtar = jo.getString("avatar");
			
			DecryptImageAsynk decryptAsynk=new DecryptImageAsynk(mContext,avtar){
			    
			    protected void onPostExecute(String result) {
				
				 if (result != null && !result.equalsIgnoreCase("null"))
					{
					
					  try
					    {
						
						 Bitmap bitmap = BitmapFactory.decodeFile(result);
						 ivProfilePic.setImageBitmap(bitmap);
						    
						    
					    } catch (OutOfMemoryError e)
					    {
						// TODO: handle exception
					    }catch (Exception e)
					    {
						// TODO: handle exception
					    }
					  
					   // imageLoader.displayImage(Constants.baseUrl_Images + avtar, ivProfilePic, options);
					}else{
					    
					    
					}
				    
				}
			    };
			
			    decryptAsynk.execute();
			
			 //new SettingFragment.decryptAsynk(avtar ).execute();
			 
			
		    } catch (Exception e)
		    {
			// TODO Auto-generated catch block
			 DebugReportOnLocat.e(e);
		    }
		}
	    };
	    task.execute();

	} catch (NullPointerException e)
	{
	     DebugReportOnLocat.e(e);
	} catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}
    }
    
    
    
//    public class decryptAsynk extends AsyncTask<String, String, String>
//    {
//	
//	String avatar="";
//
//	public decryptAsynk(String avtar)
//	{
//	    this.avatar=avtar;
//	}
//
//	/*public decryptAsynk(){
//	    
//	    
//	}*/
//
//	@Override
//	protected String doInBackground(String... params)
//	{
//	    
//	    
//	  String imageFilePath=  imageCheck(avatar);
//	    
//	    return imageFilePath;
//	    
//	}
//	
//	  
//	@Override
//	protected void onPostExecute(String result)
//	{
//	    // TODO Auto-generated method stub
//	    super.onPostExecute(result);
//	    
//	    if (result != null && !result.equalsIgnoreCase("null"))
//		{
//		
//		  try
//		    {
//			
//			 Bitmap bitmap = BitmapFactory.decodeFile(result);
//			 ivProfilePic.setImageBitmap(bitmap);
//			    
//			    
//		    } catch (OutOfMemoryError e)
//		    {
//			// TODO: handle exception
//		    }catch (Exception e)
//		    {
//			// TODO: handle exception
//		    }
//		  
//		   // imageLoader.displayImage(Constants.baseUrl_Images + avtar, ivProfilePic, options);
//		}else{
//		    
//		    
//		}
//	    
//	}
//    }
//    public String imageCheck(String imagePath){
//	
//	String nonce = "", data = "";
//	
//	
//	try{
//	
//	
//	String json = "";
//	JSONObject jObj = null;
//	
//	
//	 File imagesFolderPath = mContext.getExternalFilesDir("images");
//	    
//	    String filePath=imagesFolderPath.getAbsolutePath()+"/"
//			+imagePath ;
//	    
//	    DebugReportOnLocat.ln(" filePath >>> "+filePath);
//	    
//	    File file = new File(filePath);
//
//	
//	DebugReportOnLocat.ln(" file  "+file.getName());
//	
//	
//	if (file.exists()) {
//		
//		DebugReportOnLocat.ln(" File is exist ");
//		
//		return filePath;
//		
//	}else{
//		
//	    InputStream is = null;
//
//	    DefaultHttpClient httpClient = new DefaultHttpClient();
//            // String paramString = URLEncodedUtils.format(params, "utf-8");
//           //  url += "?" + paramString;
//             HttpGet httpGet = new HttpGet(Constants.baseUrl_Images +imagePath);
//
//             HttpResponse httpResponse = httpClient.execute(httpGet);
//             HttpEntity httpEntity = httpResponse.getEntity();
//             is = httpEntity.getContent();
//         
//             try {
//                 BufferedReader reader = new BufferedReader(new InputStreamReader(
//                         is, "iso-8859-1"), 8);
//                 StringBuilder sb = new StringBuilder();
//                 String line = null;
//                 while ((line = reader.readLine()) != null) {
//                     sb.append(line + "\n");
//                 }
//                 is.close();
//                 json = sb.toString();
//             } catch (Exception e) {
//                 DebugReportOnLocat.e("Buffer Error", "Error converting result " + e.toString());
//             }
//      
//             // try parse the string to a JSON object
//             try {
//                 jObj = new JSONObject(json);
//             } catch (JSONException e) {
//                 DebugReportOnLocat.e("JSON Parser", "Error parsing data " + e.toString());
//             }
//             
//             
//         nonce = jObj.getString("nonce");
//         data = jObj.getString("data");
//         
//       String  chatMsg = decryptMessage(nonce, data, Constants.NxtAcId);
//       
//          byte[] bytevalue;
//		
//		    bytevalue = Hex.decodeHex(chatMsg.toCharArray());
//		    
//		
//         writeToFile(filePath,bytevalue);
//         
//         
//         return filePath;
//         
//	}
//         
//     }catch(IllegalStateException e){
//         
//         DebugReportOnLocat.e(e);
//     }catch (Exception e) {
//         DebugReportOnLocat.e(e);
//	}
//	
//	return "null";
//    }
//    
//    

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

    private void callWSUpdateSubscriberDetail(String avatarContent) throws Exception
    {

	try
	{

	    MultipartEntity entityBuilder = new MultipartEntity();
	    // entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    
	    entityBuilder.addPart("key", new StringBody(Constants.ParamKey));
	    entityBuilder.addPart("nxtID", new StringBody(Constants.NxtAcId));
	    entityBuilder.addPart("deletePlanID", new StringBody("" + (getSelectedDeletePlanPosition() + 1)));

	    String unicodeString = convertToUnicodeEscaped(edtStatus.getText().toString().trim());
	    // Log.e("SettingFragment", "unicodeString=>" + unicodeString);
	    entityBuilder.addPart("status", new StringBody(unicodeString));
	    entityBuilder.addPart("city", new StringBody(edtCity.getText().toString().trim()));
	    entityBuilder.addPart("school", new StringBody(edtSchool.getText().toString().trim()));
	    entityBuilder.addPart("gender", new StringBody(genderOption.getSelectedItemPosition() == 0 ? "male" : "female"));

	    if (imagePath != null && !imagePath.equals("") && !avatarContent.equals(""))
	    {
		
		 byte[] byteData = avatarContent.getBytes();
		       
		        ByteArrayBody byteArrayBody = new ByteArrayBody(byteData, "image"); // second parameter is the name of the image (//TODO HOW DO I MAKE IT USE THE IMAGE FILENAME?)

		        entityBuilder.addPart("file", byteArrayBody);
		        
		        	File f = null;
		try
		{

		    f = new File(getActivity().getExternalCacheDir(), "tempsend.png");
		    if (f.isFile())
		    {
			f.delete();
		    }

		    f.createNewFile();
		    FileOutputStream fo = new FileOutputStream(f);
		    fo.write(byteData);
		    fo.close();
		} catch (IOException e)
		{

		     DebugReportOnLocat.e(e);
		}
		
		entityBuilder.addPart("file", new FileBody(f));
		
		//DebugReportOnLocat.e("Inside If =>", " Inside This");
		//entityBuilder.addPart("file", new FileBody(new File(imagePath)));
	    }
	    
	    
	    entityBuilder.addPart("deviceType", new StringBody("Android"));
	    entityBuilder.addPart("appleDeviceID", new StringBody(Constants.RegistationKeyGCM));
	    // CallPostWebseviceTask task = new
	    // CallPostWebseviceTask(getActivity(), true,
	    // "update_subscriber_settings", entityBuilder)
	    CallPostWebseviceTaskSetting task = new CallPostWebseviceTaskSetting(getActivity(), true, "update_subscriber_settings", entityBuilder)
	    {
		@Override
		protected void onPostExecute(String result)
		{
		    // TODO Auto-generated method stub
		    super.onPostExecute(result);

		    try
		    {
			// G code
			if (result != null)
			{

			    JSONObject jo = new JSONObject(result);
			    if (jo.getBoolean(Constants.STATUS_WKEY))
			    {
				mNotiPref.edit().putBoolean("noty_enabled", ivToggleNotification.isSelected()).commit();
				AlertUtility.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.update_success));
				Constants.TextSize = textSizeOfSpinner;
			    }
			    else
			    {
				AlertUtility.showToast(getActivity().getApplicationContext(), jo.getString(Constants.ERROR_DETAIL_WKEY));
			    }

			}
			else
			{

			    AlertUtility.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.networkIssues));

			}

		    } catch (Exception e)
		    {

			 DebugReportOnLocat.e(e);

			try
			{

			    AlertUtility.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_unknown));

			} catch (NullPointerException e2)
			{
			    DebugReportOnLocat.e(e2);
			}

		    }

		}
	    };
	    task.execute();

	} catch (IllegalStateException e)
	{
	     DebugReportOnLocat.e(e);

	} catch (Exception e)
	{

	     DebugReportOnLocat.e(e);
	}

    }

    boolean IsSubScribed = false;

    ProgressDialog pDialog = null;

    public void Support()
    {

	try
	{

	    pDialog = new ProgressDialog(getActivity());
	    pDialog.setMessage(getResources().getString(R.string.please_wait));
	    pDialog.show();

	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}

	AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.GROUPS_CHAT_KEY);
	req.setBody(JSONBody.publicChat(Constants.NxtAcId));
	req.setUserInfo(HttpUri.GROUPS_CHAT_KEY);
	// final CategoryManager cManager = new CategoryManager(this);
	new AsyncHttpClient().executeJSONObject(getActivity().getApplicationContext(), req, new ResultCallback()
	{

	    @Override
	    public void onCompleted(Exception e, String responseString, String methodInfo)
	    {

		GetContacts(responseString);

		try
		{
		    if (pDialog != null)
			pDialog.dismiss();

		} catch (Exception e2)
		{
		    e2.printStackTrace();
		}

		if (IsSubScribed)
		{
		    
		 try
		{
		    getActivity().finish();
		    
		} catch (Exception e2)
		{
		   DebugReportOnLocat.e(e);
		}
		    startActivity(new Intent(getActivity(), GroupDetailActivity.class).putExtra("gID", "6").putExtra("Name", "Support"));
		}
		else
		{
		    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		    alertDialog.setMessage(getActivity().getResources().getString(R.string.sure_subscribe) + " " + getActivity().getResources().getString(R.string.support) + " ?");
		    alertDialog.setPositiveButton(getActivity().getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
		    {
			public void onClick(DialogInterface dialog, int which)
			{
			    dialog.cancel();
			    
			    new JoinAsync().execute();
			    
			    //new JoinAsyncSettings().execute();
			}
		    });
		    alertDialog.setNegativeButton(getActivity().getResources().getString(R.string.no), new DialogInterface.OnClickListener()
		    {
			public void onClick(DialogInterface dialog, int which)
			{
			    dialog.cancel();
			}
		    });
		    alertDialog.show();
		}

	    }

	    private void GetContacts(String respStr)
	    {
		try
		{

		    if (respStr != null)
		    {

			JSONArray jArray = new JSONArray(respStr);
			for (int i = 0; i < jArray.length(); i++)
			{

			    JSONObject jobj = jArray.getJSONObject(i);
			    if (jobj.optString("title").equalsIgnoreCase("Support"))
			    {
				IsSubScribed = true;
			    }

			}

		    }
		} catch (JSONException e)
		{
		     DebugReportOnLocat.e(e);
		}
	    }
	});

    }

    /*
     * public class SupportAsync extends AsyncTask<Void, String, Void> {
     * 
     * ProgressDialog pDialog; boolean IsSubScribed = false;
     * 
     * @Override protected void onPreExecute() { super.onPreExecute();
     * 
     * pDialog = new ProgressDialog(getActivity());
     * pDialog.setMessage(getResources().getString(R.string.please_wait));
     * pDialog.show();
     * 
     * }
     * 
     * @Override protected Void doInBackground(Void... params) {
     * 
     * String respStr = null;
     * 
     * HttpClient httpclient = new DefaultHttpClient();
     * HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
     * HttpPost httppost = new HttpPost(Constants.baseUrl_Group +
     * "groups/user_groups");
     * 
     * List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
     * 
     * try { nameValuePairs.add(new BasicNameValuePair("nxtID",
     * Constants.NxtAcId)); httppost.setEntity(new
     * UrlEncodedFormEntity(nameValuePairs)); HttpResponse response =
     * httpclient.execute(httppost); respStr =
     * EntityUtils.toString(response.getEntity());
     * 
     * } catch (ClientProtocolException e) { } catch (IOException e) { }
     * 
     * try { JSONArray jArray = new JSONArray(respStr); for (int i = 0; i <
     * jArray.length(); i++) {
     * 
     * JSONObject jobj = jArray.getJSONObject(i); if
     * (jobj.optString("title").equalsIgnoreCase("Support")) { IsSubScribed =
     * true; }
     * 
     * }
     * 
     * } catch (JSONException e) {  DebugReportOnLocat.e(e); }
     * 
     * return null; }
     * 
     * @Override protected void onPostExecute(Void result) {
     * super.onPostExecute(result);
     * 
     * pDialog.dismiss(); if (IsSubScribed) { startActivity(new
     * Intent(getActivity(), GroupDetailActivity.class).putExtra("gID",
     * "6").putExtra("Name", "Support")); } else { AlertDialog.Builder
     * alertDialog = new AlertDialog.Builder(getActivity());
     * alertDialog.setMessage
     * (getActivity().getResources().getString(R.string.sure_subscribe) + " " +
     * getActivity().getResources().getString(R.string.support) + " ?");
     * alertDialog
     * .setPositiveButton(getActivity().getResources().getString(R.string.yes),
     * new DialogInterface.OnClickListener() { public void
     * onClick(DialogInterface dialog, int which) { dialog.cancel(); new
     * JoinAsyncSettings().execute(); } });
     * alertDialog.setNegativeButton(getActivity
     * ().getResources().getString(R.string.no), new
     * DialogInterface.OnClickListener() { public void onClick(DialogInterface
     * dialog, int which) { dialog.cancel(); } }); alertDialog.show(); }
     * 
     * }
     * 
     * }
     */
    public class JoinAsyncSettings extends AsyncTask<Void, String, Void>
    {

	ProgressDialog pDialog;
	String respStr = null;
	int pos;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    DebugReportOnLocat.e("##########", "  Came Here ");

	    try
	    {
		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage(getActivity().getString(R.string.please_wait));
		pDialog.show();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }

	}

	@SuppressWarnings("unused")
	@Override
	protected Void doInBackground(Void... params)
	{

	    
	    
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "subscriber/subscribe");

	    DebugReportOnLocat.e("##############", "  Constants.NxtAcId -->  " + Constants.NxtAcId);

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {
		nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		nameValuePairs.add(new BasicNameValuePair("name_alias", Constants.AliasName));
		nameValuePairs.add(new BasicNameValuePair("appleDeviceID", Constants.RegistationKeyGCM));// device id blank update
		nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost);
		respStr = EntityUtils.toString(response.getEntity());

		DebugReportOnLocat.e("##############", "  responce from post subscribe -->  " + respStr);

	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
	    }

	    
	    
//	    HttpClient httpclient = new DefaultHttpClient();
//	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
//	    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "subscriber/subscribe");
//
//	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//
//	    try
//	    {
//		nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
//		nameValuePairs.add(new BasicNameValuePair("name_alias", Constants.AliasName));
//		nameValuePairs.add(new BasicNameValuePair("appleDeviceID", Constants.RegistationKeyGCM));
//		nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
//		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//		HttpResponse response = httpclient.execute(httppost);
//		respStr = EntityUtils.toString(response.getEntity());
//
//		DebugReportOnLocat.ln("  response subscripe ->> " + response);
//
//	    } catch (ClientProtocolException e)
//	    {
//		
//		DebugReportOnLocat.e(e);
//		
//	    } catch (IOException e)
//	    {
//		DebugReportOnLocat.e(e);
//	    }
	    
	    

	   /* HttpClient httpclient1 = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient1.getParams(), 10000);
	    HttpPost httppost1 = new HttpPost(Constants.baseUrl_Group + "groups/join");
	    
	  //  HttpPost httppost1 = new HttpPost(Constants.baseUrl_Group + "groups/join");

	    List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();

	    try
	    {
		nameValuePairs1.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		nameValuePairs1.add(new BasicNameValuePair("groupID", "6"));
		nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
		
		httppost1.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
		HttpResponse response = httpclient1.execute(httppost1);
		respStr = EntityUtils.toString(response.getEntity());

		DebugReportOnLocat.e("##########", "  Came Here " + respStr);

	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
	    }*/

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

	    } catch (Exception err)
	    {
		DebugReportOnLocat.e(err);

	    }
	    ;
	    if (respStr != null)
	    {
		try
		{
		    JSONObject jobj = new JSONObject(respStr);
		    if (jobj.optString("status").equalsIgnoreCase("true"))
		    {
			startActivity(new Intent(getActivity(), GroupDetailActivity.class).putExtra("gID", "6").putExtra("Name", "Support"));
		    }
		} catch (JSONException e)
		{
		     DebugReportOnLocat.e(e);
		}
	    }

	}

    }

    private void initGallery()
    {

	Intent intent = new Intent();

	intent.setType("image/*");
	intent.setAction(Intent.ACTION_GET_CONTENT);

	startActivityForResult(Intent.createChooser(intent, "Complete action using"), ACTION_REQUEST_GALLERY);

	// startActivityForResult(new Intent(Intent.ACTION_PICK,
	// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
	// Constants.ACTION_REQUEST_GALLERY);
    }

    private void initCamera()
    {

	try
	{

	    STORAGE availableStorage = Utils.getStorageWithFreeSpace(getActivity());
	    String rootPath = Utils.getRootPath(getActivity(), availableStorage);

	    File folder = new File(rootPath);

	    if (!folder.isDirectory())
	    {
		folder.mkdir();
	    }

	    File fileCamera = new File(Utils.getImagePath(getActivity(), availableStorage, true));
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

	} catch (ActivityNotFoundException e)
	{
	     DebugReportOnLocat.e(e);
	}

    }
    
    
    
    public class JoinAsync extends AsyncTask<Void, String, Void>
    {

	ProgressDialog pDialog;
	String respStr = null;
	
	

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {

		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage(getActivity().getString(R.string.please_wait));
		pDialog.show();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }

	}

	@Override
	protected Void doInBackground(Void... params)
	{

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/join");

	    DebugReportOnLocat.e("##############", "  Constants.NxtAcId -->  " + Constants.NxtAcId);

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {

		
		    nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		    nameValuePairs.add(new BasicNameValuePair("groupID","6"));
		    nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    HttpResponse response = httpclient.execute(httppost);
		    respStr = EntityUtils.toString(response.getEntity());

		

		DebugReportOnLocat.e("##############", "  responce from post subscribe -->  " + respStr);

	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
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
	    if (respStr != null)
	    {
		try
		{
		    JSONObject jobj = new JSONObject(respStr);
		    if (jobj.optString("status").equalsIgnoreCase("true"))
		    {
			try
			{
			   // getActivity().finish();
			    
			} catch (Exception e)
			{
			    // TODO: handle exception
			}
			startActivity(new Intent(getActivity(), GroupDetailActivity.class).putExtra("gID", "6").putExtra("Name", "Support"));
		    }
		
		} catch (JSONException e)
		{
		     DebugReportOnLocat.e(e);
		} catch (IndexOutOfBoundsException e)
		{
		     DebugReportOnLocat.e(e);
		}
	    }

	}

    }
    
    
    //////////////////////////////////Avatar encryption
    
    
    private class encryptAsynk extends AsyncTask<String, String, String>
    {

	String msg = "";
	boolean isFile;

	private encryptAsynk(String message, boolean b)
	{
	    msg = message;
	    isFile = b;
	}

	@Override
	protected void onPreExecute()
	{

	    super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params)
	{
	    
	
		
		
		    String response = encryptMessage(msg, isFile);
		    
		    DebugReportOnLocat.ln(" encryptMessage response "+response);
		    return response;
		    
	   
	
	}

	@Override
	protected void onPostExecute(String result)
	{

	    super.onPostExecute(result);

	    if (result != null)
	    {
		if (result.length() > 0)
		{
		    try
		    {
			
			callWSUpdateSubscriberDetail(result) ;
			//callWSSendMessage(msg, result, isFile, null);
		    } catch (Exception e)
		    {
			// TODO Auto-generated catch block
			 DebugReportOnLocat.e(e);
		    }
		}
	    }

	}

    }

    private String encryptMessage(String content, boolean isFile)
    {
	String respStr = null;

	HttpClient httpclient = new DefaultHttpClient();
	HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 150000);
	String url = Constants.baseUrl + "?";
	HttpPost httppost = new HttpPost(url);

	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	try
	{

	    String secretPhrase = Constants.sharedPreferences.getString("SecretPhrase", "");
	    DebugReportOnLocat.ln(">>G encrypt,secretPhrase=> " + secretPhrase);

	    nameValuePairs.add(new BasicNameValuePair("requestType", "encryptTo"));
	    nameValuePairs.add(new BasicNameValuePair("secretPhrase", secretPhrase));
	   
	    DebugReportOnLocat.ln(">>G encrypt,recipient=> " + Constants.NxtAcId);
	    
	    DebugReportOnLocat.ln(">>G encrypt, messageToEncrypt=> " + content);

	    nameValuePairs.add(new BasicNameValuePair("recipient", Constants.NxtAcId));

	/*    if (!isFile)
	    {
		nameValuePairs.add(new BasicNameValuePair("messageToEncrypt", content));
		nameValuePairs.add(new BasicNameValuePair("messageToEncryptIsText", "true"));
	    }
	    else if (!content.equals(""))
	    {*/
		nameValuePairs.add(new BasicNameValuePair("messageToEncrypt", content));
		nameValuePairs.add(new BasicNameValuePair("messageToEncryptIsText", "false"));
	   // }

	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    HttpResponse response = httpclient.execute(httppost);
	    respStr = EntityUtils.toString(response.getEntity());

	    
	    DebugReportOnLocat.ln(">>G  responce for encrypt-->  " + respStr);

	} catch (ClientProtocolException e)
	{
	    DebugReportOnLocat.e(e);
		    
	} catch (IOException e)
	{
	    DebugReportOnLocat.e(e);
	    
	} catch (Exception e)
	{
	    
	     DebugReportOnLocat.e(e);
	}
	
	DebugReportOnLocat.ln(" respStr == >>> "+respStr);
	
	return respStr;
    }

    
    
    
    final int IMAGE_MAX_SIZE=500;
    private Bitmap decodeFile(File f){
	    Bitmap b = null;

	        //Decode image size
	    BitmapFactory.Options o = new BitmapFactory.Options();
	    o.inJustDecodeBounds = true;

	    FileInputStream fis = null;
	    try
	    {
		fis = new FileInputStream(f);
		  BitmapFactory.decodeStream(fis, null, o);
		  
		    
	    } catch (FileNotFoundException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }finally{
		
		  try
		{
		    fis.close();
		} catch (IOException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	  

	    int scale = 1;
	    if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
	        scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / 
	           (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
	    }

	    DebugReportOnLocat.ln(" scale>>>"+scale);
	    //Decode with inSampleSize
	    BitmapFactory.Options o2 = new BitmapFactory.Options();
	    o2.inSampleSize = scale;
	    try
	    {
		fis = new FileInputStream(f);
	    } catch (FileNotFoundException e)
	    {
		
		e.printStackTrace();
	    }
	    b = BitmapFactory.decodeStream(fis, null, o2);
	    try
	    {
		fis.close();
	    } catch (IOException e)
	    {
		
		e.printStackTrace();
	    }
	    
	    DebugReportOnLocat.ln("  byte length >>>>" +b.getByteCount());

	    return b;
	}
    
    
    @SuppressWarnings("resource")
//    public void writeToFile(String path,byte[] array) 
//    { 
//        try 
//        { 
//             
//            FileOutputStream stream = new FileOutputStream(path); 
//            stream.write(array); 
//            
//        } catch (FileNotFoundException e1) 
//        { 
//            e1.printStackTrace(); 
//        } catch (IOException e)
//	{
//	   
//	    e.printStackTrace();
//	} 
//    } 
//    
    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
    
    
    
    
    //public static String decryptMsg = "";

//    private static String decryptMessage(String nonce, String data, String senderId)
//    {
//
//	HttpClient httpclient = new DefaultHttpClient();
//	HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 15000);
//	String url = Constants.baseUrl + "?";
//	HttpPost httppost = new HttpPost(url);
//
//	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//
//	try
//	{
//
//	    String secretPhrase = Constants.sharedPreferences.getString("SecretPhrase", "");
//	    DebugReportOnLocat.e(TAG, "decrypt,Constants.secretPhrase=>" + secretPhrase);
//
//	    nameValuePairs.add(new BasicNameValuePair("requestType", "decryptFrom"));
//	    nameValuePairs.add(new BasicNameValuePair("secretPhrase", secretPhrase));
//	    nameValuePairs.add(new BasicNameValuePair("account", senderId));
//
//	    DebugReportOnLocat.e(TAG, "decrypt,nonce=>" + nonce);
//	    DebugReportOnLocat.e(TAG, "decrypt,data=>" + data);
//	    DebugReportOnLocat.e(TAG, "decrypt,senderId=>" + senderId);
//
//	  
//	    nameValuePairs.add(new BasicNameValuePair("data", data));
//	    nameValuePairs.add(new BasicNameValuePair("decryptedMessageIsText", "false"));//"true"
//	    nameValuePairs.add(new BasicNameValuePair("nonce", nonce));
//
//	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//	    HttpResponse response = httpclient.execute(httppost);
//	    String res = EntityUtils.toString(response.getEntity());
//	    DebugReportOnLocat.e(TAG, "  responce for decrypt,res-->  " + res);
//	    try
//	    {
//		JSONObject jsonObject = new JSONObject(res);
//		if (jsonObject.has("decryptedMessage"))
//		{
//
//		    String decryptMsgFromRes = jsonObject.getString("decryptedMessage");
//
//		    decryptMsg = StringEscapeUtils.unescapeJava(decryptMsgFromRes);
//		    DebugReportOnLocat.e(TAG, "decrypt msg escape=>" + decryptMsg);
//
//		}
//	    } catch (JSONException e)
//	    {
//		// TODO Auto-generated catch block
//		 DebugReportOnLocat.e(e);
//		decryptMsg = "";
//	    }
//
//	    DebugReportOnLocat.e(TAG, "  responce for decrypt-->  " + decryptMsg);
//
//	} catch (ClientProtocolException e)
//	{
//	} catch (IOException e)
//	{
//	}
//	return decryptMsg;
//    }
//    
}
