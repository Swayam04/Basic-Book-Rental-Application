package practice.bookrentalapp.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import practice.bookrentalapp.validators.annotations.AtLeastOneNotNull;

import java.lang.reflect.Field;

public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, Object> {

    private String[] fieldNames;

    @Override
    public void initialize(AtLeastOneNotNull constraintAnnotation) {
        this.fieldNames = constraintAnnotation.fieldNames();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if(o == null) {
            return true;
        }
        try {
            for (String fieldName : fieldNames) {
                Field field = o.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                if(field.get(o) != null) {
                    return true;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
