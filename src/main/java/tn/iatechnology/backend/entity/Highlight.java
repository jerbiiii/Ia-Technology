package tn.iatechnology.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "highlights")
@Data
public class Highlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    @Column(length = 1000)
    private String description;
    private String imageUrl;
    private LocalDateTime dateCreation;
    private boolean actif;
}
