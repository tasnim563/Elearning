package com.fst.elearning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CoursController {
    @GetMapping("/catalogue")
    public String catalogue() {
        return "catalogue";
    }

    @GetMapping("/cours/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        model.addAttribute("courseId", id);
        return "course";
    }
}
