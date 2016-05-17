package com.google.attUsage.connection;

public class UserBill {//class for UserBill, used to form JSON string for request and response between clients and server
	public String telNum;
	public float bill;
	public UserBill() {
		
	}
	public UserBill(String telNum, float bill) {
		this.telNum = telNum;
		this.bill = bill;
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.telNum + ": $" + String.format("%.2f", this.bill));
		return result.toString();
	}
}
