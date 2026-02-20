package tn.iatechnology.backend.controller;

import tn.iatechnology.backend.entity.Actualite;
import tn.iatechnology.backend.repository.ActualiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class ActualiteController {

    @Autowired
    private ActualiteRepository actualiteRepository;

    // ✅ Endpoint PUBLIC - accessible sans authentification (utilisé sur la Home)
    @GetMapping("/public/actualites")
    public List<Actualite> getPublicActualites() {
        return actualiteRepository.findByActifTrueOrderByDatePublicationDesc();
    }

    // Liste complète pour modérateur/admin
    @GetMapping("/actualites")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public List<Actualite> getAllActualites() {
        return actualiteRepository.findAllByOrderByDatePublicationDesc();
    }

    // Création (modérateur ou admin)
    @PostMapping("/actualites")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<Actualite> createActualite(@RequestBody Actualite actualite) {
        if (actualite.getDatePublication() == null) {
            actualite.setDatePublication(LocalDateTime.now());
        }
        Actualite saved = actualiteRepository.save(actualite);
        return ResponseEntity.ok(saved);
    }

    // Mise à jour (modérateur ou admin)
    @PutMapping("/actualites/{id}")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<Actualite> updateActualite(@PathVariable Long id, @RequestBody Actualite actualite) {
        Actualite existing = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée"));
        existing.setTitre(actualite.getTitre());
        existing.setContenu(actualite.getContenu());
        existing.setDatePublication(actualite.getDatePublication());
        existing.setActif(actualite.isActif());
        return ResponseEntity.ok(actualiteRepository.save(existing));
    }

    // Suppression (admin uniquement)
    @DeleteMapping("/actualites/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteActualite(@PathVariable Long id) {
        actualiteRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}