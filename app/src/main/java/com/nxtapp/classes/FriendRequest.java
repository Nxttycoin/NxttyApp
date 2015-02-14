package com.nxtapp.classes;

public class FriendRequest {
	private String userId;
	private String userName;
	private String deletPlanID;
	private String registrationDate;
	private String avatarImage;
	private String userStatus;
	private String school;
	private String gender;

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

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

	public String getDeletPlanID() {
		return deletPlanID;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public String getAvatarImage() {
		return avatarImage;
	}

	public void setDeletPlanID(String deletPlanID) {
		this.deletPlanID = deletPlanID;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public void setAvatarImage(String avatarImage) {
		this.avatarImage = avatarImage;
	}
}
