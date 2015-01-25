package org.mike.models;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ExecutableValidator;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mike.encrypt.BCrypt;
import org.mike.validator.MatchingParameters;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

// TODO: Is it useful to include has_secure_password concept here? Maybe by extending ActiveRecord?

@Entity
public class PasswordActiveRecord extends ActiveRecord {

	static final String PASSWORD_FIELD = "password";
	static final String PASSWORD_CONFIRMATION_FIELD = "passwordConfirmation";
	
	// TODO: min pwd length?
	@FormParam(PASSWORD_FIELD) @NotNull @Transient String password;
	@FormParam(PASSWORD_CONFIRMATION_FIELD) @Transient String passwordConfirmation;
	private String passwordDigest; // TODO: be sure this is never available to end user!
	
	// TODO: rails states has_secure_password 'provides' authenticate method
	public boolean authenticate(String password) { 
		if (BCrypt.checkpw(password, passwordDigest)) {
			return true;
		}
		return false;
	}

	/**
	 * Compares a string (rememberToken) to member parameter rememberDigest.
	 * 
	 * rememberDigest is the hashed rememberToken that has been stored in persist.
	 * 
	 * @param rememberToken string to be compared to the persisted rememberDigest
	 * @return
	 */
	public boolean isAuthenticated(String attribute, String token) {
		String digest = getProperty(this, attribute+"Digest");
		if (StringUtils.isEmpty(digest)) return false;
		if (BCrypt.checkpw(token, digest)) {
			return true;
		}
		return false;
	}
	
	public static String digest(String str) {
		String digest = BCrypt.hashpw(str, BCrypt.gensalt()); // TODO: better salt?
		return digest;
	}
	
	public static String newToken() {
		// random urlsafe base64 string, length == 22 (+ and / replaced with - and _ as per RFC 4648)
		String token = RandomStringUtils.random(22, "ABCDEFGHIJKLIMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_");
		return token;
	}

	/**
	 * Adds pwd/conf validation for secure user saves.
	 */
	@Override
	boolean validates() {
		super.validates();
		validatePassword(password, passwordConfirmation);
		if (this.errors.size() == 0) {
			setPasswordDigest(password, passwordConfirmation);
		}
		return this.errors.size() == 0;
	}
	
	/**
	 * Adds pwd/conf validation for secure user updates. Ensures pwd/conf, if present,
	 * match and creates and sets a new pwd digest on the instance. For use with updates,
	 * including pwd resets.
	 */
	@Override
	boolean validates(MultivaluedMap<String, String> attributes) {
		super.validates(attributes);
		String password = attributes.getFirst(PASSWORD_FIELD);
		String passwordConfirmation = attributes.getFirst(PASSWORD_CONFIRMATION_FIELD);
		if (!StringUtils.isEmpty(password) && !StringUtils.isEmpty(passwordConfirmation)) {
			validatePassword(password, passwordConfirmation);
			if (this.errors.size() == 0) {
				// TODO: yucky, but explained below
				// setting the password digest on the instance, probably don't have to
				setPasswordDigest(password, passwordConfirmation);
				// add the password digest to the collection of attributes so it will be updated post validation
				attributes.add("passwordDigest", this.passwordDigest);
				// remove the pwd/conf from the collection of attributes so they will not be added to the persisted document
				attributes.remove(PASSWORD_FIELD);
				attributes.remove(PASSWORD_CONFIRMATION_FIELD);
			}
		}		
		return this.errors.size() == 0;
	}

	void validatePassword(String password, String passwordConfirmation) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		ExecutableValidator executableValidator = factory.getValidator().forExecutables();
		Method method = getMethod("setPasswordDigest", String.class, String.class);
		Object[] parameterValues = { password, passwordConfirmation };
		Set<? extends ConstraintViolation<?>> errors = executableValidator.validateParameters(
				this,
				method,
				parameterValues);
		addToErrors(errors);
	}
	
	Method getMethod(String methodName, Class clazz1, Class clazz2) {
		Method method = null;
		try {
			method = getClass().getMethod(methodName, clazz1, clazz2);
		} catch (Exception e) {
			log.error("Problem retrieving method.", e);
			throw new RuntimeException(e);
		}
		return method;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}
	
	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getPasswordDigest() {
		return passwordDigest;
	}

	@MatchingParameters
	public boolean setPasswordDigest(String password, String passwordConfirmation) {
		this.passwordDigest = digest(password);
		return true;
	}
//	
//	public void setPasswordDigest(String passwordDigest) {
//		this.passwordDigest = passwordDigest;
//	}
		
}
