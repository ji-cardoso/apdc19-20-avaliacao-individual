package pt.unl.fct.di.apdc.firstwebapp.util;

public class UpdateData 
{
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
	
	public UpdateData() {
		
	}
	
	public UpdateData(String user, String user_role, String user_name, String user_email, String user_pwd, String confirmation, String user_address_street, String user_address_place, String user_address_country, String user_address_cp, String user_token, String user_phone_number) {
		
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

	}
}
