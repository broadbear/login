package org.mike.config;

public class Routes {
	public static final String ROOT_PATH = "/rest/";
	
	public static final String SIGNUP_PATH = "signup";
	public static final String LOGIN_PATH = "login";
	public static final String LOGOUT_PATH = "logout";
	public static final String USERS_PATH = "users";
	public static final String USER_PATH = "users/{id}";
	public static final String NEW_USER_PATH = "users/new";
	public static final String EDIT_USER_PATH = "users/{id}/edit";
	public static final String EDIT_ACCOUNT_ACTIVATION_PATH = "account_activations/{token}/edit";
	public static final String NEW_PASSWORD_RESET_PATH = "password_resets/new";
	public static final String PASSWORD_RESETS_PATH = "password_resets";
	public static final String PASSWORD_RESET_PATH = "password_resets/{token}";
	public static final String EDIT_PASSWORD_RESET_PATH = "password_resets/{token}/edit";

	public final String getSignupPath() {
		return SIGNUP_PATH;
	}
	public final String getRootPath() {
		return ROOT_PATH;
	}
	public final String getLoginPath() {
		return ROOT_PATH + LOGIN_PATH;
	}
	public final String getLogoutPath() {
		return ROOT_PATH + LOGOUT_PATH;
	}
	public final String getUsersPath() {
		return ROOT_PATH + USERS_PATH;
	}
	public String getUserPath(String userId) {
		return ROOT_PATH + USER_PATH.replace("{id}", userId);
	}	
	public String getNewUserPath() {
		return ROOT_PATH + NEW_USER_PATH;
	}
	public String getEditUserPath(String userId) {
		return ROOT_PATH + EDIT_USER_PATH.replace("{id}", userId);
	}
	public String getEditAccountActivationPath(String activationToken, String email) {
		String path = ROOT_PATH + EDIT_ACCOUNT_ACTIVATION_PATH.replace("{token}", activationToken);
		path = path + "?email=" + email; // TODO: messy // TODO: encode email
		return path;
	}	
	public String getNewPasswordResetPath() {
		return ROOT_PATH + NEW_PASSWORD_RESET_PATH;
	}
	public String getPasswordResetsPath() {
		return ROOT_PATH + PASSWORD_RESETS_PATH;
	}
	public String getPasswordResetPath(String token) {
		return ROOT_PATH + PASSWORD_RESET_PATH.replace("{token}", token);
	}
	public String getEditPasswordResetPath(String resetToken, String email) {
		String path = ROOT_PATH + EDIT_PASSWORD_RESET_PATH.replace("{token}", resetToken); 
		path = path + "?email=" + email; // TODO: messy // TODO: encode email
		return path;
	}
}
