package com.google.attUsage.connection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

//Spring Framework controller setting for cron jobs on Google App Engine
@Controller
@RequestMapping("/cron")
public class TextCronJobs {
	//entrance point to send data usage information for each line
	@RequestMapping("/sendDataUsage")
	public void dailyText() {
		RestTemplate template = new RestTemplate();
		template.getForEntity("http://gaeattusage.appspot.com/datanotify/all", null);
	}
	
	//entrance point to send out bill information for each line
	@RequestMapping("/sendBill")
	public void billText() {
		RestTemplate template = new RestTemplate();
		template.getForEntity("http://gaeattusage.appspot.com/billnotify/all", null);
	}
}
