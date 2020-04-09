package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
import com.google.gson.Gson;


@Path("/registerSU")
public class RegisterSUResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	// Instantiates a client
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private final Gson g = new Gson();
	private static final String USER_SU = "SU";
	public RegisterSUResource()
	{
		
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response registerSU() throws EntityNotFoundException {

		Transaction trans = datastore.beginTransaction();
		LOG.fine("Attempt to register user: " + "su");
		Key userKey = KeyFactory.createKey("user", "su");

		try {
			datastore.get(userKey);
			trans.rollback();
			return Response.status(Status.FOUND).build();
		}
		catch(Exception e) {
			Entity user = new Entity("user", "su");
			user.setProperty("user_name", "System User");
			user.setProperty("user_email", "su@gmail.com");
			user.setProperty("user_pwd", DigestUtils.sha512Hex("password").toString());
			user.setProperty("user_address_street", "system street");
			user.setProperty("user_address_place", "system place");
			user.setProperty("user_address_country", "internet");
			user.setProperty("user_address_cp", "1010-100");
			user.setIndexedProperty("user_creation_time", new Date());
			user.setProperty("user_role", USER_SU);
			user.setProperty("user_phone_number", "123456789");

			datastore.put(trans,user);
			LOG.info("User " + "su" + "successfully registered.");
			trans.commit();
			return Response.ok().build();
		}
		finally
		{
			if(trans.isActive())
				trans.rollback();
		}
	}

}
