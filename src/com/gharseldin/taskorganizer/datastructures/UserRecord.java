package com.gharseldin.taskorganizer.datastructures;

import java.io.Serializable;

public class UserRecord implements Serializable {

	private static final long serialVersionUID = 7526472295622776147L;
	private int userId;
	private String username, password, firstName, lastName, email, phone;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public UserRecord() {

	}

	public UserRecord(String user, String pass) {
		username = user;
		password = pass;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String returnUsername) {
		this.username = returnUsername;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String returnPassword) {
		this.password = returnPassword;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String returnFirstName) {
		this.firstName = returnFirstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String retrunLastName) {
		this.lastName = retrunLastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String returnEmail) {
		this.email = returnEmail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String returnPhone) {
		this.phone = returnPhone;
	}
}
