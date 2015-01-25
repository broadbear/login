package org.mike.resources;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.mvc.ErrorTemplate;
import org.glassfish.jersey.server.mvc.Viewable;
import org.hibernate.validator.constraints.NotEmpty;
import org.mike.config.Config;
import org.mike.config.Routes;
import org.mike.helpers.Flash.MessageType;
import org.mike.models.ErrorCollection;
import org.mike.models.User;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class SessionResource extends ApplicationController {
	final static Logger log = LoggerFactory.getLogger(SessionResource.class);
	
	@GET
	public Response root(@QueryParam("message") String message) {
		if (!StringUtils.isEmpty(message)) {
			ErrorCollection messages = new ErrorCollection("...", message);
			return render("/index.jsp", messages);
		}
		return render("/index.jsp", null);
	}
	
	@Path(Routes.LOGIN_PATH)
	@GET
	public Viewable newSession() {
		return new Viewable("/sessions/new.jsp");
	}

	@GET
	@Path("twitter_login")
	public Response authenticateTwitter() {		
		OAuthService service = new ServiceBuilder()
									.provider(TwitterApi.class)
									.apiKey(Config.getSingleton().getTwitterConsumerKey())
									.apiSecret(Config.getSingleton().getTwitterConsumerSecret())
									.callback(Config.getSingleton().getTwitterOauthCallback())
									.build();
		sessionHelper.setOAuthService(service);
		Token requestToken = service.getRequestToken();
		sessionHelper.setRequestToken(requestToken);
		String authorizationUri = service.getAuthorizationUrl(requestToken);	
		return Response.seeOther(UriBuilder.fromUri(authorizationUri).build()).build();
	}

	/*
		 use cases
		 I. user logs in with twitter first time
		 - user logs in with twitter
		 - access token retrieved, account 'verified'
		 - email gen'd from twitter account info (id+@email.com)
		 - user not found in app db
		 - new user created/saved
		 - user logged in (remembered?)
		 
		 II. user logs in with twitter second time
		 - user logs in with twitter
		 - access token retrieved, account 'verified' (is this necessary if access token persisted with user?)
		 - email gen'd from twitter account info (id+@email.com)
		 - user found in app db
		 - user logged in (remembered?)
		
		 III. user logs in with twitter and changes email address
		 - user logs in with twitter
		 - user navigates to 'settings' page and updates email address
		 
		 IV. user logs in with twitter after changing email address
		 - user logs in with twitter
		 - new user will be created as gen'd email no longer matches email in app db (TODO: how to handle this?)
		 - user will now essentially have two accounts, and theoretically only two (user can't change email address to same email again, as email must be unique. gen'd email should be same each time. user could change to different email, then there would be account for each unique email address)
		 - *could consider not allowing user to change email at all? or restrict to admin roles?
		 
		 V. error during twitter login
		 - user logs in with twitter
		 - an error occurs during twitter login
		 - user is redirected to info page displaying highlighted warning
	 */
	@GET
	@Path("twitter_authentication_callback")
	public Response twitterAuthenticationCallback(
			@QueryParam("oauth_token") String oauthToken, 
			@QueryParam("oauth_verifier") String oauthVerifier) 
					throws URISyntaxException {	
		
		Token requestToken = sessionHelper.getRequestToken(); // TODO: null? (not likely)
		Verifier v = new Verifier(oauthVerifier);
		OAuthService service = sessionHelper.getOAuthService();
		Token accessToken = service.getAccessToken(requestToken, v); // TODO: uh oh, service was null!
		// TODO: null service in dev, if starting with localhost:8080, and callback is 24.209.173.162:8080, session does not stick?
		
		// if all's well with accessToken get user account info
		OAuthRequest request = new OAuthRequest(Verb.GET, Config.getSingleton().getTwitterUserVerificationUrl());
		service.signRequest(accessToken, request);
		org.scribe.model.Response response = request.send();
		if (!(response.getCode() == 200)) {
			log.error("Problem with Twitter login: response code: "+response.getCode()+", message: "+response.getMessage());
			sessionHelper.addFlashMessage(MessageType.warning, "Something went wrong with Twitter login. Please register and login with a standard account.");
			return redirectTo("");
		}
		LinkedHashMap account = (LinkedHashMap)convertToPojo(response.getBody(), Object.class);
		
		// gen fake email address and find user
		String fakeTwitterEmail = account.get("id_str") + "@faketwitteremail.com";
		User user = User.findBy(User.class, "email", fakeTwitterEmail);
		// if user does not exist, create new user
		if (user == null) {
			user = User.createNew(User.class);
			user.setName((String)account.get("name"));
			user.setEmail(fakeTwitterEmail);
			// TODO: set accessToken and secret, should I persist this?
			// - I suppose if you had an access token persisted, you would not need to
			//    visit Twitter, unless the token was invalidated or expired
			
			String pwd = RandomStringUtils.random(32, "ABCDEFGHIJKLIMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
			user.setPassword(pwd);
			user.setPasswordConfirmation(pwd);
			// TODO: role/group
			
			if (user.save()) {			
				user.activate();
			}
			else {
				sessionHelper.addFlashMessage(MessageType.warning, "Something went wrong with Twitter login. Please register and login with a standard account.");
				return redirectTo("");
			}
		}
		
		// login/remember, etc...
		sessionHelper.logIn(user);
		sessionHelper.remember(user);
		return redirectTo("users/"+user.getId());
	}
	
	Object convertToPojo(String json, Class clazz) {
		Object map = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
//			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // TODO: can't find DF
			map = mapper.readValue(json, Object.class);
		} catch (JsonParseException e) {
			log.error("Problem parsing json", e);
		} catch (JsonMappingException e) {
			log.error("Problem mapping json", e);
		} catch (IOException e) {
			log.error("Problem reading json", e);
		}
		return map;
	}
	
	@Path(Routes.LOGIN_PATH)
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ErrorTemplate(name = "/sessions/new.jsp")
	@Valid
	public Response create(@NotEmpty @FormParam("email") String email, 
			@NotEmpty @FormParam("password") String password,
			@FormParam("rememberMe") String rememberMe) 
					throws URISyntaxException {
		User user = User.findBy(User.class, "email", StringUtils.lowerCase(email));
		if (user != null && user.authenticate(password)) {
			if (user.isActivated()) {
				sessionHelper.logIn(user);
				if (StringUtils.equals(rememberMe, "1")) {
					sessionHelper.remember(user);
				}
				else {
					sessionHelper.forget(user);
				}
				return redirectTo("users/"+user.getId()); // TODO: redirect_back_or
			}
			else {
				sessionHelper.addFlashMessage(MessageType.danger, "Account not activated. Check your email for the activation link.");
				return redirectTo("");
			}
		}
		else {
			ErrorCollection errors = new ErrorCollection("...", "Incorrect username or password.");
			return render("/sessions/new.jsp", errors);
		}
	}
	
	@Path(Routes.LOGOUT_PATH)
	@GET
	public Response destroy() throws URISyntaxException {
		if (sessionHelper.isLoggedIn()) {
			sessionHelper.logOut();
		}
		return redirectTo("login");
	}
}
