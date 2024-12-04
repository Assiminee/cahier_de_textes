package upf.pjt.cahier_de_textes.entities.validation_annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsValidSemestreValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsValidSemestre {
    String message() default "Invalid semestre value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
