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

import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterDataLoggedIn;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	// Instantiates a client
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private final Gson g = new Gson();
	private static final String USER = "USER";
	private static final String USER_GBO = "GBO";
	private static final String USER_SU = "SU";

	public RegisterResource() { }


	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegistration(RegisterData data) throws EntityNotFoundException {

		Transaction trans = datastore.beginTransaction();
		LOG.fine("Attempt to register user: " + data.user);
		Key userKey = KeyFactory.createKey("user", data.user);

		try {
			Entity user = datastore.get(userKey);
			trans.rollback();
			return Response.status(Status.FOUND).build();
		}
		catch(Exception e) {
				Entity user = new Entity("user", data.user);
				user.setProperty("user_name", data.user_name);
				user.setProperty("user_email", data.user_email);
				user.setProperty("user_pwd", DigestUtils.sha512Hex(data.user_pwd).toString());
				user.setProperty("user_address_street", data.user_address_street);
				user.setProperty("user_address_place", data.user_address_place);
				user.setProperty("user_address_country", data.user_address_country);
				user.setProperty("user_address_cp", data.user_address_cp);
				user.setIndexedProperty("user_creation_time", new Date());
				user.setProperty("user_role", USER);
				user.setProperty("user_phone_number", data.user_phone_number);

				datastore.put(trans,user);
				LOG.info("User " + data.user + "successfully registered.");
				trans.commit();
				return Response.ok().build();
		}
		finally
		{
			if(trans.isActive())
				trans.rollback();
		}
	}

	@POST
	@Path("/user_logged_in")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegistrationLoggedIn(RegisterDataLoggedIn data) throws EntityNotFoundException {

		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction trans = datastore.beginTransaction(options);
		LOG.fine("Attempt to register user: " + data.user);
		Key userKey = KeyFactory.createKey("user", data.user);
		Key tokenKey = KeyFactory.createKey("token", data.user_token);

		try {
			Entity user_logged_in = datastore.get(tokenKey);
			if(user_logged_in.getProperty("user_role").equals(USER_SU))
			{
				try
				{
					Entity user = datastore.get(userKey);
					trans.rollback();
					return Response.status(Status.FOUND).entity("User with given user already exists.").build();
				} 
				catch(Exception e2)
				{
					Entity user = new Entity("user", data.user);
					user.setProperty("user_name", data.user_name);
					user.setProperty("user_email", data.user_email);
					user.setProperty("user_pwd", DigestUtils.sha512Hex(data.user_pwd));
					user.setProperty("user_address_street", data.user_address_street);
					user.setProperty("user_address_place", data.user_address_place);
					user.setProperty("user_address_country", data.user_address_country);
					user.setProperty("user_address_cp", data.user_address_cp);
					user.setIndexedProperty("user_creation_time", new Date());
					user.setProperty("user_role", data.new_user_role);

					datastore.put(trans,user);
					LOG.info("User " + data.user + "successfully registered.");
					trans.commit();
					return Response.ok().build();
				}
				finally
				{
					if(trans.isActive())
						trans.rollback();
				}
			}
			else
				return Response.status(Status.FORBIDDEN).entity("This user does not have the permission to register a user.").build();
		}
		catch(Exception e)
		{
			return Response.status(Status.BAD_GATEWAY).entity("Wrong token. User is not logged in.").build();
		}
	}



	@POST
	@Path("/gbo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegistrationGBO(RegisterDataLoggedIn data) throws EntityNotFoundException {

		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction trans = datastore.beginTransaction(options);
		LOG.fine("Attempt to register user: " + data.user);

		//checking if user with given user already exists
		try {
			Key userKey = KeyFactory.createKey("user", data.user);
			Entity user = datastore.get(userKey);
			trans.rollback();
			return Response.status(Status.FOUND).entity("User with given user already exists.").build();
		}
		catch(Exception e) {
			//checking if user has permission to register user of role GBO
			try
			{
				Key userKey = KeyFactory.createKey("token", data.user_token);
				Entity user_logged_in = datastore.get(userKey);
				String role = user_logged_in.getProperty("user_role").toString();

				if(role.equalsIgnoreCase(USER_SU))
				{
					Entity user = new Entity("user", data.user);
					user.setProperty("user_name", data.user_name);
					user.setProperty("user_email", data.user_email);
					user.setProperty("user_pwd", DigestUtils.sha512Hex(data.user_pwd));
					user.setProperty("user_address_street", data.user_address_street);
					user.setProperty("user_address_place", data.user_address_place);
					user.setProperty("user_address_country", data.user_address_country);
					user.setProperty("user_address_cp", data.user_address_cp);
					user.setProperty("user_phone_number", data.user_phone_number);
					user.setIndexedProperty("user_creation_time", new Date());
					data.setRole(USER_GBO);
					user.setProperty("user_role", data.user_role);

					datastore.put(trans, user);
					trans.commit();
					return Response.ok().build();
				} 
				else
				{
					trans.rollback();
					return Response.status(Status.CONFLICT).entity("Your role does not give you permission to register a user of role GBO.").build();
				}
			}
			catch(Exception e2)
			{
				trans.rollback();
				return Response.status(Status.NOT_FOUND).entity("User of role SU not found.").build();
			}
		}
		finally
		{
			if(trans.isActive())
				trans.rollback();
		}
	}
}

