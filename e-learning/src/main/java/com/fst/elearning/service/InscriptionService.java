package com.fst.elearning.service;

import com.fst.elearning.entity.Inscription;
import com.fst.elearning.repository.InscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscriptionService {
    @Autowired
    private InscriptionRepository inscriptionRepo;

    public Inscription inscrire(Inscription inscription) {
        return inscriptionRepo.save(inscription);
    }
}
