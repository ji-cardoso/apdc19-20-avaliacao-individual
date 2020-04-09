package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.ArrayList;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/listLogs")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ListLogsResource {


	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final String USER_SU = "SU";

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response failedLoggedIns(LoginData data){
		List<String> userList = new ArrayList<String>();

		Key userKey = KeyFactory.createKey("user", data.user);

		try {
			Entity user = datastore.get(userKey);
			

			if(user.getProperty("user_role").equals(USER_SU)) {

				Query query = new Query("UserStats");
				PreparedQuery prepare_query = datastore.prepare(query);  
				for (Entity result : prepare_query.asIterable()) 
				{ 
					String username = (String) result.getProperty("user");   
					Long number_of_logs = (Long) result.getProperty("user_stats_failed");   
					String number_of_logs_fails = number_of_logs.toString();
					userList.add(username + ".............." + number_of_logs_fails);
				}
			}
				return Response.ok(g.toJson(userList)).build();

			
		}catch (EntityNotFoundException e) { 
			return Response.status(Status.NOT_ACCEPTABLE).entity("Error with login.").build();
		}

	}


}
