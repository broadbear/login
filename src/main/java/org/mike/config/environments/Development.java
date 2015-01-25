package org.mike.config.environments;

import org.mike.config.Config;

public class Development extends Config {

	Development() {
		this.host = "localhost:8080";
		this.dbUri = "mongodb://broadbear:p0sers01@ds031661.mongolab.com:31661/login";
		this.dbName = "login";
		this.smtpHost = "localhost";
		this.emailTemplatePath = "../views/user_mailer/";
		this.defaultFromEmail = "noreply@login.com";
		this.twitterOauthCallback = "http://24.209.173.162:8080/rest/twitter_authentication_callback";
		this.twitterConsumerKey = "rPZabWWO2OsrINFQv7MJ6Ct2S";
		this.twitterConsumerSecret = "X53olXsGZWzNOSJUTUWyAzlWKdCjLX4lQAblnAad1OahRtlWwv";
		this.twitterUserVerificationUrl = "https://api.twitter.com/1.1/account/verify_credentials.json";
		this.encryptionPassword = "coochiecoo";
	}
	
	// TODO:
	// inside Rails.application.configure.do {
	//   config.action_mailer.raise_delivery_errors = true
	//   config.action_mailer.delivery-method = :test
	//   host = 'example.com' (ex. localhost:3000)
	//   config.action_mailer.default_url_options = { host: host }
	// }
	
}
