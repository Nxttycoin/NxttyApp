package com.nxtapp.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

/** Last Modified Date 15 Oct 2014 IST 12:56 AM */

public class CopyOfConstants
{

    public static final String FlurryKey = "XNK5PNS2FC2QVNF7465K";

    
    // public static String baseUrl = "https://wallet.nxtty.com:7876/nxt";
    // public static String baseUrl = "http://vps2.nxtcrypto.org:7876/nxt";
    
   // http://178.62.253.41 //Messanging app
   // http://178.62.253.41:8080 // Open chat app
    
    
    //commented by gnanaoly
    //public static String baseUrl = "https://nxt1.nxtty.com:7876/nxt";
  //  public static String baseUrl_new = "http://188.226.245.191:8080/nxt/";
   // public static String baseUrl_Group = "http://128.199.248.214:8080/nxt/";
    
 // Open chat app
    public static String baseUrl_Group = "http://178.62.253.41:8080/nxt/";
    
    //Messanging app
   public static String baseUrl = "http://178.62.253.41/nxt/";
   
   public static String baseUrl_new = "http://178.62.253.41:8080/nxt/";
   
   

    public static String TipUrl = "http://128.199.211.69/1nxttycoinsender.php";

    // Web service method name
    public static String ADD_GROUP = "groups/prospective_groups";

    // http://128.199.189.226/nxt.php

    public static String baseUrl_Images = "http://188.226.245.191:8080/images/";
    public static String baseUrl_ImagesGroup = "http://128.199.248.214:8080/images/";

    public static String BASE_SUBSCRIBER_URL = "http://178.62.253.41:8080/nxt/subscriber/";
    
  //  public static String BASE_SUBSCRIBER_URL = "http://188.226.245.191:8080/nxt/subscriber/";

    public static SharedPreferences sharedPreferences;

    public static String logninMethod = "getAccountId";
    public static String NxtAcId;
    public static String AliasName = "";
    public static Bitmap ProfImageBitmapSmall = null;

    public static String RegistationKeyGCM = "";

    public static final int OFFSET_INCR = 25;
    public static int TextSize = 16;
    public static final int BITMAP_SIZE = 200;
    public static final String STATUS_WKEY = "status";
    public static final String ERROR_DETAIL_WKEY = "errorDetail";

    public static boolean ISinChatScreen = false;
    public static final int ACTION_REQUEST_CAMERA = 99;
    public static final int ACTION_REQUEST_GALLERY = 88;
    public static final int ACTION_REQUEST_CROP = 50;

    public static String ParamKey = "12qwUA8waM6oKYyxzVyGLUjtv";

    // public static String secretPhrase = "";
    public static String recipient = "";
}
