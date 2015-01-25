package org.mike.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

import org.apache.commons.lang3.StringUtils;

@SupportedValidationTarget(value = ValidationTarget.PARAMETERS)
public class MatchingParametersValidator implements ConstraintValidator<MatchingParameters, Object[]> {

	@Override
	public void initialize(MatchingParameters constraintAnnotation) {
	}

	@Override
	public boolean isValid(Object[] value, ConstraintValidatorContext context) {
		if (value.length != 2) {
			throw new IllegalArgumentException("Illegal method siganture");
		}
		
		return (StringUtils.equals((String) value[0], (String) value[1]));
	}

}
