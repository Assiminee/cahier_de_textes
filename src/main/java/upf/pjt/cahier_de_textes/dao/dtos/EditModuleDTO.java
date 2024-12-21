package upf.pjt.cahier_de_textes.dao.dtos;

import lombok.*;
import org.springframework.stereotype.Component;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.ModeEval;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Component
public class EditModuleDTO {
    private String intitule;
    private Integer nombre_Heures;
    private ModeEval modeEvaluation;
    private UUID responsable;

    public EditModuleDTO(String intitule, Integer nombre_heures, String modeEvaluation, UUID responsable) {
        this.intitule = intitule;
        this.nombre_Heures = nombre_heures;
        this.modeEvaluation = ModeEval.valueOf(modeEvaluation);
        this.responsable = responsable;
    }
    public void setModuleDetails(Module module, Professeur responsable) {
        module.setIntitule(intitule);
        module.setNombre_heures(nombre_Heures);
        module.setModeEvaluation(modeEvaluation);
        module.setResponsable(responsable);
    }
}
