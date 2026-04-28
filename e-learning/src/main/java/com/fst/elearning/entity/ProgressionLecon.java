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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getApprenant() { return apprenant; }
    public void setApprenant(Utilisateur apprenant) { this.apprenant = apprenant; }

    public Lecon getLecon() { return lecon; }
    public void setLecon(Lecon lecon) { this.lecon = lecon; }

    public boolean isCompletee() { return completee; }
    public void setCompletee(boolean completee) { this.completee = completee; }

    public LocalDateTime getDateCompletion() { return dateCompletion; }
    public void setDateCompletion(LocalDateTime dateCompletion) { this.dateCompletion = dateCompletion; }
}
