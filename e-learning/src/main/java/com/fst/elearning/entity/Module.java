package com.fst.elearning.entity;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Module {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    private int ordre;

    @ManyToOne
    private Cours cours;

    @OneToMany(mappedBy="module", cascade=CascadeType.ALL)
    private Set<Lecon> lecons = new LinkedHashSet<>();

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public Set<Lecon> getLecons() {
        return lecons;
    }

    public void setLecons(Set<Lecon> lecons) {
        this.lecons = lecons;
    }
}
