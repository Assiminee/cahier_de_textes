package upf.pjt.cahier_de_textes.dao.entities.validation_annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;

public class IsValidSemestreValidator implements ConstraintValidator<IsValidSemestre, Affectation> {

    @Override
    public boolean isValid(Affectation affectation, ConstraintValidatorContext context) {
        if (affectation == null)
            return true;

        int semestre = affectation.getSemestre();
        int niveau = affectation.getNiveau();

        if (semestre == niveau * 2 || semestre == (niveau * 2) - 1)
            return true;

        context.disableDefaultConstraintViolation();
        String err = "Le semestre pour le niveau " + niveau + " ne peut prendre que les valeurs S" + niveau * 2 + " ou S" + ((niveau * 2) - 1) + ".";
        context.buildConstraintViolationWithTemplate(err);

        return false;
    }
}
