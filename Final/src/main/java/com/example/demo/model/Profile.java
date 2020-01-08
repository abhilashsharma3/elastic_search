package com.example.demo.model;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName="profile",type="user")
public class Profile {
	String id;
	String firstName;
	String lastName;
	List<Hobby> hobby;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public List<Hobby> getHobby() {
		return hobby;
	}
	public void setHobby(List<Hobby> hobby) {
		this.hobby = hobby;
	}

}
