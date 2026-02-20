package tn.iatechnology.backend.repository;


import tn.iatechnology.backend.entity.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HighlightRepository extends JpaRepository<Highlight, Long> {
    List<Highlight> findByActifTrueOrderByDateCreationDesc();
}
