package org.mike.resources;

import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.mike.config.Routes;
import org.mike.helpers.Flash.MessageType;
import org.mike.models.User;

@Path("/")
public class AccountActivationResource extends ApplicationController {

	@Path(Routes.EDIT_ACCOUNT_ACTIVATION_PATH)
	@GET
	public Response edit(@PathParam("token") String activationToken, @QueryParam("email") String email) 
			throws URISyntaxException {
		User user = User.findBy(User.class, "email", email);
		if (user != null && !user.isActivated() && user.isAuthenticated("activation", activationToken)) {
			user.activate();
			sessionHelper.logIn(user);
			sessionHelper.addFlashMessage(MessageType.success, "Account activated!");
			return redirectTo("users/"+user.getId());
		}
		else {
			sessionHelper.addFlashMessage(MessageType.danger, "Invalid activation link");
			return redirectTo("");
		}
	}
	
}
