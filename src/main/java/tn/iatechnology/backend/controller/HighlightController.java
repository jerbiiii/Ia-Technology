package tn.iatechnology.backend.controller;

import tn.iatechnology.backend.dto.HighlightRequest;
import tn.iatechnology.backend.dto.MessageResponse;
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

    // Accès modérateur/admin
    @GetMapping("/moderator/highlights")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public List<Highlight> getAllHighlights() {
        return highlightRepository.findAll();
    }

    @GetMapping("/moderator/highlights/{id}")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<Highlight> getHighlightById(@PathVariable Long id) {
        Highlight highlight = highlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Highlight non trouvé"));
        return ResponseEntity.ok(highlight);
    }

    @PostMapping("/moderator/highlights")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public Highlight createHighlight(@RequestBody HighlightRequest request) {
        Highlight highlight = new Highlight();
        highlight.setTitre(request.getTitre());
        highlight.setDescription(request.getDescription());
        highlight.setImageUrl(request.getImageUrl());
        highlight.setDateCreation(LocalDateTime.now());
        highlight.setActif(request.isActif());
        return highlightRepository.save(highlight);
    }

    @PutMapping("/moderator/highlights/{id}")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<Highlight> updateHighlight(@PathVariable Long id, @RequestBody HighlightRequest request) {
        Highlight highlight = highlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Highlight non trouvé"));
        highlight.setTitre(request.getTitre());
        highlight.setDescription(request.getDescription());
        highlight.setImageUrl(request.getImageUrl());
        highlight.setActif(request.isActif());
        return ResponseEntity.ok(highlightRepository.save(highlight));
    }

    @DeleteMapping("/moderator/highlights/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteHighlight(@PathVariable Long id) {
        highlightRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Highlight supprimé"));
    }
}