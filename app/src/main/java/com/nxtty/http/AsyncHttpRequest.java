package com.nxtty.http;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

public class AsyncHttpRequest {

	public String mURI;
	public String mUserInfo="";
	ArrayList<NameValuePair> mBody;
	
	public AsyncHttpRequest(String uri){
		mURI = uri;
	}
	
	
	public void setBody(ArrayList<NameValuePair> body){
		 mBody = body;
	}
	
	public ArrayList<NameValuePair> getBody(){
		return mBody;
	}
	

	public void setUserInfo(String userInfo){
		mUserInfo = userInfo;
	}
	
	public String getUserInfo(){
		return mUserInfo;
	}
	
	
	public String getURI(){
		return mURI;
	}
	
	
}
