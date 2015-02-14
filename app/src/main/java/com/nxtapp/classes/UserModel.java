package com.nxtapp.classes;

public class UserModel {

	String avatar, city, deletePlanId, gender, nameAlias, nxtAccountId,
			registrationDate, school, status;
	
	ChatModel lastMessage;

	public ChatModel getLastMessage()
	{
	    return lastMessage;
	}

	public void setLastMessage(ChatModel lastMessage)
	{
	    this.lastMessage = lastMessage;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getCity() {
		return city;
	}

	public String getDeletePlanId() {
		return deletePlanId;
	}

	public String getGender() {
		return gender;
	}

	public String getNameAlias() {
		return nameAlias;
	}

	public String getNxtAccountId() {
		return nxtAccountId;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public String getSchool() {
		return school;
	}

	public String getStatus() {
		return status;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setDeletePlanId(String deletePlanId) {
		this.deletePlanId = deletePlanId;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setNameAlias(String nameAlias) {
		this.nameAlias = nameAlias;
	}

	public void setNxtAccountId(String nxtAccountId) {
		this.nxtAccountId = nxtAccountId;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
