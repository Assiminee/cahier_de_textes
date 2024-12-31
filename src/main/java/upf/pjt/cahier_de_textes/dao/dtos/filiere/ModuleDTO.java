package upf.pjt.cahier_de_textes.dao.dtos.filiere;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.repositories.ModuleRepository;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class ModuleDTO {
    private UUID id;
    private String intitule;

    public ModuleDTO(Module module) {
        this.id = module.getId();
        this.intitule = module.getIntitule();
    }

    public ModuleDTO(UUID id) {
        this.id = id;
    }
}
