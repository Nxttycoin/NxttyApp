package com.nxtty.main;

import static com.nxtty.main.CommonUtilities.SENDER_ID;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.flurry.android.FlurryAgent;
//import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;

import java.io.IOException;

public class SplashScreen extends Activity
{

    SharedPreferences pref;
    private SharedPreferences preferences_user;
    private String TAG = "Splash", regId = "";
    public static AsyncTask<Void, Void, Void> mRegisterTask;
    private boolean registered = false;
    public static int notificationId = 0;
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

        Constants.NxtAcId="";
        Constants.AliasName = "";

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);



        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // BugSenseHandler.initAndStartSession(SplashScreen.this, "35b33b15");

        setContentView(R.layout.splashscreen);

        try
        {
            FlurryAgent.setLogEnabled(true);
            FlurryAgent.setLogLevel(2);
            // FlurryAgent.onStartSession(this, Constants.FlurryKey);

            FlurryAgent.setContinueSessionMillis(5 * 10000);
        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }

        notificationId = (int) System.currentTimeMillis();
        DebugReportOnLocat.e("", "Dashboard,notificationId==>" + notificationId);

	/*
	 * if (isNetworkConnected()) {
	 */

        preferences_user = getSharedPreferences("USER_DATA", 0);

        try
        {

//	    regId = GCMRegistrar.getRegistrationId(this);
            regId = gcm.register(SENDER_ID);

            Constants.RegistationKeyGCM =regId;

        } catch (Exception e)
        {
            e.printStackTrace();
        }


        if (regId.equals(""))
        {
            GCMregister();

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


        pref = getSharedPreferences("ID", 0);

        final String NexACID = pref.getString("nxtAcId", "0");

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // new code

                preferences_user = getSharedPreferences("USER_DATA", 0);
                Boolean isAutoLogout = preferences_user.getBoolean("isAutoLogout", false);

                final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

                String Remember = prefs.getString("Remember", "No");

                // SharedPreferences.Editor edt_notifications =
                // preferences_user.edit();
                // edt_notifications.putBoolean("isAutoLogout", false);
                //

                // edt_notifications.commit();

                // new code end

                DebugReportOnLocat.ln(" Login >> "+NexACID+"  >> "+isAutoLogout);

                if (NexACID.equalsIgnoreCase("0") || isAutoLogout)
                {
                    Intent ii = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(ii);
                    finish();
                }
                else
                {


                    // if(Remember.equalsIgnoreCase("Yes")){

                    Constants.NxtAcId = NexACID;
                    Intent ii = new Intent(SplashScreen.this, Dashboard.class);
                    startActivity(ii);
                    finish();
                    //}else{
			
			/*Intent ii = new Intent(SplashScreen.this, MainActivity.class);
			    startActivity(ii);
			    finish();*/
                    // }


                }
            }
        }, 1000);

	/*
	 * } else { AlertUtility.showToast(SplashScreen.this,
	 * getResources().getString(R.string.network_error));
	 * 
	 * finish(); }
	 */

    }

    public void GCMregister()
    {

        try
        {

            // Log.i(TAG, "Inside GCM Reigster Method");
            checkNotNull(CommonUtilities.SERVER_URL, "SERVER_URL");
            checkNotNull(CommonUtilities.SENDER_ID, "SENDER_ID");
            // Make sure the device has the proper dependencies.
//	    GCMRegistrar.checkDevice(this);
            // Make sure the manifest was properly set - comment out this line
            // while developing the app, then uncomment it when it's ready.
//	    GCMRegistrar.checkManifest(this);

            registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
//	    regId = GCMRegistrar.getRegistrationId(this);
            regId = gcm.register(SENDER_ID);

            Constants.RegistationKeyGCM=regId;


            DebugReportOnLocat.e("registerID", regId);

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }
    }

    private void checkNotNull(Object reference, String name)
    {
        // Log.i(TAG,"================Inside checkNotNull  Method  GCMWebService==============================");
        if (reference == null)
        {
            throw new NullPointerException(getString(R.string.app_name, name));
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            DebugReportOnLocat.e("Broadcast Reciever", "================Inside OnReceive in BroadcastReceiver Method   GCMWebService==============================");
            // String newMessage =
            // intent.getExtras().getString(EXTRA_MESSAGE);
            // mDisplay.append(newMessage + "\n");
        }
    };

    public void GCMWebService(final String regId, final String userId)
    {
        if (regId.equals(""))
        {
            DebugReportOnLocat.e(TAG, "================Inside if in regId=null  GCMWebService==============================");
            // Automatically registers application on startup.
            try {
                Constants.RegistationKeyGCM = gcm.register(SENDER_ID);
            } catch (IOException e) {
                e.printStackTrace();
            }
//        GCMRegistrar.register(this, SENDER_ID);

//	    Constants.RegistationKeyGCM =  GCMRegistrar.getRegistrationId(this);
        }
        else
        {
            DebugReportOnLocat.e(TAG, "================Inside else in regId=null  GCMWebService==============================");
            // Device is already registered on GCM, needs to check if it is
            // registered on our server as well.
//	    if (GCMRegistrar.isRegisteredOnServer(this))
//	    {
//		// Skips registration.
//		DebugReportOnLocat.e(TAG, "================Inside else in regId=null Already register on Server GCMWebService=============================");
//		// mDisplay.append(getString(R.string.already_registered) +
//		// "\n");
//	    }
//	    else
//	    {
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
//                        GCMRegistrar.unregister(context);
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
//	}

    }

    private void regID()
    {
        DebugReportOnLocat.e(TAG, "================regID   GCMWebService==============================");

        preferences_user = getSharedPreferences("USER_DATA", 0);
        SharedPreferences.Editor edt_notifications = preferences_user.edit();

        edt_notifications.putString("pns_id", regId);
        edt_notifications.commit();
    }

    public boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null)
        {
            // There are no active networks.
            return false;
        }
        else
            return true;
    }
}
