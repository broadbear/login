package org.mike.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.mike.helpers.SessionHelper;
import org.mike.models.User;

@CorrectUser
public class CorrectUserFilter implements ContainerRequestFilter {
	@Context HttpServletRequest req;
	@Context HttpHeaders headers;

	@Override
	public void filter(ContainerRequestContext requestContext) 
			throws IOException {
		MultivaluedMap<String, String> pathParams = requestContext.getUriInfo().getPathParameters();
		String id = getParam("id", pathParams);
		User user = User.find(User.class, id);
		
		SessionHelper sessionHelper = new SessionHelper(req, headers);
		if (!sessionHelper.isCurrentUser(user)) {
			requestContext.abortWith(Response
					.status(Response.Status.UNAUTHORIZED)
					.entity("User cannot access the resource.")
					.build());
		}		
	}
	
	String getParam(String key, MultivaluedMap<String, String> pathParams) {
		List<String> ids = pathParams.get(key);
		String id = null;
		if (ids.size() > 0) {
			id = ids.get(0);
		}
		return id;
	}
}
