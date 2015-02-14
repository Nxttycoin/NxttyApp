package com.nextapp.webservice;

import java.io.File;

import org.json.JSONObject;

import com.nxtapp.classes.ChatModel;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;

public class JsonParserUtility
{

    String TAG = "JsonParserUtility";

    public ChatModel parseChat(File imagesFolderPath, JSONObject jobj,String msg)
    {
	ChatModel model = new ChatModel();
	
	try
	{
		model.setId(jobj.optString("id"));
		model.setSenderId(jobj.optString("senderId"));
		model.setReceiverId(jobj.optString("receiverId"));

		 DebugReportOnLocat.e(TAG, "chatMessage-->  " + msg);

		model.setBody(msg);
		//  DebugReportOnLocat.e("log_tag", "Body: " + jobj.optString("body"));
		model.setCreatedDate(jobj.optString("createdDate"));
		model.setRead(jobj.optString("read"));
		model.setImage(jobj.optString("image"));
		 DebugReportOnLocat.e("#####", " image -->  " + jobj.optString("image"));
		String lastSeen = jobj.optString("seenDate");
		if (lastSeen != null && !lastSeen.equals("") && !lastSeen.equals("null"))
		{
		    model.setSeenDate(lastSeen);
		}
		else
		{
		    model.setSeenDate(null);
		}
		if (jobj.optString("senderId").equalsIgnoreCase(Constants.NxtAcId))
		{
		    model.setFrom("Out");
		}
		else
		{
		    model.setFrom("In");
		}
		if (jobj.optString("body").length() > 1)
		{
		    model.setContent("Text");
		}
		else
		{
		    model.setContent("Image");
		    model.setImagePath(jobj.optString("image"));

		    

		}
		
	    
		
	} catch (Exception e)
	{
	   e.printStackTrace();
	}
	
	return model;
	
    }
}
