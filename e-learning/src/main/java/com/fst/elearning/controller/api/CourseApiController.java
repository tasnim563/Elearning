package com.fst.elearning.controller.api;

import com.fst.elearning.entity.Cours;
import com.fst.elearning.entity.Lecon;
import com.fst.elearning.entity.Module;
import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.repository.CoursRepository;
import com.fst.elearning.repository.UtilisateurRepository;
import com.fst.elearning.service.ProgressionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseApiController {

    private final CoursRepository coursRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProgressionService progressionService;

    public CourseApiController(
            CoursRepository coursRepository,
            UtilisateurRepository utilisateurRepository,
            ProgressionService progressionService) {
        this.coursRepository = coursRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.progressionService = progressionService;
    }

    @GetMapping
    public List<CourseCardDto> listCourses() {
        return coursRepository.findAll().stream()
                .sorted(Comparator.comparing(Cours::getId))
                .map(CourseApiController::toCardDto)
                .toList();
    }

    @GetMapping("/{id}")
    public CourseDetailDto getCourse(@PathVariable Long id, Authentication authentication) {
        Cours cours = coursRepository.findWithModulesById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        Set<Long> completedLessonIds = completedLessonIds(authentication);

        List<ModuleDto> modules = cours.getModules() == null ? List.of() : cours.getModules().stream()
                .sorted(Comparator.comparingInt(Module::getOrdre))
                .map(module -> new ModuleDto(
                        module.getId(),
                        module.getTitre(),
                        module.getDescription(),
                        module.getOrdre(),
                        module.getLecons() == null ? List.of() : module.getLecons().stream()
                                .sorted(Comparator.comparingInt(Lecon::getOrdre))
                                .map(lesson -> new LessonDto(
                                        lesson.getId(),
                                        lesson.getTitre(),
                                        lesson.getContenu(),
                                        lesson.getOrdre(),
                                        lesson.getDureeMin(),
                                        completedLessonIds.contains(lesson.getId())
                                ))
                                .toList()
                ))
                .toList();

        return new CourseDetailDto(
                cours.getId(),
                cours.getTitre(),
                cours.getDescription(),
                cours.getCategorie(),
                cours.getNiveau() == null ? null : cours.getNiveau().name(),
                cours.getImageUrl(),
                modules
        );
    }

    private Set<Long> completedLessonIds(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Set.of();
        }

        return utilisateurRepository.findByEmail(authentication.getName())
                .map(Utilisateur::getId)
                .map(progressionService::leconsCompletees)
                .orElse(List.of())
                .stream()
                .filter(progress -> progress.getLecon() != null)
                .map(progress -> progress.getLecon().getId())
                .collect(Collectors.toSet());
    }

    private static CourseCardDto toCardDto(Cours cours) {
        if (cours == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid course");
        }
        return new CourseCardDto(
                cours.getId(),
                cours.getTitre(),
                cours.getDescription(),
                cours.getCategorie(),
                cours.getNiveau() == null ? null : cours.getNiveau().name(),
                cours.getImageUrl()
        );
    }

    public record CourseCardDto(
            Long id,
            String titre,
            String description,
            String categorie,
            String niveau,
            String imageUrl
    ) {}

    public record CourseDetailDto(
            Long id,
            String titre,
            String description,
            String categorie,
            String niveau,
            String imageUrl,
            List<ModuleDto> modules
    ) {}

    public record ModuleDto(
            Long id,
            String titre,
            String description,
            int ordre,
            List<LessonDto> lecons
    ) {}

    public record LessonDto(
            Long id,
            String titre,
            String contenu,
            int ordre,
            int dureeMin,
            boolean completee
    ) {}
}
