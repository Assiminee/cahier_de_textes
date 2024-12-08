package upf.pjt.cahier_de_textes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import upf.pjt.cahier_de_textes.dao.ProfesseurService;
import upf.pjt.cahier_de_textes.services.UserService;

@SpringBootApplication
@RestController
class CahierDeTextesApplication implements CommandLineRunner {
    @Autowired
    private ProfesseurService professeurService;
    @Autowired
    private UserService userservice;

    public static void main(String[] args) {
        SpringApplication.run(CahierDeTextesApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @Override
    public void run(String... args) throws Exception {
        /*professeurService.saveProfesseurAndModules();*/
        userservice.saveAdminAndUser();
        System.out.println("Done");
    }
}
