package tn.iatechnology.backend.controller;

import tn.iatechnology.backend.dto.DomainDTO;
import tn.iatechnology.backend.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/domains")
public class DomainController {

    @Autowired
    private DomainService domainService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public List<DomainDTO> getAllDomains() {
        return domainService.getAllDomains();
    }

    @GetMapping("/roots")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public List<DomainDTO> getRootDomains() {
        return domainService.getRootDomains();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public ResponseEntity<DomainDTO> getDomainById(@PathVariable Long id) {
        DomainDTO domain = domainService.getDomainById(id);
        return ResponseEntity.ok(domain);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DomainDTO> createDomain(@RequestBody DomainDTO domainDTO) {
        DomainDTO created = domainService.createDomain(domainDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DomainDTO> updateDomain(@PathVariable Long id, @RequestBody DomainDTO domainDTO) {
        DomainDTO updated = domainService.updateDomain(id, domainDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDomain(@PathVariable Long id) {
        domainService.deleteDomain(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATEUR') or hasRole('UTILISATEUR')")
    public List<DomainDTO> searchDomains(@RequestParam String keyword) {
        return domainService.searchDomains(keyword);
    }
}