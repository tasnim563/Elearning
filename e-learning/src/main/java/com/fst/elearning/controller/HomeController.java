package com.fst.elearning.controller;

import com.fst.elearning.repository.CoursRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CoursRepository coursRepository;

    public HomeController(CoursRepository coursRepository) {
        this.coursRepository = coursRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        Pageable featuredPage = PageRequest.of(0, 3);
        model.addAttribute("totalCourses", coursRepository.count());
        model.addAttribute("featuredCourses", coursRepository.findAll(featuredPage).getContent());
        return "home";
    }
}
