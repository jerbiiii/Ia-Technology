package tn.iatechnology.backend.service;


import tn.iatechnology.backend.dto.PublicationRequest;
import tn.iatechnology.backend.dto.PublicationResponse;
import tn.iatechnology.backend.entity.Domain;
import tn.iatechnology.backend.entity.Publication;
import tn.iatechnology.backend.entity.Researcher;
import tn.iatechnology.backend.repository.DomainRepository;
import tn.iatechnology.backend.repository.PublicationRepository;
import tn.iatechnology.backend.repository.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PublicationService {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private ResearcherRepository researcherRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<PublicationResponse> getAllPublications() {
        return publicationRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public PublicationResponse getPublicationById(Long id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée avec l'id : " + id));
        return convertToResponse(publication);
    }

    @Transactional
    public PublicationResponse createPublication(PublicationRequest request, MultipartFile fichier) throws IOException {
        Publication publication = new Publication();
        publication.setTitre(request.getTitre());
        publication.setResume(request.getResume());
        publication.setDatePublication(request.getDatePublication());
        publication.setDoi(request.getDoi());

        // Gestion des chercheurs
        if (request.getChercheursIds() != null && !request.getChercheursIds().isEmpty()) {
            Set<Researcher> chercheurs = new HashSet<>(researcherRepository.findAllById(request.getChercheursIds()));
            publication.setChercheurs(chercheurs);
        }

        // Gestion des domaines
        if (request.getDomainesIds() != null && !request.getDomainesIds().isEmpty()) {
            Set<Domain> domaines = new HashSet<>(domainRepository.findAllById(request.getDomainesIds()));
            publication.setDomaines(domaines);
        }

        // Gestion du fichier
        if (fichier != null && !fichier.isEmpty()) {
            String fileName = fileStorageService.storeFile(fichier);
            publication.setCheminFichier(fileName);
        }

        Publication saved = publicationRepository.save(publication);
        return convertToResponse(saved);
    }

    @Transactional
    public PublicationResponse updatePublication(Long id, PublicationRequest request, MultipartFile fichier) throws IOException {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée avec l'id : " + id));

        publication.setTitre(request.getTitre());
        publication.setResume(request.getResume());
        publication.setDatePublication(request.getDatePublication());
        publication.setDoi(request.getDoi());

        // Mise à jour des chercheurs
        if (request.getChercheursIds() != null) {
            Set<Researcher> chercheurs = new HashSet<>(researcherRepository.findAllById(request.getChercheursIds()));
            publication.setChercheurs(chercheurs);
        } else {
            publication.getChercheurs().clear();
        }

        // Mise à jour des domaines
        if (request.getDomainesIds() != null) {
            Set<Domain> domaines = new HashSet<>(domainRepository.findAllById(request.getDomainesIds()));
            publication.setDomaines(domaines);
        } else {
            publication.getDomaines().clear();
        }

        // Gestion du fichier : si un nouveau fichier est fourni, on supprime l'ancien et on enregistre le nouveau
        if (fichier != null && !fichier.isEmpty()) {
            // Supprimer l'ancien fichier si existant
            if (publication.getCheminFichier() != null) {
                fileStorageService.deleteFile(publication.getCheminFichier());
            }
            String fileName = fileStorageService.storeFile(fichier);
            publication.setCheminFichier(fileName);
        }

        Publication updated = publicationRepository.save(publication);
        return convertToResponse(updated);
    }

    @Transactional
    public void deletePublication(Long id) throws IOException {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée avec l'id : " + id));
        // Supprimer le fichier associé
        if (publication.getCheminFichier() != null) {
            fileStorageService.deleteFile(publication.getCheminFichier());
        }
        publicationRepository.delete(publication);
    }

    public ResponseEntity<Resource> downloadFile(Long id) throws IOException {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée avec l'id : " + id));
        String fileName = publication.getCheminFichier();
        if (fileName == null) {
            throw new RuntimeException("Aucun fichier associé à cette publication");
        }
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private PublicationResponse convertToResponse(Publication publication) {
        PublicationResponse response = new PublicationResponse();
        response.setId(publication.getId());
        response.setTitre(publication.getTitre());
        response.setResume(publication.getResume());
        response.setDatePublication(publication.getDatePublication());
        response.setDoi(publication.getDoi());
        response.setCheminFichier(publication.getCheminFichier());
        response.setChercheursIds(publication.getChercheurs().stream().map(Researcher::getId).collect(Collectors.toSet()));
        response.setDomainesIds(publication.getDomaines().stream().map(Domain::getId).collect(Collectors.toSet()));
        return response;
    }
    public List<PublicationResponse> search(String keyword, Long domaineId, Long chercheurId) {
        // Logique de recherche combinée
        // Si plusieurs critères, on peut faire des intersections
        // Pour simplifier, on va implémenter des recherches séparées et combiner
        Set<Publication> result = new HashSet<>();
        if (keyword != null && !keyword.isEmpty()) {
            result.addAll(publicationRepository.findByKeywordInTitreOrResume(keyword));
        }
        if (domaineId != null) {
            result.addAll(publicationRepository.findByDomainesId(domaineId));
        }
        if (chercheurId != null) {
            result.addAll(publicationRepository.findByChercheursId(chercheurId));
        }
        // Si aucun critère, retourner tout
        if (result.isEmpty() && keyword == null && domaineId == null && chercheurId == null) {
            return getAllPublications();
        }
        return result.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
}
