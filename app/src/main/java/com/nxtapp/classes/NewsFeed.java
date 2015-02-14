package com.nxtapp.classes;

import java.util.ArrayList;

public class NewsFeed {

	private String activityType;
	private String body;
	private String created;
	private String id;
	private String image;
	ArrayList<UserModel> UserDetail = new ArrayList<UserModel>();

	public ArrayList<UserModel> getUserDetail() {
		return UserDetail;
	}

	public void setUserDetail(ArrayList<UserModel> userDetail) {
		UserDetail = userDetail;
	}

	public String getActivityType() {
		return activityType;
	}

	public String getBody() {
		return body;
	}

	public String getCreated() {
		return created;
	}

	public String getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
