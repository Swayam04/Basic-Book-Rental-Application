package practice.bookrentalapp.validators.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import practice.bookrentalapp.validators.AtLeastOneNotNullValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneNotNullValidator.class)
public @interface AtLeastOneNotNull {
    String message() default "At least one field must be non-null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fieldNames();
}
