package upf.pjt.cahier_de_textes.dao.entities.validation_annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Defines the implementation that validates the constraint
@Constraint(validatedBy = IsValidNiveauValidator.class)
// Defines at which level this annotation can be used
// The attribute ElementType.PARAMETER means that the
// annotation can be used at the level of method parameters
// while ElementType.FIELD refers to instance variables.
// ElementType.TYPE == class level
@Target({ElementType.TYPE})
// Determines whether the annotation is available only
// during source code, compile time, or runtime
// In this case, the annotation is available at runtime
@Retention(RetentionPolicy.RUNTIME)
public @interface IsValidNiveau {
    String message() default "Invalid niveau";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
