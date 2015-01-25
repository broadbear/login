package org.mike.filters;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.mike.helpers.SessionHelper;
import org.mike.models.User;

@Permissions("")
public class PermissionsFilter implements ContainerRequestFilter {
	@Context HttpServletRequest req;
	@Context HttpHeaders headers;

	@Override
	public void filter(ContainerRequestContext requestContext) 
			throws IOException {
		SessionHelper sessionHelper = new SessionHelper(req, headers);
		User currentUser = sessionHelper.getCurrentUser();
		if (currentUser == null) {
			// TODO: redirectTo somewhere non-allowed go
			requestContext.abortWith(
					// TODO: message: User not logged in.
//					Response.seeOther(location)
//					.build());
					Response
					.status(Response.Status.UNAUTHORIZED)
					.entity("User not logged in.")
					.build());
		}
		String permission = getAnnotationElement(requestContext);
		boolean hasPermission = sessionHelper.hasPermission(permission);
		if (!hasPermission) {
			// TODO: redirectTo somewhere non-allowed go
			requestContext.abortWith(
					// TODO: message: User cannot access the resource.
//					Response.seeOther(location)
//					.build());
					Response
					.status(Response.Status.UNAUTHORIZED)
					.entity("User cannot access the resource.")
					.build());
		}
	}

	// TODO: is there a possible use case where an action could have multiple permissions?
	String getAnnotationElement(ContainerRequestContext requestContext) {
		ResourceMethod method = ((ExtendedUriInfo) (requestContext.getUriInfo()))
				.getMatchedResourceMethod();
		Method invokedMethod = method.getInvocable().getHandlingMethod();
		Annotation annotation = invokedMethod.getAnnotation(Permissions.class);
		String[] values = ((Permissions)annotation).value();
		if (values.length > 0) {
			return values[0];
		}
		return null;
	}
}
