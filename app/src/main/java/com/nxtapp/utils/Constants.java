package com.nxtapp.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.nextapp.data.ChatInfoManager;

/** Last Modified Date 15 Oct 2014 IST 12:56 AM */

public class Constants
{

    public static final String FlurryKey = "XNK5PNS2FC2QVNF7465K";

    /** Authentication URL */
   // public static String baseUrl = "https://nxt1.nxtty.com:7876/nxt";

    public static String baseUrl = "https://nxt2.nxtty.com:7876/nxt";

    /** PHP server */
    public static String TipUrl = "http://128.199.211.69/1nxttycoinsender.php";

    // ////////////////////////////////////////////////////////////////////////////

    /** Messaging server **/
     public static String baseUrl_new = "http://188.226.245.191:8080/nxt/"; 
    
     /** Open Chat server **/
     public static String baseUrl_Group = "http://128.199.248.197:8080/nxt/";
    
     /** Open chat Image server **/
     public static String baseUrl_ImagesGroup =  "https://nxtopen.s3.amazonaws.com/";
    
     /** messaging chat Image server **/
     public static String baseUrl_Images =    "https://nxtmessaging.s3.amazonaws.com/";
    
     /** Messaging server subscriber **/
     public static String BASE_SUBSCRIBER_URL =    "http://188.226.245.191:8080/nxt/subscriber/";
    
     public static String ParamKey = "12qwUA8waM6oKYyxzVyGLUjtv";

    // //////////////////////////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // Live server link //
    // ///////////////////////////////////////////////////////////////////////////////////////////////

//    /** Messaging server **/
//    public static String baseUrl_new = "http://178.62.253.41/nxt/";
//
//    /** Open Chat server **/
//    public static String baseUrl_Group = "http://128.199.243.242/nxt/";
//
//    /** Open chat Image server **/
//    public static String baseUrl_ImagesGroup = "https://nxtopen.s3.amazonaws.com/";
//    /** messaging chat Image server **/
//    public static String baseUrl_Images = "https://nxtmessaging.s3.amazonaws.com/";
//
//    /** Messaging server subscriber **/
//    public static String BASE_SUBSCRIBER_URL = "http://178.62.253.41/nxt/subscriber/";
//    
//    
//    public static String ParamKey = "12qwUA8waM6oKYyxzVyGLUjtv";

    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    // Web service method name
    public static String ADD_GROUP = "groups/prospective_groups";
    public static String ARCADE_ADD_SCORE = "arcade/add_score";
    public static String ARCADE_GET_SCORE = "arcade/get_user_score";
    public static String ARCADE_TOP_SCORE = "arcade/top_scorers";
    public static String ARCADE_GET_LEVEL_SCORE = " arcade/get_level_scores";

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
    
    
    public static String recipient = "";
    public static boolean HomescreenDialog = false;
    public static boolean SameAvtarforGroup = false;
    public static String SameAvtarImagePath = "";
    
	public static ChatInfoManager dbMarkerManager;
}
