package tn.iatechnology.backend.repository;

import tn.iatechnology.backend.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DomainRepository extends JpaRepository<Domain, Long> {
    Optional<Domain> findByNom(String nom);
}
