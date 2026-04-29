package com.fst.elearning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Avis {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Utilisateur apprenant;

    @ManyToOne
    private Cours cours;

    private int note; // 1-5 stars
    @Column(columnDefinition = "TEXT")
    private String commentaire;
    private LocalDateTime dateAvis;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getApprenant() { return apprenant; }
    public void setApprenant(Utilisateur apprenant) { this.apprenant = apprenant; }

    public Cours getCours() { return cours; }
    public void setCours(Cours cours) { this.cours = cours; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public LocalDateTime getDateAvis() { return dateAvis; }
    public void setDateAvis(LocalDateTime dateAvis) { this.dateAvis = dateAvis; }
}
