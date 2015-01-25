package org.mike.mailers;

import org.mike.config.Config;
import org.mike.config.Routes;
import org.mike.models.User;
import org.stringtemplate.v4.ST;

public class UserMailer extends ActionMailer {
	static final String from = Config.getSingleton().getDefaultFromEmail();
	
	public static UserMailer accountActivation(User user) {
		UserMailer userMailer = new UserMailer();

		ST st = createTemplate("account_activation");
		st.add("user", user);
		String activationUrl = getEditAccountActivationUrl(user.getActivationToken(), user.getEmail());
		st.add("activation_url", activationUrl);

		userMailer.mailTo(user.getEmail(), from, "Account activation", st.render());
		return userMailer;
	}
	
	public static UserMailer passwordReset(User user) {
		UserMailer userMailer = new UserMailer();
		
		ST st = createTemplate("password_reset");
		String resetUrl = getEditPasswordResetUrl(user.getResetToken(), user.getEmail());
		st.add("reset_url", resetUrl);
		
		userMailer.mailTo(user.getEmail(), from, "Password reset", st.render());
		return userMailer;
	}
	
	static String getEditAccountActivationUrl(String activationToken, String email) {
		Routes r = new Routes();
		String activationPath = r.getEditAccountActivationPath(activationToken, email);
		String activationUrl = "http://" + Config.getSingleton().getHost() + activationPath;
		return activationUrl;
	}
	
	static String getEditPasswordResetUrl(String resetToken, String email) {
		Routes r = new Routes();
		String resetPath = r.getEditPasswordResetPath(resetToken, email);
		String resetUrl = "http://" + Config.getSingleton().getHost() + resetPath;
		return resetUrl;
	}
}
