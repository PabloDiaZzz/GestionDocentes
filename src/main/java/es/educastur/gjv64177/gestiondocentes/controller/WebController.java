package es.educastur.gjv64177.gestiondocentes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping({"/login", "/logout"})
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String docentes() {
        return "admin";
    }
}
