package tn.iatechnology.backend.controller;

import tn.iatechnology.backend.entity.Actualite;
import tn.iatechnology.backend.repository.ActualiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ActualiteController {

    @Autowired
    private ActualiteRepository actualiteRepository;

    // Accès public
    @GetMapping("/public/actualites")
    public List<Actualite> getAllActualites() {
        return actualiteRepository.findAllByOrderByDatePublicationDesc();
    }

    // Accès modérateur et admin
    @PostMapping("/actualites")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public Actualite createActualite(@RequestBody Actualite actualite) {
        // À améliorer : ajouter l'utilisateur connecté comme auteur
        return actualiteRepository.save(actualite);
    }

    @PutMapping("/actualites/{id}")
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
    public Actualite updateActualite(@PathVariable Long id, @RequestBody Actualite actualite) {
        Actualite existing = actualiteRepository.findById(id).orElseThrow(() -> new RuntimeException("Actualité non trouvée"));
        existing.setTitre(actualite.getTitre());
        existing.setContenu(actualite.getContenu());
        existing.setDatePublication(actualite.getDatePublication());
        // Ne pas changer l'auteur ?
        return actualiteRepository.save(existing);
    }

    @DeleteMapping("/actualites/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteActualite(@PathVariable Long id) {
        actualiteRepository.deleteById(id);
    }
}