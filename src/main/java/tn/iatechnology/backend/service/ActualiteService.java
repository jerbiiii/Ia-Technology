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

    public List<ActualiteDTO> getAllActualites() {
        return actualiteRepository.findAllByOrderByDatePublicationDesc().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    public ActualiteDTO getActualiteById(Long id) {
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec l'id : " + id));
        return convertToDTO(actualite);
    }

    @Transactional
    public ActualiteDTO createActualite(ActualiteDTO actualiteDTO, Long auteurId) {
        User auteur = userRepository.findById(auteurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur auteur non trouvé avec l'id : " + auteurId));

        Actualite actualite = new Actualite();
        actualite.setTitre(actualiteDTO.getTitre());
        actualite.setContenu(actualiteDTO.getContenu());
        actualite.setDatePublication(LocalDateTime.now());
        actualite.setAuteur(auteur);

        Actualite saved = actualiteRepository.save(actualite);
        return convertToDTO(saved);
    }

    @Transactional
    public ActualiteDTO updateActualite(Long id, ActualiteDTO actualiteDTO) {
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec l'id : " + id));
        actualite.setTitre(actualiteDTO.getTitre());
        actualite.setContenu(actualiteDTO.getContenu());
        // On ne change pas la date ni l'auteur
        Actualite updated = actualiteRepository.save(actualite);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteActualite(Long id) {
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec l'id : " + id));
        actualiteRepository.delete(actualite);
    }

    private ActualiteDTO convertToDTO(Actualite actualite) {
        ActualiteDTO dto = new ActualiteDTO();
        dto.setId(actualite.getId());
        dto.setTitre(actualite.getTitre());
        dto.setContenu(actualite.getContenu());
        dto.setDatePublication(actualite.getDatePublication());
        dto.setAuteurId(actualite.getAuteur() != null ? actualite.getAuteur().getId() : null);
        dto.setAuteurNom(actualite.getAuteur() != null ? actualite.getAuteur().getNom() + " " + actualite.getAuteur().getPrenom() : null);
        return dto;
    }
}
