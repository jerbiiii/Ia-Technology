package tn.iatechnology.backend.repository;



import tn.iatechnology.backend.entity.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResearcherRepository extends JpaRepository<Researcher, Long> {
    List<Researcher> findByNomContainingIgnoreCase(String nom);
    List<Researcher> findByPrenomContainingIgnoreCase(String prenom);
    List<Researcher> findByDomainePrincipalNomIgnoreCase(String domaineNom);
    // Recherche par nom ou prénom (pour la recherche globale)
    List<Researcher> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}
