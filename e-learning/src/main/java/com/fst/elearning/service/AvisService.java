package com.fst.elearning.service;

import com.fst.elearning.entity.Avis;
import com.fst.elearning.entity.Cours;
import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.repository.AvisRepository;
import com.fst.elearning.repository.CoursRepository;
import com.fst.elearning.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AvisService {
    @Autowired
    private AvisRepository avisRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CoursRepository coursRepository;

    public Avis createAvis(Long apprenantId, Long coursId, int note, String commentaire) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }

        Optional<Utilisateur> apprenant = utilisateurRepository.findById(apprenantId);
        Optional<Cours> cours = coursRepository.findById(coursId);

        if (apprenant.isEmpty() || cours.isEmpty()) {
            throw new IllegalArgumentException("Apprenant ou cours introuvable");
        }

        Avis avis = new Avis();
        avis.setApprenant(apprenant.get());
        avis.setCours(cours.get());
        avis.setNote(note);
        avis.setCommentaire(commentaire);
        avis.setDateAvis(LocalDateTime.now());

        return avisRepository.save(avis);
    }

    public Avis updateAvis(Long avisId, int note, String commentaire) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }

        Optional<Avis> avisOpt = avisRepository.findById(avisId);
        if (avisOpt.isEmpty()) {
            throw new IllegalArgumentException("Avis introuvable");
        }

        Avis avis = avisOpt.get();
        avis.setNote(note);
        avis.setCommentaire(commentaire);
        avis.setDateAvis(LocalDateTime.now());

        return avisRepository.save(avis);
    }

    public List<Avis> getAvisByCours(Long coursId) {
        return avisRepository.findByCoursIdOrderByDateAvisDesc(coursId);
    }

    public Optional<Avis> getAvisByApprenantAndCours(Long apprenantId, Long coursId) {
        return avisRepository.findByApprenantIdAndCoursId(apprenantId, coursId);
    }

    public double getAverageNote(Long coursId) {
        Double avg = avisRepository.averageNoteByCoursId(coursId);
        return avg != null ? avg : 0.0;
    }

    public long getTotalAvis(Long coursId) {
        return avisRepository.countByCoursId(coursId);
    }

    public void deleteAvis(Long avisId) {
        avisRepository.deleteById(avisId);
    }
}
