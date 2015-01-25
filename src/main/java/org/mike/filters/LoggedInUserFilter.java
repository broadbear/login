package org.mike.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.mike.helpers.SessionHelper;

@LoggedInUser
public class LoggedInUserFilter implements ContainerRequestFilter {
	@Context HttpServletRequest req;
	@Context HttpHeaders headers;
	
	@Override
	public void filter(ContainerRequestContext requestContext) 
			throws IOException {
		SessionHelper sessionHelper = new SessionHelper(req, headers);
		if (!sessionHelper.isLoggedIn()) {
			requestContext.abortWith(Response
					.status(Response.Status.UNAUTHORIZED)
					.entity("User cannot access the resource.")
					.build());
		}		
	}
	
}
