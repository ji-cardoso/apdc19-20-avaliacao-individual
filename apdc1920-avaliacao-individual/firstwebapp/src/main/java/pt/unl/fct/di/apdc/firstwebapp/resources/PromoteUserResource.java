package pt.unl.fct.di.apdc.firstwebapp.resources;
import java.util.logging.Logger;


import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.EntityNotFoundException;


import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.PromoteUserData;




//remove own user
@Path("/promoteUser")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PromoteUserResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private final Gson g = new Gson();
	
	private static final String USER_GBO = "GBO";
	private static final String USER_SU = "SU";
	private static final String USER = "USER";


	
	public PromoteUserResource()
	{
		
	}
	
	@Path("/user")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response promoteUser(PromoteUserData data) throws EntityNotFoundException
	{
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction trans = datastore.beginTransaction(options);
		
		
		try
		{
			Key userKey = KeyFactory.createKey("user", data.user_to_promote);
			Key user = KeyFactory.createKey("user", data.user);

			
			Entity user_promote = datastore.get(userKey);
			Entity user_logged_in = datastore.get(user);
			if(user_logged_in.getProperty("user_role").equals(USER_SU))
				if(user_promote.getProperty("user_role").equals(USER))
				{
					trans.commit();
					user_promote.setProperty("user_role", USER_GBO);
					datastore.put(user_promote);
					return Response.ok().build();
				}
				else
					return Response.status(Status.FORBIDDEN).entity("The user you're trying to promote has already the role of GBO.").build();
			else
				return Response.status(Status.FORBIDDEN).entity("You're not allowed to promote a user.").build();
		}
		catch(Exception e)
		{
			trans.rollback();
			return Response.status(Status.FORBIDDEN).entity("Attempt to promote a user failed.").build();
		}
		finally
		{
			if(trans.isActive())
				trans.rollback();
		}
		
	}
}