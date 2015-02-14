package com.nxtty.http;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;

public class JSONBody
{

    // Arcade

    // nxtID=14945202239184794178
    /*
     * key=12qwUA8waM6oKYyxzVyGLUjtv level=Medium shots=150 time=79
     * completed=true
     */

    public static ArrayList<NameValuePair> addScoreArcade(String nxtID, String key, String level, int shots, long time, String completed)
    {
	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	pairs.add(new BasicNameValuePair("nxtID", "" + nxtID));
	pairs.add(new BasicNameValuePair("key", "" + key));
	pairs.add(new BasicNameValuePair("level", "" + level));//LevelManager.DifficultyStrings[mHighScoreManager.getLevel()]
	pairs.add(new BasicNameValuePair("shots", "" + shots));
	pairs.add(new BasicNameValuePair("time", "" + time));
	pairs.add(new BasicNameValuePair("completed", "" + completed));

	return pairs;
    }

    public static ArrayList<NameValuePair> getScoreArcade(String nxtID, String key)
    {
	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	pairs.add(new BasicNameValuePair("nxtID", "" + nxtID));
	pairs.add(new BasicNameValuePair("key", "" + key));

	return pairs;
    }
    
    //Easy, Medium, Hard or Insane
    public static ArrayList<NameValuePair> getTopScorersArcade(String level, String key)
    {
	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	pairs.add(new BasicNameValuePair("nxtID", "" + level));
	pairs.add(new BasicNameValuePair("key", "" + key));

	return pairs;
    }
    
    
    ///////////////////
    
    public static ArrayList<NameValuePair> approveReferral(String alias, String code,String key)
    {
	//referrals/approve
	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	
	pairs.add(new BasicNameValuePair("alias", "" + alias));
	pairs.add(new BasicNameValuePair("code", "" + code));
	
	pairs.add(new BasicNameValuePair("key", "" + key));

	return pairs;
    }
    
    
    public static ArrayList<NameValuePair> addReferral(String nxtID, String code,String key)
    {
	//referrals/approve
	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	
	pairs.add(new BasicNameValuePair("nxtID", "" + nxtID));
	pairs.add(new BasicNameValuePair("code", "" + code));
	
	pairs.add(new BasicNameValuePair("key", "" + key));

	return pairs;
    }
    
  
    public static ArrayList<NameValuePair> validateReferral(String code,String key)
    {
	//referrals/approve
	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	
	
	pairs.add(new BasicNameValuePair("code", "" + code));
	
	pairs.add(new BasicNameValuePair("key", "" + key));

	return pairs;
    }
    
    
    public static ArrayList<NameValuePair> duplicateDeviceIDCheckForReferral(String deviceID ,String key)
    {
	//referrals/approve
	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	
	
	pairs.add(new BasicNameValuePair("deviceID ", "" + deviceID));
	
	pairs.add(new BasicNameValuePair("key", "" + key));

	return pairs;
    }
    
    

    // ////////////////////

    public static ArrayList<NameValuePair> login(String email, String password)
    {
	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	pairs.add(new BasicNameValuePair("s_email", "" + email));
	pairs.add(new BasicNameValuePair("s_password", "" + password));

	return pairs;
    }

    public static ArrayList<NameValuePair> chat(String NxtAcId, String ContactID)
    {

	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("nxtID", "" + NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("contactID", "" + ContactID));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));// G

	return nameValuePairs;
    }

    public static ArrayList<NameValuePair> send(String NxtAcId, String ContactID)
    {

	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("nxtID", "" + NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("contactID", "" + ContactID));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));// G

	return nameValuePairs;
    }

    public static ArrayList<NameValuePair> publicChat(String NxtAcId)
    {

	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("nxtID", "" + Constants.NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));// G

	DebugReportOnLocat.ln("nxtID >> " + Constants.NxtAcId);
	DebugReportOnLocat.ln("key  >>>> " + Constants.ParamKey);
	// G
	return nameValuePairs;
    }

    public static ArrayList<NameValuePair> contacts(String NxtAcId, int Offset)
    {

	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	nameValuePairs.add(new BasicNameValuePair("nxtID", "" + Constants.NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("offset", "" + Offset));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));// G

	return nameValuePairs;
    }

    public static ArrayList<NameValuePair> discoverContacts(String NxtAcId, String searchText, int Offset, String searchCriteria)
    {
	Log.e("JSONBody", "Constants.NxtAcId=>" + Constants.NxtAcId);
	Log.e("JSONBody", "searchText=>" + searchText);
	Log.e("JSONBody", "searchCriteria=>" + searchCriteria);
	Log.e("JSONBody", "Offset=>" + Offset);
	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	nameValuePairs.add(new BasicNameValuePair("nxtID", "" + Constants.NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));// G

	String escaped = searchText.replaceAll("([\'\\\\\\.\\[\\{\\(\\*\\+\\?\\^\\$\\|])", "\\\\$1");
	nameValuePairs.add(new BasicNameValuePair(searchCriteria, "" + escaped));
	// nameValuePairs.add(new BasicNameValuePair(searchCriteria,
	// ""+searchText));
	nameValuePairs.add(new BasicNameValuePair("offset", "" + Offset));

	return nameValuePairs;
    }

    public static ArrayList<NameValuePair> groupPost(String NxtAcId, String GroupID, String type, String Query, int Offset)
    {

	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
	nameValuePairs.add(new BasicNameValuePair("groupID", GroupID));
	nameValuePairs.add(new BasicNameValuePair("type", "0"));
	nameValuePairs.add(new BasicNameValuePair("query", ""));
	nameValuePairs.add(new BasicNameValuePair("offset", "" + Offset));
	nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));// G

	return nameValuePairs;
    }

}
