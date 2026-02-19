package tn.iatechnology.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "researchers")
@Data
@NoArgsConstructor
public class Researcher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String email;

    private String affiliation;

    @ManyToOne
    @JoinColumn(name = "domaine_principal_id")
    private Domain domainePrincipal;

    // Relation avec User (si le chercheur a un compte)
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "researcher_domain",
            joinColumns = @JoinColumn(name = "researcher_id"),
            inverseJoinColumns = @JoinColumn(name = "domain_id"))
    private Set<Domain> autresDomaines = new HashSet<>();

    // Pour les publications, on fera une relation bidirectionnelle plus tard
}
