package com.fst.elearning.controller;

import com.fst.elearning.service.CertificateService;
import com.fst.elearning.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/certificate")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/download/{coursId}")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable Long coursId,
            Authentication authentication) throws IOException {
        
        String username = authentication.getName();
        Long apprenantId = utilisateurRepository.findByEmail(username)
                .map(com.fst.elearning.entity.Utilisateur::getId)
                .orElse(null);
        
        if (apprenantId == null || !certificateService.canGenerateCertificate(apprenantId, coursId)) {
            return ResponseEntity.badRequest().build();
        }

        byte[] pdfBytes = certificateService.generateCertificatePdf(apprenantId, coursId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificat-completion.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
