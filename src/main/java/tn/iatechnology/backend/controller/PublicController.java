package tn.iatechnology.backend.controller;

import tn.iatechnology.backend.dto.DomainDTO;
import tn.iatechnology.backend.dto.PublicationResponse;
import tn.iatechnology.backend.dto.ResearcherDTO;
import tn.iatechnology.backend.entity.Publication;
import tn.iatechnology.backend.repository.PublicationRepository;
import tn.iatechnology.backend.service.DomainService;
import tn.iatechnology.backend.service.PublicationService;
import tn.iatechnology.backend.service.ResearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Endpoints publics — aucune authentification requise.
 * Le chemin /api/public/** est ignoré par Spring Security (WebSecurityConfig).
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private ResearcherService researcherService;

    @Autowired
    private DomainService domainService;

    // ══════════════════════ PUBLICATIONS ══════════════════════

    /**
     * GET /api/public/publications
     * Toutes les publications (sans authentification)
     */
    @GetMapping("/publications")
    public List<PublicationResponse> getAllPublications() {
        return publicationService.getAllPublications();
    }

    /**
     * GET /api/public/publications/{id}
     * Détail d'une publication (sans authentification)
     */
    @GetMapping("/publications/{id}")
    public ResponseEntity<PublicationResponse> getPublicationById(@PathVariable Long id) {
        return ResponseEntity.ok(publicationService.getPublicationById(id));
    }

    /**
     * GET /api/public/publications/search?keyword=...&domaineId=...&chercheur=...
     * Recherche de publications (sans authentification)
     */
    @GetMapping("/publications/search")
    public List<PublicationResponse> searchPublications(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long domaineId,
            @RequestParam(required = false) String chercheur) {

        List<Publication> results;

        if (domaineId != null) {
            results = publicationRepository.findByDomainesId(domaineId);
        } else if (chercheur != null && !chercheur.isBlank()) {
            results = publicationRepository.findByChercheurNomOuPrenom(chercheur, chercheur);
        } else if (keyword != null && !keyword.isBlank()) {
            results = publicationRepository.findByKeywordInTitreOrResume(keyword);
        } else {
            results = publicationRepository.findAll();
        }

        return results.stream()
                .map(publicationService::convertToResponse)
                .collect(Collectors.toList());
    }

    // ══════════════════════ CHERCHEURS ══════════════════════

    /**
     * GET /api/public/researchers
     * Tous les chercheurs (sans authentification)
     */
    @GetMapping("/researchers")
    public List<ResearcherDTO> getAllResearchers() {
        return researcherService.getAllResearchers();
    }

    /**
     * GET /api/public/researchers/{id}
     * Détail d'un chercheur (sans authentification)
     */
    @GetMapping("/researchers/{id}")
    public ResponseEntity<ResearcherDTO> getResearcherById(@PathVariable Long id) {
        return ResponseEntity.ok(researcherService.getResearcherById(id));
    }

    /**
     * GET /api/public/researchers/search?keyword=...&domaineId=...&nom=...
     * Recherche de chercheurs (sans authentification)
     */
    @GetMapping("/researchers/search")
    public List<ResearcherDTO> searchResearchers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String domaine) {

        // "keyword" est un alias pour "nom" (utilisé par SearchPage.jsx)
        String searchNom = nom != null ? nom : keyword;
        return researcherService.search(searchNom, null, domaine);
    }

    // ══════════════════════ DOMAINES ══════════════════════

    /**
     * GET /api/public/domains
     * Tous les domaines (sans authentification) — utilisé par SearchPage et filtres
     */
    @GetMapping("/domains")
    public List<DomainDTO> getAllDomains() {
        return domainService.getAllDomains();
    }

    /**
     * GET /api/public/domains/{id}
     * Détail d'un domaine (sans authentification)
     */
    @GetMapping("/domains/{id}")
    public ResponseEntity<DomainDTO> getDomainById(@PathVariable Long id) {
        return ResponseEntity.ok(domainService.getDomainById(id));
    }
}