package com.nextapp.sinch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.CurrentCall;
import com.nxtty.main.IncomingCallScreenActivity;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

public class SinchClientService {

    static final String LOG_TAG = SinchClientService.class.getSimpleName();

    private SinchClient mSinchClient = null;

    private CallClient mCallClient = null;

    private Context mContext;

    public void start(Context context, String userName) {
	if(mSinchClient!=null && mSinchClient.isStarted())
	{
	    return;
	}
	
        this.mContext = context.getApplicationContext();

//        mSinchClient = Sinch.getSinchClientBuilder().context(context).userId(userName)
//                .applicationKey("4a0688cb-4c89-464a-9435-998a4a31d298")
//                .applicationSecret("rt07kRHgjki1adaxh08JTg==")
//                .environmentHost("sandbox.sinch.com").build();
        
  
        
        try
	{
            
            mSinchClient = Sinch.getSinchClientBuilder().context(context).userId(userName)
                    .applicationKey("ded15c3f-9a09-4b1e-8a95-414a133afe28")
                    .applicationSecret("nZ4Kd9Riu0Grps72yjDsBg==")
                    .environmentHost("clientapi.sinch.com").build();
            
	    
	}
        catch (RuntimeException e) {
 DebugReportOnLocat.e(e);
 	}
        catch (Exception e)
	{
	     DebugReportOnLocat.e(e);
	}

        mSinchClient.setSupportCalling(true);
        
        ////////////////////
        mSinchClient.setSupportActiveConnectionInBackground(true);
        mSinchClient.setSupportPushNotifications(true);
       // mSinchClient.startListeningOnActiveConnection();
        ///////////////////////////////////////////////////
        
        mSinchClient.startListeningOnActiveConnection();

        mSinchClient.addSinchClientListener(new MySinchClientListener());
        mSinchClient.start();

        mCallClient = mSinchClient.getCallClient();
        mCallClient.addCallClientListener(new SinchCallClientListener());
    }

    public void stop() {
        mSinchClient.terminate();
    }

    public boolean isStarted() {
        return mSinchClient.isStarted();
    }

    public SinchClient getSinchClient() {
        return mSinchClient;
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            DebugReportOnLocat.e(LOG_TAG, "SinchClient error: " + error);
        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(LOG_TAG, "SinchClient started");
        }

        @Override
        public void onClientStopped(SinchClient client) {
            Log.d(LOG_TAG, "SinchClient stopped");
        }


        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    DebugReportOnLocat.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client,
                ClientRegistration clientRegistration) {
        }
    }


    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(LOG_TAG, "Incoming call");
            CurrentCall.currentCall = call;
            Intent intent = new Intent(mContext, IncomingCallScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

        }
    }

}
