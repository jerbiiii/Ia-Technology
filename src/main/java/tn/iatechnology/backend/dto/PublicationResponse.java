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
    private String cheminFichier; // URL ou chemin relatif
    private Set<Long> chercheursIds;
    private Set<Long> domainesIds;
}
