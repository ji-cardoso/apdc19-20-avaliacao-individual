package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.UUID;

public class AuthToken {
	
	public static final long EXPIRATION_TIME = 1000*60*60*2; //2h
	
	public String user;
	public String user_token;
	public long creationData;
	public long expirationData;
	public String user_role;
	
	public AuthToken() {
		
	}
	
	public AuthToken(String user, String user_role) {
		this.user = user;
		this.user_token = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
		this.user_role = user_role;
	}
}
