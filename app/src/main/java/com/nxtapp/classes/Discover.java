package com.nxtapp.classes;

public class Discover {
private String userId;
private String userName;
private String userStatus;
public String getUserId() {
	return userId;
}
public void setUserId(String userId) {
	this.userId = userId;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getUserStatus() {
	return userStatus;
}
public void setUserStatus(String userStatus) {
	this.userStatus = userStatus;
}
public Discover(String userId, String userName, String userStatus) {
	super();
	this.userId = userId;
	this.userName = userName;
	this.userStatus = userStatus;
}


}
