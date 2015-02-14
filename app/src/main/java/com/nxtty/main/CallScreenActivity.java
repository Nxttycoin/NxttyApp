package com.nxtty.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nextapp.sinch.AudioPlayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.StaticUtility;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

public class CallScreenActivity extends Activity implements OnClickListener
{

    static final String LOG_TAG = CallScreenActivity.class.getSimpleName();

    private AudioPlayer mAudioPlayer;

    private Button mEndCallButton;

    private Call mCall;

    public static long mCallStart;
    String callDuration;
    private TextView mCallDuration;
    private ImageLoader imageLoader;
    private DisplayImageOptions circularOptions;
    Context context;
    // private TextView mCallState;
    String inCallNumber="";
    private TextView mCallerName, outGoingClUser;

    private Timer mTimer;

    private UpdateCallDurationTask mDurationTask;

    private LinearLayout outGoingClLayout, inClLayout;
    private String comeFromIncomingClScreen = "", name = "", Avtar = "";

    // private Bitmap AvtarIN = null;

    private ImageView outGoingClProfilePic, outGoingProfPicWaiting;
    // mute functionality from downloaded source on Oct 07 2014
    private ImageView speaker, mute;
    public boolean isSpeakerOn = false;
    public static boolean isMuteOn = false;
    private class UpdateCallDurationTask extends TimerTask
    {

        @Override
        public void run()
        {
            CallScreenActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    updateCallDuration();
                }
            });
        }
    }



    @Override
    public void onPause()
    {
        super.onPause();


        if(!isCallEnd ){
            notifyUser(mCall.getRemoteUserId());
        }

//	Notify(mCall.getRemoteUserId(),
//		callDuration);
	
	/*if (mDurationTask != null)
	{
	    mDurationTask.cancel();
	}*/

    }

    // added from downloaded source on Oct 07
    @Override
    protected void onRestart()
    {
        // TODO Auto-generated method stub
        super.onRestart();
        mTimer = null;
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
    }
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callscreen);
        context =this;

        isCallEnd=false;

        isSpeakerOn = false;
        //PhoneCallListener phoneListener = new PhoneCallListener();
