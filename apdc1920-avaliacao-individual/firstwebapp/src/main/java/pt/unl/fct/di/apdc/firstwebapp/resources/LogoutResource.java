package pt.unl.fct.di.apdc.firstwebapp.resources;


import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;



import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.LogoutData;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;


@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {

	/**
	 * Logger Object
	 */
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private final Gson g = new Gson();

	@Path("/v1")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response logout(LogoutData data)
	{
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction trans = datastore.beginTransaction(options);
		try 
		{
			Key userKey = KeyFactory.createKey("token", data.user_token);
			Entity user_logged_in = datastore.get(userKey);
			if(user_logged_in.getProperty("user").equals(data.user))
			{
				datastore.delete(userKey);
				trans.commit();
				return Response.ok().build();
			}
			else
			{
				trans.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
		}
		catch(EntityNotFoundException e)
		{
			trans.rollback();
			return Response.status(Status.NOT_FOUND).entity("Couldn't logout.").build();
		}
		finally
		{
			if(trans.isActive())
				trans.rollback();
		}

	}

}
