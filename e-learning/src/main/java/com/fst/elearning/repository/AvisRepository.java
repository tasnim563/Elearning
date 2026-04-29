package com.fst.elearning.repository;

import com.fst.elearning.entity.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {
    List<Avis> findByCoursId(Long coursId);
    Optional<Avis> findByApprenantIdAndCoursId(Long apprenantId, Long coursId);
    List<Avis> findByCoursIdOrderByDateAvisDesc(Long coursId);
    
    @Query("SELECT COALESCE(AVG(a.note), 0.0) FROM Avis a WHERE a.cours.id = :coursId")
    double averageNoteByCoursId(@Param("coursId") Long coursId);
    
    long countByCoursId(Long coursId);
}
