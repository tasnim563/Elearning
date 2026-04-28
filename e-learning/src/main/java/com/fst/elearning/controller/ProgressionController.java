package com.fst.elearning.controller;

import com.fst.elearning.entity.Cours;
import com.fst.elearning.entity.Inscription;
import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.repository.InscriptionRepository;
import com.fst.elearning.repository.UtilisateurRepository;
import com.fst.elearning.service.ProgressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProgressionController {
    @Autowired
    private ProgressionService progressionService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @GetMapping("/progression")
    public String progression(Model model, Authentication authentication) {
        List<CourseProgress> courseProgresses = new ArrayList<>();
        double globalProgress = 0.0;

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Utilisateur user = utilisateurRepository.findByEmail(email).orElse(null);

            if (user != null) {
                List<Inscription> inscriptions = inscriptionRepository.findByApprenantId(user.getId());
                for (Inscription inscription : inscriptions) {
                    Cours cours = inscription.getCours();
                    if (cours != null) {
                        double progress = progressionService.calculerProgression(user.getId(), cours.getId());
                        courseProgresses.add(new CourseProgress(
                                cours.getId(),
                                cours.getTitre(),
                                cours.getCategorie(),
                                cours.getImageUrl(),
                                progress,
                                inscription.getStatut() != null ? inscription.getStatut().name() : "EN_COURS"
                        ));
                    }
                }
            }
        }

        if (!courseProgresses.isEmpty()) {
            globalProgress = courseProgresses.stream().mapToDouble(CourseProgress::progress).average().orElse(0.0);
        }

        model.addAttribute("progression", globalProgress);
        model.addAttribute("courseProgresses", courseProgresses);
        return "progression";
    }

    @PostMapping("/progression/complete")
    public String completeLesson(
            @RequestParam Long leconId,
            @RequestParam(required = false) String redirectUrl,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Utilisateur user = utilisateurRepository.findByEmail(email).orElse(null);
            if (user != null) {
                boolean saved = progressionService.marquerLeconCompletee(user.getId(), leconId);
                if (saved) {
                    redirectAttributes.addFlashAttribute("flashSuccess", "Lecon marquee comme completee !");
                } else {
                    redirectAttributes.addFlashAttribute("flashError", "Impossible de sauvegarder la progression.");
                }
            }
        }
        return "redirect:" + (redirectUrl != null && !redirectUrl.isBlank() ? redirectUrl : "/progression");
    }

    public record CourseProgress(Long courseId, String title, String category, String imageUrl, double progress, String status) {}
}
