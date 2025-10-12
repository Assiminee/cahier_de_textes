package upf.pjt.cahier_de_textes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import java.util.List;

@SpringBootApplication
class CahierDeTextesApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CahierDeTextesApplication.class, args);
    }


    @Override
    @Transactional
    public void run(String... args) {}
}
