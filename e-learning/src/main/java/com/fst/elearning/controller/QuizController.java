package com.fst.elearning.controller;

import com.fst.elearning.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/{quizId}/soumettre")
    public String soumettreQuiz(@PathVariable Long quizId,
                                @RequestParam("reponses") int[] reponses,
                                Model model) {
        // Appel du service sans apprenantId
        int score = quizService.corrigerQuiz(quizId, reponses);

        // Envoi du score vers la vue
        model.addAttribute("score", score);
        return "quizResult";
    }
}
