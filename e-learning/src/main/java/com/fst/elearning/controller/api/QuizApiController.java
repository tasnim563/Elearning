package com.fst.elearning.controller.api;

import com.fst.elearning.entity.Question;
import com.fst.elearning.entity.Quiz;
import com.fst.elearning.repository.QuizRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizApiController {

    private final QuizRepository quizRepository;

    public QuizApiController(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @GetMapping("/{lessonId}")
    public QuizDto getQuizByLesson(@PathVariable Long lessonId) {
        Quiz quiz = quizRepository.findByLeconId(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        List<QuestionDto> questions = quiz.getQuestions() == null ? List.of() : quiz.getQuestions().stream()
                .sorted(Comparator.comparing(Question::getId))
                .map(question -> new QuestionDto(
                        question.getId(),
                        question.getIntitule(),
                        question.getChoix() == null ? List.of() : question.getChoix(),
                        question.getBonneReponse(),
                        question.getExplication()
                ))
                .toList();

        return new QuizDto(
                quiz.getId(),
                quiz.getTitre(),
                lessonId,
                questions
        );
    }

    public record QuizDto(
            Long id,
            String titre,
            Long lessonId,
            List<QuestionDto> questions
    ) {}

    public record QuestionDto(
            Long id,
            String intitule,
            List<String> choix,
            int bonneReponse,
            String explication
    ) {}
}

