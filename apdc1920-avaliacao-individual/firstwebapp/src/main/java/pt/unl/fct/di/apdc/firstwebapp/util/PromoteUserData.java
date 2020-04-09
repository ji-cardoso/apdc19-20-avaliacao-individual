package pt.unl.fct.di.apdc.firstwebapp.util;

public class PromoteUserData {
	
	public String user;
	public String user_token;
	public String user_to_promote;
	
	public PromoteUserData() {
		
	}
	
	public PromoteUserData(String user, String user_token, String user_to_promote) {
		this.user_token = user_token;
		this.user = user;
		this.user_to_promote = user_to_promote;
	}

}
