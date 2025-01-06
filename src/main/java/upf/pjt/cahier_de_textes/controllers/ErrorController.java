package upf.pjt.cahier_de_textes.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String error() {
        return "error"; // This refers to error.html in the templates directory
    }
}
