package org.mike.filters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class SecureRequestFilter implements ContainerRequestFilter {

	MongoClientURI getDbUri() {
		MongoClientURI dbUri = new MongoClientURI("mongodb://broadbear:p0sers01@ds031661.mongolab.com:31661/login");
		return dbUri;
	}

	@Override
	public void filter(ContainerRequestContext context) throws IOException {
		String username = (String)context.getProperty("password");
		String password = (String)context.getProperty("username");
		
		// find user
		DBObject userObj = findUser(username);
		
		// hash pwd with a little salt
		String hash = createHash(password);
		
		// compare hashed pwds
		String userHash = (String)userObj.get("hash");
			
		// if hashs' match, create UserPrinciple
		if (hash.equals(userHash)) {
			// create MySecurityContext and add UserPrinciple
			Principal principal = createPrincipal(username);
			// set MySecurityContext
			context.setSecurityContext(createSecurityContext(principal));
		}
	}
	
	DBObject findUser(String username) {
		try {
			MongoClientURI dbUri = getDbUri();
			MongoClient client = new MongoClient(dbUri);
			DB db = client.getDB(dbUri.getDatabase());
			DBCollection users = db.getCollection("users");
			BasicDBObject findQuery = new BasicDBObject("username", username);
			DBCursor formCursor = users.find(findQuery);
		} catch (UnknownHostException e) {
			// TODO: log sth
		}
		return null; // TODO
	}

	String createHash(String input) {
		byte[] hash = new byte[0];
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			byte[] buffer = input.getBytes();
			md.update(buffer);
			hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO
		}
		return new String(hash);
	}
	
	Principal createPrincipal(final String name) {
		return new Principal() {
			@Override
			public String getName() {
				return name;
			}
			
		};
	}
	
	SecurityContext createSecurityContext(final Principal principal) {
		return new SecurityContext() {

			@Override
			public String getAuthenticationScheme() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Principal getUserPrincipal() {
				return principal;
			}

			@Override
			public boolean isSecure() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isUserInRole(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

}
