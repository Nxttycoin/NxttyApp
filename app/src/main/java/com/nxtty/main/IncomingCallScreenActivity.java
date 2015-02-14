package com.nxtty.main;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nextapp.sinch.AudioPlayer;
import com.nextapp.webservice.JsonParserUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.adapter.ContactAdapter;
import com.nxtapp.classes.FriendRequest;
import com.nxtapp.classes.UserModel;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.fragment.HomeFragment;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.StaticUtility;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpClient;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

public class IncomingCallScreenActivity extends Activity
{

    static final String LOG_TAG = IncomingCallScreenActivity.class.getSimpleName();

    private ImageButton mAnswer;

    private TextView mCallerName;

    private ImageButton mDecline;

    private Call mCall;

    Context mContext;


    private ImageLoader imageLoader;
    private DisplayImageOptions circularOptions;


    private AudioPlayer mAudioPlayer;
    ImageView ivProfilePic;
    String Avtar="";

    boolean isCallEnd=true;

    public static boolean notifyflag=true;
    static final int NOTIFIY_ID=10;

    @Override
    protected void onDestroy()
    {
        mAudioPlayer.stopRingtone();
        isCallEnd=true;
        super.onDestroy();
    }



    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        isCallEnd=false;
        notifyflag=true;
    }

    @Override
    public void onPause()
    {
        super.onPause();



        if(!isCallEnd && notifyflag){

            notifyUserIncomingCallCallRinging(mCall.getRemoteUserId());

        }



    }

    String aliasName="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming_call);

        mContext=this;
        isCallEnd=false;
        notifyflag=true;

        mAnswer = (ImageButton) findViewById(R.id.answerButton);
        mCallerName = (TextView) findViewById(R.id.incomingClUser);
        mDecline = (ImageButton) findViewById(R.id.declineButton);

        mCall = CurrentCall.currentCall;
        mCall.addCallListener(new SinchCallListener());

        try
        {

            imageLoader = ImageLoader.getInstance();

            circularOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return BitmapUtility.maskingRoundBitmap(bitmap);
                    }
                })
                .build();


        } catch (Exception e)
        {
            e.printStackTrace();
        }

        mAnswer.setOnClickListener(incomingClickListener);
        mDecline.setOnClickListener(incomingClickListener);
        mCallerName.setText(mCall.getRemoteUserId());

        aliasName=mCall.getRemoteUserId();

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();

        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);


        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        LinearLayout lnMainBG = (LinearLayout) findViewById(R.id.ln_callMainBG);



        try
        {


            File background = StaticUtility.getResideMenuBackgroundImagePath(IncomingCallScreenActivity.this);
            if (background != null && background.isFile())
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(background.getAbsolutePath());
                Drawable d = new BitmapDrawable(getResources(), myBitmap);
                lnMainBG.setBackgroundDrawable(d);
            }
            else
            {
                lnMainBG.setBackgroundResource(R.drawable.menu_background_new);
            }



        } catch (Exception e)
        {
            e.printStackTrace();
        }


        try
        {
            if(HomeFragment.HomeScreenList!=null){

                if(HomeFragment.HomeScreenList.size()==0){


                    // new GetHomeDataAsync().execute();

                    SubcriberContact();

                }else{

                    for(int i=0;i<HomeFragment.HomeScreenList.size();i++){

                        try
                        {

                            if(HomeFragment.HomeScreenList.get(i).getNameAlias().equals(mCall.getRemoteUserId())){


                                Avtar=HomeFragment.HomeScreenList.get(i).getAvatar();

                                System.out.println(" avtar>>"+Avtar);

                                if (Avtar != null)
                                {
                                    imageLoader.displayImage(Avtar, ivProfilePic, circularOptions);


                                    // AvtarIN = BitmapUtility.decodeBitmapFromBase64(Avtar, 100, 100);
                                }


                                break;
                            }


                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }

                    if(Avtar.equals("")){

                        SubcriberContact();
                    }

                }



            }





        } catch (Exception e)
        {
            // TODO: handle exception
        }

        try
        {
            //  Avtar = getIntent().getExtras().getString("Avtar");


        } catch (Exception e)
        {
        }

    }

    private void answerClicked()
    {
        isCallEnd=true;
        notifyflag=false;
        mAudioPlayer.stopRingtone();
        mCall.answer();

        cancelNotification(mContext, NOTIFIY_ID);
        startActivity(new Intent(this, CallScreenActivity.class).putExtra("comeFromIncomingCl", "true").putExtra("Avtar", Avtar));

    }

    private void declineClicked()
    {
        mAudioPlayer.stopRingtone();
        cancelNotification(mContext, NOTIFIY_ID);
        mCall.hangup();
        isCallEnd=true;
        notifyflag=false;
        finish();
    }


    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
    @Override
    public void onBackPressed()
    {
        // User should exit activity by ending call, not by going back.

        AlertUtility.showConfirmDialog(mContext, mContext.getResources().getString(R.string.call_end) + " " ,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mAudioPlayer.stopRingtone();
                        mCall.hangup();
                        isCallEnd=true;
                    }
                });
    }


    private class SinchCallListener implements CallListener
    {

        @Override
        public void onCallEnded(Call call)
        {
            CallEndCause cause = call.getDetails().getEndCause();

            Log.d(" G>>> check reason " , "Call ended, cause: " + cause.toString());

            //  if (call.getDetails().getEndCause() == CallEndCause.CANCELED)



            if (call.getDetails().getEndCause() == CallEndCause.NO_ANSWER)
            {
                DebugReportOnLocat.e(LOG_TAG, "missed call from user,getRemoteUserId=>" + call.getRemoteUserId());

                // sendNotification(call.getRemoteUserId());
                // displayNotification(call.getRemoteUserId());
                // updateNotification(call.getRemoteUserId());

                notifyUser(call.getRemoteUserId());
            }
            else if (call.getDetails().getEndCause() == CallEndCause.HUNG_UP)
            {
                DebugReportOnLocat.e(LOG_TAG, " Missed call from user,getRemoteUserId=>" + call.getRemoteUserId());


                // sendNotification(call.getRemoteUserId());
                // displayNotification(call.getRemoteUserId());

                //notifyUser(call.getRemoteUserId());
            }

            isCallEnd=true;

            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call)
        {
            Log.d(LOG_TAG, "Call established");


        }

        @Override
        public void onCallProgressing(Call call)
        {
            Log.d(LOG_TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs)
        {
            Log.d(LOG_TAG, "Should send push notification");



        }
    }

    @SuppressWarnings("deprecation")
    public void notifyUser(String user)
    {

        DebugReportOnLocat.e(LOG_TAG, "notifyUser=>");

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, Dashboard.class);

        // use the flag FLAG_UPDATE_CURRENT to override any notification already
        // there
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification(R.drawable.logo, "Missed call from " + user, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.FLAG_NO_CLEAR;
        notification.setLatestEventInfo(this, "Missed call from " + user + " " + new SimpleDateFormat("hh:mm a").format(System.currentTimeMillis()), "", contentIntent);
        // NOTIFIY_ID is a random number I chose to act as the id for this notification
        notificationManager.notify(NOTIFIY_ID, notification);

    }


    @SuppressWarnings("deprecation")
    public void notifyUserIncomingCallCallRinging(String user)
    {

        //mAudioPlayer.stopRingtone();

        DebugReportOnLocat.e(LOG_TAG, "notifyUser=>");

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, IncomingCallScreenActivity.class);

        // use the flag FLAG_UPDATE_CURRENT to override any notification already
        // there
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification(R.drawable.logo, "Incoming call " + user, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL ;
        notification.setLatestEventInfo(this, "Incoming call from " + user + " " + new SimpleDateFormat("hh:mm a").format(System.currentTimeMillis()), "", contentIntent);
        // 10 is a random number I chose to act as the id for this notification
        notificationManager.notify(NOTIFIY_ID, notification);

    }

    public void sendNotification(String user)
    {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.logo, "Missed call from " + user, System.currentTimeMillis());

        Intent intent = new Intent(this, Dashboard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notification.setLatestEventInfo(getApplicationContext(), "Missed call from " + user + " " + new SimpleDateFormat("hh:mm a").format(System.currentTimeMillis()), "",
                pendingIntent);
        notificationManager.notify(9999, notification);

    }

    public static int numMessages = -1;
    NotificationManager mNotificationManager;

    @SuppressLint("NewApi")
    protected void displayNotification(String user)
    {

	/* Invoking the default notification service */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Missed call");

        mBuilder.setTicker("Missed call from " + user);
        mBuilder.setSmallIcon(R.drawable.logo);

	/* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);
        DebugReportOnLocat.e(LOG_TAG, "num msg=>" + numMessages);
        int msgCount = numMessages + 1;
        if (msgCount == 1)
        {
            mBuilder.setContentText(msgCount + " missed call");
        }
        else
        {
            mBuilder.setContentText(msgCount + " missed calls");
        }

	/* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this, Dashboard.class).putExtra("comingFromNoti", "true");

        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Dashboard.class);

	/* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        DebugReportOnLocat.e("missed call ", "Dashboard.notificationId=>" + SplashScreen.notificationId);
	/* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(SplashScreen.notificationId, mBuilder.build());
    }

    @SuppressLint("NewApi")
    protected void updateNotification(String user)
    {
        Log.i("Update", "notification");

	/* Invoking the default notification service */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Missed call");

        mBuilder.setTicker("Missed call from " + user);
        mBuilder.setSmallIcon(R.drawable.logo);

	/* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);

        DebugReportOnLocat.e(LOG_TAG, "num msg=>" + numMessages);
        int msgCount = numMessages + 1;
        if (msgCount == 1)
        {
            mBuilder.setContentText(msgCount + " missed call");
        }
        else
        {
            mBuilder.setContentText(msgCount + " missed calls");
        }

	/* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this, Dashboard.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Dashboard.class);

	/* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	/* Update the existing notification using same notification ID */
        mNotificationManager.notify(SplashScreen.notificationId, mBuilder.build());
    }

    private OnClickListener incomingClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };





    public class GetHomeDataAsync extends AsyncTask<Void, String, String>
    {



        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... params)
        {

            DebugReportOnLocat.e("web_service", " FR Constants.NxtAcId  :  " + Constants.NxtAcId);

            String respStr = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
            HttpPost httppost = new HttpPost(Constants.baseUrl_new + "subscriber/active_conversations");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


            try
            {
                if(Constants.NxtAcId==null){

                    SharedPreferences edt_notifications = getSharedPreferences("ID", 0);
                    Constants.NxtAcId= edt_notifications.getString("nxtAcId", "0");
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
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



                    for (int i = 0; i < jArray.length(); i++)
                    {

                        try
                        {

                            JSONObject jobj = jArray.getJSONObject(i);
                            JSONObject jobjIn = jobj.getJSONObject("user");

                            if(aliasName.equals(jobjIn.optString("nameAlias"))){


                                return Constants.baseUrl_Images + jobjIn.optString("avatar");

                            }

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }



                }
            } catch (JSONException e)
            {
                DebugReportOnLocat.e(e);
            }catch (NullPointerException e) {
                DebugReportOnLocat.e(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);


            try
            {
                if(result!=null){

                    Avtar=result;
                    imageLoader.displayImage(result, ivProfilePic, circularOptions);

                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }



    public void SubcriberContact(){







        try
        {


            AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.SUBSCRIBER_CONTACTS);
            req.setBody(JSONBody.contacts(Constants.NxtAcId,0));
            req.setUserInfo(HttpUri.SUBSCRIBER_CONTACTS);
            //final CategoryManager cManager = new CategoryManager(this);
            new AsyncHttpClient().executeJSONObject(mContext,req,
                    new ResultCallback() {

                        @Override
                        public void onCompleted(Exception e, String responseString, String methodInfo) {

                            GetContacts(responseString);




                        }

                        private void GetContacts(String respStr)
                        {



                            if(respStr!=null){




                                try
                                {
                                    JSONArray jArray = new JSONArray(respStr);
                                    FriendRequest model;
                                    for (int i = 0; i < jArray.length(); i++)
                                    {

                                        JSONObject jobj = jArray.getJSONObject(i);
                                        model = new FriendRequest();
                                        model.setUserId(jobj.optString("nxtAccountId"));
                                        model.setUserName(jobj.optString("nameAlias"));
                                        model.setDeletPlanID(jobj.optString("deletePlanId"));
                                        model.setRegistrationDate(jobj.optString("registrationDate"));
                                        model.setAvatarImage(Constants.baseUrl_Images + jobj.optString("avatar"));
                                        model.setUserStatus(StringEscapeUtils.unescapeJava(jobj.optString("status")));
                                        model.setSchool(jobj.optString("school"));


                                        if(aliasName.equals(jobj.optString("nameAlias"))){


                                            Avtar= Constants.baseUrl_Images + jobj.optString("avatar");


                                            //  Avtar=result;
                                            imageLoader.displayImage(Avtar, ivProfilePic, circularOptions);


                                            return;
                                        }


                                    }

                                } catch (JSONException e)
                                {
                                    DebugReportOnLocat.e(e);

                                }catch (IndexOutOfBoundsException e) {

                                    DebugReportOnLocat.e(e);

                                }
                                finally {




                                }


                            }

                        }


                    });



        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }
    }




}
