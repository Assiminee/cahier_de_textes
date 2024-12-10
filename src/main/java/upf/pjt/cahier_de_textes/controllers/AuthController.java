package upf.pjt.cahier_de_textes.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin("*")
public class AuthController {

    @GetMapping("/auth/login")
    public String login() {
        return "login";
    }
}
