package upf.pjt.cahier_de_textes.dao;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upf.pjt.cahier_de_textes.entities.Professeur;
import upf.pjt.cahier_de_textes.entities.Module;
import upf.pjt.cahier_de_textes.entities.Qualification;
import upf.pjt.cahier_de_textes.entities.Role;
import upf.pjt.cahier_de_textes.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.entities.enumerations.ModeEval;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;

import java.time.LocalDate;
import java.util.Set;

@Service
public class ProfesseurService {
    private final ProfesseurRepository professeurRepository;
    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;

    @Autowired
    public ProfesseurService(ProfesseurRepository professeurRepository, RoleRepository roleRepository, ModuleRepository moduleRepository) {
        this.professeurRepository = professeurRepository;
        this.roleRepository = roleRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional
    public void saveProfesseurAndModules() {
        Role prof = new Role();
        prof.setRole(RoleEnum.PROF);
        roleRepository.save(prof);

        Professeur professeur = getProfesseur(prof);


        Module module1 = new Module();
        module1.setIntitule("Mathematics");
        module1.setNombre_heures(40);
        module1.setModeEvaluation(ModeEval.EXAM);
        module1.setResponsable(professeur);

        Module module2 = new Module();
        module2.setIntitule("Physics");
        module2.setNombre_heures(60);
        module2.setModeEvaluation(ModeEval.PROJET);
        module2.setResponsable(professeur);

        professeur.getModules().add(module1);
        professeur.getModules().add(module2);

        professeur.setRole(prof);

        professeurRepository.save(professeur);
    }

    private static Professeur getProfesseur(Role prof) {
        Professeur professeur = new Professeur();
        professeur.setRole(prof);
        professeur.setNom("Znatni");
        professeur.setPrenom("Yasmine");
        professeur.setDateNaissance(LocalDate.of(1997, 10, 18));
        professeur.setEmail("znatni.yasmine@gmail.com");
        professeur.setTelephone("+212648288553");
        professeur.setAdresse("60 LOT Nouzha RTE Ain Chkef Fes");
        professeur.setGenre(Genre.F);
        professeur.setCin("R362971");
        professeur.setPwd("ProjectM@648288553");
        professeur.setGrade(Grade.PHD);
        professeur.setDateDernierDiplome(LocalDate.of(2020, 6, 15));
        professeur.setDateEmbauche(LocalDate.of(2021, 9, 1));
        professeur.getQualifications().add(getQualification(professeur));

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Professeur>> constraintViolations = validator.validate(professeur);

        for (ConstraintViolation<Professeur> violation : constraintViolations) {
            System.out.println(violation.getPropertyPath());
            System.out.println(violation.getMessage());
        }

        factory.close();
        return professeur;
    }

    private static Qualification getQualification(Professeur professeur) {
        Qualification qualification = new Qualification();
        qualification.setIntitule("Amazing PHD");
        qualification.setDateObtention(LocalDate.of(2020, 6, 15));
        qualification.setProf(professeur);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Qualification>> constraintViolations = validator.validate(qualification);

        for (ConstraintViolation<Qualification> violation : constraintViolations) {
            System.out.println(violation.getPropertyPath());
            System.out.println(violation.getMessage());
        }

        factory.close();
        return qualification;
    }
}
