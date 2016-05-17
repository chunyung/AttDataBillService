package com.google.attUsage.connection;

import java.util.List;

public class BillInfo {// class of BillInfo, used to form JSON string for request and response between client and server
	public float totalBill;
	public String period;
	public List<UserBill> users;
	public BillInfo() {
	}
	public BillInfo(List<UserBill> users, float totalBill, String period) {
		this.users = users;
		this.totalBill = totalBill;
		this.period = period;
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Total bill: $" + this.totalBill + "\n");
		for (UserBill user : this.users) {
			result.append(user.toString()).append("\n");
		}
		return result.toString();
	}
}
