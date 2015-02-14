package com.nextapp.data;

import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nxtapp.classes.ChatModel;
import com.nxtapp.classes.GroupModel;
import com.nxtapp.utils.DebugReportOnLocat;

public class ChatInfoManager extends BaseManager implements
		IDataBaseManager {

	public ChatInfoManager(Context context) {
		super(context);

	}
	
	
/*	CREATE TABLE "publicChat" ("id" TEXT PRIMARY KEY  NOT NULL , 
		"title" TEXT, "description" TEXT, "category" TEXT,
		"avatar" TEXT, "deleted" TEXT, "created" TEXT,
		"modified" TEXT, "posts" TEXT, "members" TEXT)*/
	
	
public <T> long addPublicChat(T object) {

	    
    GroupModel chatModel = (GroupModel) object;
	    
	             
	    
	    try
	    {
		//Calendar cal=Calendar.getInstance();
		   
		 

		ContentValues values = new ContentValues();

		values.put("id", chatModel.getId());
		values.put("title", chatModel.getTitle());
		values.put("description", chatModel.getDescription());
		values.put("category", chatModel.getCategory());
		values.put("avatar", chatModel.getAvatar());
		values.put("deleted", chatModel.getDeleted());
		values.put("created", chatModel.getCreated());
		values.put("modified", chatModel.getModified());
		values.put("posts", chatModel.getPosts());
		values.put("members", chatModel.getMembers());
		values.put("DateOfInsert", System.currentTimeMillis());
		
	
		System.out.println(" id >>"+chatModel.getId()+" title>> "+chatModel.getTitle()+" >description>"+chatModel.getDescription()
			+ "  crTime>>"+ System.currentTimeMillis());

	return this.dataBase.insert("publicChat", null, values);
		
	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }
	    
	    return 0;
	}

	

	@Override
	public <T> long add(T object) {

	    
	    ChatModel chatModel = (ChatModel) object;
	    
	             
	    
	    try
	    {
		//Calendar cal=Calendar.getInstance();
		   
		 

		ContentValues values = new ContentValues();

		values.put("id", chatModel.getId());
		values.put("senderId", chatModel.getSenderId());
		values.put("receiverId", chatModel.getReceiverId());
		values.put("createdDate", chatModel.getCreatedDate());
		values.put("read", chatModel.getRead());
		values.put("image", chatModel.getImagePath());
		values.put("seenDate", chatModel.getSeenDateLong());
		values.put("msg", chatModel.getBody());
		values.put("DateOfInsert", System.currentTimeMillis());
		
	
		System.out.println(" id >>"+chatModel.getId()+" senderId>> "+chatModel.getSenderId()+" >receiverId>"+chatModel.getReceiverId()+ "  crTime>>"+ System.currentTimeMillis());

	return this.dataBase.insert("privateChat", null, values);
		
	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }
	    
	    return 0;
	}

	@Override
	public <T> int delete(String whereID) {
	    SQLiteDatabase db = this.dataBase;
		
	    int x =db.delete("privateChat", "id = ?", new String[] { whereID });
		return x;
	}

	public <T> int deleteUnUsedMessage(String senderId,String receiverId,String idList){
	    

	    
	    try
	    {
		
		deleteRowsCount(senderId,receiverId,idList);
		
		/*long daysInMiliSec = new Date().getTime() - X
		            * (24L * 60L * 60L * 1000L);*/
		
		
		 String sql="delete  From privateChat  where senderId='"+senderId+"' AND receiverId='"+receiverId+"'  AND id not in ("+idList+") AND +DateOfInsert <= date('now','-5 day')";
		    SQLiteDatabase db = this.dataBase;
		    
		    DebugReportOnLocat.ln(" delete "+sql);
		    
		    db.execSQL(sql);
		
	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }
	    return 0;
	}
	@Override
	public <T> int deleteAll() {

		SQLiteDatabase db = this.dataBase;
		return db.delete("privateChat", null, null);

	}

	@Override
	public <T> boolean isExists(String id) {

		String query = "SELECT * FROM privateChat WHERE id = '" + id+"'";

		SQLiteDatabase db = this.dataBase;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			// cursor.close();
			return true;
		} else {
			// cursor.close();
			return false;
		}
	}

	
	@Override
	public <T> boolean isExistsPublicChat(String id) {

		String query = "SELECT * FROM publicChat WHERE id = '" + id+"'";

		SQLiteDatabase db = this.dataBase;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			// cursor.close();
			return true;
		} else {
			// cursor.close();
			return false;
		}
	}


	public int rowsCount() {
		String query = "SELECT Count(*) as Count FROM privateChat";

		SQLiteDatabase db = this.dataBase;
		Cursor cursor = db.rawQuery(query, null);
		int count = 0;

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			count = cursor.getInt(cursor.getColumnIndex("Count"));

			cursor.moveToNext();
		}

		return count;

	}

	
	
	public int deleteRowsCount(String senderId,String receiverId,String idList) {
		String query = "SELECT Count(*) as Count FROM privateChat  where senderId='"+senderId+"' AND receiverId='"+receiverId+"'  AND id not in ("+idList+") AND +DateOfInsert <= date('now','-5 day')";

		SQLiteDatabase db = this.dataBase;
		Cursor cursor = db.rawQuery(query, null);
		int count = 0;

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			count = cursor.getInt(cursor.getColumnIndex("Count"));

			cursor.moveToNext();
		}

		System.out.println(" id>> Count >>"+count);
		return count;

	}

	
	@Override
	public <T> T selectMsg(String whereID) {

		String query = "SELECT * FROM privateChat WHERE id = '" + whereID+"'";

		SQLiteDatabase db = this.dataBase;
		Cursor cursor = db.rawQuery(query, null);
		ChatModel chatModel = new ChatModel();
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			String id = cursor.getString(cursor.getColumnIndex("id"));
			String senderId = cursor.getString(cursor.getColumnIndex("senderId"));
			String receiverId = cursor.getString(cursor.getColumnIndex("receiverId"));
			String msg = cursor.getString(cursor.getColumnIndex("msg"));
			

			chatModel.setId(id);
			chatModel.setSenderId(senderId);
			chatModel.setReceiverId(receiverId);
			chatModel.setBody(msg);
			
			System.out.println(" id >>"+chatModel.getId()+" senderId>> "+chatModel.getSenderId()+" >receiverId>"+chatModel.getReceiverId());

			cursor.moveToNext();
		}
		return (T) chatModel;
	}
	
	
	
	@Override
	public <T> T selectPublicChatTopic(String whereID) {

		String query = "SELECT * FROM publicChat WHERE id = '" + whereID+"'";

		SQLiteDatabase db = this.dataBase;
		Cursor cursor = db.rawQuery(query, null);
		GroupModel chatModel = new GroupModel();
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			String id = cursor.getString(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String description = cursor.getString(cursor.getColumnIndex("description"));
			String category = cursor.getString(cursor.getColumnIndex("category"));
			
			String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
			String deleted = cursor.getString(cursor.getColumnIndex("deleted"));
			String created = cursor.getString(cursor.getColumnIndex("created"));
			String modified = cursor.getString(cursor.getColumnIndex("modified"));
			String posts = cursor.getString(cursor.getColumnIndex("posts"));
			
			String members = cursor.getString(cursor.getColumnIndex("members"));
			String DateOfInsert = cursor.getString(cursor.getColumnIndex("DateOfInsert"));


			    
			chatModel = new GroupModel(id,title,description,category,avatar,created,modified,posts,members,deleted,true);
			
			
			//System.out.println(" id >>"+chatModel.getId()+" senderId>> "+chatModel.getSenderId()+" >receiverId>"+chatModel.getReceiverId());

			cursor.moveToNext();
		}
		return (T) chatModel;
	}

	@SuppressWarnings("unchecked")
	public <T> T selectObject(int whereID, String senderID) {

		String query = "SELECT * FROM privateChat WHERE id ='" + whereID+ "' ";
		
		SQLiteDatabase db = this.dataBase;
		Cursor cursor = db.rawQuery(query, null);
		ChatModel chatModel = null;
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			chatModel = new ChatModel();
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String senderId = cursor.getString(cursor.getColumnIndex("senderId"));
			String receiverId = cursor.getString(cursor.getColumnIndex("receiverId"));
			String msg = cursor.getString(cursor.getColumnIndex("msg"));
			

			chatModel.setId(id);
			chatModel.setSenderId(senderId);
			chatModel.setReceiverId(receiverId);
			chatModel.setBody(msg);
			
			System.out.println(" id >>"+chatModel.getId()+" senderId>> "+chatModel.getSenderId()+" >receiverId>"+chatModel.getReceiverId());

			cursor.moveToNext();
		}
		return (T) chatModel;
	}



	@Override
	public <T> List<T> selectAll()
	{
	   
	    return null;
	}



	@Override
	public <T> List<T> selectAllMsg(String where)
	{
	    
	    return null;
	}


	

	
}
