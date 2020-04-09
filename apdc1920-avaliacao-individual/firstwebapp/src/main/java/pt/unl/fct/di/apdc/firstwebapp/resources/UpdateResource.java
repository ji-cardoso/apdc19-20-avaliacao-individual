package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.UpdateData;

@Path("/update")
public class UpdateResource 
{

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	// Instantiates a client
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private final Gson g = new Gson();
	private static final String USER = "USER";
	private static final String USER_GBO = "GBO";

	public UpdateResource()
	{

	}

	@PUT
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updateUser(UpdateData data) throws EntityNotFoundException {

		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction trans = datastore.beginTransaction(options);

		Key tokenKey = KeyFactory.createKey("token", data.user_token);

		try {
			Entity user_logged_in = datastore.get(tokenKey);
			if(user_logged_in.getProperty("user_role").equals(USER_GBO) || (user_logged_in.getProperty("user_role").equals(USER) && (user_logged_in.getProperty("user").equals(data.user))))
			{
					try
					{
						Key userKey = KeyFactory.createKey("user", data.user);
						Entity user = datastore.get(userKey);
						if(data.user_name != null && !data.user_name.equals(""))
							user.setProperty("user_name", data.user_name);
						if(data.user_email != null && !data.user_email.equals(""))
							user.setProperty("user_email", data.user_email);
						if(data.user_pwd != null && !data.user_pwd.equals("") && data.user_pwd.equals(data.confirmation))
							user.setProperty("user_pwd", DigestUtils.sha512Hex(data.user_pwd));
						if(data.user_address_street != null && !data.user_address_street.equals(""))
							user.setProperty("user_address_street", data.user_address_street);
						if(data.user_address_country != null && !data.user_address_country.equals(""))
							user.setProperty("user_address_country", data.user_address_country);
						if(data.user_address_place != null && !data.user_address_place.equals(""))
							user.setProperty("user_address_place", data.user_address_place);
						if(data.user_address_cp != null && !data.user_address_cp.equals(""))
							user.setProperty("user_address_cp", data.user_address_cp);
						if(data.user_phone_number != null && !data.user_phone_number.equals(""))
							user.setProperty("user_phone_number", data.user_phone_number);

						datastore.put(user);
						trans.commit();
						return Response.ok().build();
					}
					catch(Exception e1)
					{
						return Response.status(Status.CONFLICT).build();
					}
				}
			return Response.status(Status.NOT_FOUND).build();
		}
		finally
		{
			if(trans.isActive())
				trans.rollback();
		}
	}

}
