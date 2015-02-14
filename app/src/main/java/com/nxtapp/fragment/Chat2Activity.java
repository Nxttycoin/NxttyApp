package com.nxtapp.fragment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
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
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aniways.Aniways;
import com.nextapp.data.ChatInfoManager;
import com.nextapp.imageloader.ImageLoader_Nxtty;
import com.nextapp.tasks.CallPostWebseviceTask;
import com.nextapp.webservice.JsonParserUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.classes.ChatModel;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.KeyboardUtility;
import com.nxtapp.utils.LoadMoreFromTopListView;
import com.nxtapp.utils.LoadMoreFromTopListView.OnLoadMoreListener;
import com.nxtapp.utils.StaticUtility;
import com.nxtty.main.CallScreenActivity;
import com.nxtty.main.CurrentCall;
import com.nxtty.main.ImageViewDisplay;
import com.nxtty.main.R;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;

public class Chat2Activity extends Activity
{
    private static String TAG = "Chat2Activity";
    private static final int ACTION_REQUEST_CAMERA = 1001;
    private static final int ACTION_REQUEST_CROP = 1002;
    private static final int ACTION_REQUEST_GALLERY = 1003;

    private LoadMoreFromTopListView lvChat;
    private ArrayList<ChatModel> chatModelList = new ArrayList<ChatModel>();
    private ChatAdapter chatAdapter;
    private EditText textEditor;
    private String contactID;
    private ImageView ivChatBack, ivCall, ivAttachImage, ivProfImageOut;// ,
    // ivDelete;
    private String name = "";
    private TextView txtHeading, txtLastseen;
    private ImageLoader imageLoader;
    private DisplayImageOptions options,circularOptions;
    String Avtar;
    String imagePath = "";
    ImageLoader_Nxtty imageloader_nxtty;
    File profileImage;

    private int offset = 0;
    private static int jsonArrayItem = -1;

    private Handler handlerWait = new Handler();
    private Runnable runnableWait = new Runnable()
    {

        @Override
        public void run()
        {

            callWSUnreadMessages();
        }
    };



    ChatInfoManager chatInfoManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Aniways.init(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.chat_screen);

        Constants.ISinChatScreen = true;


        try
        {

            chatInfoManager = new ChatInfoManager(this);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            contactID = getIntent().getExtras().getString("contactID");
            name = getIntent().getExtras().getString("Name");
            Avtar = getIntent().getExtras().getString("Avtar");

        } catch (Exception e)
        {
        }

        initObjects();

        callGetSubscribersConversation(false);

        try
        {
            //  decryptMessageTest();

        } catch (Exception e)
        {
            // TODO: handle exception
        }

        // new GetChatDataAsync().execute();

