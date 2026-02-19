package tn.iatechnology.backend.repository;


import tn.iatechnology.backend.entity.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    // Recherche par titre (insensible à la casse, partiel)
    List<Publication> findByTitreContainingIgnoreCase(String titre);

    // Recherche par chercheur (via son ID)
    List<Publication> findByChercheursId(Long chercheurId);

    // Recherche par domaine (via son ID)
    List<Publication> findByDomainesId(Long domaineId);

    // Recherche par nom ou prénom de chercheur (requête JPQL)
    @Query("SELECT p FROM Publication p JOIN p.chercheurs c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))")
    List<Publication> findByChercheurNomOuPrenom(@Param("nom") String nom, @Param("prenom") String prenom);

    // Recherche par mots-clés dans le titre ou le résumé (simplifié)
    @Query("SELECT p FROM Publication p WHERE LOWER(p.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.resume) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Publication> findByKeywordInTitreOrResume(@Param("keyword") String keyword);
}
