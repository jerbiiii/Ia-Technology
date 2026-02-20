package tn.iatechnology.backend.controller;

import tn.iatechnology.backend.dto.ResearcherDTO;
import tn.iatechnology.backend.service.AuditLogService;
import tn.iatechnology.backend.service.ResearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/researchers")
public class ResearcherController {

    @Autowired
    private ResearcherService researcherService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public List<ResearcherDTO> getAllResearchers() {
        return researcherService.getAllResearchers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public ResponseEntity<ResearcherDTO> getResearcherById(@PathVariable Long id) {
        return ResponseEntity.ok(researcherService.getResearcherById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResearcherDTO> createResearcher(@RequestBody ResearcherDTO researcherDTO) {
        ResearcherDTO created = researcherService.createResearcher(researcherDTO);
        auditLogService.log("CREATE", "RESEARCHER", created.getId(),
                "Création du chercheur : " + created.getPrenom() + " " + created.getNom());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResearcherDTO> updateResearcher(@PathVariable Long id, @RequestBody ResearcherDTO researcherDTO) {
        ResearcherDTO updated = researcherService.updateResearcher(id, researcherDTO);
        auditLogService.log("UPDATE", "RESEARCHER", id,
                "Modification du chercheur : " + updated.getPrenom() + " " + updated.getNom());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteResearcher(@PathVariable Long id) {
        // Récupérer infos avant suppression pour le log
        try {
            ResearcherDTO r = researcherService.getResearcherById(id);
            auditLogService.log("DELETE", "RESEARCHER", id,
                    "Suppression du chercheur : " + r.getPrenom() + " " + r.getNom());
        } catch (Exception ignored) {}

        researcherService.deleteResearcher(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public List<ResearcherDTO> searchResearchers(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String domaine) {
        return researcherService.search(nom, prenom, domaine);
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('UTILISATEUR') or hasRole('MODERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<ResearcherDTO> getResearcherByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(researcherService.getResearcherByUserId(userId));
    }
}