        // new GetChatFromServerAsync("Start").execute();

    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        Constants.ISinChatScreen = true;
    }


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

    private void initObjects()
    {

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .build();

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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).discCacheExtraOptions(720  , 1280 , null).build();

        ImageLoader.getInstance().init(config);


        lvChat = (LoadMoreFromTopListView) findViewById(R.id.lv_aniwaysListchat);
        lvChat.setOnLoadMoreListener(onLoadMore);
        // lvChat.setOnItemLongClickListener(onListLongItemClick);
        txtHeading = (TextView) findViewById(R.id.txt_head_chat);
        txtLastseen = (TextView) findViewById(R.id.txt_lastseen);
        txtLastseen.setVisibility(View.GONE);
        txtHeading.setText(name);

        ivProfImageOut = (ImageView) findViewById(R.id.iv_ProfileImageChatUser);
        if (Avtar != null)
        {
            imageLoader.displayImage(Avtar, ivProfImageOut, circularOptions);
        }

        ivAttachImage = (ImageView) findViewById(R.id.iv_attachmentChat);
        ivAttachImage.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                openUploadoption();
            }
        });

        ivCall = (ImageView) findViewById(R.id.iv_call);
        ivCall.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                if (name != null && CurrentCall.mSinchClientService != null)
                {

                    CallClient callClient = CurrentCall.mSinchClientService.getSinchClient().getCallClient();
                    Call call = callClient.callUser(name);
                    CurrentCall.currentCall = call;
                    startActivity(new Intent(Chat2Activity.this, CallScreenActivity.class).putExtra("comeFromIncomingCl", "false").putExtra("Name", name).putExtra("Avtar", Avtar));

                }
                else
                {

                    Toast.makeText(Chat2Activity.this, "This facility is not available now.  Please try agin later! ", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ivChatBack = (ImageView) findViewById(R.id.iv_chatBack);
        ivChatBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                backManage();
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
                }else if(s.toString().startsWith("\n")){
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

                String message = Aniways.encodeMessage(textEditor.getText());

                DebugReportOnLocat.e(TAG, "message=>" + message);

                if (message.length() > 0)
                {
                    textEditor.setText("");
                    try
                    {
                        new encryptAsynk(message, false).execute();

                    } catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        DebugReportOnLocat.e(e);
                        AlertUtility.showToast(Chat2Activity.this, getString(R.string.err_unknown));
                    }
                }
                else
                {
                    AlertUtility.showToast(Chat2Activity.this, "Please Enter Text");
                }

            }
        });
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

    private OnLoadMoreListener onLoadMore = new OnLoadMoreListener()
    {

        @Override
        public void onLoadMore()
        {

            callGetSubscribersConversation(true);
        }
    };

    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        backManage();
    }

    protected void backManage()
    {
        // TODO Auto-generated method stub
        try
        {
            hideKeyBoard();
            Intent data = new Intent();
            data.putExtra("last_message", chatModelList.get(chatModelList.size() - 1));
            data.putExtra("contact_id", contactID);
            setResult(Activity.RESULT_OK, data);



        } catch (Exception e)
        {

        }

        try
        {
            new DeleteUnUsedMsgAsynk(Constants.NxtAcId,contactID).execute();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        finish();

    }

    private void hideKeyBoard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
        {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void callGetSubscribersConversation(final boolean isLoadMore)
    {
        if (offset < 0)
        {
            lvChat.onLoadMoreComplete();
            return;
        }

        System.out.println("id>> contactID>>"+contactID+ " nxt>>> "+Constants.NxtAcId);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
        nameValuePairs.add(new BasicNameValuePair("contactID", contactID));
        nameValuePairs.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

        Log.d("web_service", Constants.NxtAcId + " GetSubscribers " + contactID);

        CallPostWebseviceTask task = new CallPostWebseviceTask(Chat2Activity.this, !isLoadMore, "conversation", nameValuePairs)
        {
            @SuppressLint("SimpleDateFormat")
            @Override
            protected void onPostExecute(String result)
            {
                // TODO Auto-generated method stub
                super.onPostExecute(result);

                try
                {

                    if (result != null)
                    {

                        DebugReportOnLocat.e("log_tag", "callGetSubscribersConversation,Responce Main : " + result);
                        JSONArray jsonArray = new JSONArray(result);
                        jsonArrayItem = 0;
                        if (jsonArray.length() > 0)
                        {

                            File imagesFolderPath = getExternalFilesDir("images");


                            if (jsonArray.length() > 0)
                            {
                                checkForDecryptChat(jsonArray, imagesFolderPath, jsonArray.getJSONObject(jsonArrayItem), "Subscribers");
                            }

                        }
                        if (jsonArray.length() < 25)
                        {
                            offset = -1;
                        }
                        else
                        {
                            offset += Constants.OFFSET_INCR;
                        }


                        // Display last seen...
                        for (int i = chatModelList.size() - 1; i >= 0; i--)
                        {
                            ChatModel chatModel = chatModelList.get(i);
                            if (chatModel.getSeenDate() != null)
                            {
                                txtLastseen.setText("last seen " + StaticUtility.getLastSeenFromGMTTime(chatModel.getSeenDateLong()));
                                txtLastseen.setVisibility(View.VISIBLE);
                                break;
                            }
                        }

                    }
                } catch (Exception e)
                {
                    DebugReportOnLocat.e(e);
                }

                if (!isLoadMore)
                {
                    if (chatAdapter != null && chatAdapter.getCount() <= 0)
                    {
                        KeyboardUtility.showKeypad(Chat2Activity.this);
                    }

                    callWSUnreadMessages();
                }
                else
                {
                    lvChat.onLoadMoreComplete();
                }

            }
        };

        task.execute();
    }

    private void checkForDecryptChat(JSONArray jsonArray, File imagesFolderPath, JSONObject jobj, String comingFrom) throws JSONException
    {


        if (jobj.optString("body").contains("errorCode"))
        {

        }
        else
        {

            if (jobj.optString("body").contains("nonce"))
            {
                DebugReportOnLocat.e(TAG, "checkForDecryptChat contains nonce");
                JSONObject jsonObject;
                try
                {
                    jsonObject = new JSONObject(jobj.optString("body"));
                    String nonce = jsonObject.getString("nonce");
                    String data = jsonObject.getString("data");

                    new Chat2Activity.decryptAsynk(jobj.optString("id"),jsonArray, nonce, data, jobj.optString("senderId"), imagesFolderPath, jobj, comingFrom,"True").execute();

                    // chatMessage = Chat2Activity.decryptMessage(nonce, data);
                } catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    DebugReportOnLocat.e(e);
                }

            }
            else
            {


                new Chat2Activity.decryptAsynk(jobj.optString("id"),jsonArray, "", "", jobj.optString("senderId"), imagesFolderPath, jobj, comingFrom,"false").execute();





            }
        }

    }

    private void updateListData(int selectionPos)
    {
        if (chatAdapter == null)
        {
            chatAdapter = new ChatAdapter();
            lvChat.setAdapter(chatAdapter);

            // new deleteUnUsedMsgAsynk().execute();
        }
        else
        {
            chatAdapter.notifyDataSetChanged();

            // new deleteUnUsedMsgAsynk().execute();
        }
        if (selectionPos > 0)
        {
            lvChat.setSelection(selectionPos);
        }

    }

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

            if(isFile){


                String response = encryptMessage(msg, isFile);

                DebugReportOnLocat.ln(" encryptMessage response "+response);
                return response;

            }else{

                DebugReportOnLocat.ln("msg-->> "+msg);
                String unicodeString = convertToUnicodeEscaped(msg);

                DebugReportOnLocat.e(TAG, "unicodeString=>" + unicodeString);

                String response = encryptMessage(unicodeString, isFile);

                return response;
            }

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
                        callWSSendMessage(msg, result, isFile, null);
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

            DebugReportOnLocat.ln(">>G encrypt,recipient=> " + contactID);

            DebugReportOnLocat.ln(">>G encrypt, messageToEncrypt=> " + content);

            nameValuePairs.add(new BasicNameValuePair("recipient", contactID));

            if (!isFile)
            {
                nameValuePairs.add(new BasicNameValuePair("messageToEncrypt", content));
                nameValuePairs.add(new BasicNameValuePair("messageToEncryptIsText", "true"));
            }
            else if (!content.equals(""))
            {
                nameValuePairs.add(new BasicNameValuePair("messageToEncrypt", content));
                nameValuePairs.add(new BasicNameValuePair("messageToEncryptIsText", "false"));
            }

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


    //for Testing large File
    public String encryptMessagefile(String content, boolean isFile){


        try
        {



            MultipartEntity entityBuilder = new MultipartEntity();


            String secretPhrase = Constants.sharedPreferences.getString("SecretPhrase", "");
            DebugReportOnLocat.ln(">>G encrypt,secretPhrase=> " + secretPhrase);



            DebugReportOnLocat.ln(">>G encrypt,recipient=> " + contactID);

            DebugReportOnLocat.ln(">>G encrypt, messageToEncrypt=> " + content);





            DebugReportOnLocat.e("requestType", "encryptTo");
            entityBuilder.addPart("secretPhrase", new StringBody(secretPhrase));
            entityBuilder.addPart("recipient", new StringBody(contactID));

            entityBuilder.addPart("messageToEncrypt", new StringBody(content));
            entityBuilder.addPart("messageToEncryptIsText", new StringBody("false"));

            InputStream is;
            String line;
            String result = null;
            DebugReportOnLocat.ln("callPostMethodWithMultipart url: " + Constants.baseUrl+ "?");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.baseUrl);

            if (entityBuilder != null)
            {
                httpPost.setEntity(entityBuilder);
            }

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entityResponse = response.getEntity();
            is = entityResponse.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null)
            {

                sb.append(line + "\n");
            }

            is.close();
            result = sb.toString();
            // Log.i("web_service", "result: " + result);
            return result;



        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }
        return content;
    }



    public class decryptAsynk extends AsyncTask<String, String, String>
    {

        String nonce = "", data = "", senderId = "", comingFrom = "";
        File imagesFolderPath;
        JSONObject jobj;
        JSONArray jsonArray;
        String trueOrFalse="";
        String id="";

        public decryptAsynk(String id,JSONArray jsonArray, String nonce, String data, String senderID, File imagesFolderPath, JSONObject jobj, String comingFrom,String trueOrFalse)
        {
            this.nonce = nonce;
            this.data = data;
            this.senderId = senderID;
            this.imagesFolderPath = imagesFolderPath;
            this.jobj = jobj;
            this.comingFrom = comingFrom;
            this.jsonArray = jsonArray;
            this.trueOrFalse=trueOrFalse;
            this.id=id;
        }

        @Override
        protected String doInBackground(String... params)
        {

            if (Constants.NxtAcId.equalsIgnoreCase(jobj.optString("senderId")))
            {
                senderId = jobj.optString("receiverId");
            }
            else
            {
                senderId = jobj.optString("senderId");
            }



            String chatMsg="";
            if(trueOrFalse.equals("false")){

                String json = "";
                JSONObject jObj = null;


                try {

                    JsonParserUtility jsonUtility = new JsonParserUtility();

                    ChatModel chat=   jsonUtility.parseChat(imagesFolderPath, jobj, "");


                    File imagesFolderPath = getExternalFilesDir("images");

                    String filePath=imagesFolderPath.getAbsolutePath()+"/"
                            +chat.getImagePath() ;

                    DebugReportOnLocat.ln(" filePath >>> "+filePath);

                    File file = new File(filePath);


                    DebugReportOnLocat.ln(" file  "+file.getName());


                    if (file.exists()) {

                        DebugReportOnLocat.ln(" File is exist ");

                    }else{





                        InputStream is = null;

		         /*       // request method is POST
		                // defaultHttpClient
		                DefaultHttpClient httpClient = new DefaultHttpClient();
		                HttpPost httpPost = new HttpPost(chat.getImagePath());
		                //httpPost.setEntity(new UrlEncodedFormEntity(params));
		 
		                HttpResponse httpResponse = httpClient.execute(httpPost);
		                HttpEntity httpEntity = httpResponse.getEntity();
		                is = httpEntity.getContent();*/

                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        // String paramString = URLEncodedUtils.format(params, "utf-8");
                        //  url += "?" + paramString;
                        HttpGet httpGet = new HttpGet(Constants.baseUrl_Images +chat.getImagePath());

                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    is, "iso-8859-1"), 8);
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            is.close();
                            json = sb.toString();
                        } catch (Exception e) {
                            DebugReportOnLocat.e("Buffer Error", "Error converting result " + e.toString());
                        }

                        // try parse the string to a JSON object
                        try {
                            jObj = new JSONObject(json);
                        } catch (JSONException e) {
                            DebugReportOnLocat.e("JSON Parser", "Error parsing data " + e.toString());
                        }
		                
		                /*
			        HttpClient client = new DefaultHttpClient();
			        HttpGet request = new HttpGet(chat.getImagePath());
			        
		            String nonce = null,data = null;
		            
		            HttpResponse response = client.execute(request);
		            InputStream in;
		            in = response.getEntity().getContent();
		            BufferedReader reader = new BufferedReader(
		                    new InputStreamReader(in));
		            StringBuilder str = new StringBuilder();
		            String line = null;
		            while ((line = reader.readLine()) != null) {
		                str.append(line);
		            }
		            in.close();
		            
		            JSONObject jsonObj = new JSONObject(str.toString().replace("\"", "\\\""));
		            */
                        nonce = jObj.getString("nonce");
                        data = jObj.getString("data");

                        chatMsg = decryptMessage(nonce, data, senderId,trueOrFalse);
                        byte[] bytevalue;

                        bytevalue = Hex.decodeHex(chatMsg.toCharArray());


                        writeToFile(filePath,bytevalue);

                    }

                }catch(IllegalStateException e){

                    DebugReportOnLocat.e(e);
                }catch (Exception e) {
                    DebugReportOnLocat.e(e);
                }

                return "";
            }else{

                try
                {




                    if(chatInfoManager.isExists(id)){

                        ChatModel chatModel=chatInfoManager.selectMsg(id);
                        chatMsg=chatModel.getBody();

                    }else{

                        chatMsg = decryptMessage(nonce, data, senderId,trueOrFalse);

                        //  ChatModel chatModel=new ChatModel();

                        JsonParserUtility jsonUtility = new JsonParserUtility();

                        ChatModel chat=   jsonUtility.parseChat(imagesFolderPath, jobj, chatMsg);

                        chatInfoManager.add(chat);

                    }



                }

                catch (IllegalStateException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }








            return chatMsg;
        }

        private boolean isIdExist(String id2)
        {



            return false;
        }

        @Override
        protected void onPostExecute(String result)
        {

            super.onPostExecute(result);
            JsonParserUtility jsonUtility = new JsonParserUtility();

            if (comingFrom.equalsIgnoreCase("Subscribers"))
            {
                chatModelList.add(0, jsonUtility.parseChat(imagesFolderPath, jobj, result));
            }
            else if (comingFrom.equalsIgnoreCase("Unread"))
            {
                chatModelList.add(jsonUtility.parseChat(imagesFolderPath, jobj, result));
            }

            updateListData(chatModelList.size());

            jsonArrayItem = jsonArrayItem + 1;

            if (jsonArray.length() > 0 && jsonArrayItem < jsonArray.length())
            {
                if (comingFrom.equalsIgnoreCase("Subscribers"))
                {
                    try
                    {
                        // jsonArrayItem = jsonArrayItem+1;
                        checkForDecryptChat(jsonArray, imagesFolderPath, jsonArray.getJSONObject(jsonArrayItem), "Subscribers");
                    } catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        DebugReportOnLocat.e(e);
                    }
                }
                else if (comingFrom.equalsIgnoreCase("Unread"))
                {
                    try
                    {
                        // jsonArrayItem = jsonArrayItem+1;
                        checkForDecryptChat(jsonArray, imagesFolderPath, jsonArray.getJSONObject(jsonArrayItem), "Unread");
                    } catch (JSONException e)
                    {

                        DebugReportOnLocat.e(e);
                    }
                }
            }
        }

    }

    public static String decryptMsg = "";

    private static String decryptMessage(String nonce, String data, String senderId,String trueOrFalse)
    {

        HttpClient httpclient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 15000);
        String url = Constants.baseUrl + "?";
        HttpPost httppost = new HttpPost(url);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        try
        {

            String secretPhrase = Constants.sharedPreferences.getString("SecretPhrase", "");
            DebugReportOnLocat.e(TAG, "decrypt,Constants.secretPhrase=>" + secretPhrase);

            nameValuePairs.add(new BasicNameValuePair("requestType", "decryptFrom"));
            nameValuePairs.add(new BasicNameValuePair("secretPhrase", secretPhrase));
            nameValuePairs.add(new BasicNameValuePair("account", senderId));

            DebugReportOnLocat.e(TAG, "decrypt,nonce=>" + nonce);
            DebugReportOnLocat.e(TAG, "decrypt,data=>" + data);
            DebugReportOnLocat.e(TAG, "decrypt,senderId=>" + senderId);

            // nameValuePairs.add(new BasicNameValuePair("account",
            // Constants.recipient));
            nameValuePairs.add(new BasicNameValuePair("data", data));
            nameValuePairs.add(new BasicNameValuePair("decryptedMessageIsText", trueOrFalse));//"true"
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

            DebugReportOnLocat.e(TAG, "  responce for decrypt-->  " + decryptMsg);

        } catch (ClientProtocolException e)
        {
        } catch (IOException e)
        {
        }
        return decryptMsg;
    }




    //Testing

    public static String decryptMsgTest = "";

    private static String decryptMessageTest()
    {

        String nonce="f0d29867e9b5eade6e9b83aa25fcb7d55870cb8e48ee8bf710234e6548c21826";
        String senderId="1660808656669148223";

        String scretPhase="gnanaoly@sdi.la_123456_puducherry";
        String data="5b18b681b232d9f697c9314d60c25a984cb76769a7e8a0aa3b9e50004f53c6421ab2ae301df619cd976a0a6a057d7512b10baeb52a048c4ec8a7888fc35b5f6"
                + "cfad9b6de35985aee2370da6d5265edf846d538a273e57966ff0c1b4dfe76ecec1e83ac46c9ffdcb0f42475f9465d46bd4c08a1b4efbe6d0b526b46711750c78fe"
                + "4eddbbc479eece233a77147c4b706320774cc8c58e17f4de762d15b135aab256dfe6126288583ec3879657716ebd6d9c797db1e346faa6a56823c1455322c75d3c69"
                + "8a7bc09d604796943750d617b996a032a97875f8c66c0d0b05dd3f1cff6060137f2ef806bb7d1b5bbce1e1221af63f1b4404b6850ba7665c7bfcaf236012b88474a0c1"
                + "95779602a07b38aa4dbb12b130f1a3b93e8e7dc9e8372addc771837ea1257542dcbda7650f4bed5d9b933ea649b208182b444b875decd2dbcc5b950be78977f7b4cfab9"
                + "42aea8338d4708159dcd7718a9609443e95fb9a58870f7a84d26325130278b3bc032af3bf0a00aa47f5b3b808071fb413e88e7052d1ea03060dbcb58bdd6e3c345a7c4f8"
                + "61278933ae88353d9138397fd46ec99ff9821434af4fd429d6f7a4c9d35b70d979187f65d89b325622ac360b138cf064da32004f677cee71362347b852a58643f2e2f78b7"
                + "fd8a1c1f01b7c4bc81cd1f9ae25c15e1480efe6529f8221d6ef014704cc767e4607aa8e7d64a5e506a15eb74d3d7d9e0a61dc027344e56531d49063fff2b30a081d75f0f"
                + "14a6522a42fb6a61b7442d9766b3376d9c1ead128dc0a20e45da461ff476b9b08fc72395660120dcb914cad24739697f09f958ee94f75b5ef2cf0ed3cda25b1cad26a683"
                + "786d14fc259451797db3a810494a0ed2379eba600ad8abf6f068b550c8f427e5b2ce84ed77f1c74bc57e4a35950c980a95c01cfa8ee01cd51d9a9d9867f4fde6aee89ca7"
                + "8b0b276a56c990735302e5b116100b2480436a6a2165ca7ed397ad41f69f6ba67e2ed5bfd6950f4cf2718e79b3a959ad29e83be065a009b2a1e13cb8efa39eee0b2602c3"
                + "900cf20e70c5095aca36cbac3d761127d9d93269383726cdaa8e23a5e655e2916ac803e22ebd7e162a072f7f9c75fe5730fb47d2df760b2fcb216e72d565cc9a4668d"
                + "c1b7791b2158504c6aa20d7d07f95f7045a2c5917b1436829c791277e70d449c4ea3357eb078d1cffd967e88db2887c041e024874fa40c57d2b6ab599a224051bc5f2b2"
                + "02bc40b6d1e462e2c142193d58a2648922372d806db24174ce434c9d97f2d05a272efde87f9518c6d5207b77928f6917f39fdb3ff3f71bcc7d3ae355dbf1a824edc21a9"
                + "b2b0f4e2a31ccfd689602e005c6d1b20b60f3573cd997269c03628bdfb10e819678e78dc287a67274e090bedc5182661fb384026fc3d0e53d474b12fc51e1c88ee9fe4"
                + "77e765a711cb5dd8f831120e8e42b03be5c4c87401cb7d3f593c6559f71ba5011317c649db74c36702e920a773c53e94115b988cdb3a50f36578616f1c58277a103dd9"
                + "c50f45861978e7fd518722116ac9afd132cdd3eabef8e40f5dca563b4d4d0fee581ce610f596a086da1ca81805c33417ed27d0c6182d7b5b35d0bf6af48bb4e80b239c8"
                + "3e2325c0cfc23d8a53f6037b59722864431ee0b31daabafa128783f44576c3b97d474c8cb46909057fde8ba5f0b3b8c830d7fc3964f255fec0399135ef7d57356d9f2e13"
                + "867dac3b56e73fd2c3cec85c0010127ef31932919e591edf0be172ed755dc31d440d193a7ab9a4c4eccfa57548ed4eafea51e08c48e59fab9907d11a62896b960ce18d13"
                + "048e716ace0120449892a6a9b91249ea9592718cc06627caab0bb33a2d44345df09cd9230975a03c06fde90e75d6b9e6ec74344420d004a1712d0401b96bc4e321511a521"
                + "f6ba2437f4b2d67bbce2124833a5c5d34b0a923f79131c7faf5397a16535b0f6a79c7cc439b25e1a50f70e03fc857b93bf78386b52ac96371e3d30d105fe914f2ee7f125"
                + "4f6b06582edcf7a2d8debf30db1588a58383a5c2dd9bc8a3be07efa8f673639ff25ea1eb59978fb3205fa31f1d1a57820e216c5415f891b380ca89d175052ed289aeffe8"
                + "e5f3c6130f437fb8f1acbb43e12c2e36f49c017f82900de7064a31056bb87362dd9cf131962d5daf0bdf9d9551cc1851643f406412c5da61bf55cf2c3d191aafc3944235"
                + "ab5b5db56ddf42e88cfcd9db4d13edddd869af3e67a8685cb119f1e347676aadd101b8f0802613969268229f567693c924a021a9624fa6d03af8a4ea9b8b94b2ba232f2"
                + "d2324c012724466aec2890f3c01bb326c19f057cc2ea79c754189ffbc488fa513f71cec94bbd2513e24c3bc470a9145370132d6dcea1bcd9f295a07972060b59453ae73"
                + "65f6f17fff795537e0de851a6ab7bbb0000aacdea092f72eaa3fccd957846b8be3c8f85b31bfcfd2912c459324b884edd729b0b8352213c1c25f0712020e94176f698d"
                + "704021669a52b9d592704b396608c706385489c82c5dd19288f7ef8bf8d8df1abf502635798a38e98b146fe6449db218347b27aae3ebb0e5569f59f452f0053174c359f"
                + "082842f31e5edde36ef694a51b8e99b66ff8ea8687ff54dab58491041604b896ab87b052adc21da49084b5d411dd68c873d42d210a8d88c56d2b60ff3a2da2fef4200f"
                + "5bdee49a917fe78a17c74603bb65bcfa7da284df5bc63607706ec1db9df8193b3d5727396cf9553eb0c9089762079e7406cd5cac8a95674ecf2fba078be785e4c5a80b4"
                + "860d0293"
                + "";

        HttpClient httpclient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 15000);
        String url = Constants.baseUrl + "?";
        HttpPost httppost = new HttpPost(url);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        try
        {

            // String secretPhrase = Constants.sharedPreferences.getString("SecretPhrase", "");
            DebugReportOnLocat.e(TAG, "decrypt,Constants.secretPhrase=> GGG " + scretPhase);

            nameValuePairs.add(new BasicNameValuePair("requestType", "decryptFrom"));
            nameValuePairs.add(new BasicNameValuePair("secretPhrase", scretPhase));
            nameValuePairs.add(new BasicNameValuePair("account", senderId));

            DebugReportOnLocat.e(TAG, "decrypt,nonce=>" + nonce);
            DebugReportOnLocat.e(TAG, "decrypt,data=>" + data);
            DebugReportOnLocat.e(TAG, "decrypt,senderId=>" + senderId);

            // nameValuePairs.add(new BasicNameValuePair("account",
            // Constants.recipient));
            nameValuePairs.add(new BasicNameValuePair("data", data));
            nameValuePairs.add(new BasicNameValuePair("decryptedMessageIsText", "false"));//"true"
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
                    DebugReportOnLocat.e(TAG, "decrypt msg escape=> GGG " + decryptMsg);

                }
            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                DebugReportOnLocat.e(e);
                decryptMsg = "";
            }

            DebugReportOnLocat.e(TAG, "  responce for decrypt-->  " + decryptMsg);

        } catch (ClientProtocolException e)
        {
        } catch (IOException e)
        {
        }
        return decryptMsg;
    }


    String byteString = "";

    private void callWSSendMessage(final String msg, final String content, final boolean isFile, final Uri uri) throws Exception
    {

	/*Thread thread = new Thread()
	{
	    public void run()
	    {*/

        try
        {

            DebugReportOnLocat.ln("Thread Running");

            if (content == null)
            {
                return;
            }
            DebugReportOnLocat.e("log_tag", "content: " + content);

            MultipartEntity entityBuilder = new MultipartEntity();

            SharedPreferences pref = getSharedPreferences("ID", 0);
            Constants.NxtAcId = pref.getString("nxtAcId", "0");

            DebugReportOnLocat.e("Chat2", "Constants.NxtAcId: " + Constants.NxtAcId);
            entityBuilder.addPart("nxtID", new StringBody(Constants.NxtAcId));
            entityBuilder.addPart("contacts", new StringBody(contactID));
            entityBuilder.addPart("key", new StringBody(Constants.ParamKey));

            if (!isFile)
            {
                entityBuilder.addPart("message", new StringBody(content));
                // entityBuilder.addPart("message", new
                // StringBody(content,
                // ContentType.DEFAULT_BINARY));
            }
            // else if (!content.equals(""))
            else
            {
                entityBuilder.addPart("message", new StringBody(""));



                byte[] byteData = content.getBytes();

                ByteArrayBody byteArrayBody = new ByteArrayBody(byteData, "image"); // second parameter is the name of the image (//TODO HOW DO I MAKE IT USE THE IMAGE FILENAME?)

                entityBuilder.addPart("file", byteArrayBody);

//			        
//			Bitmap bitmap = null;
//			try
//			{
//			    if (uri != null)
//			    {
//				bitmap = MediaStore.Images.Media.getBitmap(Chat2Activity.this.getContentResolver(), uri);
//			    }
//			} catch (FileNotFoundException e1)
//			{
//			    
//			    e1.printStackTrace();
//			} catch (IOException e1)
//			{
//			    // TODO Auto-generated catch block
//			    e1.printStackTrace();
//			}
//			byte[] byteArray = null;
//			if (bitmap != null)
//			{
//
//			    DebugReportOnLocat.ln( bitmap.getWidth() + " bitmap " + bitmap.getHeight());
//			   
//			    byteArray = BitmapUtility.convertBitmapToByteArray(bitmap);
//			   
//			    DebugReportOnLocat.ln( "byteArray=>" + byteArray);
//			   
//			    byteString = String.valueOf(byteArray);
//			   
//			    DebugReportOnLocat.ln("byteString=>" + byteString);
//
//			    ByteArrayBody byteArrayBody = new ByteArrayBody(byteArray, "image");
//
//			    entityBuilder.addPart("file", byteArrayBody);
//			}
                File f = null;
                try
                {

                    f = new File(getExternalCacheDir(), "tempsend.png");
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
            }

            CallPostWebseviceTask task = new CallPostWebseviceTask(Chat2Activity.this, true, "send_message", entityBuilder)
            {
                @Override
                protected void onPostExecute(String result)
                {

                    super.onPostExecute(result);

                    try
                    {
                        String imagePath= "";
                        DebugReportOnLocat.ln(" Chat >>>>>>"+result);
                        try
                        {
                            JSONObject jo = new JSONObject(result);

                            imagePath= jo.getString("image");

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        DebugReportOnLocat.ln("imagePath >> "+imagePath);
                        // if (jo.getBoolean(Constants.STATUS_WKEY))
                        // {
                        ChatModel model = new ChatModel();
                        model.setSenderId(Constants.NxtAcId);
                        model.setReceiverId(contactID);
                        model.setBody(msg);
                        if (isFile)
                        {

                            try
                            {


                                File imagesFolderPath = getExternalFilesDir("images");

                                String filePath=imagesFolderPath.getAbsolutePath()+"/"
                                        +imagePath ;

                                DebugReportOnLocat.ln(" filePath >>> "+filePath);

                                // File file = new File(filePath);



                                byte[] bytevalue;

                                bytevalue = Hex.decodeHex(msg.toCharArray());


                                writeToFile(filePath,bytevalue);



                                // 743fcabb-1e7d-4001-a711-67bb48018603-140511133412.jpg

                                model.setImagePath(imagePath);

                            } catch (Exception e)
                            {
                                // TODO: handle exception
                                model.setImagePath("");
                            }

                        }
                        else
                        {
                            model.setImagePath("");
                        }
                        // model.setImagePath("file://" + uri);

                        // model.setImagePath(byteString);
                        model.setCreatedDate(Long.toString(System.currentTimeMillis()));
                        model.setFrom("Out");
                        model.setContent(isFile ? "Image" : "Text");

                        chatModelList.add(model);
                        if (chatAdapter == null)
                        {
                            chatAdapter = new ChatAdapter();
                            lvChat.setAdapter(chatAdapter);

                            // new deleteUnUsedMsgAsynk().execute();


                        }
                        else
                        {
                            chatAdapter.notifyDataSetChanged();

                            //new deleteUnUsedMsgAsynk().execute();
                        }

                        lvChat.setSelection(chatModelList.size());
                        // }
                        // else
                        // {
                        // AlertUtility.showToast(Chat2Activity.this,
                        // jo.getString(Constants.ERROR_DETAIL_WKEY));
                        // }

                    } catch (Exception e)
                    {
                        DebugReportOnLocat.e(e);
                        AlertUtility.showToast(Chat2Activity.this, getString(R.string.err_unknown));
                    }

                }
            };
            task.execute();

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }
	  /*  }
	};

	thread.start();*/

    }

    public static String toHex(byte[] bytes)
    {
        DebugReportOnLocat.e(TAG, "toHex");
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

    @Override
    protected void onPause()
    {
        Constants.ISinChatScreen = false;
        super.onPause();
    }

    @Override
    public void onDestroy()
    {

        Constants.ISinChatScreen = false;
        // TODO Auto-generated method stub
        if (handlerWait != null && runnableWait != null)
        {
            handlerWait.removeCallbacks(runnableWait);
            handlerWait = null;
            runnableWait = null;
        }
        super.onDestroy();

    }

    private void callWSUnreadMessages()
    {
        // Log.i("log_tag", "callWSUnreadMessages");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
        nameValuePairs.add(new BasicNameValuePair("contactID", contactID));
        nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

        CallPostWebseviceTask task = new CallPostWebseviceTask(Chat2Activity.this, false, "unread_messages", nameValuePairs)
        {
            @Override
            protected void onPostExecute(String result)
            {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                try
                {
                    JSONArray jsonArray = new JSONArray(result);
                    // JsonParserUtility jsonUtility = new JsonParserUtility();
                    File imagesFolderPath = getExternalFilesDir("images");
                    for (int i = (jsonArray.length() - 1); i >= 0; i--)
                    {
                        checkForDecryptChat(jsonArray, imagesFolderPath, jsonArray.getJSONObject(i), "Unread");
                        // chatModelList.add(jsonUtility.parseChat(imagesFolderPath,
                        // jsonArray.getJSONObject(i)));
                    }
                    if (jsonArray.length() > 0)
                    {
                        // AlertUtility.showToast(Chat2Activity.this,
                        // "New Msg");
                        // DebugReportOnLocat.e("web_service", "New msg " +
                        // jsonArray.toString());
                        updateListData(chatModelList.size());
                    }

                } catch (Exception e)
                {
                    DebugReportOnLocat.e(e);
                }

                if (handlerWait != null && runnableWait != null)
                {
                    handlerWait.removeCallbacks(runnableWait);
                    handlerWait.postDelayed(runnableWait, 500);
                }
            }
        };

        task.execute();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void openUploadoption()
    {

        final Dialog dialog = new Dialog(Chat2Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list_dialog);
        ListView lv = (ListView) dialog.findViewById(R.id.dialog_list);
        lv.setAdapter(new ArrayAdapter(Chat2Activity.this, R.layout.row, getResources().getStringArray(R.array.uploadpicoption)));
        lv.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {

                dialog.dismiss();

                if (arg2 == 0)
                {

                    camera();

                }
                else if (arg2 == 1)
                {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), ACTION_REQUEST_GALLERY);
                }
            }
        });
        dialog.show();

    }

    public String currImagePath;


    public void camera()
    {

        Date d = new Date();
        currImagePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + d.getTime() + "_MyTempImage.jpg";

        File f = new File(currImagePath);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        // end new code

        try
        {
            camera.putExtra("return-data", true);
            startActivityForResult(camera, ACTION_REQUEST_CAMERA);
            // startActivityForResult(camera, 1);

        } catch (ActivityNotFoundException e)
        {
            DebugReportOnLocat.e(e);
        }
        // end previous code

    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null)
        {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else
            return null;
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
                case ACTION_REQUEST_CAMERA:

                    if (data != null)
                    {

                        Uri selectedImageUri = data.getData();
//		    // OI FILE Manager
//		    String filemanagerstring = selectedImageUri.getPath();
//

                        //   imagePath = getPath(selectedImageUri);
//		    imagePath = filemanagerstring;
//
//		    File f = new File(imagePath);
//
//		    DebugReportOnLocat.e(TAG, "uri camera=>" + f.toString());
//		    try
//		    {
//
//			callWSSendMessage("", "", true, Uri.fromFile(f));
//		    } catch (Exception e)
//		    {
//
//			 DebugReportOnLocat.e(e);
//			AlertUtility.showToast(Chat2Activity.this, getString(R.string.err_unknown));
//		    }
//		    

                        try
                        {
                            Bitmap bitmap = null;

                            File f=new File(selectedImageUri.toString());

                            bitmap = decodeFile(f);

                            //bitmap = decodeSampledBitmapFromResource(selectedImageUri, false);


//			try
//			{
//			    if (selectedImageUri != null)
//			    {
//				bitmap = MediaStore.Images.Media.getBitmap(Chat2Activity.this.getContentResolver(), selectedImageUri);
//			    }
//			} catch (FileNotFoundException e1)
//			{
//			    // TODO Auto-generated catch block
//			    e1.printStackTrace();
//			} catch (IOException e1)
//			{
//			    // TODO Auto-generated catch block
//			    e1.printStackTrace();
//			}
//	

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

                                //Hex binary string to image conversion
				/*byte[] bytevalue=   Hex.decodeHex(message.toCharArray());
				
				Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytevalue , 0, bytevalue .length);
				
			    
				ivChatBack.setImageBitmap(bitmap1);
*/




                                new encryptAsynk(message, true).execute();
                            }


                            //callWSSendMessage("", "", true, uri);

                        } catch (Exception e)
                        {

                            DebugReportOnLocat.e(e);
                            AlertUtility.showToast(Chat2Activity.this, getString(R.string.err_unknown));
                        }

                    }
                    else
                    {

                        File f = new File(currImagePath);

                        DebugReportOnLocat.e(TAG, "uri camera=>" + f.toString());

                        try
                        {


                            Bitmap bitmap = null;

                            bitmap = decodeFile(f);

                            //bitmap = decodeSampledBitmapFromResource( Uri.fromFile(f),false);


//			Bitmap bitmap = null;
//			try
//			{
//			    if (currImagePath != null)
//			    {
//				bitmap = MediaStore.Images.Media.getBitmap(Chat2Activity.this.getContentResolver(), Uri.fromFile(f));
//			    }
//			} catch (FileNotFoundException e1)
//			{
//			    // TODO Auto-generated catch block
//			    e1.printStackTrace();
//			} catch (IOException e1)
//			{
//			    // TODO Auto-generated catch block
//			    e1.printStackTrace();
//			}
//	

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

                                //Hex binary string to image conversion
				/*byte[] bytevalue=   Hex.decodeHex(message.toCharArray());
				
				Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytevalue , 0, bytevalue .length);
				
			    
				ivChatBack.setImageBitmap(bitmap1);
*/




                                new encryptAsynk(message, true).execute();
                            }


                            //callWSSendMessage("", "", true, uri);

                        } catch (Exception e)
                        {

                            DebugReportOnLocat.e(e);
                            AlertUtility.showToast(Chat2Activity.this, getString(R.string.err_unknown));
                        }
		    
		   /* try
		    {

			callWSSendMessage("", "", true, Uri.fromFile(f));
		    } catch (Exception e)
		    {

			 DebugReportOnLocat.e(e);
			AlertUtility.showToast(Chat2Activity.this, getString(R.string.err_unknown));
		    }*/
                    }

                    break;

                case ACTION_REQUEST_GALLERY:
                    if (data != null)
                    {

                        Uri uri = data.getData();

                        try
                        {


                            Bitmap bitmap = null;
                            try
                            {
                                if (uri != null)
                                {

                                    imagePath = getPath(uri);

                                    File f=new File(imagePath);

                                    bitmap = decodeFile(f);

                                    //bitmap = decodeSampledBitmapFromResource(uri,true);

                                    DebugReportOnLocat.ln("  byte length >>>>" +bitmap.getByteCount());

                                    //bitmap = MediaStore.Images.Media.getBitmap(Chat2Activity.this.getContentResolver(), uri);
                                }
                            }  catch (Exception e1)
                            {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }


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

                                //Hex binary string to image conversion
				/*byte[] bytevalue=   Hex.decodeHex(message.toCharArray());
				
				Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytevalue , 0, bytevalue .length);
				
			    
				ivChatBack.setImageBitmap(bitmap1);
*/




                                new encryptAsynk(message, true).execute();
                            }


                            //callWSSendMessage("", "", true, uri);

                        } catch (Exception e)
                        {

                            DebugReportOnLocat.e(e);
                            AlertUtility.showToast(Chat2Activity.this, getString(R.string.err_unknown));
                        }
                    }
                    else
                    {
                        AlertUtility.showToast(Chat2Activity.this, getString(R.string.err_unknown));
                    }

                    break;

                case ACTION_REQUEST_CROP:

                    break;
            }
        }
        else if (requestCode == ACTION_REQUEST_CROP)
        {

        }
    }



    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }


    void redirectTestURLEnter(String imageUrl){

        Intent i = new Intent(this, ImageViewDisplay.class);
        i.putExtra(Extra.IMAGES, imageUrl);
        startActivity(i);
    }


    private void openDialog(String filePath)
    {
        DebugReportOnLocat.e("log_tag", "openDialog: " + filePath);
        Dialog dialog = new Dialog(Chat2Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.large_image);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ImageView tImageView = (ImageView) dialog.findViewById(R.id.iv_largeImage);
        Drawable d = getResources().getDrawable(R.drawable.loading);
        tImageView.setImageDrawable(d);


        try
        {

            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            tImageView.setImageBitmap(bitmap);


        } catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
//	     
//	  byte[] bytevalue;
//		try
//		{
//		    bytevalue = Hex.decodeHex(imgeContent.toCharArray());
//		    Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytevalue , 0, bytevalue .length);
//			
//		    
//		    tImageView.setImageBitmap(bitmap1);
//		    
//		} catch (DecoderException e)
//		{
//		    
//		     DebugReportOnLocat.e(e);
//		     
//		  
//		}


        //imageLoader.displayImage(link, tImageView, options);

        dialog.show();
    }


    private void openDialogPrevious(String link)
    {
        DebugReportOnLocat.e("log_tag", "openDialog: " + link);
        Dialog dialog = new Dialog(Chat2Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.large_image);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ImageView tImageView = (ImageView) dialog.findViewById(R.id.iv_largeImage);


        imageLoader.displayImage(link, tImageView, options);

        dialog.show();
    }
    private void callWSDeleteMessage(final int pos)
    {

        try
        {

            if (chatModelList.size() > pos)
            {

                // TODO Auto-generated method stubdsaf
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("messageID", chatModelList.get(pos).getId()));
                nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

                CallPostWebseviceTask task = new CallPostWebseviceTask(Chat2Activity.this, true, "delete_message", nameValuePairs)
                {
                    @Override
                    protected void onPostExecute(String result)
                    {

                        super.onPostExecute(result);

                        try
                        {
                            JSONObject jo = new JSONObject(result);

                            if (jo.getBoolean(Constants.STATUS_WKEY))
                            {
                                if (chatModelList.size() > pos)
                                {
                                    try
                                    {

                                        chatInfoManager.delete(chatModelList.get(pos).getId());

                                    } catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }


                                    chatModelList.remove(pos);
                                }

                                updateListData(-1);
                            }

                        } catch (Exception e)
                        {
                            DebugReportOnLocat.e(e);
                        }

                    }
                };

                task.execute();

            }
        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }
    }

    private class ChatAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {

            return chatModelList.size();
        }

        @Override
        public Object getItem(int position)
        {

            return position;
        }

        @Override
        public long getItemId(int position)
        {

            return position;
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {

            ViewHolder holder = null;
            LayoutInflater mInflater = (LayoutInflater) getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.chat_row, parent, false);
                holder = new ViewHolder();
                holder.txtChatTextOut = (TextView) convertView.findViewById(R.id.message_wall_outgoing);
                holder.txtChatTextOut.setMovementMethod(LinkMovementMethod.getInstance());
                holder.txtChatTextIn = (TextView) convertView.findViewById(R.id.message_wall_incoming);
                holder.txtChatTextIn.setMovementMethod(LinkMovementMethod.getInstance());
                holder.txtTimeIn = (TextView) convertView.findViewById(R.id.txt_Time_incoming);
                holder.txtTimeOut = (TextView) convertView.findViewById(R.id.txt_Time_outgoing);
                holder.lnIn = (LinearLayout) convertView.findViewById(R.id.ln_inComing);
                holder.lnOut = (LinearLayout) convertView.findViewById(R.id.ln_outGoing);
                holder.lnOutText = (LinearLayout) convertView.findViewById(R.id.ln_out_text);

                holder.lnInText = (LinearLayout) convertView.findViewById(R.id.ln_in_text);

                holder.lnOutImage = (LinearLayout) convertView.findViewById(R.id.ln_out_Image);
                holder.lnInImage = (LinearLayout) convertView.findViewById(R.id.ln_in_Image);

                holder.ivInImage = (ImageView) convertView.findViewById(R.id.iv_in_chatImage);
                holder.ivOutImage = (ImageView) convertView.findViewById(R.id.iv_out_chatImage);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtChatTextIn.setTextSize(Constants.TextSize);
            holder.txtChatTextOut.setTextSize(Constants.TextSize);

            // String Time =
            // StaticUtility.getDateFromGMTTime(Long.parseLong(chatModelList.get(position).getCreatedDate()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.setTimeInMillis(Long.parseLong(chatModelList.get(position).getCreatedDate()));
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, hh:mm a");
            sdf.setTimeZone(TimeZone.getDefault());
            String Time = sdf.format(calendar.getTime());

            if (chatModelList.get(position).getFrom().equalsIgnoreCase("In"))
            {

                holder.lnIn.setVisibility(View.VISIBLE);
                holder.lnOut.setVisibility(View.GONE);

                holder.ivInImage.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {

                        File imagesFolderPath = getExternalFilesDir("images");

                        String filePath=imagesFolderPath.getAbsolutePath()+"/"
                                +chatModelList.get(position).getImagePath() ;

                        DebugReportOnLocat.e("##########", "  : " + chatModelList.get(position).getImagePath() + " pos : " + position);
                        //openDialog(filePath);
                        //redirectTestURLEnter(filePath);

                    }
                });

                holder.ivInImage.setTag(position);

                holder.ivInImage.setOnLongClickListener(imageDelete);

                if (chatModelList.get(position).getContent().equalsIgnoreCase("Text"))
                {

                    holder.lnInText.setVisibility(View.VISIBLE);
                    holder.lnInImage.setVisibility(View.GONE);

                    holder.txtChatTextIn.setText(chatModelList.get(position).getBody());
                    holder.txtChatTextIn.setOnClickListener(new OnClickListener()
                    {

                        @Override
                        public void onClick(View v)
                        {

                            TextView tv = (TextView) v;
                            if (tv.getSelectionStart() != -1 && tv.getSelectionEnd() != -1)
                            {
                                showShareLinkDialog(tv.getText().toString().substring(tv.getSelectionStart(), tv.getSelectionEnd()));
                            }
                        }
                    });

                }
                else
                {

                    //Hex binary string to image conversion
		/*	byte[] bytevalue;
			try
			{
			    bytevalue = Hex.decodeHex(chatModelList.get(position).getBody().toCharArray());
			    Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytevalue , 0, bytevalue .length);
				
			    holder.ivInImage.setImageBitmap(bitmap1);
			} catch (DecoderException e)
			{
			    // TODO Auto-generated catch block
			     DebugReportOnLocat.e(e);
			}*/


                    //holder.ivInImage.setImageBitmap(bitmap1);


                    holder.lnInText.setVisibility(View.GONE);
                    holder.lnInImage.setVisibility(View.VISIBLE);

                    File imagesFolderPath = getExternalFilesDir("images");

                    String filePath=imagesFolderPath.getAbsolutePath()+"/"
                            +chatModelList.get(position).getImagePath() ;


                    //  DebugReportOnLocat.ln(" Image path -->> "+chatModelList.get(position).getImagePath());
                    // imageLoader.displayImage(Uri.fromFile(new File(filePath)).toString(), holder.ivInImage, options);


                    try
                    {

                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        holder.ivInImage.setImageBitmap(bitmap);


                    } catch (OutOfMemoryError e)
                    {

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                }

                holder.txtTimeIn.setText(Time);

            }
            else
            {

                holder.lnIn.setVisibility(View.GONE);
                holder.lnOut.setVisibility(View.VISIBLE);

                if (chatModelList.get(position).getContent().equalsIgnoreCase("Text"))
                {

                    holder.lnOutText.setVisibility(View.VISIBLE);
                    holder.lnOutImage.setVisibility(View.GONE);

                    holder.txtChatTextOut.setText(chatModelList.get(position).getBody());
                    holder.txtChatTextOut.setOnClickListener(new OnClickListener()
                    {

                        @Override
                        public void onClick(View v)
                        {

                            TextView tv = (TextView) v;
                            if (tv.getSelectionStart() != -1 && tv.getSelectionEnd() != -1)
                            {
                                showShareLinkDialog(tv.getText().toString().substring(tv.getSelectionStart(), tv.getSelectionEnd()));
                            }
                        }
                    });

                    // newly added code on Oct 07 from download source code
                    holder.txtChatTextOut.setTag(position);

                    holder.txtChatTextOut.setOnLongClickListener(new OnLongClickListener()
                    {

                        @Override
                        public boolean onLongClick(View v)
                        {
                            // TODO Auto-generated method stub

                            final int pos = (Integer) v.getTag();
                            Log.e(TAG, "pos=>" + pos);
                            if (chatModelList.get(pos).getFrom().equalsIgnoreCase("In"))
                            {
                                return false;
                            }
                            AlertUtility.showConfirmDialog(Chat2Activity.this, "Are you sure you want to delete message?", new DialogInterface.OnClickListener()
                            {

                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // TODO Auto-generated method stub

                                    if (chatModelList.size() >= pos)
                                    {
                                        callWSDeleteMessage(pos);
                                    }

                                }

                            });
                            return true;

                        }
                    });
                }
                else
                {

                    holder.lnOutText.setVisibility(View.GONE);
                    holder.lnOutImage.setVisibility(View.VISIBLE);

		  /*  byte[] bytevalue;
			try
			{
			    bytevalue = Hex.decodeHex(chatModelList.get(position).getBody().toCharArray());
			    Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytevalue , 0, bytevalue .length);
				
			    
			    holder.ivOutImage.setImageBitmap(bitmap1);
			} catch (DecoderException e)
			{
			    // TODO Auto-generated catch block
			     DebugReportOnLocat.e(e);
			}*/
                    //    imageLoader.displayImage(getFilesDir().getAbsolutePath()+"/"+chatModelList.get(position).getImagePath(), holder.ivOutImage, options);


                    File imagesFolderPath = getExternalFilesDir("images");

                    String filePath=imagesFolderPath.getAbsolutePath()+"/"
                            +chatModelList.get(position).getImagePath() ;

                    try
                    {

                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        holder.ivOutImage.setImageBitmap(bitmap);


                    }catch (OutOfMemoryError e)
                    {
                        // TODO: handle exception
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                    }


                    //  DebugReportOnLocat.ln(" Image path -->> "+chatModelList.get(position).getImagePath());
                    // imageLoader.displayImage(Uri.fromFile(new File(filePath)).toString(), holder.ivInImage, options);


                }

                holder.txtTimeOut.setText(Time);


                holder.ivOutImage.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        File imagesFolderPath = getExternalFilesDir("images");

                        String filePath=imagesFolderPath.getAbsolutePath()+"/"
                                +chatModelList.get(position).getImagePath() ;

                        DebugReportOnLocat.e("##########", "  : " + chatModelList.get(position).getImagePath() + " pos : " + position);
                        //redirectTestURLEnter(filePath);

                        //openDialog(filePath);

                    }
                });

                holder.ivOutImage.setOnLongClickListener(imageDelete);


                holder.ivOutImage.setTag(position);


            }

            return convertView;
        }


        OnLongClickListener imageDelete =new OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {

                // TODO Auto-generated method stub

                final int pos = (Integer) v.getTag();
                Log.e(TAG, "pos=>" + pos);
                if (chatModelList.get(pos).getFrom().equalsIgnoreCase("In"))
                {
                    return false;
                }
                AlertUtility.showConfirmDialog(Chat2Activity.this, "Are you sure you want to delete message?", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // TODO Auto-generated method stub

                        if (chatModelList.size() >= pos)
                        {
                            callWSDeleteMessage(pos);
                        }

                    }

                });
                return true;


            }
        };


        @SuppressWarnings({ "unchecked", "rawtypes" })
        protected void showShareLinkDialog(final String link)
        {

            final Dialog dialog = new Dialog(Chat2Activity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.list_dialog);
            ListView lv = (ListView) dialog.findViewById(R.id.dialog_list);
            lv.setAdapter(new ArrayAdapter(Chat2Activity.this, R.layout.row, getResources().getStringArray(R.array.share_link_option)));
            lv.setOnItemClickListener(new OnItemClickListener()
            {

                @SuppressWarnings("deprecation")
                @SuppressLint("NewApi")
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                {

                    dialog.dismiss();
                    if (position == 0)
                    {
                        DebugReportOnLocat.e("############", "  LINK DIALOG  " + link);
                        String str = link;
                        if (!link.contains("http://"))
                        {
                            str = "http://" + link;
                        }

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                        startActivity(browserIntent);
                    }
                    else if (position == 1)
                    {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, link);
                        startActivity(Intent.createChooser(share, "Share with Friends"));
                    }
                    else if (position == 2)
                    {
                        int sdk = android.os.Build.VERSION.SDK_INT;
                        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB)
                        {
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(link);
                        }
                        else
                        {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("link", link);
                            clipboard.setPrimaryClip(clip);
                        }

                        AlertUtility.showToast(Chat2Activity.this, "Copied");
                    }
                }
            });
            dialog.show();
        }

        private class ViewHolder
        {
            TextView txtChatTextOut, txtChatTextIn, txtTimeOut, txtTimeIn;
            LinearLayout lnIn, lnOut, lnInText, lnInImage, lnOutText, lnOutImage;
            ImageView ivInImage, ivOutImage;// ivProfileIN, ivprofileOUT;
        }

    }




    public  Bitmap decodeSampledBitmapFromResource(Uri path, boolean isFromGallery) {
	
	
/*	// 	VGA Resolution 1.333 Aspect Ratio
        int reqWidth=640;
        int reqHeight=480; */


        // 	VGA Resolution 1.333 Aspect Ratio
        int reqWidth=480;
        int reqHeight=320;

        if( isFromGallery){

            reqWidth=640;
            reqHeight=480;
        }
        // 	Apple iPhone 4/4S
        // int reqWidth=960;
        // int reqHeight=640;


        DebugReportOnLocat.e("path", path);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //return BitmapFactory.decodeFile(path, options);

        try
        {
            return BitmapFactory.decodeStream(Chat2Activity.this.getContentResolver().openInputStream(path), null, options);
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }


    public  int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    final int IMAGE_MAX_SIZE=600;
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

        if(f.length()>8000){

            scale = 5;

            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        b = BitmapFactory.decodeStream(fis, null, o2);
        try
        {
            fis.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DebugReportOnLocat.ln("  byte length >>>>" +b.getByteCount());

        return b;
    }


    public void writeToFile(String path,byte[] array)
    {
        try
        {

            FileOutputStream stream = new FileOutputStream(path);
            stream.write(array);

        } catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static class Extra {
        public static final String IMAGES = "com.IMAGES";
        public static final String IMAGE_POSITION = "com.IMAGE_POSITION";
    }

    public String getIDList(){

        String idList="";
        try
        {





            if(chatModelList==null)return "";

            for(int i=0;i<chatModelList.size();i++){

                if(i==0){

                    idList=chatModelList.get(i).getId();

                }else{

                    idList+=","+chatModelList.get(i).getId();
                }



            }
            return idList;



        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return idList;
    }



    public class DeleteUnUsedMsgAsynk extends AsyncTask<String, String, String>
    {


        private String userId1;
        public DeleteUnUsedMsgAsynk(String userId1, String userId2)
        {
            super();
            this.userId1 = userId1;
            this.userId2 = userId2;
        }
        private String userId2;
        @Override
        protected String doInBackground(String... params)
        {



            try
            {

                String idList=getIDList();

                if(idList.length()==0)return null;

                chatInfoManager.deleteUnUsedMessage(userId1,userId2 ,idList) ;

                chatInfoManager.deleteUnUsedMessage(userId2,userId1, idList) ;


            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }



    }


}
