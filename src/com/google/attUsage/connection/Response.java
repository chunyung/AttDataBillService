package com.google.attUsage.connection;

public class Response {//class for response, used to form JSON string for response in order to update status back to client
	public int status;
	public String message;
	public Response(int status, String message) {
		this.status = status;
		this.message = message;
	}
}
