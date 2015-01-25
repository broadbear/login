package org.mike.resources;

import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.mike.config.Routes;
import org.mike.helpers.Flash.MessageType;
import org.mike.models.ErrorCollection;
import org.mike.models.User;

@Path("/")
public class PasswordResetResource extends ApplicationController {
	// TODO: 
	// before_action :get_user, only: [:edit, :update]
	// before_action :valid_user, only: [:edit, :update]
	// before_action :check_expiration, only: [:edit, :update]
	
	@Path(Routes.NEW_PASSWORD_RESET_PATH)
	@GET
	public Response newPasswordReset(@QueryParam("message") String message) {
		if (!StringUtils.isEmpty(message)) {
			ErrorCollection messages = new ErrorCollection("...", message);
			return render("/password_resets/new.jsp", messages);
		}
		return render("/password_resets/new.jsp", null);
	}

	@Path(Routes.PASSWORD_RESETS_PATH)
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response create(@FormParam("email") String email) 
			throws URISyntaxException {
		User user = User.findBy(User.class, "email", StringUtils.lowerCase(email));
		if (user != null) {
			user.createResetDigest();
			user.sendPasswordResetEmail();
			sessionHelper.addFlashMessage(MessageType.success, "Email sent with password reset instructions");
			return redirectTo("");
		}
		else {
			ErrorCollection errors = new ErrorCollection("...", "Email address not found");
			return render("/password_resets/new.jsp", errors);
		}
		
	}

	@Path(Routes.EDIT_PASSWORD_RESET_PATH)
	@GET
	public Response edit(
			@PathParam("token") String resetToken, 
			@QueryParam("email") String email) 
			throws URISyntaxException {
		User user = User.findBy(User.class, "email", email);
		if (user == null || !isValidUser(user, resetToken)) {
			sessionHelper.addFlashMessage(MessageType.danger, "Account not activated. Check your email for the activation link.");
			return redirectTo("");
		}
		if (isExpired(user)) {
			sessionHelper.addFlashMessage(MessageType.danger, "Password reset has expired.");
			return redirectTo("password_resets/new");
		}
		
		user.setResetToken(resetToken);
		return render("/password_resets/edit.jsp", user);
	}
	
	@Path(Routes.PASSWORD_RESET_PATH)
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response update(
			@PathParam("token") String resetToken, 
			MultivaluedMap<String, String> formParams) 
					throws URISyntaxException {
		String email = formParams.getFirst("email");
		User user = User.findBy(User.class, "email", email);
		if (user != null && !isValidUser(user, resetToken)) {
			sessionHelper.addFlashMessage(MessageType.danger, "Account not activated. Check your email for the activation link.");
			return redirectTo("");
		}
		if (isExpired(user)) {
			sessionHelper.addFlashMessage(MessageType.danger, "Password reset has expired.");
			return redirectTo("password_resets/new");
		}
		
		// Exit action if pwd blank, this is because updateAttributes allows blank pwd/conf
		if (areBothPasswordsBlank(formParams)) {
			// TODO: get errors added to user instance somehow and pass user instance back as model, but must have user instance cause you need email
			ErrorCollection errors = new ErrorCollection("...", "Password/Confirmation can't be blank");
			return render("/password_resets/edit.jsp", errors);
		}
		else if (user.updateAttributes(formParams)) {
			sessionHelper.logIn(user);
			sessionHelper.addFlashMessage(MessageType.success, "Password has been reset.");
			return redirectTo("users/"+user.getId());
		}
		else {
			return render("/password_resets/edit.jsp", user);
		}
		
	}
	
	boolean areBothPasswordsBlank(MultivaluedMap<String, String> params) {
		String password = params.getFirst("password");
		String passwordConfirmation = params.getFirst("passwordConfirmation");
		return StringUtils.isEmpty(password) && StringUtils.isEmpty(passwordConfirmation);
	}
		
	boolean isValidUser(User user, String resetToken) {
		if (user != null && user.isActivated() && user.isAuthenticated("reset", resetToken)) {
			return true;
		}
		return false;
	}
	
	boolean isExpired(User user) {
		return user.passwordResetExpired();
	}
}
