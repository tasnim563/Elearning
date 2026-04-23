package com.fst.elearning.controller;

import com.fst.elearning.service.ProgressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProgressionController {
    @Autowired
    private ProgressionService progressionService;

    @GetMapping("/progression")
    public String progression(Model model) {
        double progression = progressionService.calculerProgression(1L, 1L); // test avec apprenant 1 et cours 1
        model.addAttribute("progression", progression);
        return "progression";
    }
}
