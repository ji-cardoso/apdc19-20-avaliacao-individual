package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.HttpHeaders;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;


@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public LoginResource() {}

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLogin(LoginData data, @Context HttpServletRequest request,
			@Context HttpHeaders headers) {
	
		LOG.fine("Attempt to login user: " + data.user);
		
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction trans = datastore.beginTransaction(options);

		Key userKey = KeyFactory.createKey("user", data.user);

		try {
			Entity user = datastore.get(userKey);

			// Obtain the user login statistics
			Query ctrQuery = new Query("UserStats").setAncestor(userKey); // Ancestor Filter
			List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			Entity ustats = null;

			if (results.isEmpty()) {
				ustats = new Entity("UserStats", user.getKey());
				ustats.setProperty("user_stats_logins", 0L);
				ustats.setProperty("user_stats_failed", 0L);
				ustats.setProperty("user", data.user);
			} else {
				ustats = results.get(0);
			}

			if (user.getProperty("user_pwd").equals(DigestUtils.sha512Hex(data.user_pwd).toString())) 
			{ 

				// construct the logs
				Entity log = new Entity("log", user.getKey());	
				log.setProperty("user_login_ip", request.getRemoteAddr()); 
				log.setProperty("user_login_host", request.getRemoteHost()); 
				log.setProperty("user_login_latlon", headers.getHeaderString("X-AppEngine-CityLatLong")); 
				log.setProperty("user_login_city", headers.getHeaderString("X-AppEngine-City")); 
				log.setProperty("user_login_country", headers.getHeaderString("X-AppEngine-Country")); 
				log.setProperty("user_login_time", new Date());

				// Get the user statistics and updates it
				ustats.setProperty("user_stats_logins", 1L + (long) ustats.getProperty("user_stats_logins"));
				ustats.setProperty("user_stats_failed", 0L);
				ustats.setProperty("user_stats_last", new Date());

				// Batch operation
				List<Entity> logs = Arrays.asList(log, ustats); 
				datastore.put(trans, logs);

				// return token
				String userRole = (String) user.getProperty("user_role");
				AuthToken token = new AuthToken(data.user, userRole);
				//Create a new entity with the token
				Entity tokens = new Entity("token", token.user_token);
				tokens.setProperty("user_role", token.user_role);
				tokens.setProperty("user", token.user);
				tokens.setProperty("Creation_Date", token.creationData);
				tokens.setProperty("Expiration_Date", token.expirationData);
				datastore.put(trans, tokens);
				trans.commit();
				
				LOG.warning("User '" + data.user + "' logged in sucessfully.");
				return Response.ok(g.toJson(token)).build();
			} else {
				// Incorrect password
				ustats.setProperty("user_stats_failed", 1L + (long) ustats.getProperty("user_stats_failed"));
				datastore.put(trans, ustats);
				trans.commit();

				return Response.status(1).entity("Wrong password for username: " + data.user).build();
			}
		} catch (EntityNotFoundException e) {
			trans.rollback();
			return Response.status(Status.FORBIDDEN).entity("Failed login attempt for username: " + data.user).build();
		} 

	}
	
}
