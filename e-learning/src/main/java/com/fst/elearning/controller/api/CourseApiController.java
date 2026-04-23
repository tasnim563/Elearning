package com.fst.elearning.controller.api;

import com.fst.elearning.entity.Cours;
import com.fst.elearning.entity.Lecon;
import com.fst.elearning.entity.Module;
import com.fst.elearning.repository.CoursRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseApiController {

    private final CoursRepository coursRepository;

    public CourseApiController(CoursRepository coursRepository) {
        this.coursRepository = coursRepository;
    }

    @GetMapping
    public List<CourseCardDto> listCourses() {
        return coursRepository.findAll().stream()
                .sorted(Comparator.comparing(Cours::getId))
                .map(CourseApiController::toCardDto)
                .toList();
    }

    @GetMapping("/{id}")
    public CourseDetailDto getCourse(@PathVariable Long id) {
        Cours cours = coursRepository.findWithModulesById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

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
                                        lesson.getDureeMin()
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
            int dureeMin
    ) {}
}

