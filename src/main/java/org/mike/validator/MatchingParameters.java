package org.mike.validator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = MatchingParametersValidator.class)
@Documented
public @interface MatchingParameters {

    String message() default "{org.mike.validator.MatchingParameters.message}";
    
    Class<?>[] groups() default { };
    
    Class<? extends Payload> [] payload() default {};
}
