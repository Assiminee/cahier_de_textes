package upf.pjt.cahier_de_textes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.UUID;

@SpringBootApplication
class CahierDeTextesApplication implements CommandLineRunner {

    @Autowired
    PasswordEncoder encoder;

    public static void main(String[] args) {
        SpringApplication.run(CahierDeTextesApplication.class, args);
    }


    @Override
    public void run(String... args) {
        String[] pwds = new String[]
                {"te@123Kate@123",
                        "David@456David@456",
                        "Susan@789Susan@789",
                        "Jonathan@321Jonathan@321",
                        "Anna@654Anna@654",
                        "Peter@987Peter@987",
                        "Mary@135Mary@135",
                        "James@246James@246",
                        "Linda@357Linda@357",
                        "Ryan@468Ryan@468",
                        "Admin123@Admin123",
                        "Maria@579Maria@579",
                        "Jack@780Jack@780",
                        "Lucy@981Lucy@981",
                        "Admin456@Admin456",
                        "Mike@102Mike@102",
                        "Julia@214Julia@214",
                        "Admin678@Admin678",
                        "Susan@345Susan@345",
                        "Matthew@456Matthew@456"};

        for (String pwd : pwds) {
            System.out.println(encoder.encode(pwd));
        }
    }
}
