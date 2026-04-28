package com.fst.elearning.controller;

import com.fst.elearning.entity.Cours;
import com.fst.elearning.entity.Lecon;
import com.fst.elearning.entity.Module;
import com.fst.elearning.entity.Niveau;
import com.fst.elearning.entity.Role;
import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.repository.CoursRepository;
import com.fst.elearning.repository.LeconRepository;
import com.fst.elearning.repository.ModuleRepository;
import com.fst.elearning.repository.UtilisateurRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
public class BackOfficeController {

    private final CoursRepository coursRepository;
    private final ModuleRepository moduleRepository;
    private final LeconRepository leconRepository;
    private final UtilisateurRepository utilisateurRepository;

    public BackOfficeController(
            CoursRepository coursRepository,
            ModuleRepository moduleRepository,
            LeconRepository leconRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.coursRepository = coursRepository;
        this.moduleRepository = moduleRepository;
        this.leconRepository = leconRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        populateDashboard(model, "admin");
        return "admin";
    }

    @GetMapping("/manager")
    public String managerPanel(Model model) {
        populateDashboard(model, "manager");
        return "manager";
    }

    @PostMapping("/admin/courses")
    public String createCourse(
            @RequestParam String titre,
            @RequestParam String description,
            @RequestParam String categorie,
            @RequestParam String niveau,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Long formateurId,
            @RequestParam(defaultValue = "true") boolean actif,
            RedirectAttributes redirectAttributes
    ) {
        if (titre == null || titre.isBlank()) {
            redirectAttributes.addFlashAttribute("flashError", "Le titre du cours est obligatoire.");
            return "redirect:/admin";
        }

        Cours cours = new Cours();
        cours.setTitre(titre.trim());
        cours.setDescription(description == null ? "" : description.trim());
        cours.setCategorie(categorie == null ? "" : categorie.trim());
        cours.setNiveau(parseNiveau(niveau));
        cours.setImageUrl(imageUrl == null ? "" : imageUrl.trim());
        cours.setActif(actif);
        cours.setDateCreation(LocalDateTime.now());

        if (formateurId != null) {
            utilisateurRepository.findById(formateurId).ifPresent(cours::setFormateur);
        }

        coursRepository.save(cours);
        redirectAttributes.addFlashAttribute("flashSuccess", "Cours cree avec succes.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/modules")
    public String createModule(
            @RequestParam Long coursId,
            @RequestParam String titre,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "1") int ordre,
            RedirectAttributes redirectAttributes
    ) {
        if (titre == null || titre.isBlank()) {
            redirectAttributes.addFlashAttribute("flashError", "Le titre du module est obligatoire.");
            return "redirect:/admin";
        }

        Cours cours = coursRepository.findById(coursId).orElse(null);
        if (cours == null) {
            redirectAttributes.addFlashAttribute("flashError", "Cours introuvable.");
            return "redirect:/admin";
        }

        Module module = new Module();
        module.setCours(cours);
        module.setTitre(titre.trim());
        module.setDescription(description == null ? "" : description.trim());
        module.setOrdre(Math.max(1, ordre));
        moduleRepository.save(module);

        redirectAttributes.addFlashAttribute("flashSuccess", "Module ajoute.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/lessons")
    public String createAdminLesson(
            @RequestParam Long moduleId,
            @RequestParam String titre,
            @RequestParam String contenu,
            @RequestParam(defaultValue = "1") int ordre,
            @RequestParam(defaultValue = "10") int dureeMin,
            RedirectAttributes redirectAttributes
    ) {
        return saveLesson(moduleId, titre, contenu, ordre, dureeMin, redirectAttributes, "/admin");
    }

    @PostMapping("/manager/lessons")
    public String createManagerLesson(
            @RequestParam Long moduleId,
            @RequestParam String titre,
            @RequestParam String contenu,
            @RequestParam(defaultValue = "1") int ordre,
            @RequestParam(defaultValue = "10") int dureeMin,
            RedirectAttributes redirectAttributes
    ) {
        return saveLesson(moduleId, titre, contenu, ordre, dureeMin, redirectAttributes, "/manager");
    }

    private String saveLesson(
            Long moduleId,
            String titre,
            String contenu,
            int ordre,
            int dureeMin,
            RedirectAttributes redirectAttributes,
            String redirectTarget
    ) {
        if (titre == null || titre.isBlank()) {
            redirectAttributes.addFlashAttribute("flashError", "Le titre de la lecon est obligatoire.");
            return "redirect:" + redirectTarget;
        }

        Module module = moduleRepository.findById(moduleId).orElse(null);
        if (module == null) {
            redirectAttributes.addFlashAttribute("flashError", "Module introuvable.");
            return "redirect:" + redirectTarget;
        }

        Lecon lecon = new Lecon();
        lecon.setModule(module);
        lecon.setTitre(titre.trim());
        lecon.setContenu(contenu == null ? "" : contenu.trim());
        lecon.setOrdre(Math.max(1, ordre));
        lecon.setDureeMin(Math.max(1, dureeMin));
        leconRepository.save(lecon);

        redirectAttributes.addFlashAttribute("flashSuccess", "Lecon ajoutee.");
        return "redirect:" + redirectTarget;
    }

    private void populateDashboard(Model model, String mode) {
        List<Cours> courses = coursRepository.findAllByOrderByTitreAsc();
        List<CourseView> courseViews = courses.stream()
                .map(this::toCourseView)
                .toList();

        long moduleCount = courses.stream()
                .flatMap(course -> course.getModules() == null ? java.util.stream.Stream.empty() : course.getModules().stream())
                .count();
        long lessonCount = courses.stream()
                .flatMap(course -> course.getModules() == null ? java.util.stream.Stream.empty() : course.getModules().stream())
                .flatMap(module -> module.getLecons() == null ? java.util.stream.Stream.empty() : module.getLecons().stream())
                .count();

        model.addAttribute("mode", mode);
        model.addAttribute("stats", new Stats(courses.size(), moduleCount, lessonCount, courses.stream().filter(Cours::isActif).count()));
        model.addAttribute("courses", courseViews);
        model.addAttribute("courseOptions", courses.stream()
                .map(course -> new OptionView(course.getId(), course.getTitre()))
                .toList());
        model.addAttribute("moduleOptions", courses.stream()
                .flatMap(course -> course.getModules() == null ? java.util.stream.Stream.empty() : course.getModules().stream())
                .sorted(Comparator.comparingInt(Module::getOrdre).thenComparing(Module::getTitre, String.CASE_INSENSITIVE_ORDER))
                .map(module -> new OptionView(module.getId(), labelForModule(module)))
                .toList());
        model.addAttribute("formateurs", utilisateurRepository.findByRole(Role.FORMATEUR));
    }

    private CourseView toCourseView(Cours course) {
        List<ModuleView> modules = course.getModules() == null ? List.of() : course.getModules().stream()
                .sorted(Comparator.comparingInt(Module::getOrdre).thenComparing(Module::getTitre, String.CASE_INSENSITIVE_ORDER))
                .map(this::toModuleView)
                .toList();

        return new CourseView(
                course.getId(),
                course.getTitre(),
                course.getCategorie(),
                course.getNiveau() == null ? "" : course.getNiveau().name(),
                course.isActif(),
                course.getFormateur() == null ? "Aucun" : course.getFormateur().getNom(),
                modules
        );
    }

    private ModuleView toModuleView(Module module) {
        List<LessonView> lessons = module.getLecons() == null ? List.of() : module.getLecons().stream()
                .sorted(Comparator.comparingInt(Lecon::getOrdre).thenComparing(Lecon::getTitre, String.CASE_INSENSITIVE_ORDER))
                .map(lesson -> new LessonView(
                        lesson.getId(),
                        lesson.getTitre(),
                        lesson.getOrdre(),
                        lesson.getDureeMin(),
                        preview(lesson.getContenu())
                ))
                .toList();

        return new ModuleView(
                module.getId(),
                module.getTitre(),
                module.getDescription(),
                module.getOrdre(),
                lessons
        );
    }

    private Niveau parseNiveau(String niveau) {
        if (niveau == null || niveau.isBlank()) {
            return Niveau.DEBUTANT;
        }
        try {
            return Niveau.valueOf(niveau.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Niveau.DEBUTANT;
        }
    }

    private String labelForModule(Module module) {
        String courseTitle = module.getCours() == null ? "Cours" : module.getCours().getTitre();
        return courseTitle + " - " + module.getTitre();
    }

    private String preview(String content) {
        if (content == null || content.isBlank()) {
            return "Contenu vide";
        }
        String normalized = content.replace("\n", " ").trim();
        return normalized.length() <= 120 ? normalized : normalized.substring(0, 117) + "...";
    }

    public record Stats(long courseCount, long moduleCount, long lessonCount, long activeCourseCount) {}

    public record OptionView(Long id, String label) {}

    public record CourseView(
            Long id,
            String titre,
            String categorie,
            String niveau,
            boolean actif,
            String formateur,
            List<ModuleView> modules
    ) {}

    public record ModuleView(
            Long id,
            String titre,
            String description,
            int ordre,
            List<LessonView> lecons
    ) {}

    public record LessonView(
            Long id,
            String titre,
            int ordre,
            int dureeMin,
            String preview
    ) {}
}
