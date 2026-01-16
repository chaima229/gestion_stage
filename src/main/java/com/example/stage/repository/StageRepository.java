package com.example.stage.repository;

import com.example.stage.entity.Stage;
import com.example.stage.entity.StageEtat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
    List<Stage> findByEtudiantId(Long etudiantId);
    List<Stage> findByEncadrantId(Long encadrantId);
    List<Stage> findByFiliereId(Long filiereId);
    List<Stage> findByEtat(StageEtat etat);
    List<Stage> findByFiliereIdAndEtat(Long filiereId, StageEtat etat);

    @Query("SELECT s FROM Stage s WHERE s.entreprise LIKE %?1%")
    List<Stage> findByEntreprise(String entreprise);

    @Query("SELECT COUNT(s) FROM Stage s WHERE s.etat = ?1")
    long countByEtat(StageEtat etat);

    @Query("SELECT s.filiereId, COUNT(s) FROM Stage s GROUP BY s.filiereId")
    List<Object[]> countByFiliere();
}
