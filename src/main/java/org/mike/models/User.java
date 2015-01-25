package org.mike.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.mike.mailers.UserMailer;
import org.mike.validator.Unique;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity(value = "users")
public class User extends PasswordActiveRecord {
	final static Logger log = LoggerFactory.getLogger(User.class);

	@FormParam("name")
	@NotNull
	@Size(max = 50)
	private String name;

	@FormParam("email")
	@NotNull
	@Size(max = 255)
	@Email
	@Unique("email")
	private String email;

	private boolean admin;
	private String roleId;
	private String groupId;
		
	@Transient private String rememberToken; // This should not be stored in the DB
	private String rememberDigest; // This is stored in the DB

	@Transient private String activationToken; // This should not be stored in the DB
	private String activationDigest; // This is stored in the DB 
	private boolean activated;
	private long activatedAt;

	@Transient private String resetToken;
	private String resetDigest;
	private long resetSentAt;

	/**
	 * before_create create_activation_digest
	 */
	@Override
	void beforeCreate() {
		createActivationDigest(); 
	}
		
	/**
	 * before_save downcase_email
	 */
	@Override
	void beforeSave() {
		downcaseEmail();
	}

	// TODO: is there a way to make the default constructor private?
	//  This would help protect api users from instantiating this class
	//  with incomplete information. There is already an issue if a user
	//  calls User.createNew(), then user.updateAttributes() without calling
	//  user.save() first.
	
	void downcaseEmail() {
		this.email = StringUtils.lowerCase(this.email);		
	}
	
	public void remember() {
		this.rememberToken = newToken();
		updateAttribute("rememberDigest", digest(this.rememberToken));
	}
	
	public void forget() {
		updateAttribute("rememberDigest", "");
	}

	void createActivationDigest() {
		this.activationToken = newToken();
		this.activationDigest = digest(activationToken);
	}	

	public void activate() {
		updateAttribute("activated", true);
		updateAttribute("activatedAt", System.currentTimeMillis());
	}	

	public void sendActivationEmail() {
		UserMailer.accountActivation(this).deliverNow();
	}
	
	public void createResetDigest() {
		resetToken = User.newToken();
		updateAttribute("resetDigest", User.digest(resetToken));
		updateAttribute("resetSentAt", System.currentTimeMillis());
	}
	
	public boolean passwordResetExpired() {
		return resetSentAt < System.currentTimeMillis() - (1000 * 60 * 60 * 2);
	}

	public void sendPasswordResetEmail() {
		UserMailer.passwordReset(this).deliverNow();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public String getRoleId() {
		return roleId;
	}
	
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	public Role getRole() {
		Role role = Role.findBy(Role.class, "id", roleId);
		return role;
	}

	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getRememberDigest() {
		return rememberDigest;
	}
	
	public void setRememberDigest(String rememberDigest) {
		this.rememberDigest = rememberDigest;
	}
	
	public String getRememberToken() {
		return rememberToken;
	}
	
	public void setRememberToken(String rememberToken) {
		this.rememberToken = rememberToken;
	}
	
	public String getActivationToken() {
		return activationToken;
	}

	public String getActivationDigest() {
		return activationDigest;
	}
	
	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}
	
	public String getResetDigest() {
		return resetDigest;
	}
	
	public boolean isActivated() {
		return activated;
	}
}
