package tn.iatechnology.backend.repository;

import tn.iatechnology.backend.entity.Actualite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActualiteRepository extends JpaRepository<Actualite, Long> {
    // Récupère toutes les actualités triées de la plus récente à la plus ancienne
    List<Actualite> findAllByOrderByDatePublicationDesc();
}
