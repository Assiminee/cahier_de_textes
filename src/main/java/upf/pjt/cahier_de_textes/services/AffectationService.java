package upf.pjt.cahier_de_textes.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.AffectationDTO;
import upf.pjt.cahier_de_textes.dao.entities.*;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Jour;
import upf.pjt.cahier_de_textes.dao.repositories.AffectationRepository;
import upf.pjt.cahier_de_textes.dao.repositories.CahierRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ModuleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.errors.ErrorResponse;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
public class AffectationService {
    private final ModuleRepository moduleRepository;
    private final ProfesseurRepository professeurRepository;
    private final AffectationRepository affectationRepository;
    private final CahierRepository cahierRepository;

    @Autowired
    public AffectationService(ModuleRepository moduleRepository, ProfesseurRepository professeurRepository, AffectationRepository affectationRepository, CahierRepository cahierRepository) {
        this.moduleRepository = moduleRepository;
        this.professeurRepository = professeurRepository;
        this.affectationRepository = affectationRepository;
        this.cahierRepository = cahierRepository;
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

            affectation = getSavedAffectation(affDTO, filiere, prof, module);

            Cahier cahier = new Cahier(affectation);
            cahierRepository.save(cahier);

            affectation.setCahier(cahier);
            affectation = affectationRepository.save(affectation);
        } catch (Exception e) {
            log.error(String.valueOf(e.getCause()));
            err.internalServerError();
        }

        return affectation;
    }

    private Affectation getSavedAffectation(AffectationDTO affDTO, Filiere filiere, Professeur prof, Module module) {
        Affectation affectation = new Affectation(
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

        return affectationRepository.save(affectation);
    }

    public ErrorResponse deleteAffectation(UUID affId) {
        ErrorResponse err = new ErrorResponse();

        try {
            Affectation affectation = affectationRepository.findById(affId).orElse(null);

            if (affectation == null) {
                err.affectationNotFound();
                return err;
            }

            deleteAffectation(affectation);

            boolean exists = affectationRepository.existsById(affId);

            if (exists) {
                err.internalServerError();
                return err;
            }

        } catch (Exception e) {
            log.error(String.valueOf(e.getMessage()));
            err.internalServerError();
            return err;
        }

        return null;
    }

    public void deleteAffectation(Affectation affectation) {
        Cahier cahier = affectation.getCahier();
        affectation.setCahier(null);

        if (cahier != null) {
            if (cahier.getEntrees() != null && !cahier.getEntrees().isEmpty()) {
                cahier.setAffectation(null);
                cahier.setArchived(true);

                cahierRepository.save(cahier);
            } else {
                cahierRepository.delete(cahier);
            }
        }

        affectationRepository.delete(affectation);
    }

    public Affectation modifyAffectation(UUID affId, Filiere filiere, AffectationDTO affDTO, ErrorResponse err) {
        Affectation affectation = null;

        try {
            affectation = affectationRepository.findById(affId).orElse(null);

            if (affectation == null) {
                err.affectationNotFound();
                return null;
            }

            Cahier cahier = affectation.getCahier();
            Professeur prof = getProfesseur(affDTO, affectation, cahier, err);

            if (prof == null)
                return null;

            Module module = getModule(affDTO, affectation, filiere, cahier, err);

            if (module == null)
                return null;

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

            if (affectation.getJour() != affDTO.getJour() || affectation.getHeureDebut() != affDTO.getHeureDebut()) {
                if (scheduleConflict(filiere, affDTO)) {
                    err.scheduleConflict();
                    return null;
                }
                affectation.setJour(affDTO.getJour());
                affectation.setHeureDebut(affDTO.getHeureDebut());
                affectation.setHeureFin(affDTO.getHeureFin());
            }

            cahier.setModule(module.getIntitule());
            cahier.setProfesseur(prof.getFullName());
            cahierRepository.save(cahier);

            affectation = affectationRepository.save(affectation);
        } catch (Exception e) {
            log.error(String.valueOf(e.getCause()));
            err.internalServerError();
        }

        return affectation;
    }

    public Module getModule(AffectationDTO affDTO, Affectation aff, Filiere fil, Cahier cahier, ErrorResponse err) {
        Module module = moduleRepository.findById(affDTO.getModule().getId()).orElse(null);

        if (module == null) {
            err.moduleNotFound();
            return null;
        }

        if (!module.getId().equals(aff.getModule().getId())) {
            if (cahier != null && cahier.getEntrees() != null && !cahier.getEntrees().isEmpty()) {
                err.forbiddenModification();
                return null;
            }

            if (duplicateData(fil, module, affDTO)) {
                err.duplicateAffectation();
                return null;
            }
            aff.setModule(module);
        }

        return module;
    }

    public Professeur getProfesseur(AffectationDTO affDTO, Affectation aff, Cahier cahier, ErrorResponse err) {
        Professeur prof = professeurRepository.findById(affDTO.getProfesseur().getId()).orElse(null);

        if (prof == null) {
            err.profNotFound();
            return null;
        }

        if (!prof.getId().equals(aff.getProf().getId())) {
            if (cahier != null &&cahier.getEntrees() != null && !cahier.getEntrees().isEmpty()) {
                err.forbiddenModification();
                return null;
            }
        }

        return prof;
    }

    public Page<AffectationDTO> getAffectationDTOPage(String filiere, String module, String professeur, int heure, String jour, Pageable pageable) {
        Jour parsedJour;

        try {
            parsedJour = Jour.valueOf(jour);
        } catch (Exception e) {
            parsedJour = null;
        }

        Page<Affectation> affectations = affectationRepository.getAffectations(filiere, module, professeur, heure, parsedJour, pageable);

        return affectations.map(AffectationDTO::new);
    }

    public Page<AffectationDTO> getProfesseurAffectationDTOPage(UUID id, String filiere, String module, int heure, String jour, Pageable pageable) {
        Jour searchJour;

        try {
            searchJour = Jour.valueOf(jour);
        } catch (Exception e) {
            searchJour = null;
        }

        Page<Affectation> affectations = affectationRepository.getProfesseurAffectations(id, filiere, module, heure, searchJour, pageable);

        return affectations.map(AffectationDTO::new);
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
