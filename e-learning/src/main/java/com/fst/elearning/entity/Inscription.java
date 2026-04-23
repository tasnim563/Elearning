package com.fst.elearning.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Inscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Utilisateur apprenant;

    @ManyToOne
    private Cours cours;

    private LocalDate dateInscription;

    @Enumerated(EnumType.STRING)
    private Statut statut; // EN_COURS, TERMINE, ABANDONNE

    // Getters & Setters
}
