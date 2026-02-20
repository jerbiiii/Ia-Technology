package tn.iatechnology.backend.controller;


import tn.iatechnology.backend.entity.Highlight;
import tn.iatechnology.backend.repository.HighlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class HighlightController {

    @Autowired
    private HighlightRepository highlightRepository;

    // Accès public
    @GetMapping("/public/highlights")
    public List<Highlight> getActiveHighlights() {
        return highlightRepository.findByActifTrueOrderByDateCreationDesc();
    }

    // Gestion par modérateur/admin
    @GetMapping("/highlights")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public List<Highlight> getAllHighlights() {
        return highlightRepository.findAll();
    }

    @PostMapping("/highlights")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public Highlight createHighlight(@RequestBody Highlight highlight) {
        highlight.setDateCreation(LocalDateTime.now());
        return highlightRepository.save(highlight);
    }

    @PutMapping("/highlights/{id}")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<Highlight> updateHighlight(@PathVariable Long id, @RequestBody Highlight details) {
        Highlight highlight = highlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Highlight non trouvé"));
        highlight.setTitre(details.getTitre());
        highlight.setDescription(details.getDescription());
        highlight.setImageUrl(details.getImageUrl());
        highlight.setActif(details.isActif());
        return ResponseEntity.ok(highlightRepository.save(highlight));
    }

    @DeleteMapping("/highlights/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteHighlight(@PathVariable Long id) {
        highlightRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
