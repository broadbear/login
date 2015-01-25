package org.mike.models;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "permissions")
public class Permission extends ActiveRecord {
	
	// TODO: workaround as primary key to this collection is not _id, 
	//  I just don't want to deal with the autogen'd ObjectId for these
	//  classes. This, however, is very, very messy.
	String id;

	
	public String getLesserId() {
		return id;
	}
}
