package com.fst.elearning.service;

import com.fst.elearning.repository.ProgressionLeconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgressionService {
    @Autowired
    private ProgressionLeconRepository progressionRepo;

    public double calculerProgression(Long apprenantId, Long coursId) {
        int total = progressionRepo.countByApprenantIdAndLecon_Module_CoursId(apprenantId, coursId);
        int completes = progressionRepo.countByApprenantIdAndLecon_Module_CoursIdAndCompleteeTrue(apprenantId, coursId);
        return total == 0 ? 0 : (double) completes / total * 100;
    }
}
