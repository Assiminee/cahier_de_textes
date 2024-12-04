package upf.pjt.cahier_de_textes.entities.validation_annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HasAtLeastOneQualificationValidator.class)
public @interface HasAtLeastOneQualification {
    String message() default "Professor must have at least one qualification";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
