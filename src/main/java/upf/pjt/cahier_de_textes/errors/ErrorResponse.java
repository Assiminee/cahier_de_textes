package upf.pjt.cahier_de_textes.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private HttpStatus httpStatus;
    private final String MODULE_NOT_FOUND = "Le module selectionné n'existe pas";
    private final String PROF_NOT_FOUND = "Le professeur selectionné n'existe pas";
    private final String DUPLICATE_AFFECTATION = "Vous ne pouvez pas créer deux affectation avec le même module pour un semestre dans une filière";
    private final String SCHEDULE_CONFLICT = "Vous ne pouvez pas planifier deux séance en même temps pour la même classe";
    private final String HOURS_SURPASSED = "Le professeur que vous avez selectionné a atteint le nombre maximal d'heure qu'il peut enseigner ce semsestre";
    private final String MAX_AFF_PER_PROF = "Un professeur ne peut pas dépasser 3 affectations par niveau et semestre dans une filière";
    private final String INTERNAL_SERVER_ERROR = "Une erreur s'est produite lors de la création, modification, ou suppression de l'affectation. Veuillez réessayer plus tard.";
    private final String AFFECTATION_NOT_FOUND = "L'affectation que vous essayé de modifier ou supprimer n'existe pas";
    private final String FORBIDDEN_MODIFICATION = "Un cahier de texte est associé a cette affectation. Vous devez supprimer l'affectation et créer une nouvelle";

    public void moduleNotFound() {
        this.message = MODULE_NOT_FOUND;
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    public void profNotFound() {
        this.message = PROF_NOT_FOUND;
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    public void duplicateAffectation() {
        this.message = DUPLICATE_AFFECTATION;
        this.httpStatus = HttpStatus.CONFLICT;
    }

    public void scheduleConflict() {
        this.message = SCHEDULE_CONFLICT;
        this.httpStatus = HttpStatus.CONFLICT;
    }

    public void hoursSurpassed() {
        this.message = HOURS_SURPASSED;
        this.httpStatus = HttpStatus.CONFLICT;
    }

    public void maxAffectationsReached() {
        this.message = MAX_AFF_PER_PROF;
        this.httpStatus = HttpStatus.CONFLICT;
    }

    public void internalServerError() {
        this.message = INTERNAL_SERVER_ERROR;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public void affectationNotFound() {
        this.message = AFFECTATION_NOT_FOUND;
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    public void forbiddenModification() {
        this.message = FORBIDDEN_MODIFICATION;
        this.httpStatus = HttpStatus.CONFLICT;
    }
}
