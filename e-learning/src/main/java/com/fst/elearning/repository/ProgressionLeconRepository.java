package com.fst.elearning.repository;

import com.fst.elearning.entity.ProgressionLecon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressionLeconRepository extends JpaRepository<ProgressionLecon, Long> {
    int countByApprenantIdAndLecon_Module_CoursId(Long apprenantId, Long coursId);
    int countByApprenantIdAndLecon_Module_CoursIdAndCompleteeTrue(Long apprenantId, Long coursId);
    int countByApprenantIdAndCompleteeTrue(Long apprenantId);
    List<ProgressionLecon> findByApprenantIdAndCompleteeTrue(Long apprenantId);
    Optional<ProgressionLecon> findTopByApprenantIdAndLecon_Module_CoursIdAndCompleteeTrueOrderByDateCompletionDesc(Long apprenantId, Long coursId);
    Optional<ProgressionLecon> findByApprenantIdAndLeconId(Long apprenantId, Long leconId);
}
