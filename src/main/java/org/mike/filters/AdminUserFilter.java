package org.mike.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.mike.helpers.SessionHelper;
import org.mike.models.User;

@AdminUser
public class AdminUserFilter implements ContainerRequestFilter {
	@Context HttpServletRequest req;
	@Context HttpHeaders headers;

	@Override
	public void filter(ContainerRequestContext requestContext) 
			throws IOException {
		SessionHelper sessionHelper = new SessionHelper(req, headers);
		User currentUser = sessionHelper.getCurrentUser();
		if (currentUser != null && !currentUser.isAdmin()) {
			requestContext.abortWith(Response
					.status(Response.Status.UNAUTHORIZED)
					.entity("User cannot access the resource.")
					.build());
		}		
	}

}
