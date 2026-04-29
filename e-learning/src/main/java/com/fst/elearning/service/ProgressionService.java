package com.fst.elearning.service;

import com.fst.elearning.entity.Inscription;
import com.fst.elearning.entity.ProgressionLecon;
import com.fst.elearning.entity.Statut;
import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.entity.Lecon;
import com.fst.elearning.repository.InscriptionRepository;
import com.fst.elearning.repository.CoursRepository;
import com.fst.elearning.repository.ProgressionLeconRepository;
import com.fst.elearning.repository.UtilisateurRepository;
import com.fst.elearning.repository.LeconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressionService {
    @Autowired
    private ProgressionLeconRepository progressionRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private LeconRepository leconRepo;

    @Autowired
    private InscriptionRepository inscriptionRepo;

    @Autowired
    private CoursRepository coursRepo;

    public double calculerProgression(Long apprenantId, Long coursId) {
        int total = leconRepo.countByModule_CoursId(coursId);
        int completes = progressionRepo.countByApprenantIdAndLecon_Module_CoursIdAndCompleteeTrue(apprenantId, coursId);
        return total == 0 ? 0 : (double) completes / total * 100;
    }

    public int compterLecons(Long coursId) {
        return leconRepo.countByModule_CoursId(coursId);
    }

    public int compterLeconsCompletees(Long apprenantId, Long coursId) {
        return progressionRepo.countByApprenantIdAndLecon_Module_CoursIdAndCompleteeTrue(apprenantId, coursId);
    }

    public int compterToutesLeconsCompletees(Long apprenantId) {
        return progressionRepo.countByApprenantIdAndCompleteeTrue(apprenantId);
    }

    public Optional<ProgressionLecon> derniereLeconCompletee(Long apprenantId, Long coursId) {
        return progressionRepo.findTopByApprenantIdAndLecon_Module_CoursIdAndCompleteeTrueOrderByDateCompletionDesc(apprenantId, coursId);
    }

    public List<ProgressionLecon> leconsCompletees(Long apprenantId) {
        return progressionRepo.findByApprenantIdAndCompleteeTrue(apprenantId);
    }

    public boolean marquerLeconCompletee(Long apprenantId, Long leconId) {
        Optional<ProgressionLecon> existing = progressionRepo.findByApprenantIdAndLeconId(apprenantId, leconId);
        if (existing.isPresent()) {
            ProgressionLecon pl = existing.get();
            if (!pl.isCompletee()) {
                pl.setCompletee(true);
                pl.setDateCompletion(LocalDateTime.now());
                progressionRepo.save(pl);
            }
            if (pl.getLecon() != null && pl.getLecon().getModule() != null && pl.getLecon().getModule().getCours() != null) {
                mettreAJourStatutInscription(apprenantId, pl.getLecon().getModule().getCours().getId());
            }
            return true;
        }

        Optional<Utilisateur> apprenant = utilisateurRepo.findById(apprenantId);
        Optional<Lecon> lecon = leconRepo.findById(leconId);

        if (apprenant.isEmpty() || lecon.isEmpty()) {
            return false;
        }

        ProgressionLecon pl = new ProgressionLecon();
        pl.setApprenant(apprenant.get());
        pl.setLecon(lecon.get());
        pl.setCompletee(true);
        pl.setDateCompletion(LocalDateTime.now());
        progressionRepo.save(pl);
        mettreAJourStatutInscription(apprenantId, lecon.get().getModule().getCours().getId());
        return true;
    }

    private void mettreAJourStatutInscription(Long apprenantId, Long coursId) {
        int total = compterLecons(coursId);
        if (total == 0) {
            return;
        }

        double progression = calculerProgression(apprenantId, coursId);
        Optional<Inscription> existing = inscriptionRepo.findByApprenantId(apprenantId).stream()
                .filter(inscription -> inscription.getCours() != null && coursId.equals(inscription.getCours().getId()))
                .findFirst();

        Inscription inscription = existing.orElseGet(() -> {
            Optional<com.fst.elearning.entity.Cours> cours = coursRepo.findById(coursId);
            if (cours.isEmpty()) {
                return null;
            }
            Inscription created = new Inscription();
            utilisateurRepo.findById(apprenantId).ifPresent(created::setApprenant);
            created.setCours(cours.get());
            created.setDateInscription(LocalDate.now());
            return created;
        });

        if (inscription != null) {
            inscription.setStatut(progression >= 100.0 ? Statut.TERMINE : Statut.EN_COURS);
            inscriptionRepo.save(inscription);
        }
    }
}
