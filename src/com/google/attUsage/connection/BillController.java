package com.google.attUsage.connection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

//Spring Framework controller setting for Billing information
@Controller
public class BillController {
	//entrance point to update bill information for each line using JSON as input format
	@RequestMapping(value = "/billupdate", method = RequestMethod.POST)
	public @ResponseBody Response updateBill(@RequestBody BillInfo billinfo) {
		if (billinfo == null) {
			return new Response(404, "Fail to update!");
		}
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		if (!updateBillToStore(datastore, billinfo)) {
			return new Response(404, "Fail to update!");
		} else {
			return new Response(200, "Update successfully!");
		}
	}
	
	//update bill information to Google App Engine DataStore
	public boolean updateBillToStore(DatastoreService datastore, BillInfo billinfo) {
		Filter filter = new FilterPredicate("period", FilterOperator.EQUAL, billinfo.period);
		Query query = new Query("Bill");
		query.setFilter(filter);
		Entity sameEntity = datastore.prepare(query).asSingleEntity();
		if (sameEntity != null) {
			return true;
		}
		filter = new FilterPredicate("status", FilterOperator.EQUAL, "current");
		query = new Query("Bill");
		query.setFilter(filter);
		Entity pre = datastore.prepare(query).asSingleEntity();
		if (pre != null) {
			pre.setProperty("status", "past");
			datastore.put(pre);
		}
		Entity entity = new Entity("Bill");
		for (UserBill user : billinfo.users) {
			entity.setProperty(user.telNum, String.valueOf(user.bill));
		}
		entity.setProperty("totalBill", String.valueOf(billinfo.totalBill));
		entity.setProperty("period", billinfo.period);
		entity.setProperty("status", "current");
		datastore.put(entity);
		return true;
	}
}
