package upf.pjt.cahier_de_textes.dao.entities.validation_annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;

public class IsValidNiveauValidator implements ConstraintValidator<IsValidNiveau, Affectation> {

    @Override
    public boolean isValid(Affectation affectation, ConstraintValidatorContext context) {
        if (affectation == null || affectation.getFiliere() == null)
            return false;

        int niveau = affectation.getNiveau();
        int nombreAnnees = affectation.getFiliere().getNombreAnnees();

        if (niveau >= 1 && niveau <= nombreAnnees)
            return true;

        // To set the error message dynamically, disable
        // the Default Constraint Violation
        context.disableDefaultConstraintViolation();
        String errorMessage = String.format(
                "Le niveau doit être compris entre 1 et %d",
                nombreAnnees
        );
        // Add a new Constraint Violation with the desired text
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addConstraintViolation();

        return false;
    }
}
