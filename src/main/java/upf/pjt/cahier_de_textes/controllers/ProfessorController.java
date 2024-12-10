package upf.pjt.cahier_de_textes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import upf.pjt.cahier_de_textes.dao.ProfesseurRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/professeurs")
public class ProfessorController {

    @Autowired
    private ProfesseurRepository professorRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getProfessors() {
        List<Map<String, String>> professors = professorRepository.findAll().stream().map(p -> {
            Map<String, String> profMap = new HashMap<>();
            profMap.put("id", p.getId().toString());
            profMap.put("nom", p.getNom() + " " + p.getPrenom());
            return profMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(professors);
    }
}
