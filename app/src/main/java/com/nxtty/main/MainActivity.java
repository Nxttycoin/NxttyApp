package com.nxtty.main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.AndroidBug5497Workaround;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.KeyboardUtility;
import com.nxtapp.utils.Network;
import com.nxtapp.utils.StaticUtility;
import com.nxtapp.utils.WebClientDevWrapper;

public class MainActivity extends Activity
{

    private String TAG = "MainActivity";
    private LinearLayout btn_login;
    private EditText edt_email, edt_pass, edt_confirmpass, city_of_birth;
    // String secretPharase, keyValue;
    String email = "", pwd = "", city = "";
    private ProgressDialog progressDialog;
    CheckBox chkRemember;

    public static String key = "", pKey = "";

    private void dismissProgressDialog()
    {
        try
        {
            progressDialog.dismiss();
        } catch (Exception e)
        {

        }
    }

    private static Activity activity;

    public static void finishActivity()
    {
        if (activity != null)
        {
            activity.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Constants.ISinChatScreen = false;

        Constants.sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        activity = this;

        try
        {
            AndroidBug5497Workaround.assistActivity(activity);

        } catch (IllegalStateException e)
        {
            DebugReportOnLocat.e(e);
        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }

        edt_email = (EditText) findViewById(R.id.edittext_emails);
        edt_pass = (EditText) findViewById(R.id.edittext_pass);
        edt_confirmpass = (EditText) findViewById(R.id.edittext_pass_confirm);
        city_of_birth = (EditText) findViewById(R.id.edittext_dob);

        chkRemember = (CheckBox) findViewById(R.id.chkRemember);
        chkRemember.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putString("Remember", "Yes");
                    editor.commit();
                }
                else
                {
                    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putString("Remember", "No");
                    editor.commit();
                }
            }
        });

        String Remember = prefs.getString("Remember", "No");
        if (Remember.equalsIgnoreCase("Yes"))
        {
            chkRemember.setChecked(true);
            edt_email.setText(prefs.getString("Email", ""));
            edt_pass.setText(prefs.getString("Password", ""));
            edt_confirmpass.setText(prefs.getString("Password", ""));
            city_of_birth.setText(prefs.getString("City", ""));
        }
        else
        {
            chkRemember.setChecked(false);
        }

        city_of_birth.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {
                if (charSequence.toString().startsWith(" "))
                {

                    AlertUtility.showToast(activity, "Empty start up spaces not allowed.");
                    city_of_birth.setText("");
                    return;
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub

            }

        });
        edt_confirmpass.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {
                if (charSequence.toString().startsWith(" "))
                {

                    AlertUtility.showToast(activity, "Empty start up spaces not allowed.");
                    edt_confirmpass.setText("");
                    return;
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub

            }

        });
        edt_pass.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {
                if (charSequence.toString().startsWith(" "))
                {

                    AlertUtility.showToast(activity, "Empty start up spaces not allowed.");
                    edt_pass.setText("");
                    return;
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub

            }

        });
        edt_email.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {
                if (charSequence.toString().startsWith(" "))
                {
                    // disableButton(...)
                    AlertUtility.showToast(activity, "Empty start up spaces not allowed.");
                    edt_email.setText("");

                    return;

                }
                else
                {
                    // edt_email.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub

            }

        });
        btn_login = (LinearLayout) findViewById(R.id.ln_enter);
        btn_login.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                Constants.NxtAcId="";
                Constants.AliasName = "";
                String msg="";
                // TODO Auto-generated method stub
                email = edt_email.getText().toString().trim();
                if (email == null || email.equals(""))
                {

                    msg+="Please enter email.\n";
                    // AlertUtility.showToast(activity, "Please enter email");
                    //  return;
                }else if (!StaticUtility.isValidEmail(email))
                {
                    msg+="Please enter valid email.\n";
//		    AlertUtility.showToast(activity, "Please enter valid email");
//		    return;
                }

                pwd = edt_pass.getText().toString().trim();
                if (pwd == null || pwd.equals(""))
                {
                    msg+="Please enter password.\n";

                    //  AlertUtility.showToast(activity, "Please enter password");
                    //  return;
                }
		
	/*	if (pwd.trim().length()<6)
		{
		    msg+="Please enter password min 6 character.\n";
		    
		  //  AlertUtility.showToast(activity, "Please enter password");
		  //  return;
		}*/



                String confirmPassword = edt_confirmpass.getText().toString().trim();
                if (confirmPassword == null || confirmPassword.equals(""))
                {
                    //  AlertUtility.showToast(activity, "Please enter confirm password");
                    //  return;
                    msg+="Please enter confirm password.\n";
                }

                if (!pwd.equals(confirmPassword) && !confirmPassword.equals("") && !pwd.equals(""))
                {
//		    AlertUtility.showToast(MainActivity.this, "Sorry! your password and confirm password doesn't match");
//		    return;

                    msg+="Sorry! your password and confirm password doesn't match.\n";

                }

                city = city_of_birth.getText().toString().trim();
                if (city == null || city.equals(""))
                {
                    // AlertUtility.showToast(activity, "Please enter city of your birth");
                    // return;
                    msg+="Please enter city of your birth.\n";
                }

                if(msg.trim().length()>1){


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            activity);



                    // set dialog message
                    alertDialogBuilder
                            .setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {


                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();




                    return;
                }

                if (!Network.isNetworkConnected(getApplicationContext()))
                {

                    AlertUtility.showToast(activity, getResources().getString(R.string.network_error));
                    return;
                }

                String secretPharase = email + "_" + pwd + "_" + city;

                // secretPharase = "temp@me.com_123456_qwe";

                KeyboardUtility.hideKeypad(MainActivity.this);
                Editor editor = Constants.sharedPreferences.edit();
                editor.putString("SecretPhrase", secretPharase);
                editor.commit();


                SharedPreferences pref = getSharedPreferences("DeviceType", 0);
                SharedPreferences.Editor edt_notifications = pref.edit();
                edt_notifications.putString("update_deviceType", "YES");
                edt_notifications.commit();


                SharedPreferences prefs = getSharedPreferences("PwdSafe", 0);
                String FirstCallAns = prefs.getString("option", "No");

                if (FirstCallAns.equalsIgnoreCase("No"))
                {
                    AlertDialog(secretPharase);
                }
                else
                {
                    signInRequest(secretPharase);
                }

            }
        });

        Typeface myFont = Typeface.createFromAsset(getAssets(), "SourceSansPro-Bold.otf");
        edt_email.setTypeface(myFont);
        edt_pass.setTypeface(myFont);
        edt_confirmpass.setTypeface(myFont);
        city_of_birth.setTypeface(myFont);
        edt_pass.setTransformationMethod(new PasswordTransformationMethod());
        edt_confirmpass.setTransformationMethod(new PasswordTransformationMethod());

	/*
	 * if (savedInstanceState == null) {
	 * getSupportFragmentManager().beginTransaction() .add(R.id.container,
	 * new PlaceholderFragment()) .commit(); }
	 */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // if (id == R.id.action_settings)
        // {
        // return true;
        // }
        return super.onOptionsItemSelected(item);
    }

    void signInRequest(String secretPharase) {
	/*
	 * JSONObject json = new JSONObject();
	 * 
	 * 
	 * Thread t = new Thread() {
	 * 
	 * public void run() { Looper.prepare();
	 */

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();

        btn_login.setEnabled(false);

        DebugReportOnLocat.e("web_service", "SignInRequest secretPharase :  " + secretPharase);

        RequestParams params = new RequestParams();
        params.put("requestType", "getAccountId");
        params.put("secretPhrase", secretPharase);
        params.put("key", Constants.ParamKey);

        // send post request for user login
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.baseUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = "";
                try {
                    str = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String keyValue = null, publicKey = null;
                JSONObject object = null;
                try {
                    object = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    DebugReportOnLocat.e("web_service", "responce   " + object);
                    keyValue = object.getString("account");
                    Constants.recipient = keyValue;
                    publicKey = object.getString("publicKey");
                    DebugReportOnLocat.e("web_service", "signInRequest response " + object.toString());

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    DebugReportOnLocat.e(e);
                }

                if (keyValue != "") {

                    if (Network.isNetworkConnected(getApplicationContext())) {
                        checkSubscription(keyValue, publicKey);
                    } else {
                        AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
                    }
                } else {
                    dismissProgressDialog();
                    AlertUtility.showToast(MainActivity.this, getString(R.string.err_unknown));
                    btn_login.setEnabled(true);
                }
//            }
//
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                btn_login.setEnabled(true);
                String str = "";
                try {
                    str = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                dismissProgressDialog();
                try {
                    DebugReportOnLocat.e("web_service", "Error " + responseBody);
                    JSONObject jobject = new JSONObject(str);
                    int status = jobject.getInt("status");
                    if (status == 0) {
                    }
                } catch (Exception ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }
        });
    }
