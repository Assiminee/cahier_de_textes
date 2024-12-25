package upf.pjt.cahier_de_textes.dao.dtos.filiere;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class ModuleDTO {
    private UUID id;
    private String Intitule;

    public ModuleDTO(Module module) {
        this.id = module.getId();
        this.Intitule = module.getIntitule();
    }
}
