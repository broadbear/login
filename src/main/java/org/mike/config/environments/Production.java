package org.mike.config.environments;

import org.mike.config.Config;

public class Production extends Config {

	Production() {
		this.host = "www.login.com";
		this.dbUri = "mongodb://broadbear:p0sers01@ds031661.mongolab.com:31661/login";
		this.dbName = "login";
		this.smtpHost = "localhost";
		this.emailTemplatePath = "../views/user_mailer/";
		this.defaultFromEmail = "noreply@login.com";
		this.twitterOauthCallback = "http://www.login.com/rest/twitter_authentication_callback";
		this.twitterConsumerKey = "rPZabWWO2OsrINFQv7MJ6Ct2S";
		this.twitterConsumerSecret = "X53olXsGZWzNOSJUTUWyAzlWKdCjLX4lQAblnAad1OahRtlWwv";
		this.twitterUserVerificationUrl = "https://api.twitter.com/1.1/account/verify_credentials.json";
		this.encryptionPassword = "coochiecoo";
	}
	
}
