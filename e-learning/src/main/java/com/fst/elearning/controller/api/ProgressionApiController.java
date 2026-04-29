package com.fst.elearning.controller.api;

import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.repository.UtilisateurRepository;
import com.fst.elearning.service.ProgressionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/progress")
public class ProgressionApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProgressionApiController.class);

    private final ProgressionService progressionService;
    private final UtilisateurRepository utilisateurRepository;

    public ProgressionApiController(ProgressionService progressionService, UtilisateurRepository utilisateurRepository) {
        this.progressionService = progressionService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public CompletionDto completeLesson(@PathVariable Long lessonId, Authentication authentication) {
        logger.info("Complete lesson request for lessonId={} authentication={}", lessonId, authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthenticated request to complete lesson lessonId={}", lessonId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Utilisateur user = utilisateurRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        boolean saved = progressionService.marquerLeconCompletee(user.getId(), lessonId);
        if (!saved) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found");
        }

        return new CompletionDto(lessonId, true);
    }

    public record CompletionDto(Long lessonId, boolean completee) {}
}
