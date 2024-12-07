package upf.pjt.cahier_de_textes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import upf.pjt.cahier_de_textes.dao.ProfesseurService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import upf.pjt.cahier_de_textes.dao.UserRepository;
import upf.pjt.cahier_de_textes.entities.User;

import java.util.List;

@SpringBootApplication
@RestController
class CahierDeTextesApplication implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ProfesseurService professeurService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(CahierDeTextesApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @Override
    public void run(String... args) throws Exception {
        List<User> userList = userRepository.findAll();

        for (User user : userList) {
            System.out.println("\"id\": \"" + user.getId() + "\", \"email\": \"" + user.getEmail() + "\", ");
        }
    }
}
