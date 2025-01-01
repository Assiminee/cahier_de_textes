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

    @Autowired
    ProfesseurRepository prep;

    public static void main(String[] args) {
        SpringApplication.run(CahierDeTextesApplication.class, args);
    }


    @Override
    @Transactional
    public void run(String... args) {
        List<Professeur> allProfesseurs = prep.findAll();
        for (Professeur p : allProfesseurs) {
            Object obj = prep.getTotalHoursTaught(p);

            // Cast the result to an Object array
            Object[] result = (Object[]) obj;

            // Get the Professeur object (first element of the array)
            Professeur professeur = (Professeur) result[0];

            // Get the total hours taught (second element of the array)
            Long totalHoursTaught = (Long) result[1];

            // Print the result
            System.out.println("Professeur id: " + professeur.getId());
            System.out.println("Professeur " + professeur.getFullName() + " teaches " + totalHoursTaught + " hours");
        }

        List<Professeur> ps = prep.findAvailableProfesseurs();
        for (Professeur p : ps) {
            System.out.println("Professeur id: " + p.getId());
            System.out.println("Professeur Full name: " + p.getFullName());
        }
    }
}
