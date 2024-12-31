package upf.pjt.cahier_de_textes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.AffectationDTO;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.SaveEditAffectationDTO;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.repositories.FiliereRepository;
import upf.pjt.cahier_de_textes.services.AffectationService;

import java.util.UUID;

@RestController
public class AffectationController {
    private final FiliereRepository filiereRepository;
    private final AffectationService affectationService;

    @Autowired
    public AffectationController(FiliereRepository filiereRepository, AffectationService affectationService) {
        this.filiereRepository = filiereRepository;
        this.affectationService = affectationService;
    }

    @PostMapping("/filieres/{id}/affectations")
    public ResponseEntity<AffectationDTO> saveAffecation(@PathVariable("id") UUID id, @RequestBody SaveEditAffectationDTO affectationDTO, RedirectAttributes redAtts) {
        Filiere filiere = filiereRepository.findById(id).orElse(null);

        if (filiere == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        Affectation affectation = affectationService.saveAffectation(filiere, new AffectationDTO(affectationDTO));

        if (affectation == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.status(HttpStatus.OK).body(new AffectationDTO(affectation));
    }
}
