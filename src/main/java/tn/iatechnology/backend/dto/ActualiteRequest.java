package tn.iatechnology.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActualiteRequest {
    private String titre;
    private String contenu;
    private LocalDateTime datePublication;
    private boolean actif;
}