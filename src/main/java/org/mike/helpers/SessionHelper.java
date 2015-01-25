package org.mike.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;
import org.mike.config.Config;
import org.mike.config.Routes;
import org.mike.models.Group;
import org.mike.models.Permission;
import org.mike.models.Role;
import org.mike.models.User;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public class SessionHelper {
	static final String USER_ID_ATTR_NAME = "user_id";
	static final String REMEMBER_TOKEN_ATTR_NAME = "remember_token";
	static final String CURRENT_USER_ATTR_NAME = "current_user";
	static final String AUTH_FLOW_ATTR_NAME = "auth_flow";
	static final String OAUTH_SERVICE_ATTR_NAME = "oauth_service";
	static final String OAUTH_REQUEST_TOKEN_ATTR_NAME = "oauth_request_token";
	
	Map<String, Cookie> cookies;
	List<NewCookie> newCookies;
	HttpSession session;
	
	public SessionHelper(@Context HttpServletRequest req, @Context HttpHeaders headers) {
		this.session = req.getSession();
		this.cookies = headers.getCookies();
		this.newCookies = new ArrayList<NewCookie>();
		
		// TODO: for access in templates, kindof yuckie
		// TODO: what if sessionHelper is recreated each time... may overwrite things needed in session?
		this.session.setAttribute("sessionHelper", this);
		this.session.setAttribute("routes", new Routes());
	}
	
	public void logIn(User user) {
		session.setAttribute(USER_ID_ATTR_NAME, user.getId());
	}

	public void remember(User user) {
		user.remember();
		addCookie(USER_ID_ATTR_NAME, sign(user.getId()));
		addCookie(REMEMBER_TOKEN_ATTR_NAME, user.getRememberToken());
	}
	
	public boolean isCurrentUser(User user) {
		if (user != null && getCurrentUser() != null) {
			if (user.getId().equals(getCurrentUser().getId())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check the session for a user_id attribute. If one exists, user session
	 * is still valid. Set and return currentUser. 
	 * 
	 * Otherwise, check cookies for a user_id cookie. If one exists, user selected
	 * remember me on login. 
	 * @return
	 */
	public User getCurrentUser() {
		User currentUser = null;
		if (session.getAttribute(USER_ID_ATTR_NAME) != null) {
			currentUser = getCurrentUserInSession();
			if (currentUser == null) {
				String userId = (String) session.getAttribute(USER_ID_ATTR_NAME);
				currentUser = User.find(User.class, userId);
				setCurrentUserInSession(currentUser);
			}
		}
		else if (cookies.get(USER_ID_ATTR_NAME) != null) {
			String userId = unsign(getCookieValue(USER_ID_ATTR_NAME));
			User user = User.find(User.class, userId);
			String rememberToken = getCookieValue(REMEMBER_TOKEN_ATTR_NAME);
			if (user != null && user.isAuthenticated("remember", rememberToken)) {
				logIn(user);
				setCurrentUserInSession(user);
				currentUser = user;
			}
		}
		return currentUser;
				
		// TODO: Should use findBy as opposed to find because 
		// findBy should only return nil if user does not exist
		// wheras find throws an exception.
		// TODO: findBy currently does not work for _id as it
		// must be ObjectId.
	}
	
	private User getCurrentUserInSession() {
		return (User) session.getAttribute(CURRENT_USER_ATTR_NAME);
	}
	
	private void setCurrentUserInSession(User user) {
		session.setAttribute(CURRENT_USER_ATTR_NAME, user);
	}
	
	private void removeCurrentUserInSession() {
		session.removeAttribute(USER_ID_ATTR_NAME);
		session.removeAttribute(CURRENT_USER_ATTR_NAME);
	}
	
	private String getCookieValue(String key) {
		Cookie cookie = cookies.get(key);
		if (cookie != null) {
			return cookie.getValue();
		}
		return null;
	}

	String sign(String str) {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(Config.getSingleton().getEncyrptionPassword());
		String encryptedStr = textEncryptor.encrypt(str);
		return encryptedStr;
	}
	
	String unsign(String signedStr) {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(Config.getSingleton().getEncyrptionPassword());
		String str = textEncryptor.decrypt(signedStr);
		return str;
	}

	public boolean isLoggedIn() {
		return getCurrentUser() != null;
	}
	
	public void forget(User user) {
		user.forget();
		removeCookie(USER_ID_ATTR_NAME);
		removeCookie(REMEMBER_TOKEN_ATTR_NAME);
	}
	
	public void logOut() {
		forget(getCurrentUserInSession());
		removeCurrentUserInSession();
	}

	void addCookie(String name, String value) {
		NewCookie newCookie = new NewCookie(name, value);
		newCookies.add(newCookie);
	}
	
	void removeCookie(String name) {
		Cookie cookie = cookies.get(name);
		if (cookie != null) {
			NewCookie antiCookie = new NewCookie(cookie, "", 0, false);
			newCookies.add(antiCookie);
		}
	}
	
	public List<NewCookie> getNewCookies() {
		return newCookies;
	}

	public boolean hasPermission(String permission) {
		if (getCurrentUser() == null) {
			return false;
		}
		Role userRole = getCurrentUser().getRole();
		if (userRole != null) {
			Permission[] userPerms = userRole.getPermissions();
			boolean found = contains(userPerms, permission);
			return found;
		}
		return false;
	}
	
	boolean contains(Permission[] perms, String str) {
		for (Permission p: perms) {
			if (StringUtils.equals(str, p.getLesserId())) {
				return true;
			}
		}
		return false;
	}

	// Basically this method assumes you are calling it because you want
	//  to fetch the roles so you can create a new user. In this case,
	//  you need to check if the user has the permission to create a super
	//  admin. If not, the super-admin role needs to be removed from the
	//  list before returning it.
	public List<Role> getAllowedRoles() {
		List<Role> roles = Role.all(Role.class);
		// If current user does not have permission to create 
		// super admins, remove super admin role from list.
		if (!hasPermission("create-super-admin")) {
			for(Iterator<Role> iter = roles.iterator(); iter.hasNext();) {
				Role r = iter.next();
				if (StringUtils.equals(r.getLesserId(), "super-admin")) {
					iter.remove();
				}
			}
		}
		return roles;
	}
	
	public List<Group> getAllowedGroups() {
		List<Group> groups = Group.all(Group.class);
		return groups;
	}
	
	public Flash getFlash() {
		Flash flash = (Flash)session.getAttribute("flash");
		session.removeAttribute("flash");
		return flash;
	}
	
	public void addFlashMessage(Flash.MessageType type, String text) {
		Flash flash = (Flash)session.getAttribute("flash");
		if (flash == null) {
			flash = new Flash();
			session.setAttribute("flash", flash);
		}
		flash.addMessage(type, text);
	}

	public OAuthService getOAuthService() {
		OAuthService service = (OAuthService) session.getAttribute(OAUTH_SERVICE_ATTR_NAME);
		return service;
	}

	public void setOAuthService(OAuthService service) {
		session.setAttribute(OAUTH_SERVICE_ATTR_NAME, service);
	}

	public Token getRequestToken() {
		Token requestToken = (Token) session.getAttribute(OAUTH_REQUEST_TOKEN_ATTR_NAME);
		return requestToken;
	}
	
	public void setRequestToken(Token requestToken) {
		session.setAttribute(OAUTH_REQUEST_TOKEN_ATTR_NAME, requestToken);
	}
}
