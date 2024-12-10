package upf.pjt.cahier_de_textes.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upf.pjt.cahier_de_textes.dao.ModuleRepository;
import upf.pjt.cahier_de_textes.dao.ProfesseurRepository;
import upf.pjt.cahier_de_textes.entities.Module;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ProfesseurRepository professorRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @GetMapping()
    public ResponseEntity<List<Module>> getModules(
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String keyword
    ) {
        List<Module> modules;

        if (field != null && keyword != null) {
            // Dynamic query to filter modules based on field and keyword
            String hql = "SELECT m FROM Module m WHERE m." + field + " LIKE :keyword";
            var query = entityManager.createQuery(hql, Module.class);
            query.setParameter("keyword", "%" + keyword + "%");
            modules = query.getResultList();
        } else {
            // Fetch all modules
            modules = moduleRepository.findAll();
        }

        return new ResponseEntity<>(modules, HttpStatus.OK);
    }


   /* @PostMapping
    public ResponseEntity<Module> addModule(@RequestBody Module module) {
        Module savedModule = moduleRepository.save(module);
        return new ResponseEntity<>(savedModule, HttpStatus.CREATED);
    }*/

/*
    @PutMapping("/{id}")
    public ResponseEntity<Module> updateModule(@PathVariable UUID id, @RequestBody Module module) {
        return moduleRepository.findById(id)
                .map(existingModule -> {
                    existingModule.setIntitule(module.getIntitule());
                    existingModule.setNombre_heures(module.getNombre_heures());
                    existingModule.setModeEvaluation(module.getModeEvaluation());
                    existingModule.setResponsable(module.getResponsable());
                    moduleRepository.save(existingModule);
                    return new ResponseEntity<>(existingModule, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }*/


   /* @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable UUID id) {
        if (moduleRepository.existsById(id)) {
            moduleRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }*/
}

