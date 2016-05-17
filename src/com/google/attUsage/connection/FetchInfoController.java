package com.google.attUsage.connection;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

//Spring Framework controller setting for retrieving wireless data usage information
@Controller
@RequestMapping("/fetch")
public class FetchInfoController {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	//entrance point to reteieving wireless data usage for whole group; response is wrapped in JSON
	@RequestMapping("/group")
	public @ResponseBody UsageInfo getUsers() {
		Query q = new Query("User");
		PreparedQuery queryResult = datastore.prepare(q);
		List<User> users = new ArrayList<User>();
		for (Entity entity : queryResult.asIterable()) {
			users.add(User.getUserFromEntity(entity));
		}
		UsageInfo result = new UsageInfo();
		result.users = users;
		Entity entity = datastore.prepare(new Query("Group")).asSingleEntity();
		result.billingCycle = ((Long)entity.getProperty("billingCycle")).intValue();
		result.leftUsage = ((Double)entity.getProperty("leftUsage")).floatValue();
		result.totalData = ((Double)entity.getProperty("totalData")).floatValue();
		result.totalUsage = ((Double)entity.getProperty("totalUsage")).floatValue();
		result.updateDate = (String)entity.getProperty("updateDate");
		return result;
	}
}
