package com.fst.elearning.controller;

import com.fst.elearning.entity.Inscription;
import com.fst.elearning.service.InscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class InscriptionController {
    @Autowired
    private InscriptionService inscriptionService;

    @PostMapping("/inscrire")
    public String inscrire(Inscription inscription) {
        inscriptionService.inscrire(inscription);
        return "redirect:/catalogue";
    }
}
