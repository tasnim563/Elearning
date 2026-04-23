package com.fst.elearning.service;

import com.fst.elearning.entity.Quiz;
import com.fst.elearning.entity.Question;
import com.fst.elearning.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepo;

    // Méthode pour corriger un quiz
    public int corrigerQuiz(Long quizId, int[] reponses) {
        Quiz quiz = quizRepo.findById(quizId).orElseThrow();
        List<Question> questions = quiz.getQuestions();

        // Vérification : nombre de réponses doit correspondre au nombre de questions
        if (reponses.length != questions.size()) {
            throw new IllegalArgumentException("Nombre de réponses incorrect !");
        }

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            if (q.getBonneReponse() == reponses[i]) {
                score++;
            }
        }
        return score;
    }
}
