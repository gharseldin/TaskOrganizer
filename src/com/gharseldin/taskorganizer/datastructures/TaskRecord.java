package com.gharseldin.taskorganizer.datastructures;

import java.io.Serializable;

public class TaskRecord implements Serializable{

	private static final long serialVersionUID = 7526472295622776147L;
	private int taskID, userID;
	private String content, picture, dateSet, dateDue, emailNotify, smsNotify, status, priority;
	
	
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public int getTaskID() {
		return taskID;
	}
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getDateSet() {
		return dateSet;
	}
	public void setDateSet(String dateSet) {
		this.dateSet = dateSet;
	}
	public String getDateDue() {
		return dateDue;
	}
	public void setDateDue(String dateDue) {
		this.dateDue = dateDue;
	}
	public String getEmailNotify() {
		return emailNotify;
	}
	public void setEmailNotify(String emailNotify) {
		this.emailNotify = emailNotify;
	}
	public String getSmsNotify() {
		return smsNotify;
	}
	public void setSmsNotify(String smsNotify) {
		this.smsNotify = smsNotify;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
