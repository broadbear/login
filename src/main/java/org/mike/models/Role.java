package org.mike.models;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "roles")
public class Role extends ActiveRecord {

	// TODO: workaround as primary key to this collection is not _id, 
	//  I just don't want to deal with the autogen'd ObjectId for these
	//  classes. This, however, is very, very messy. A first whack at
	//  generalizing _id to T instead of ObjectId was not successful.
	String id;
	String[] permissions;
	
	public Permission[] getPermissions() {
		if (permissions != null) {
			Permission[] permissionObjs = new Permission[permissions.length];
			for (int i = 0; i < permissions.length; i++) {
				Permission p = Permission.findBy(Permission.class, "id", permissions[i]);
				permissionObjs[i] = p;
			}
			return permissionObjs;
		}
		else {
			return new Permission[0];
		}
	}
	
	public String getLesserId() {
		return id;
	}
}
