package pt.unl.fct.di.apdc.firstwebapp.util;

public class LoginData {
	
	public String user;
	public String user_pwd;
	public String user_token;
	
	public LoginData() {
		
	}
	
	public LoginData(String user, String user_pwd, String user_token) {
		this.user = user;
		this.user_pwd = user_pwd;
		this.user_token = user_token;
	}

}
