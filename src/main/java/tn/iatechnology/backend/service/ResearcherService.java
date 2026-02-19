package tn.iatechnology.backend.service;


import tn.iatechnology.backend.dto.ResearcherDTO;
import tn.iatechnology.backend.entity.Domain;
import tn.iatechnology.backend.entity.Researcher;
import tn.iatechnology.backend.entity.User;
import tn.iatechnology.backend.repository.DomainRepository;
import tn.iatechnology.backend.repository.ResearcherRepository;
import tn.iatechnology.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResearcherService {

    @Autowired
    private ResearcherRepository researcherRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ResearcherDTO> getAllResearchers() {
        return researcherRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ResearcherDTO getResearcherById(Long id) {
        Researcher researcher = researcherRepository.findById(id).orElseThrow(() -> new RuntimeException("Chercheur non trouvé"));
        return convertToDTO(researcher);
    }

    @Transactional
    public ResearcherDTO createResearcher(ResearcherDTO dto) {
        Researcher researcher = new Researcher();
        researcher.setNom(dto.getNom());
        researcher.setPrenom(dto.getPrenom());
        researcher.setEmail(dto.getEmail());
        researcher.setAffiliation(dto.getAffiliation());

        // Domaine principal
        if (dto.getDomainePrincipalId() != null) {
            Domain domainePrincipal = domainRepository.findById(dto.getDomainePrincipalId())
                    .orElseThrow(() -> new RuntimeException("Domaine non trouvé"));
            researcher.setDomainePrincipal(domainePrincipal);
        }

        // Autres domaines
        if (dto.getAutresDomainesIds() != null) {
            Set<Domain> autresDomaines = new HashSet<>(domainRepository.findAllById(dto.getAutresDomainesIds()));
            researcher.setAutresDomaines(autresDomaines);
        }

        // Lier à un utilisateur existant si userId fourni
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            researcher.setUser(user);
        }

        Researcher saved = researcherRepository.save(researcher);
        return convertToDTO(saved);
    }

    @Transactional
    public ResearcherDTO updateResearcher(Long id, ResearcherDTO dto) {
        Researcher researcher = researcherRepository.findById(id).orElseThrow(() -> new RuntimeException("Chercheur non trouvé"));
        researcher.setNom(dto.getNom());
        researcher.setPrenom(dto.getPrenom());
        researcher.setEmail(dto.getEmail());
        researcher.setAffiliation(dto.getAffiliation());

        if (dto.getDomainePrincipalId() != null) {
            Domain domainePrincipal = domainRepository.findById(dto.getDomainePrincipalId())
                    .orElseThrow(() -> new RuntimeException("Domaine non trouvé"));
            researcher.setDomainePrincipal(domainePrincipal);
        } else {
            researcher.setDomainePrincipal(null);
        }

        if (dto.getAutresDomainesIds() != null) {
            Set<Domain> autresDomaines = new HashSet<>(domainRepository.findAllById(dto.getAutresDomainesIds()));
            researcher.setAutresDomaines(autresDomaines);
        } else {
            researcher.getAutresDomaines().clear();
        }

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            researcher.setUser(user);
        } else {
            researcher.setUser(null);
        }

        Researcher updated = researcherRepository.save(researcher);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteResearcher(Long id) {
        researcherRepository.deleteById(id);
    }

    private ResearcherDTO convertToDTO(Researcher researcher) {
        ResearcherDTO dto = new ResearcherDTO();
        dto.setId(researcher.getId());
        dto.setNom(researcher.getNom());
        dto.setPrenom(researcher.getPrenom());
        dto.setEmail(researcher.getEmail());
        dto.setAffiliation(researcher.getAffiliation());
        if (researcher.getDomainePrincipal() != null) {
            dto.setDomainePrincipalId(researcher.getDomainePrincipal().getId());
            dto.setDomainePrincipalNom(researcher.getDomainePrincipal().getNom());
        }
        dto.setAutresDomainesIds(researcher.getAutresDomaines().stream().map(Domain::getId).collect(Collectors.toSet()));
        if (researcher.getUser() != null) {
            dto.setUserId(researcher.getUser().getId());
        }
        return dto;
    }
    public List<ResearcherDTO> search(String nom, String prenom, String domaine) {
        // Logique similaire
        List<Researcher> researchers;
        if (domaine != null && !domaine.isEmpty()) {
            researchers = researcherRepository.findByDomainePrincipalNomIgnoreCase(domaine);
        } else if (nom != null && !nom.isEmpty() && prenom != null && !prenom.isEmpty()) {
            researchers = researcherRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(nom, prenom);
        } else if (nom != null && !nom.isEmpty()) {
            researchers = researcherRepository.findByNomContainingIgnoreCase(nom);
        } else if (prenom != null && !prenom.isEmpty()) {
            researchers = researcherRepository.findByPrenomContainingIgnoreCase(prenom);
        } else {
            researchers = researcherRepository.findAll();
        }
        return researchers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
