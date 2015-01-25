package org.mike.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.server.mvc.Viewable;
import org.mike.config.Routes;
import org.mike.helpers.SessionHelper;
public class ApplicationController {
	@BeanParam SessionHelper sessionHelper;
	Routes routes = new Routes();

	protected <T> Response render(String path, T model) {	
		return Response.ok(new Viewable(path, model)).build();
	}
	
	protected Response redirectTo(String path) throws URISyntaxException {
		ResponseBuilder resBuilder = Response.seeOther(new URI(path)); // Returns a 303 which converts POST to GET
		List<NewCookie> newCookies = sessionHelper.getNewCookies();
		for (NewCookie cookie: newCookies) {
			resBuilder.cookie(cookie);
		}
		return resBuilder.build();
	}

	MultivaluedMap<String, String> permit(MultivaluedMap<String, String> formParams, String... keys) {
		MultivaluedMap<String, String> permitParams = new MultivaluedHashMap<String, String>();
		for (String key: keys) {
			permitParams.add(key, formParams.getFirst(key));
		}
		return permitParams;
	}	
}