/*	TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
	    telephonyManager.listen(phoneListener,
	            PhoneStateListener.LISTEN_CALL_STATE);*/
        imageLoader = ImageLoader.getInstance();

        circularOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        Bitmap cicularBitmap = BitmapUtility.maskingRoundBitmap(bitmap);
                        return cicularBitmap;
                    }
                })
                .showImageOnFail(R.drawable.loading).build();

        outGoingClLayout = (LinearLayout) findViewById(R.id.outGoingClLayout);
        inClLayout = (LinearLayout) findViewById(R.id.inClLayout);
        outGoingClProfilePic = (ImageView) findViewById(R.id.outGoingClProfilePic);
        outGoingProfPicWaiting = (ImageView) findViewById(R.id.ivProfilePic);
        outGoingClUser = (TextView) findViewById(R.id.outGoingClUser);

        LinearLayout lnMainBG = (LinearLayout) findViewById(R.id.ln_callMainBG);

        File background = StaticUtility.getResideMenuBackgroundImagePath(CallScreenActivity.this);
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

        try
        {
            comeFromIncomingClScreen = getIntent().getExtras().getString("comeFromIncomingCl");
            name = getIntent().getExtras().getString("Name");
            Avtar = getIntent().getExtras().getString("Avtar");

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }

        if (Avtar != null)
        {
            imageLoader.displayImage(Avtar, outGoingClProfilePic, circularOptions);
            imageLoader.displayImage(Avtar, outGoingProfPicWaiting, circularOptions);

            // AvtarIN = BitmapUtility.decodeBitmapFromBase64(Avtar, 100, 100);
        }

        DebugReportOnLocat.e("", "comeFromIncomingClScreen=>" + comeFromIncomingClScreen);
        if (comeFromIncomingClScreen.length() > 0)
        {

            if (comeFromIncomingClScreen.equalsIgnoreCase("true"))
            {
                outGoingClLayout.setVisibility(View.GONE);
                inClLayout.setVisibility(View.VISIBLE);
                comeFromIncomingClScreen = "false";
            }
            else
            {
                outGoingClLayout.setVisibility(View.VISIBLE);
                inClLayout.setVisibility(View.GONE);
            }

        }

        speaker = (ImageView) findViewById(R.id.speaker);
        speaker.setOnClickListener(this);
        mute = (ImageView) findViewById(R.id.mute);
        mute.setOnClickListener(this);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        // mCallDuration.setVisibility(View.INVISIBLE);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        // mCallState = (TextView) findViewById(R.id.callState);
        mEndCallButton = (Button) findViewById(R.id.hangupButton);

        mCall = CurrentCall.currentCall;
        mCall.addCallListener(new SinchCallListener());

        mCallerName.setText(mCall.getRemoteUserId());
        // mCallState.setVisibility(View.GONE);
        // mCallState.setText(mCall.getState().toString());
        mAudioPlayer = new AudioPlayer(this);

        // if (AvtarIN != null)
        // {
        // outGoingClProfilePic.setImageBitmap(AvtarIN);
        //
        // }
        if (name != null)
        {
            outGoingClUser.setText(name);
        }
        mEndCallButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAudioPlayer.stopProgressTone();
                endCall();
            }
        });

    }




    @SuppressWarnings("deprecation")
    public void notifyUser(String user)
    {

        DebugReportOnLocat.e(LOG_TAG, "notifyUser=>");

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, CallScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // use the flag FLAG_UPDATE_CURRENT to override any notification already
        // there
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,  PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification(R.drawable.logo, "Incoming Call " + user+ " "+ inCallNumber, System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT|Notification.FLAG_AUTO_CANCEL ;
        notification.setLatestEventInfo(this, "Incoming Call " + user + " " + new SimpleDateFormat("hh:mm a").format(System.currentTimeMillis()), "", contentIntent);
        // 10 is a random number I chose to act as the id for this notification
        notificationManager.notify(10, notification);

    }
    @Override
    public void onResume()
    {
        super.onResume();
        isMuteOn = false;
    }


    boolean isCallEnd=true;
    public boolean isMissedCallNotify=true;

    @Override
    public void onBackPressed()
    {
        // User should exit activity by ending call, not by going back.

        AlertUtility.showConfirmDialog(context, context.getResources().getString(R.string.call_end) + " " ,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        endCall();
                    }
                });
    }

    private void endCall()
    {
        mCall.hangup();
        isCallEnd=true;

        finish();
    }

    private CharSequence formatTimespan(long timespan)
    {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration()
    {
        mCallDuration.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
        callDuration=formatTimespan(System.currentTimeMillis() - mCallStart).toString();
    }

    //   private class PhoneCallListener extends PhoneStateListener {
//
//	    private boolean isPhoneCalling = false;
//	  
//	    @Override
//	    public void onCallStateChanged(int state, String incomingNumber) {
//
//	        if (TelephonyManager.CALL_STATE_RINGING == state) {
//	            // phone ringing
//	           DebugReportOnLocat.ln( "RINGING, number: " + incomingNumber);
//	           inCallNumber=incomingNumber;
//	        }
//
//	        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
//	            // active
//	            DebugReportOnLocat.ln( "OFFHOOK");
//
//	            isPhoneCalling = true;
//	        }
//
//	        if (TelephonyManager.CALL_STATE_IDLE == state) {
//	            // run when class initial and phone call ended, need detect flag
//	            // from CALL_STATE_OFFHOOK
//	            DebugReportOnLocat.ln(  "IDLE number");
//
//	            if (isPhoneCalling) {
//
//	                Handler handler = new Handler();
//
//	                //Put in delay because call log is not updated immediately when state changed
//	                // The dialler takes a little bit of time to write to it 500ms seems to be enough
//	                handler.postDelayed(new Runnable() {
//
//	                    @Override
//	                    public void run() {
//	                        // get start of cursor
//	                          Log.i("CallLogDetailsActivity", "Getting Log activity...");
//	                            String[] projection = new String[]{Calls.NUMBER};
//	                            Cursor cur = getContentResolver().query(Calls.CONTENT_URI, projection, null, null, Calls.DATE +" desc");
//	                            cur.moveToFirst();
//	                            String lastCallnumber = cur.getString(0);
//	                            DebugReportOnLocat.ln("LastCallNumber::::"+lastCallnumber);
//	                    }
//	                },500);
//
//	                isPhoneCalling = false;
//	            }
//
//	        }
//	    }
//	}
//   
    @SuppressWarnings("deprecation")
    private void Notify(String notificationTitle, String notificationMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        Notification notification = new Notification(R.drawable.logo,
                name, System.currentTimeMillis());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(CallScreenActivity.this, notificationTitle,
                notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);
    }


    private class SinchCallListener implements CallListener
    {

        @Override
        public void onCallEnded(Call call)
        {
            mAudioPlayer.stopProgressTone();
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(LOG_TAG, "Call ended. Reason: " + cause.toString());
            // AlertUtility.showToast(CallScreenActivity.this, "" +
            // cause.toString());
            endCall();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call call)
        {
            Log.d(LOG_TAG, "Call established");

            isCallEnd=false;

            outGoingClLayout.setVisibility(View.GONE);
            inClLayout.setVisibility(View.VISIBLE);

            mAudioPlayer.stopProgressTone();
            // mCallState.setVisibility(View.GONE);
            // mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            mCallDuration.setVisibility(View.VISIBLE);
            mCallStart = System.currentTimeMillis();
            mTimer = new Timer();
            mDurationTask = new UpdateCallDurationTask();
            mTimer.schedule(mDurationTask, 0, 500);
        }

        @Override
        public void onCallProgressing(Call call)
        {
            Log.d(LOG_TAG, "Call progressing");

            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs)
        {
            Log.d(LOG_TAG, "Should send push");
        }

    }

    @Override
    public void onClick(View v)
    {
        if (v == speaker)
        {
            AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);

            if (isSpeakerOn)
            {
                audioManager.setSpeakerphoneOn(false);
                speaker.setImageResource(R.drawable.incall_cancel_speaker);
                isSpeakerOn = false;
            }
            else
            {

                audioManager.setSpeakerphoneOn(true);
                speaker.setImageResource(R.drawable.incall_sound_on);
                isSpeakerOn = true;

            }
        }
        else if (v == mute)
        {
            AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
            Log.e("###########", "  Mute CLicked  ");

            if (isMuteOn)
            {
                isMuteOn = false;
                mute.setImageResource(R.drawable.incall_mute);
                audioManager.setMicrophoneMute(false);
            }
            else
            {
                isMuteOn = true;
                mute.setImageResource(R.drawable.incall_mute_off);
                audioManager.setMicrophoneMute(true);
            }
        }
    }
    //future code bala
//    public void makecall(View view){ 
//	    try {
//	        Intent callIntent = new Intent(Intent.ACTION_CALL);
//	        callIntent.setData(Uri.parse("tel:NUMBER"));
//	        startActivity(callIntent);
//	        Toast.makeText(this, "TEST",Toast.LENGTH_LONG).show(); 
//
//	        Runnable showDialogRun = new Runnable() {
//	            public void run(){
//	                Intent showDialogIntent = new Intent(this, DialogActivity.class);
//	                startActivity(showDialogIntent);
//	            }
//	        };
//	        Handler h = new Handler();
//	        h.postDelayed(showDialogRun, 2000);
//	    } catch (ActivityNotFoundException activityException) {
//	        Throwable e = null;
//	        Log.e("helloandroid dialing example", "Callfailed", e); 
//	    }
//	}
}