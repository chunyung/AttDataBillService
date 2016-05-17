package com.google.attUsage.connection;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

//Spring Framework controller setting for notification which is used to remind each user the amount of wireless data they used
@Controller
public class NotifyController {
	//entrance point to send text message to the wireless line about their data usage
	@RequestMapping("/datanotify/{telNum}")
	public void notifyUserData(@PathVariable String telNum) throws UnsupportedEncodingException, TooManyResultsException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity groupEntity = datastore.prepare(new Query("Group")).asSingleEntity();
		if (telNum.equals("all")) {
			//Filter filter = new FilterPredicate("telNumber", FilterOperator.NOT_EQUAL, "2133004821");
			Query query = new Query("User");
			//query.setFilter(filter);
			for (Entity entity : datastore.prepare(query).asIterable()) {
				datanotify(entity, groupEntity);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			Filter filter = new FilterPredicate("telNumber", FilterOperator.EQUAL, telNum);
			Query query = new Query("User").setFilter(filter);
			datanotify(datastore.prepare(query).asSingleEntity(), groupEntity);
		}
	}

	@RequestMapping("/balance")
	public void createBalance() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new FilterPredicate("id", FilterOperator.EQUAL, "balance");
		Query query = new Query("Bill");
		query.setFilter(filter);
		Entity balanceEntity = datastore.prepare(query).asSingleEntity();
		filter = new FilterPredicate("status", FilterOperator.EQUAL, "current");
		Entity currentBillEntity = datastore.prepare(new Query("Bill").setFilter(filter)).asSingleEntity();
		if (balanceEntity == null) {
			Entity e = new Entity("Bill", "balance");
			for (Map.Entry<String, Object> entry : currentBillEntity.getProperties().entrySet()) {
				if (entry.getKey().equals("status") || entry.getKey().equals("period") || entry.getKey().equals("totalBill")) {
					continue;
				} else {
					e.setProperty(entry.getKey(), 0);
				}
			}
			datastore.put(e);	
		} else {
			Map<String, Object> currentBalance = balanceEntity.getProperties();
			for (Map.Entry<String, Object> entry : currentBillEntity.getProperties().entrySet()) {
				if (entry.getKey().equals("status") || entry.getKey().equals("period") || entry.getKey().equals("totalBill")) {
					continue;
				} else if (!currentBalance.containsKey(entry.getKey())) {
					balanceEntity.setProperty(entry.getKey(), 0);
				}
			}
			datastore.put(balanceEntity);
		}
	}
	
	@RequestMapping("/billnotify/{telNum}")
	public void notifyUserBill(@PathVariable String telNum) throws UnsupportedEncodingException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new FilterPredicate("status", FilterOperator.EQUAL, "current");
		Query query = new Query("Bill");
		query.setFilter(filter);
		Entity billEntity = datastore.prepare(query).asSingleEntity();
		if (telNum.equals("all")) {
			billnotify(billEntity);
		} else {
			billnotify(telNum, (String)billEntity.getProperty(telNum), (String)billEntity.getProperty("period"));
		}
	}
	
	private void billnotify(String telNum, String bill, String period) throws UnsupportedEncodingException {
		String sender = "chunyung@gmail.com", receiver = telNum + "@txt.att.net";
		String mailBody = "\nYour current At&t bill for " + period + " is: $" + bill;
		mailout(sender, receiver, mailBody);
	}
	
	private void billnotify(Entity entity) throws UnsupportedEncodingException {
		for (Map.Entry<String, Object> entry : entity.getProperties().entrySet()) {
			if (entry.getKey().equals("status") || entry.getKey().equals("period") || entry.getKey().equals("totalBill")) {
				continue;
			} else {
				billnotify(entry.getKey(), (String)entry.getValue(), (String)entity.getProperty("period"));
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void mailout(String sender, String receiver, String mailBody) throws UnsupportedEncodingException {
		Session session = Session.getDefaultInstance(new Properties(), null);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(sender, "At&t Mobile Share Group"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver, ""));
            msg.setText(mailBody);
            Transport.send(msg);
        } catch (AddressException e) {
            // ...
        } catch (MessagingException e) {
            // ...
        }
	}
	
	private void datanotify(Entity entity, Entity groupEntity) throws UnsupportedEncodingException {
		User user = User.getUserFromEntity(entity);
		//String mailBody = "\nTest";
		String mailBody = "\nYour usuage: " + user.dataUsage + " GB data, " + user.textUsage + " of unlimited messages.\n";
		mailBody += "Group usuage: " + String.format("%.2f", (Double)(groupEntity.getProperty("totalUsage"))) + "/40 GB.\n";
		mailBody += "Next billing cycle in " + String.valueOf((Long)(groupEntity.getProperty("billingCycle"))) + " days.\n";
		String receiver = user.telNumber + "@txt.att.net", sender = "chunyung@gmail.com";
		mailout(sender, receiver, mailBody);
	}
}
