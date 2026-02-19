package tn.iatechnology.backend.service;


import tn.iatechnology.backend.dto.DomainDTO;
import tn.iatechnology.backend.entity.Domain;
import tn.iatechnology.backend.repository.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DomainService {

    @Autowired
    private DomainRepository domainRepository;

    public List<DomainDTO> getAllDomains() {
        return domainRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public DomainDTO getDomainById(Long id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Domaine non trouvé avec l'id : " + id));
        return convertToDTO(domain);
    }

    @Transactional
    public DomainDTO createDomain(DomainDTO domainDTO) {
        Domain domain = new Domain();
        domain.setNom(domainDTO.getNom());
        domain.setDescription(domainDTO.getDescription());
        Domain saved = domainRepository.save(domain);
        return convertToDTO(saved);
    }

    @Transactional
    public DomainDTO updateDomain(Long id, DomainDTO domainDTO) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Domaine non trouvé avec l'id : " + id));
        domain.setNom(domainDTO.getNom());
        domain.setDescription(domainDTO.getDescription());
        Domain updated = domainRepository.save(domain);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteDomain(Long id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Domaine non trouvé avec l'id : " + id));
        domainRepository.delete(domain);
    }

    private DomainDTO convertToDTO(Domain domain) {
        DomainDTO dto = new DomainDTO();
        dto.setId(domain.getId());
        dto.setNom(domain.getNom());
        dto.setDescription(domain.getDescription());
        return dto;
    }
}
