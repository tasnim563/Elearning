package com.fst.elearning.config;

import com.fst.elearning.entity.Role;
import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataSeeder {

    @Bean
    CommandLineRunner seedTestUser(UtilisateurRepository utilisateurRepository) {
        return args -> {
            String email = "test@elearning.local";
            if (utilisateurRepository.findByEmail(email).isPresent()) {
                return;
            }

            Utilisateur u = new Utilisateur();
            u.setNom("Compte Test");
            u.setEmail(email);
            u.setMotDePasse("{noop}test1234");
            u.setRole(Role.APPRENANT);

            utilisateurRepository.save(u);
        };
    }
}
