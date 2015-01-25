package org.mike.resources;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mike.config.Routes;
import org.mike.models.User;

public class UserResourceTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(UserResource.class);
	}
	
//	@Test
	public void Test() {
		List<User> users = target(Routes.USERS_PATH).request().get(List.class);
		assertNotNull(users);
	}
	
}
