package org.mike.models;

import java.util.ArrayList;
import java.util.List;

public class ErrorCollection {

	List<ResourceError> errors = new ArrayList<ResourceError>();
	
	public ErrorCollection(String propertyPath, String message) {
		ResourceError error = new ResourceError(propertyPath, message);
		errors.add(error);
	}
	
	public void addError(ResourceError error) {
		errors.add(error);
	}
	
	public List<ResourceError> getErrors() {
		return errors;
	}
	
	public void setErrors(List<ResourceError> errors) {
		this.errors = errors;
	}	
}
