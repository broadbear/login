package org.mike.resources;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.mvc.ErrorTemplate;
import org.mike.config.Routes;
import org.mike.filters.Permissions;
import org.mike.helpers.Flash.MessageType;
import org.mike.models.User;

@Path("/")
@ErrorTemplate(name = "/errors/errors.jsp")
public class UserResource extends ApplicationController {
	
	@Path(Routes.SIGNUP_PATH)
	@GET
	// TODO: permissions for users registering is different from admins creating a user
	// TODO: or is it? maybe anyone can create a user, but only certain permissions granted
	//    to create certain types of users?
	public Response newUser() {
		// need blank user instance so new.jsp has non-null model instance to reference (avoids NPE).
		User user = User.createNew(User.class);
		return render("/users/new.jsp", user);
	}		

	@Path(Routes.USERS_PATH)
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response create(@BeanParam User userForm) 
			throws URISyntaxException {
		User user = User.createNew(User.class, userForm); // TODO: redundant/confusing to pass User instance to create method

		prepareRoleAndGroup(userForm);
		
		if (user.save()) {
			user.sendActivationEmail();
			sessionHelper.addFlashMessage(MessageType.info, "Please check your email to activate your account.");
			return redirectTo("");
		}
		else {
			return render("/users/new.jsp", user);
		}		
	}
	
	void prepareRoleAndGroup(User user) {
		// If 'registering' current user will be null
		if (sessionHelper.getCurrentUser() == null) {
			user.setRoleId("guest");
			user.setGroupId("guest");
		}
		else { // admin is creating a new user
			if (StringUtils.isEmpty(user.getRoleId())) {
				user.setRoleId(sessionHelper.getCurrentUser().getRoleId());
			}
			
			if (StringUtils.isEmpty(user.getGroupId())) {
				user.setGroupId(sessionHelper.getCurrentUser().getGroupId());
			}
		}
	}
	
	@Path(Routes.USERS_PATH)
	@GET
	@Permissions({"view-all-users"})
	@Produces(MediaType.TEXT_HTML)
	public Response index() {
		List<User> users = User.all(User.class);
		return render("/users/index.jsp", users);
	}
	
	@Path(Routes.USERS_PATH)
	@GET
	@Permissions({"view-all-users"})
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> indexAsJson() {
		List<User> users = User.all(User.class);
		return users;
	}

	@Path(Routes.USER_PATH)
	@GET
	@Permissions({"view-user"})
	@Produces(MediaType.TEXT_HTML)
	public Response show(@PathParam("id") String id) {
		User user = User.find(User.class, id);
		return render("/users/show.jsp", user); // TODO: if user null, return 404
	}

	@Path(Routes.USER_PATH)
	@GET
	@Permissions({"view-user"})
	@Produces(MediaType.APPLICATION_JSON)
	public User showAsJson(@PathParam("id") String id) {
		User user = User.find(User.class, id);
		return user; // TODO: if user null, return 404
	}
	
	@Path(Routes.EDIT_USER_PATH)
	@GET
	@Permissions({"update-user"}) // TODO: staff user has ability to update own user (profile)
	public Response edit(@PathParam("id") String id) {
		User user = User.find(User.class, id);
		return render("/users/edit.jsp", user);
	}
	
	// TODO: is there a vulnerability? If User has an 'admin' field, and unauthorized 
	//  user submits User instance with updated 'admin' field, will it update admin field?
	// TODO: do we need to the concept of strong params here?
	@Path(Routes.USER_PATH) 
	@POST
	@Permissions({"update-user"})
	public Response update(@PathParam("id") String id, MultivaluedMap<String, String> params) 
			throws URISyntaxException {
		// TODO: make sure params does not include pwd/conf if they were left blank on submit
		//   otherwise, we must remove the blank params from the map, may not be an issue if they
		//   are not null, but merely empty string, this should validate as empty string reports
		//   as 'not changed', and updateAttributes will skip and not validate them.
		
		User user = User.find(User.class, id);
		if (user.updateAttributes(params)) {
			return redirectTo("users/"+id);
		}
		else {
			// return updates in progress
			params.add("id", user.getId()); // TODO: user.getId() returns null?
			return render("/users/edit.jsp", params);
		}
	}

	@Path(Routes.USER_PATH)
	@DELETE
	@Permissions({"destroy-user"})
	public Response destroy(@PathParam("id") String id) 
			throws URISyntaxException {
		User.find(User.class, id).destroy();
		sessionHelper.addFlashMessage(MessageType.success, "User deleted");
		return redirectTo("users/index");
	}
	
	MultivaluedMap<String, String> userParams(MultivaluedMap<String, String> formParams) {
		return permit(formParams, "name", "email", "password", "passwordConfirmation");
	}
	
}

