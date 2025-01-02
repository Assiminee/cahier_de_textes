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
import upf.pjt.cahier_de_textes.errors.ErrorResponse;
import upf.pjt.cahier_de_textes.services.AffectationService;

import java.util.UUID;

@RestController
public class AffectationRestController {
    private final FiliereRepository filiereRepository;
    private final AffectationService affectationService;

    @Autowired
    public AffectationRestController(FiliereRepository filiereRepository, AffectationService affectationService) {
        this.filiereRepository = filiereRepository;
        this.affectationService = affectationService;
    }

    @PostMapping("/filieres/{id}/affectations")
    public ResponseEntity<?> saveAffecation(@PathVariable("id") UUID id, @RequestBody SaveEditAffectationDTO affectationDTO, RedirectAttributes redAtts) {
        ErrorResponse err = new ErrorResponse();
        Filiere filiere = filiereRepository.findById(id).orElse(null);

        if (filiere == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La filière n'existe pas");

        Affectation affectation = affectationService.saveAffectation(filiere, new AffectationDTO(affectationDTO), err);

        if (affectation == null) {
            return ResponseEntity.status(err.getHttpStatus())
                    .body(err.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(new AffectationDTO(affectation));
    }

    @PutMapping("/filieres/{id}/affectations/{affId}")
    public ResponseEntity<?> modifyAffecation(@PathVariable("id") UUID id, @PathVariable("affId") UUID affId, @RequestBody SaveEditAffectationDTO affectationDTO, RedirectAttributes redAtts) {
        ErrorResponse err = new ErrorResponse();
        Filiere filiere = filiereRepository.findById(id).orElse(null);

        if (filiere == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La filière n'existe pas");

        Affectation affectation = affectationService.modifyAffectation(affId, filiere, new AffectationDTO(affectationDTO), err);

        if (affectation == null) {
            return ResponseEntity.status(err.getHttpStatus())
                    .body(err.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(new AffectationDTO(affectation));
    }

    @DeleteMapping("/filieres/{id}/affectations/{affId}")
    public ResponseEntity<?> deleteAffection(@PathVariable("id") UUID id, @PathVariable("affId") UUID affId) {
        Filiere filiere = filiereRepository.findById(id).orElse(null);

        if (filiere == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La filière n'existe pas");

        ErrorResponse err = affectationService.deleteAffectation(affId);

        if (err == null)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");

        return ResponseEntity.status(err.getHttpStatus()).body(err.getMessage());
    }
}
