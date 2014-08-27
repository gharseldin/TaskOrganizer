package com.gharseldin.taskorganizer.datastructures;

import java.io.Serializable;


public class SettingsUnit implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int settingId, userId;
	private String key, value;
	
	public String toString(){
		return key + " " + value;
	}
	
	public int getSettingId() {
		return settingId;
	}
	public void setSettingId(int settingId) {
		this.settingId = settingId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
