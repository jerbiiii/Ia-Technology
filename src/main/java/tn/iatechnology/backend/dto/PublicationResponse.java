package tn.iatechnology.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class PublicationResponse {
    private Long id;
    private String titre;
    private String resume;
    private LocalDate datePublication;
    private String doi;
    private String cheminFichier; // nom du fichier ou URL
    private Set<Long> chercheursIds;
    private Set<String> chercheursNoms; // optionnel pour affichage
    private Set<Long> domainesIds;
    private Set<String> domainesNoms;
}