package com.fst.elearning.repository;

import com.fst.elearning.entity.Inscription;
import com.fst.elearning.entity.Statut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByApprenantId(Long apprenantId);
    long countByStatut(Statut statut);
    long count();
}
