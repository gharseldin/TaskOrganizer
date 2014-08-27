package com.gharseldin.taskorganizer.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class SettingWrapper implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private ArrayList<SettingsUnit> profile;
	
	public SettingWrapper(ArrayList<SettingsUnit> profile){
		this.profile = profile;
	}
	
	public ArrayList<SettingsUnit> getProfile(){
		return profile;
	}

}
