package org.mike.config;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.cglib.core.ReflectUtils;

import org.mike.config.environments.Development;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config implements ServletContextListener {
	final static Logger log = LoggerFactory.getLogger(Config.class);

	protected static Config instance;
	protected String host;
	protected String dbName;
	protected String dbUri;
	protected String smtpHost;
	protected String emailTemplatePath;
	protected String defaultFromEmail;
	protected String twitterOauthCallback;
	protected String twitterConsumerKey;
	protected String twitterConsumerSecret;
	protected String twitterUserVerificationUrl;
	protected String encryptionPassword;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			InputStream inputStream = sce.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(inputStream);
			Attributes attributes = manifest.getMainAttributes();
			String env = attributes.getValue("Environment");
			instance = (Config) ReflectUtils.newInstance(forName("org.mike.config.environments."+env));
		}
		catch (Exception e) {
			throw new RuntimeException("Problem initializing config.");
		}
	}
	
	static <T> Class<T> forName(String className) {
		try {
			Class c = Class.forName(className);
			return c;
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Problem creating class.", e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@SuppressWarnings("unchecked")
	public static <E extends Config> Config getSingleton() {
		if (instance == null) {
			instance = (E)ReflectUtils.newInstance(Development.class);
		}
		return instance;
	}
	
	public String getHost() {
		return host;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbUri() {
		return dbUri;
	}

	public String getSmtpHost() {
		return smtpHost;
	}
	
	public String getEmailTemplatePath() {
		return emailTemplatePath;
	}
	
	public String getDefaultFromEmail() {
		return defaultFromEmail;
	}

	public String getTwitterOauthCallback() {
		return twitterOauthCallback;
	}

	public String getTwitterConsumerKey() {
		return twitterConsumerKey;
	}

	public String getTwitterConsumerSecret() {
		return twitterConsumerSecret;
	}

	public String getTwitterUserVerificationUrl() {
		return twitterUserVerificationUrl;
	}

	public String getEncyrptionPassword() {
		return encryptionPassword;
	}
}
