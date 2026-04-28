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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getApprenant() { return apprenant; }
    public void setApprenant(Utilisateur apprenant) { this.apprenant = apprenant; }

    public Cours getCours() { return cours; }
    public void setCours(Cours cours) { this.cours = cours; }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
}
