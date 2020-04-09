package pt.unl.fct.di.apdc.firstwebapp.util;

public class RemoveData {
	
	public String user;
	public String user_token; 
	public String removing; 
	public String user_role;
	
	public RemoveData() {
		
	}
	
	public RemoveData(String user, String user_token, String removing, String user_role) {
		this.user_token = user_token;
		this.user = user;
		this.removing = removing;
		this.user_role = user_role;
	}

}
