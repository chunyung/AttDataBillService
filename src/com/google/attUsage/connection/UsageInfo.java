package com.google.attUsage.connection;
import java.util.ArrayList;
import java.util.List;

public class UsageInfo {// class of UsageInfo
	public int billingCycle;
	public float totalUsage, leftUsage, totalData;
	public String updateDate;
	public List<User> users;
	public UsageInfo() {
		this.users = new ArrayList<User>();
		this.billingCycle = -1;
		this.totalData = -1;
		this.updateDate = "";
		this.totalUsage = 0;
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (User user : users) {
			result.append(user.toString()).append("\n");
		}
		result.append("Group used: " + this.totalUsage + "GB, left: " + this.leftUsage + "GB, total data: " + this.totalData + "GB.\n");
		result.append("Information updated at: " + this.updateDate);
		return result.toString();
	}
}
