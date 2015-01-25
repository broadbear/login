package org.mike.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.mike.models.User;

public class UniqueValidator implements ConstraintValidator<Unique, String> {
	private String key; // TODO: good to have this for flexibility, but how to derive this from the property name?
	
	@Override
	public void initialize(Unique constraintAnnotation) {
		this.key = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
		if (object == null) {
			return false;
		}
		
		User user = User.findBy(User.class, key, object); // TODO: how to generalize this so it not only works for User?
		if (user == null) {
			return true;
		}		
		return false;
	}
}
