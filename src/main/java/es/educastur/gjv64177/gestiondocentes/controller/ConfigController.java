package es.educastur.gjv64177.gestiondocentes.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.educastur.gjv64177.gestiondocentes.config.MainConfigProperties;
import es.educastur.gjv64177.gestiondocentes.util.MetodosAux;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    @Autowired
    private MainConfigProperties config;
    @Autowired
    private MetodosAux metodosAux;

    @GetMapping("/main")
    public MainConfigProperties getConfig() {
        return config;
    }

    @GetMapping("/trimestre-index/{fecha}")
    public Integer getTrimestreIndex(@PathVariable String fecha) {
        return metodosAux.getIndiceTrimestre(LocalDate.parse(fecha));
    }
}
