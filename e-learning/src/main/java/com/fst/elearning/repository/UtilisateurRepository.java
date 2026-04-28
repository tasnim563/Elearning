package com.fst.elearning.repository;

import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByRole(Role role);
}
