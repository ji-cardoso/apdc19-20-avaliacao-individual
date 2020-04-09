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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.EntityNotFoundException;


import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.RemoveData;




//remove own user
@Path("/remove")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private final Gson g = new Gson();
	
	private static final String USER_GBO = "GBO";
	private static final String USER = "USER";


	
	public RemoveResource()
	{
		
	}
	
	@Path("/self")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response removeSelf(RemoveData data) throws EntityNotFoundException
	{
		Transaction trans = datastore.beginTransaction();
		
		
		try
		{
			
			Key userKey = KeyFactory.createKey("user", data.user);
			
			datastore.delete(userKey);
			trans.commit();
			return Response.ok().entity("User deleted").build();
		}
		catch(Exception e)
		{
			trans.rollback();
			return Response.status(Status.FORBIDDEN).entity("Attempt to remove user failed.").build();
		}
		finally
		{
			if(trans.isActive())
				trans.rollback();
		}
		
	}
	
	@Path("/other")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response removeOther(RemoveData data) throws EntityNotFoundException
	{		
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction trans = datastore.beginTransaction(options);
		
		Key tokenKey = KeyFactory.createKey("user_token", data.user_token);

		try
		{
			Entity user_logged_in = datastore.get(tokenKey);
			if(user_logged_in.getProperty("user_role").equals(USER_GBO) && user_logged_in.getProperty("user").equals(data.user))
			{
				Key removing = KeyFactory.createKey("user", data.removing);
				try
				{
					Entity user_to_remove = datastore.get(removing);
					if(user_to_remove.getProperty("user_role").equals(USER))
					{
						datastore.delete(removing);
						trans.commit();
						LOG.warning("User deleted.");
						return Response.ok().build();
					}
					else
						return Response.status(Status.BAD_GATEWAY).entity("The account you're trying to delete doesn't exist.").build();
				}
				catch(Exception e2)
				{
					return Response.status(Status.BAD_GATEWAY).entity("The account you're trying to delete doesn't have the role USER.").build();
				}
			}
			Key userKey = KeyFactory.createKey("user", data.user);
			
			datastore.delete(userKey);
			trans.commit();
			return Response.ok().entity("User deleted").build();
		}
		catch(Exception e)
		{
			trans.rollback();
			return Response.status(Status.BAD_GATEWAY).entity("Error with token ID.").build();
		}
		finally
		{
			if(trans.isActive())
				trans.rollback();
		}
	}
	
}
	
	
