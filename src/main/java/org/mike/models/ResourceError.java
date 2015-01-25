package org.mike.models;

public class ResourceError {
	String propertyPath;
	String message;
	
	public ResourceError(String propertyPath, String message) {
		this.propertyPath = propertyPath;
		this.message = message;
	}

	public String getPropertyPath() {
		return propertyPath;
	}
	
	public String getMessage() {
		return message;
	}
}
