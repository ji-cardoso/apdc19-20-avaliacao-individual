package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterDataLoggedIn {

	public String user;
	public String user_name;
	public String user_email;	
	public String user_pwd;
	public String confirmation;
	public String user_address_street;
	public String user_address_place;
	public String user_address_country;
	public String user_role;
	public String user_address_cp;
	public String user_token;
	public String user_phone_number;
	public String new_user_role;
	
	public RegisterDataLoggedIn() {
		
	}
	
	public RegisterDataLoggedIn(String user, String new_user_role, String user_name, String user_email, String user_pwd, String confirmation, String user_address_street, String user_address_place, String user_address_country, String user_address_cp, String user_token, String user_role, String user_phone_number) {
		
			this.user = user;
			this.user_name = user_name;
			this.user_email = user_email;
			this.user_pwd = user_pwd;
			this.confirmation = confirmation;
			this.user_address_street = user_address_street;
			this.user_address_place = user_address_place;
			this.user_address_country = user_address_country;
			this.user_role = user_role;
			this.user_address_cp = user_address_cp;
			this.user_token = user_token;
			this.user_phone_number = user_phone_number;
			this.new_user_role = new_user_role;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(user) &&
			   validField(user_name) &&
			   validField(user_email) &&
			   validField(user_pwd) &&
			   validField(confirmation) &&
			   user_pwd.equals(confirmation) &&
			   user_email.contains("@") &&
			   user_email.contains(".") &&
			   user_address_cp.matches("[0-9]{4}" + "-" + "[0-9]{3}");
	}
	
	public void setRole(String user_role)
	{
		this.user_role = user_role;
	}

}
