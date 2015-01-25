package org.mike.config.environments;

import org.mike.config.Config;

public class Staging extends Config {

	Staging() {
		this.host = "localhost:8081";
		this.dbUri = "mongodb://broadbear:p0sers01@ds031661.mongolab.com:31661/login";
		this.dbName = "login";
		this.smtpHost = "localhost";
		this.emailTemplatePath = "../views/user_mailer/";
		this.defaultFromEmail = "noreply@login.com";
		// TODO: problem with callback, local 8080 traffic forwards to my desktop, and vm is dhcp
		// TODO: could start tomcat on different port, and foward traffic to different port
		this.twitterOauthCallback = "http://24.209.173.162:8081/rest/twitter_authentication_callback"; // TODO: change ip
		this.twitterConsumerKey = "rPZabWWO2OsrINFQv7MJ6Ct2S";
		this.twitterConsumerSecret = "X53olXsGZWzNOSJUTUWyAzlWKdCjLX4lQAblnAad1OahRtlWwv";
		this.twitterUserVerificationUrl = "https://api.twitter.com/1.1/account/verify_credentials.json";
		this.encryptionPassword = "coochiecoo";
	}
	
}
