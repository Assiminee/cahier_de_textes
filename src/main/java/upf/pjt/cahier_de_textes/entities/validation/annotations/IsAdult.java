package upf.pjt.cahier_de_textes.entities.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsAdultValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAdult {
    String message() default "Invalid birthdate: Must be an adult";

    int yearsAgo() default 18;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
