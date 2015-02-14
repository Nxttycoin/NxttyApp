package com.nxtty.main;

import static com.nxtty.main.CommonUtilities.SENDER_ID;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import aniways.com.google.analytics.tracking.android.Log;

//import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nxtapp.fragment.GroupDetailActivity;
import com.nxtapp.fragment.SettingFragment.JoinAsync;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;

public class Verify extends Activity
{
    private LinearLayout btn_moveToDashboard;
    private EditText edt_verify,edtReffrealCode;
    private String errorDetails;
    private String userAlias;
    private String code;
    private boolean isSubscribed;
    private String keyValueAgain;
    private GoogleCloudMessaging gcm;

    private ProgressDialog progressDialog;

    private void dismissProgressDialog()
    {
        try
        {
            progressDialog.dismiss();
        } catch (Exception e)
        {

        }
    }
    private SharedPreferences mNotiPref;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gcm = GoogleCloudMessaging.getInstance(this);
        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.verify_account);
        Intent i = getIntent();
        keyValueAgain = i.getExtras().getString("accountId");
        edt_verify = (EditText) findViewById(R.id.edittext_verifycode);

        edtReffrealCode= (EditText) findViewById(R.id.edittextReffrealcode);

        mNotiPref =getSharedPreferences("noty_settings", 0);

        mNotiPref.edit().putBoolean("noty_enabled", true).commit();

        try
        {

            if(Constants.RegistationKeyGCM!=null){
                if(Constants.RegistationKeyGCM.trim().equals("")){
                    try{
//                        regId = GCMRegistrar.getRegistrationId(this);
                        Constants.RegistationKeyGCM =regId;

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                    if (regId.equals(""))
                    {


                        if (regId.equals(""))
                        {
                            GCMWebService(regId, "196");
                        }
                        else
                        {
                            regID();

                            //Constants.RegistationKeyGCM = preferences_user.getString("pns_id", "");
                        }
                    }

                }



            }



        } catch (Exception e)
        {
            // TODO: handle exception
        }

        // Request focus and show soft keyboard automatically
        edt_verify.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        edt_verify.addTextChangedListener(new TextWatcher()
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

                    AlertUtility.showToast(getApplicationContext(), "Empty start up spaces not allowed.");
                    edt_verify.setText("");
                    return;
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub

            }

        });


        edtReffrealCode.addTextChangedListener(new TextWatcher()
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

                    AlertUtility.showToast(getApplicationContext(), "Empty start up spaces not allowed.");
                    edtReffrealCode.setText("");
                    return;
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {


            }

        });

        btn_moveToDashboard = (LinearLayout) findViewById(R.id.ln_subscribe);

        btn_moveToDashboard.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {


                code=edtReffrealCode.getText().toString();

                //123


                //approve(userAlias);


                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                // TODO Auto-generated method stub
                userAlias = edt_verify.getText().toString();

                code=edtReffrealCode.getText().toString();

                if (userAlias == null || userAlias.equals(""))
                {
                    AlertUtility.showToast(Verify.this, "Please enter alias name");

                }else if(userAlias.trim().length()>3 && code.trim().length()>0){


                    validate();

                }

                else if(userAlias.trim().length()<3){


                    //AlertUtility.showToast(Verify.this, "Please enter alias name minimum three letters");

                    new AlertDialog.Builder(Verify.this)
                            .setMessage("Please enter an alias name with minimum three letters")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
                else
                {
                    subscribeUser();
                }

            }
        });

    }

    private void subscribeUser()
    {



        RequestParams params = new RequestParams();
        params.put("nxtID", keyValueAgain);
        params.put("name_alias", userAlias);
        //New parameter
        params.put("city", "");
        params.put("gender", "");
        params.put("school", "");
        params.put("deviceID", Constants.RegistationKeyGCM);
        params.put("deviceType", "Android");


        params.put("key", Constants.ParamKey);



        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(Verify.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCanceledOnTouchOutside(false);
        }



        progressDialog.show();
        // send post request for user login
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.baseUrl_new+"subscriber/subscribe", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                // int status = 0;
                String str = "";
                JSONObject object = null;
                try{
                    str = new String(responseBody,"UTF-8");
                    object = new JSONObject(str);
                }catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismissProgressDialog();
                DebugReportOnLocat.e("web_service", "subscribe response  " + object.toString());
                try{
                    isSubscribed = object.getBoolean("status");
                    errorDetails = object.getString("errorDetail");
                } catch (JSONException e){
                    // TODO Auto-generated catch block
                    DebugReportOnLocat.e(e);
                }

                /*
                 * Toast.makeText(getApplicationContext(), "is =>" +
                 * isSubscribed, Toast.LENGTH_LONG) .show();
                 */
                if (isSubscribed == true){
                    try{
                        approve(userAlias);

                    } catch (Exception e){
                        DebugReportOnLocat.e(e);
                    }
                    Constants.NxtAcId = keyValueAgain;
                    try{

                        new JoinAsync().execute();
                    } catch (Exception e){
                        DebugReportOnLocat.e(e);
                    }
                    SharedPreferences.Editor edt_notifications = getSharedPreferences("ID", 0).edit();
                    edt_notifications.putString("nxtAcId", keyValueAgain);
                    edt_notifications.commit();

                    Intent dashboardActivity = new Intent(Verify.this, Dashboard.class);
                    startActivity(dashboardActivity);
                    MainActivity.finishActivity();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "" + errorDetails, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String str = "";
                try{
                    str = new String(responseBody, "UTF-8");

                }catch(UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                dismissProgressDialog();
                try{
                    JSONObject jobject = new JSONObject(str);
                    int status = jobject.getInt("status");
                    if (status == 0)
                    {

                    }
                } catch (JSONException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }

            protected void handleFailureMessage(Throwable e, String responseBody)
            {

            }

        });
//        client.post(Constants.baseUrl_new+"subscriber/subscribe", params, new JsonHttpResponseHandler()
//        {
//
//            @Override
//            public void onSuccess(final JSONObject object)
//            {
//                // int status = 0;
//                dismissProgressDialog();
//                DebugReportOnLocat.e("web_service", "subscribe response  " + object.toString());
//
//                try
//                {
//                    isSubscribed = object.getBoolean("status");
//                    errorDetails = object.getString("errorDetail");
//
//                } catch (JSONException e)
//                {
//                    // TODO Auto-generated catch block
//                    DebugReportOnLocat.e(e);
//                }
//		/*
//		 * Toast.makeText(getApplicationContext(), "is =>" +
//		 * isSubscribed, Toast.LENGTH_LONG) .show();
//		 */
//                if (isSubscribed == true)
//                {
//                    try
//                    {
//                        approve(userAlias);
//
//                    } catch (Exception e)
//                    {
//                        DebugReportOnLocat.e(e);
//                    }
//
//                    Constants.NxtAcId = keyValueAgain;
//                    try
//                    {
//
//                        new JoinAsync().execute();
//
//                    } catch (Exception e)
//                    {
//                        DebugReportOnLocat.e(e);
//                    }
//
//
//                    SharedPreferences.Editor edt_notifications = getSharedPreferences("ID", 0).edit();
//                    edt_notifications.putString("nxtAcId", keyValueAgain);
//                    edt_notifications.commit();
//
//                    Intent dashboardActivity = new Intent(Verify.this, Dashboard.class);
//                    startActivity(dashboardActivity);
//                    MainActivity.finishActivity();
//                    finish();
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(), "" + errorDetails, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            protected void handleFailureMessage(Throwable e, String responseBody)
//            {
//                // spinner.setVisibility(View.INVISIBLE);
//                dismissProgressDialog();
//                try
//                {
//                    JSONObject jobject = new JSONObject(responseBody);
//                    int status = jobject.getInt("status");
//                    if (status == 0)
//                    {
//
//                    }
//                } catch (JSONException ex)
//                {
//                    // TODO Auto-generated catch block
//                    ex.printStackTrace();
//                }
//            }
//        });

    }





    void approve(String userAlias){


        //String url=Constants.baseUrl_new+"referrals/approve";



        AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.APPROVE_REFERRALS);
        req.setBody(JSONBody.approveReferral(userAlias,code,Constants.ParamKey));
        req.setUserInfo(HttpUri.APPROVE_REFERRALS);
        DebugReportOnLocat.ln("url >>>"+HttpUri.APPROVE_REFERRALS);
        //final CategoryManager cManager = new CategoryManager(this);
        new com.nxtty.http.AsyncHttpClient().executeJSONObject(getApplicationContext(),req,
                new ResultCallback() {

                    @Override
                    public void onCompleted(Exception e, String responseString, String methodInfo) {


                        DebugReportOnLocat.ln(" responseString"+responseString);

                        //  responseString{"status":false,"errorDetail":"Invalid or epxire code!"}

                    }

                });






    }




    void  validate(){



        DebugReportOnLocat.ln(" Validate >>");
        DebugReportOnLocat.ln(" code >>"+code);
        DebugReportOnLocat.ln(" key >>"+Constants.ParamKey);


        AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.VALIDATE_REFERRALS);
        req.setBody(JSONBody.validateReferral(code,Constants.ParamKey));
        req.setUserInfo(HttpUri.VALIDATE_REFERRALS);
        DebugReportOnLocat.ln("url >>>"+HttpUri.VALIDATE_REFERRALS);
        //final CategoryManager cManager = new CategoryManager(this);
        new com.nxtty.http.AsyncHttpClient().executeJSONObject(getApplicationContext(),req,
                new ResultCallback() {

                    @Override
                    public void onCompleted(Exception e, String responseString, String methodInfo) {


                        //it will return an integer value which can be interpreted as following:
                        //0 - Code is incorrect
                        //1 - Code is valid
                        //2 - Code is no longer valid or expired.


                        DebugReportOnLocat.ln(" Validate responseString"+responseString);
					/*   
					   if(responseString.equals("2")){
					       
					       
					      
					     //  Toast.makeText(getApplicationContext(), "" +getResources().getString(R.string.code_no_longer) , Toast.LENGTH_LONG).show();
					       
					   }else*/ if(responseString.equals("0")){


                            Toast.makeText(getApplicationContext(), "" +getResources().getString(R.string.code_incorrect) , Toast.LENGTH_LONG).show();

                        }else{

                            duplicateDeviceIDCheck();
                        }


                        //  responseString{"status":false,"errorDetail":"Invalid or epxire code!"}

                    }



                });





    }



    void duplicateDeviceIDCheck(){






        DebugReportOnLocat.ln(" Validate >>");
        DebugReportOnLocat.ln(" code >>"+code);
        DebugReportOnLocat.ln(" key >>"+Constants.ParamKey);


        AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.IS_DUPLICATE_DEVICE_ID);
        req.setBody(JSONBody.duplicateDeviceIDCheckForReferral(Constants.RegistationKeyGCM,Constants.ParamKey));
        req.setUserInfo(HttpUri.IS_DUPLICATE_DEVICE_ID);
        DebugReportOnLocat.ln("url >>>"+HttpUri.IS_DUPLICATE_DEVICE_ID);
        //final CategoryManager cManager = new CategoryManager(this);
        new com.nxtty.http.AsyncHttpClient().executeJSONObject(getApplicationContext(),req,
                new ResultCallback() {

                    @Override
                    public void onCompleted(Exception e, String responseString, String methodInfo) {


                        //it will return an integer value which can be interpreted as following:
                        //0 - Code is incorrect
                        //1 - Code is valid
                        //2 - Code is no longer valid or expired.


                        DebugReportOnLocat.ln(" IS_DUPLICATE_DEVICE_ID responseString "+responseString);


                        if (responseString != null && !responseString.equals("null"))
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(responseString);
                                boolean status = jsonObject.getBoolean("status");
                                if (status)
                                {

                                    Toast.makeText(getApplicationContext(), "" +getResources().getString(R.string.code_incorrect) , Toast.LENGTH_LONG).show();

                                }else{

                                    subscribeUser();

                                }

                            } catch (JSONException err)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }





                    }


                });






    }

    public static AsyncTask<Void, Void, Void> mRegisterTask;
    private String TAG = "Splash", regId = "";
    private boolean registered = false;
    private SharedPreferences preferences_user;


    public void GCMWebService(final String regId, final String userId)
    {
        if (regId.equals(""))
        {
            DebugReportOnLocat.e(TAG, "================Inside if in regId=null  GCMWebService==============================");
            // Automatically registers application on startup.
//            GCMRegistrar.register(this, SENDER_ID);

            try {
                gcm.register(SENDER_ID);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Constants.RegistationKeyGCM =  GCMRegistrar.getRegistrationId(this);
        }
        else
        {
            DebugReportOnLocat.e(TAG, "================Inside else in regId=null  GCMWebService==============================");
            // Device is already registered on GCM, needs to check if it is
            // registered on our server as well.
//            if (GCMRegistrar.isRegisteredOnServer(this))
//            {
//                // Skips registration.
//                DebugReportOnLocat.e(TAG, "================Inside else in regId=null Already register on Server GCMWebService=============================");
//                // mDisplay.append(getString(R.string.already_registered) +
//                // "\n");
//            }
//            else
//            {
                DebugReportOnLocat.e(TAG, "================Inside else in regId=null trying to  register on Server GCMWebService=============================");
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>()
                {

                    @Override
                    protected Void doInBackground(Void... params)
                    {
                        DebugReportOnLocat.e(TAG, "================Inside doInBackground Method       GCMWebService==============================");
                        registered = ServerUtilities.register(context, regId, userId);

                        // if (regId != null && !regId.equalsIgnoreCase("")) {
                        // WebCallForuID();
                        // }
                        DebugReportOnLocat.e(TAG, "================Server side REegistered or not?" + registered);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered)
                        {
                            DebugReportOnLocat.e(TAG, "================Inside unregister inside Do in backgroound GCMWebService==============================");
                            try {
                                gcm.unregister();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result)
                    {
                        DebugReportOnLocat.e(TAG, "================Inside onPostExecute Method   GCMWebService==============================");
                        mRegisterTask = null;

                    }

                };
                mRegisterTask.execute(null, null, null);
            }
//        }

    }

    private void regID()
    {
        DebugReportOnLocat.e(TAG, "================regID   GCMWebService==============================");

        preferences_user = getSharedPreferences("USER_DATA", 0);
        SharedPreferences.Editor edt_notifications = preferences_user.edit();

        edt_notifications.putString("pns_id", regId);
        edt_notifications.commit();
    }




    public class JoinAsync extends AsyncTask<Void, String, Void>
    {


        String respStr = null;
        //int pos;



        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();



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
                nameValuePairs.add(new BasicNameValuePair("groupID", "8"));
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


            if (respStr != null)
            {
                try
                {
                    JSONObject jobj = new JSONObject(respStr);
                    if (jobj.optString("status").equalsIgnoreCase("true"))
                    {


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

}
