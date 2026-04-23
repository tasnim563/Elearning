package com.fst.elearning.repository;

import com.fst.elearning.entity.Cours;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CoursRepository extends JpaRepository<Cours, Long> {
    Page<Cours> findByTitreContaining(String titre, Pageable pageable);

    @EntityGraph(attributePaths = {"modules", "modules.lecons"})
    Optional<Cours> findWithModulesById(Long id);
}
