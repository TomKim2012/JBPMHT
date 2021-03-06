package com.duggan.workflow.shared.model;

import java.io.Serializable;
import java.lang.String;

public class HTUser implements Serializable {

	private static final long serialVersionUID = -5249516544970187459L;
	private String name;
	private String id;
	private String email;
	private String surname;
	private String password;

	public HTUser() {
	}

	public HTUser(String id) {
		this.id = id;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
