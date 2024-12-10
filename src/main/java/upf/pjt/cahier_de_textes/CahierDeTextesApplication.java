package upf.pjt.cahier_de_textes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;
import upf.pjt.cahier_de_textes.dao.FiliereRepository;
import upf.pjt.cahier_de_textes.dao.ModuleRepository;
import upf.pjt.cahier_de_textes.dao.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.UserRepository;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.User;
import java.util.UUID;

@SpringBootApplication
class CahierDeTextesApplication implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfesseurRepository professeurRepository;
    @Autowired
    ModuleRepository moduleRepository;
    @Autowired
    FiliereRepository filiereRepository;

    public static void main(String[] args) {
        SpringApplication.run(CahierDeTextesApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {}
//    @Transactional
//    @Override
//    public void run(String... args) throws Exception {
//        UUID id = UUID.fromString("42345678-1234-5678-1234-567812345678");
//        User user = userRepository.findById(id).orElse(null);
//
//        if (user == null)
//            return;
//
//        if (user.getRole().getAuthority().equals("ROLE_PROF")) {
//            Professeur prof = professeurRepository.findById(id).orElse(null);
//
//            if (prof == null)
//                return;
//
//            for (Module module : prof.getModules()) {
//                module.setResponsable(null);
//                moduleRepository.save(module);
//            }
//            prof.setModules(null);
//
//            if (prof.getFiliere() != null) {
//                prof.getFiliere().setCoordinateur(null);
//                filiereRepository.save(prof.getFiliere());
//                prof.setFiliere(null);
//            }
//
//            userRepository.delete(user);
//        }
//
//
//    }
}
