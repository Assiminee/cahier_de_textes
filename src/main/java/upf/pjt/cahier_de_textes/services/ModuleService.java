package upf.pjt.cahier_de_textes.services;

import org.springframework.stereotype.Service;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.Module;

@Service
public class ModuleService {
    private final AffectationService affectationService;

    public ModuleService(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    public void deleteCahiers(Module module) {
        for (Affectation aff : module.getAffectations()) {
            affectationService.deleteAffectation(aff);
        }
    }
}
