package upf.pjt.cahier_de_textes.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.AffectationDTO;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.repositories.AffectationRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ModuleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;

@Slf4j
@Service
public class AffectationService {
    private final ModuleRepository moduleRepository;
    private final ProfesseurRepository professeurRepository;
    private final AffectationRepository affectationRepository;

    @Autowired
    public AffectationService(ModuleRepository moduleRepository, ProfesseurRepository professeurRepository, AffectationRepository affectationRepository) {
        this.moduleRepository = moduleRepository;
        this.professeurRepository = professeurRepository;
        this.affectationRepository = affectationRepository;
    }

    @Transactional
    public Affectation saveAffectation(Filiere filiere, AffectationDTO affDTO) {
        Affectation affectation = null;

        try {
            Module module = moduleRepository.findById(affDTO.getModule().getId()).orElse(null);
            Professeur prof = professeurRepository.findById(affDTO.getProfesseur().getId()).orElse(null);

            if (module == null || prof == null)
                return null;

            affectation = new Affectation(
                    affDTO.getId(),
                    affDTO.getNiveau(),
                    affDTO.getSemestre(),
                    affDTO.getHeureDebut(),
                    affDTO.getHeureFin(),
                    affDTO.getJour(),
                    filiere,
                    prof,
                    module
            );

            affectation = affectationRepository.save(affectation);
        } catch (Exception e) {
            log.error(String.valueOf(e.getCause()));
        }

        return affectation;
    }
}
