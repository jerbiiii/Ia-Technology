package tn.iatechnology.backend.controller;

import tn.iatechnology.backend.dto.PublicationRequest;
import tn.iatechnology.backend.dto.PublicationResponse;
import tn.iatechnology.backend.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public List<PublicationResponse> getAllPublications() {
        return publicationService.getAllPublications();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public ResponseEntity<PublicationResponse> getPublicationById(@PathVariable Long id) {
        PublicationResponse publication = publicationService.getPublicationById(id);
        return ResponseEntity.ok(publication);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<PublicationResponse> createPublication(
            @RequestPart("publication") PublicationRequest request,
            @RequestPart(value = "fichier", required = false) MultipartFile fichier) throws IOException {
        PublicationResponse created = publicationService.createPublication(request, fichier);
        return ResponseEntity.ok(created);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR')")
    public ResponseEntity<PublicationResponse> updatePublication(
            @PathVariable Long id,
            @RequestPart("publication") PublicationRequest request,
            @RequestPart(value = "fichier", required = false) MultipartFile fichier) throws IOException {
        PublicationResponse updated = publicationService.updatePublication(id, request, fichier);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePublication(@PathVariable Long id) throws IOException {
        publicationService.deletePublication(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        return publicationService.downloadFile(id);
    }
}