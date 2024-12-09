package upf.pjt.cahier_de_textes.dao.entities.validation_annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class IsAdultValidator implements ConstraintValidator<IsAdult, LocalDate> {

    int yearsAgo;

    @Override
    public void initialize(IsAdult constraintAnnotation) {
        this.yearsAgo = constraintAnnotation.yearsAgo();
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null)
            return true;

        LocalDate now = LocalDate.now();
        return Period.between(date, now).getYears() >= yearsAgo;
    }
}
