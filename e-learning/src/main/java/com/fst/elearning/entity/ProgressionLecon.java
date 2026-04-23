package com.fst.elearning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ProgressionLecon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Utilisateur apprenant;

    @ManyToOne
    private Lecon lecon;

    private boolean completee;
    private LocalDateTime dateCompletion;

    // Getters & Setters
}
