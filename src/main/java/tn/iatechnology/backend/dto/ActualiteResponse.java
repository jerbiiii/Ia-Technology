package tn.iatechnology.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActualiteResponse {
    private Long id;
    private String titre;
    private String contenu;
    private LocalDateTime datePublication;
    private String auteurNom; // nom du modérateur
    private boolean actif;
}