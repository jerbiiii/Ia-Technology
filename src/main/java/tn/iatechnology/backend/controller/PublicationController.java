package tn.iatechnology.backend.controller;

import tn.iatechnology.backend.dto.PublicationRequest;
import tn.iatechnology.backend.dto.PublicationResponse;
import tn.iatechnology.backend.entity.Publication;
import tn.iatechnology.backend.repository.PublicationRepository;
import tn.iatechnology.backend.service.AuditLogService;
import tn.iatechnology.backend.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public List<PublicationResponse> getAllPublications() {
        return publicationService.getAllPublications();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public ResponseEntity<PublicationResponse> getPublicationById(@PathVariable Long id) {
        return ResponseEntity.ok(publicationService.getPublicationById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public List<PublicationResponse> searchPublications(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long domaineId,
            @RequestParam(required = false) Long chercheurId) {

        List<Publication> results;

        if (chercheurId != null) {
            results = publicationRepository.findByChercheursId(chercheurId);
        } else if (domaineId != null) {
            results = publicationRepository.findByDomainesId(domaineId);
        } else if (keyword != null && !keyword.isEmpty()) {
            results = publicationRepository.findByKeywordInTitreOrResume(keyword);
        } else {
            results = publicationRepository.findAll();
        }

        return results.stream()
                .map(publicationService::convertToResponse)
                .collect(Collectors.toList());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<PublicationResponse> createPublication(
            @RequestPart("publication") PublicationRequest request,
            @RequestPart(value = "fichier", required = false) MultipartFile fichier) throws IOException {
        PublicationResponse created = publicationService.createPublication(request, fichier);
        auditLogService.log("CREATE", "PUBLICATION", created.getId(),
                "Création de la publication : " + created.getTitre());
        return ResponseEntity.ok(created);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<PublicationResponse> updatePublication(
            @PathVariable Long id,
            @RequestPart("publication") PublicationRequest request,
            @RequestPart(value = "fichier", required = false) MultipartFile fichier) throws IOException {
        PublicationResponse updated = publicationService.updatePublication(id, request, fichier);
        auditLogService.log("UPDATE", "PUBLICATION", id,
                "Modification de la publication : " + updated.getTitre());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePublication(@PathVariable Long id) throws IOException {
        try {
            PublicationResponse p = publicationService.getPublicationById(id);
            auditLogService.log("DELETE", "PUBLICATION", id,
                    "Suppression de la publication : " + p.getTitre());
        } catch (Exception ignored) {}

        publicationService.deletePublication(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        auditLogService.log("DOWNLOAD", "PUBLICATION", id, "Téléchargement du fichier PDF");
        return publicationService.downloadFile(id);
    }
}