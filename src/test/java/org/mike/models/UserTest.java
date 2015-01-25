package org.mike.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;


public class UserTest {

	User user;
	static final String PWD1 = "qwerty";
	static final String PWD2 = "asdfgh";

	@Before
	public void before() {
		User.destroyAll(User.class);
		user = createUser("test", "test@test.com", "qwerty", "qwerty");
		assertTrue(user.save());
	}
	
	@Test
	public void testCreateNew() {
		User user = createUser("testCreateNew", "testCreateNew@test.com", "qwerty", "qwerty");
		assertNotNull(user);
		assertTrue(user instanceof User);
	}
	
	@Test
	public void testSave() {
		user = createUser("testSave", "testSave@test.com", "qwerty", "qwerty");
		boolean saved = user.save();
		assertTrue(saved);
		assertNotNull(user._id);		
	}

	@Test
	public void testSaveFail() {		
		// name
		User user = createUser(null, "a@email.com", PWD1, PWD1);
		assertFalse(user.save());
		user = createUser(createLongString(61), "a@email.com", PWD1, PWD1);
		assertFalse(user.save());
		
		// email
		user = createUser("name", null, PWD1, PWD1);
		assertFalse(user.save());
		user = createUser("name", createLongString(256)+"@email.com", PWD1, PWD1);
		assertFalse(user.save());
		user = createUser("name", "a", PWD1, PWD1);
		assertFalse(user.save());
//		user = createUser("name", "a@email", PWD1, PWD1); // TODO: why does this pass?
//		assertFalse(user.save());
		user = createUser("name", "@email.com", PWD1, PWD1);
		assertFalse(user.save());
		user = createUser("name", "a@.com", PWD1, PWD1);
		assertFalse(user.save());
		user = createUser("name", "test@test.com", PWD1, PWD1);
		assertFalse(user.save());
		
		// pwd/conf
		user = createUser("name", "a@email.com", null, PWD1);
		assertFalse(user.save());
		user = createUser("name", "a@email.com", PWD1, null);
		assertFalse(user.save());
		user = createUser("name", "a@email.com", null, null);
		assertFalse(user.save());
		user = createUser("name", "a@email.com", PWD1, PWD2);
		assertFalse(user.save());
	}
	
	@Test
	public void testAll() {
		List<User> users = User.all(User.class);
		assertNotSame(0, users.size());
	}
	
	@Test
	public void testFind() {
		User res = User.find(User.class, user.getId());
		assertNotNull(res);
		assertEquals(user.getId(), res.getId());
	}
	
	@Test
	public void testFindBy() {
		User res = User.findBy(User.class, "email", user.getEmail());
		assertNotNull(res);
		assertEquals(user.getEmail(), res.getEmail());
	}
	
	@Test
	public void testUpdateAttributes() {
		User original = User.findBy(User.class, "email", user.getEmail());
		User myUser = User.findBy(User.class, "email", user.getEmail());
		assertNotNull(myUser);
		MultivaluedMap<String, String> attributes = createUserMap("newname", "new@email.com", "newpwd", "newpwd");
		myUser.updateAttributes(attributes);
		User res = User.findBy(User.class, "email", "new@email.com");
		assertNotNull(res);
		assertEquals("new@email.com", res.getEmail());
		assertEquals("newname", res.getName());
		assertNotSame(original.getPasswordDigest(), res.getPasswordDigest());
	}
	
	@Test
	public void testValidatesAttributes() {
		User myUser = User.findBy(User.class, "email", user.getEmail());
		assertNotNull(myUser);
		MultivaluedMap<String, String> attributes = createUserMap("newname", "new@email.com", PWD1, PWD1);
		boolean valid = myUser.validates(attributes);
		assertTrue(valid);

		myUser = User.findBy(User.class, "email", user.getEmail());
		attributes = createUserMap("newname", "new@email.com", "", "");
		valid = myUser.validates(attributes);
		assertTrue(valid);

		myUser = User.findBy(User.class, "email", user.getEmail());
		attributes = createUserMap("newname", "new@email.com", null, null);
		valid = myUser.validates(attributes);
		assertTrue(valid);

		myUser = User.findBy(User.class, "email", user.getEmail());
		attributes = createUserMap("newname", "new@email.com", PWD1, PWD2);
		valid = myUser.validates(attributes);
		assertFalse(valid);
		assertEquals(1, myUser.getErrors().size());

		myUser = User.findBy(User.class, "email", user.getEmail());
		attributes = createUserMap("newname", "new", PWD1, PWD1);
		valid = myUser.validates(attributes);
		assertFalse(valid);
		assertEquals(1, myUser.getErrors().size());
		
		myUser = User.findBy(User.class, "email", user.getEmail());
		attributes = createUserMap("newname", "new", PWD1, PWD2);
		valid = myUser.validates(attributes);
		assertFalse(valid);
		assertEquals(2, myUser.getErrors().size());
	}
	
	@Test
	public void testUpdateAttribute() {
		String newEmail = "blah@blah.com";
		user.updateAttribute("email", newEmail);
		User res = User.find(User.class, user.getId());
		assertNotNull(res);
		assertEquals(newEmail, res.getEmail());		
	}
	
	@Test
	public void testDestroy() {
		User.find(User.class, user.getId()).destroy();
		User res = User.find(User.class, user.getId());
		assertNull(res);
	}

	static User createUser(String name, String email, String password, String passwordConfirmation) {
		User user = User.createNew(User.class);
		user.setName(name);
		user.setEmail(email);
		user.password = password;
		user.passwordConfirmation = passwordConfirmation;
		return user;
	}
	
	static MultivaluedMap<String, String> createUserMap(String name, String email, String password, String passwordConfirmation) {
		MultivaluedMap<String, String> userMap = new MultivaluedHashMap<String, String>();
		userMap.add("name", name);
		userMap.add("email", email);
		userMap.add("password", password);
		userMap.add("passwordConfirmation", passwordConfirmation);
		return userMap;
	}
	
	static String createLongString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append("a");
		}
		return sb.toString();
	}
}
