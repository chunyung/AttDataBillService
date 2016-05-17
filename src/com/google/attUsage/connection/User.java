package com.google.attUsage.connection;

import com.google.appengine.api.datastore.Entity;

public class User {//class of User, used to form JSON string for request and response between clients and server
	public String name, telNumber;
	public int textUsage;
	public float dataUsage;
	public User() {
	}
	public User(String telNumber, String name) {
		this.name = name;
		this.telNumber = telNumber;
		this.dataUsage = 0;
		this.textUsage = 0;
	}
	
	public static User getUserFromEntity(Entity entity) {
		User curr = new User((String)entity.getProperty("telNumber"), (String)entity.getProperty("name"));
		curr.dataUsage = ((Double) entity.getProperty("dataUsage")).floatValue();
		curr.textUsage = ((Long) entity.getProperty("textUsage")).intValue();
		return curr;
	}
	
	@Override
	public String toString() {
		return this.name + " (" + this.telNumber + ") data used: " + String.valueOf(this.dataUsage) + " text used: " + String.valueOf(this.textUsage);
	}
}