//	client.post(Constants.baseUrl, params, new JsonHttpResponseHandler()
//	{
//
//	    @Override
//	    public void onSuccess(final JSONObject object)
//	    {
//
//		String keyValue = null, publicKey = null;
//
//		try
//		{
//		    // keyValue = object.getString("accountId");
//		    DebugReportOnLocat.e("web_service", "responce   " + object);
//
//		    keyValue = object.getString("account");
//		    Constants.recipient = keyValue;
//		    publicKey = object.getString("publicKey");
//
//		    // DebugReportOnLocat.e("", msg)
//
//		    // keyValue = object.getString("AccountNUM");
//
//		    DebugReportOnLocat.e("web_service", "signInRequest response " + object.toString());
//
//		} catch (JSONException e)
//		{
//		    // TODO Auto-generated catch block
//		     DebugReportOnLocat.e(e);
//		}
//
//		if (keyValue != "")
//		{
//		    /*
//		     * Toast.makeText(getApplicationContext(),
//		     * "Account Id is =>" + keyValue, Toast.LENGTH_LONG)
//		     * .show();
//		     */
//
//		    if (Network.isNetworkConnected(getApplicationContext()))
//		    {
//
//			checkSubscription(keyValue, publicKey);
//
//		    }
//		    else
//		    {
//
//			AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
//		    }
//
//		}
//		else
//		{
//		    dismissProgressDialog();
//		    AlertUtility.showToast(MainActivity.this, getString(R.string.err_unknown));
//		    btn_login.setEnabled(true);
//		}
//	    }
//
//	    @Override
//	    protected void handleFailureMessage(Throwable e, String responseBody)
//	    {
//		// spinner.setVisibility(View.INVISIBLE);
//		btn_login.setEnabled(true);
//		dismissProgressDialog();
//		try
//		{
//		    DebugReportOnLocat.e("web_service", "Error " + responseBody);
//		    JSONObject jobject = new JSONObject(responseBody);
//		    int status = jobject.getInt("status");
//		    if (status == 0)
//		    {
//			// UserInfo userInfo= UserInfo.getInstance(context);
//			// DebugReportOnLocat.e("error response",jobject.getString("error_message").toString());
//			// Utility.showAlertDialog(jobject.getString("error_message"),
//			// LoginActivity.this);
//			// userInfo.saveUserInfo(context);
//		    }
//		} catch (Exception ex)
//		{
//		    // TODO Auto-generated catch block
//		    ex.printStackTrace();
//		}
//	    }
//	});
//
//    }

    private void checkSubscription(final String keyValue, final String publicKey)
    {
        RequestParams params = new RequestParams();

        Log.i("web_service", "checkSubscription  keyValue  :  " + keyValue);

        params.put("nxtID", keyValue);
        params.put("key", Constants.ParamKey);


        // send post request for user login
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.baseUrl_new + "subscriber/issubscribed", params, new AsyncHttpResponseHandler()
        {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                boolean isSubscribed = false;
                String str = "";
                JSONObject object = null;
                try{
                    str = new String(responseBody,"UTF-8");
                    object = new JSONObject(str);
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try{
                    Log.i("web_service", "checkSubscription response:  " + object.toString());
                    DebugReportOnLocat.e("web_service", "CAME HERE");

                    isSubscribed = object.getBoolean("status");

                } catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    DebugReportOnLocat.e(e);
                }

                /*
                 * Toast.makeText(getApplicationContext(), "is =>" +
                 * isSubscribed, Toast.LENGTH_LONG) .show();
                 */
                btn_login.setEnabled(true);
                SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                editor.putString("Email", email);
                editor.putString("Password", pwd);
                editor.putString("City", city);
                editor.commit();
                dismissProgressDialog();
                if (!isSubscribed){

                    Intent i2 = new Intent(MainActivity.this, GetNewDataService.class);
                    key = keyValue;
                    pKey = publicKey;
                    startService(i2);

                    Intent ii = new Intent(MainActivity.this, Verify.class);
                    ii.putExtra("accountId", keyValue);
                    startActivity(ii);

                }
                else{
                    SharedPreferences sharedPreferences = getSharedPreferences("PublicChatSubscribeDetails", Context.MODE_PRIVATE);
                    Constants.NxtAcId = keyValue;
                    try{
                        //Subscribed public chat check when login
                        if(sharedPreferences.getString("nxtAcId", "0").equals(keyValue)){
                        }else{

                            SharedPreferences.Editor edt_notifications = sharedPreferences.edit();
                            edt_notifications.putString("nxtAcId", "0");
                            edt_notifications.putBoolean("isSubscribed", false);
                            edt_notifications.commit();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    SharedPreferences.Editor edt_notifications = getSharedPreferences("ID", 0).edit();
                    edt_notifications.putString("nxtAcId", keyValue);
                    edt_notifications.commit();

                    Intent ii = new Intent(MainActivity.this, Dashboard.class);
                    startActivity(ii);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
                btn_login.setEnabled(true);
                String str = "";
                try{
                    str = new String(responseBody,"UTF-8");
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                try
                {
                    DebugReportOnLocat.e("web_service", "Error " + responseBody);
                    JSONObject jobject = new JSONObject(str);
                    int status = jobject.getInt("status");
                    if (status == 0){
                    }
                } catch (JSONException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }
        });
//        client.post(Constants.baseUrl_new + "subscriber/issubscribed", params, new JsonHttpResponseHandler()
//        {
//
//            @Override
//            public void onSuccess(final JSONObject object)
//            {
//                boolean isSubscribed = false;
//                try
//                {
//
//                    Log.i("web_service", "checkSubscription response:  " + object.toString());
//                    DebugReportOnLocat.e("web_service", "CAME HERE");
//
//                    isSubscribed = object.getBoolean("status");
//
//                } catch (JSONException e)
//                {
//                    // TODO Auto-generated catch block
//                    DebugReportOnLocat.e(e);
//                }
//
//		/*
//		 * Toast.makeText(getApplicationContext(), "is =>" +
//		 * isSubscribed, Toast.LENGTH_LONG) .show();
//		 */
//                btn_login.setEnabled(true);
//                SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
//                editor.putString("Email", email);
//                editor.putString("Password", pwd);
//                editor.putString("City", city);
//                editor.commit();
//                dismissProgressDialog();
//                if (!isSubscribed)
//                {
//
//                    Intent i2 = new Intent(MainActivity.this, GetNewDataService.class);
//                    key = keyValue;
//                    pKey = publicKey;
//                    startService(i2);
//
//                    Intent ii = new Intent(MainActivity.this, Verify.class);
//                    ii.putExtra("accountId", keyValue);
//                    startActivity(ii);
//
//                }
//                else
//                {
//
//                    SharedPreferences sharedPreferences = getSharedPreferences("PublicChatSubscribeDetails", Context.MODE_PRIVATE);
//
//                    Constants.NxtAcId = keyValue;
//
//                    try
//                    {
//                        //Subscribed public chat check when login
//
//                        if(sharedPreferences.getString("nxtAcId", "0").equals(keyValue)){
//
//
//                        }else{
//
//                            SharedPreferences.Editor edt_notifications = sharedPreferences.edit();
//                            edt_notifications.putString("nxtAcId", "0");
//                            edt_notifications.putBoolean("isSubscribed", false);
//                            edt_notifications.commit();
//                        }
//
//
//                    } catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//
//
//
//
//                    SharedPreferences.Editor edt_notifications = getSharedPreferences("ID", 0).edit();
//                    edt_notifications.putString("nxtAcId", keyValue);
//                    edt_notifications.commit();
//
//                    Intent ii = new Intent(MainActivity.this, Dashboard.class);
//                    startActivity(ii);
//                    finish();
//                }
//            }
//
//            @Override
//            protected void handleFailureMessage(Throwable e, String responseBody)
//            {
//                dismissProgressDialog();
//                btn_login.setEnabled(true);
//                try
//                {
//                    DebugReportOnLocat.e("web_service", "Error " + responseBody);
//                    JSONObject jobject = new JSONObject(responseBody);
//                    int status = jobject.getInt("status");
//                    if (status == 0)
//                    {
//                        // UserInfo userInfo=
//                        // UserInfo.getInstance(context);
//                        // DebugReportOnLocat.e("error response",jobject.getString("error_message").toString());
//                        // Utility.showAlertDialog(jobject.getString("error_message"),
//                        // LoginActivity.this);
//                        // userInfo.saveUserInfo(context);
//                    }
//                } catch (JSONException ex)
//                {
//                    // TODO Auto-generated catch block
//                    ex.printStackTrace();
//                }
//            }
//        });

    }

    private void AlertDialog(final String phase)
    {
        View checkBoxView = View.inflate(this, R.layout.alertdialog_login, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    SharedPreferences.Editor editor = getSharedPreferences("PwdSafe", 0).edit();
                    editor.putString("option", "Yes");
                    editor.commit();
                }
            }
        });
        checkBox.setText(getResources().getString(R.string.do_not_ask_me_again));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(checkBoxView).setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                signInRequest(phase);
            }
        })

                .show();
    }

    //
    //
    // its always ask me to enter alias name

    public HttpResponse request(String url, JSONObject request) throws ClientProtocolException, IOException, IllegalStateException, JSONException
    {

        DefaultHttpClient client = (DefaultHttpClient) WebClientDevWrapper.getNewHttpClient();

        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(request.toString(), "utf-8"));
        HttpResponse response = client.execute(post);
        return response;
    }
}
