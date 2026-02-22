package tn.iatechnology.backend.service;

import tn.iatechnology.backend.dto.ActualiteDTO;
import tn.iatechnology.backend.entity.Actualite;
import tn.iatechnology.backend.entity.User;
import tn.iatechnology.backend.repository.ActualiteRepository;
import tn.iatechnology.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActualiteService {

    @Autowired
    private ActualiteRepository actualiteRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ActualiteDTO> getAll() {
        return actualiteRepository.findAllByOrderByDatePublicationDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ActualiteDTO getById(Long id) {
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée : " + id));
        return convertToDTO(actualite);
    }


    @Transactional
    public ActualiteDTO create(ActualiteDTO dto) {
        Actualite actualite = new Actualite();
        actualite.setTitre(dto.getTitre());
        actualite.setContenu(dto.getContenu());
        actualite.setDatePublication(LocalDateTime.now());

        // Rattacher l'auteur si l'ID est fourni
        if (dto.getAuteurId() != null) {
            User auteur = userRepository.findById(dto.getAuteurId())
                    .orElseThrow(() -> new RuntimeException("Auteur non trouvé : " + dto.getAuteurId()));
            actualite.setAuteur(auteur);
        }

        return convertToDTO(actualiteRepository.save(actualite));
    }

    @Transactional
    public ActualiteDTO update(Long id, ActualiteDTO dto) {
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée : " + id));

        actualite.setTitre(dto.getTitre());
        actualite.setContenu(dto.getContenu());

        return convertToDTO(actualiteRepository.save(actualite));
    }

    @Transactional
    public void delete(Long id) {
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée : " + id));
        actualiteRepository.delete(actualite);
    }

    private ActualiteDTO convertToDTO(Actualite actualite) {
        ActualiteDTO dto = new ActualiteDTO();
        dto.setId(actualite.getId());
        dto.setTitre(actualite.getTitre());
        dto.setContenu(actualite.getContenu());
        dto.setDatePublication(actualite.getDatePublication());
        dto.setAuteurId(actualite.getAuteur() != null ? actualite.getAuteur().getId() : null);
        dto.setAuteurNom(actualite.getAuteur() != null
                ? actualite.getAuteur().getNom() + " " + actualite.getAuteur().getPrenom()
                : null);
        return dto;
    }
}