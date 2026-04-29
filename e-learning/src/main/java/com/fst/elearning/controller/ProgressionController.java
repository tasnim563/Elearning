package com.fst.elearning.controller;

import com.fst.elearning.entity.Cours;
import com.fst.elearning.entity.Inscription;
import com.fst.elearning.entity.ProgressionLecon;
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
import java.time.LocalDateTime;

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
        int totalLessons = 0;
        int completedLessons = 0;
        int completedCourses = 0;
        int weeklyCompletedLessons = 0;

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Utilisateur user = utilisateurRepository.findByEmail(email).orElse(null);

            if (user != null) {
                List<Inscription> inscriptions = inscriptionRepository.findByApprenantId(user.getId());
                for (Inscription inscription : inscriptions) {
                    Cours cours = inscription.getCours();
                    if (cours != null) {
                        double progress = progressionService.calculerProgression(user.getId(), cours.getId());
                        int courseTotalLessons = progressionService.compterLecons(cours.getId());
                        int courseCompletedLessons = progressionService.compterLeconsCompletees(user.getId(), cours.getId());
                        String lastLesson = progressionService.derniereLeconCompletee(user.getId(), cours.getId())
                                .map(ProgressionLecon::getLecon)
                                .map(lecon -> lecon.getTitre())
                                .orElse(null);
                        totalLessons += courseTotalLessons;
                        completedLessons += courseCompletedLessons;
                        if (progress >= 100.0) {
                            completedCourses++;
                        }
                        courseProgresses.add(new CourseProgress(
                                cours.getId(),
                                cours.getTitre(),
                                cours.getCategorie(),
                                cours.getImageUrl(),
                                progress,
                                inscription.getStatut() != null ? inscription.getStatut().name() : "EN_COURS",
                                courseCompletedLessons,
                                courseTotalLessons,
                                lastLesson
                        ));
                    }
                }
                LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
                weeklyCompletedLessons = (int) progressionService.leconsCompletees(user.getId()).stream()
                        .filter(progress -> progress.getDateCompletion() != null && progress.getDateCompletion().isAfter(sevenDaysAgo))
                        .count();
            }
        }

        if (!courseProgresses.isEmpty()) {
            globalProgress = courseProgresses.stream().mapToDouble(CourseProgress::progress).average().orElse(0.0);
        }

        model.addAttribute("progression", globalProgress);
        model.addAttribute("courseProgresses", courseProgresses);
        model.addAttribute("totalCourses", courseProgresses.size());
        model.addAttribute("completedCourses", completedCourses);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("completedLessons", completedLessons);
        model.addAttribute("activeCourses", Math.max(0, courseProgresses.size() - completedCourses));
        model.addAttribute("weeklyCompletedLessons", weeklyCompletedLessons);
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
                    redirectAttributes.addFlashAttribute("flashSuccess", "Leçon marquée comme terminée.");
                } else {
                    redirectAttributes.addFlashAttribute("flashError", "Impossible de sauvegarder la progression.");
                }
            }
        }
        return "redirect:" + (redirectUrl != null && !redirectUrl.isBlank() ? redirectUrl : "/progression");
    }

    public record CourseProgress(
            Long courseId,
            String title,
            String category,
            String imageUrl,
            double progress,
            String status,
            int completedLessons,
            int totalLessons,
            String lastLesson
    ) {}
}
