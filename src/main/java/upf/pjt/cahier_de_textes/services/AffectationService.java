package upf.pjt.cahier_de_textes.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.AffectationDTO;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.repositories.AffectationRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ModuleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.errors.ErrorResponse;

import java.util.UUID;

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
    public Affectation saveAffectation(Filiere filiere, AffectationDTO affDTO, ErrorResponse err) {
        Affectation affectation = null;

        try {
            Module module = moduleRepository.findById(affDTO.getModule().getId()).orElse(null);

            if (module == null) {
                err.moduleNotFound();
                return null;
            }

            Professeur prof = professeurRepository.findById(affDTO.getProfesseur().getId()).orElse(null);

            if (prof == null) {
                err.profNotFound();
                return null;
            }

            if (duplicateData(filiere, module, affDTO)) {
                err.duplicateAffectation();
                return null;
            }

            if (scheduleConflict(filiere, affDTO)) {
                err.scheduleConflict();
                return null;
            }

            if (hoursSurpassed(prof, module)) {
                err.hoursSurpassed();
                return null;
            }

            if (maxAffectationsReached(filiere, prof, affDTO)) {
                err.maxAffectationsReached();
                return null;
            }

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
            err.internalServerError();
        }

        return affectation;
    }

    public ErrorResponse deleteAffectation(UUID affId) {
        ErrorResponse err = new ErrorResponse();

        try {
            boolean exists = affectationRepository.existsById(affId);

            if (!exists) {
                err.affectationNotFound();
                return err;
            }

            affectationRepository.deleteById(affId);
            exists = affectationRepository.existsById(affId);

            if (exists) {
                err.internalServerError();
                return err;
            }

        } catch (Exception e) {
            log.error(String.valueOf(e.getCause()));
            err.internalServerError();
            return err;
        }

        return null;
    }

    public Affectation modifyAffectation(UUID affId, Filiere filiere, AffectationDTO affDTO, ErrorResponse err) {
        Affectation affectation = null;

        try {
            affectation = affectationRepository.findById(affId).orElse(null);

            if (affectation == null) {
                err.affectationNotFound();
                return null;
            }

            Professeur prof = professeurRepository.findById(affDTO.getProfesseur().getId()).orElse(null);

            if (prof == null) {
                err.profNotFound();
                return null;
            }

            Module module = moduleRepository.findById(affDTO.getModule().getId()).orElse(null);

            if (module == null) {
                err.moduleNotFound();
                return null;
            }

            if (affectation.getProf().getId() != prof.getId()) {
                if (hoursSurpassed(prof, module)) {
                    err.hoursSurpassed();
                    return null;
                }

                if (maxAffectationsReached(filiere, prof, affDTO)) {
                    err.maxAffectationsReached();
                    return null;
                }
                affectation.setProf(prof);
            }

            if (affectation.getModule().getId() != module.getId()) {
                if (duplicateData(filiere, module, affDTO)) {
                    err.duplicateAffectation();
                    return null;
                }
                affectation.setModule(module);
            }

            if (affectation.getJour() != affDTO.getJour() || affectation.getHeureDebut() != affDTO.getHeureDebut()) {
                if (scheduleConflict(filiere, affDTO)) {
                    err.scheduleConflict();
                    return null;
                }
                affectation.setJour(affDTO.getJour());
                affectation.setHeureDebut(affDTO.getHeureDebut());
                affectation.setHeureFin(affDTO.getHeureFin());
            }

            affectation = affectationRepository.save(affectation);
        } catch (Exception e) {
            log.error(String.valueOf(e.getCause()));
            err.internalServerError();
        }

        return affectation;
    }

    private Boolean duplicateData(Filiere filiere, Module module, AffectationDTO affDTO) {
        Integer count = affectationRepository.countAffectationByFiliereAndModuleAndNiveauAndSemestre(
                filiere, module, affDTO.getNiveau(), affDTO.getSemestre()
        );

        return count >= 1;
    }

    private Boolean scheduleConflict(Filiere filiere, AffectationDTO affDTO) {
        Integer count = affectationRepository
                .countAffectationByFiliereAndNiveauAndSemestreAndJourAndHeureDebutAndHeureFin(
                        filiere, affDTO.getNiveau(), affDTO.getSemestre(),
                        affDTO.getJour(), affDTO.getHeureDebut(), affDTO.getHeureFin()
                );

        return count >= 1;
    }

    private Boolean hoursSurpassed(Professeur prof, Module module) {
        Object result = professeurRepository.getTotalHoursTaught(prof);
        Object[] resultList = (Object[]) result;
        Long hours = (Long) resultList[1];
        int max = prof.getGrade() == Grade.MA ? 90 : 150;

        return hours + module.getNombre_heures() > max;
    }

    private Boolean maxAffectationsReached(Filiere filiere, Professeur prof, AffectationDTO affDTO) {
        Integer count = affectationRepository.countAffectationByFiliereAndNiveauAndSemestreAndProf(
                filiere, affDTO.getNiveau(), affDTO.getSemestre(), prof
        );

        return count >= 3;
    }

}
