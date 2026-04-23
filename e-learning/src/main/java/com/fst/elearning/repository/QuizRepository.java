package com.fst.elearning.repository;

import com.fst.elearning.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByLeconId(Long leconId);
}
