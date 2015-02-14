package com.nextapp.data;

import java.util.List;

public interface IDataBaseManager {

	public <T> long add(T object);
	
	public <T> long addPublicChat(T object);
	
	public <T> T selectPublicChatTopic(String whereID);
	
	
	
	public <T> int delete(String whereID);
	public <T> int deleteAll();
	public <T> boolean isExists(String id);
	public <T> boolean isExistsPublicChat(String id);
	
	public <T> List<T> selectAll();	
	public <T> List<T> selectAllMsg(String where);
	public <T> T selectMsg(String whereID);
}
