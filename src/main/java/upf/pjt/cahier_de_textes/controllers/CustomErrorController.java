package upf.pjt.cahier_de_textes.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    @GetMapping
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == 404)
                return "redirect:/error/404";

            if (statusCode == 403)
                return "redirect:/error/403";

            if (statusCode == 405)
                return "redirect:/error/405";

            if (statusCode == 401)
                return "redirect:/error/401";
        }

        return "redirect:/error/500";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }

    @GetMapping("/401")
    public String unauthenticated() {
        return "error/401";
    }

    @GetMapping("/404")
    public String notFound() {
        return "error/404";
    }

    @GetMapping("/405")
    public String forbiddenMethod() { return "error/405"; }

    @GetMapping("/500")
    public String internalServerError() { return "error/default"; }
}
