package org.mike.models;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "groups")
public class Group extends ActiveRecord {

	String id;
	
	public String getLesserId() {
		return id;
	}
}
