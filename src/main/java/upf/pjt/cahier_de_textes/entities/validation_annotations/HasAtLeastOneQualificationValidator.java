package upf.pjt.cahier_de_textes.entities.validation_annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import upf.pjt.cahier_de_textes.entities.Qualification;

import java.util.List;

public class HasAtLeastOneQualificationValidator implements ConstraintValidator<HasAtLeastOneQualification, List<?>> {

    @Override
    public boolean isValid(List<?> qualifications, ConstraintValidatorContext context) {
        if (qualifications == null)
            return true;

        return !qualifications.isEmpty() && qualifications.stream().allMatch(qualification -> qualification instanceof Qualification);
    }
}
