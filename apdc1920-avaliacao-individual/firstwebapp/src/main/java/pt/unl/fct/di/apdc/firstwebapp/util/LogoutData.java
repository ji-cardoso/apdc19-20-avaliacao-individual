package pt.unl.fct.di.apdc.firstwebapp.util;

public class LogoutData {	
	public String user_token;
	public String user;
	
	public LogoutData() {
		
	}
	
	public LogoutData(String user_token, String user) {
		this.user_token = user_token;
		this.user = user;
	}

}
