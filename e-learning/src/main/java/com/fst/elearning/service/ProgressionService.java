package com.fst.elearning.service;

import com.fst.elearning.entity.ProgressionLecon;
import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.entity.Lecon;
import com.fst.elearning.repository.ProgressionLeconRepository;
import com.fst.elearning.repository.UtilisateurRepository;
import com.fst.elearning.repository.LeconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProgressionService {
    @Autowired
    private ProgressionLeconRepository progressionRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private LeconRepository leconRepo;

    public double calculerProgression(Long apprenantId, Long coursId) {
        int total = progressionRepo.countByApprenantIdAndLecon_Module_CoursId(apprenantId, coursId);
        int completes = progressionRepo.countByApprenantIdAndLecon_Module_CoursIdAndCompleteeTrue(apprenantId, coursId);
        return total == 0 ? 0 : (double) completes / total * 100;
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
        return true;
    }
}
