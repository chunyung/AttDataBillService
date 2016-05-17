package com.google.attUsage.connection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

//Spring Framework controller setting for wireless data usage update 
@Controller
public class DataController {
	// entrance point to update data usage of every line in the group
	@RequestMapping(value = "/dataupdate", method = RequestMethod.POST)
	public @ResponseBody Response updateGroup(@RequestBody UsageInfo usageinfo) {
		if (usageinfo == null) {
			return new Response(404, "Fail to update!");
		}
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		if (!updateGroupToStore(datastore, usageinfo)) {
			return new Response(404, "Fail to update!");
		} else {
			return new Response(200, "Update successfully!");
		}
	}
	//update each line user's data usage
	private void updateUser(DatastoreService datastore, User user, int userKey) {
		Entity e = new Entity("User", userKey);
		e.setProperty("name", user.name);
		e.setProperty("telNumber", user.telNumber);
		e.setProperty("dataUsage", user.dataUsage);
		e.setProperty("textUsage", user.textUsage);
		datastore.put(e);
	}
	
	//update whole group's wireless data usage
	public boolean updateGroupToStore(DatastoreService datastore, UsageInfo info) {
		Entity e = null;
		int userKey = 1;
		for (User user : info.users) {
			updateUser(datastore, user, userKey++);
		}
		if (info.totalData != -1) {
			e = new Entity("Group", 1);
			e.setProperty("totalUsage", info.totalUsage);
			e.setProperty("leftUsage", info.leftUsage);
			e.setProperty("totalData", info.totalData);
			e.setProperty("billingCycle", info.billingCycle);
			e.setProperty("updateDate", info.updateDate);
			datastore.put(e);
			return true;
		} else {
			return false;
		}
	}
}
