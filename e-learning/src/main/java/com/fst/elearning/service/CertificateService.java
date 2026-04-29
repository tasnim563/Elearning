package com.fst.elearning.service;

import com.fst.elearning.entity.Cours;
import com.fst.elearning.entity.Inscription;
import com.fst.elearning.entity.Utilisateur;
import com.fst.elearning.repository.CoursRepository;
import com.fst.elearning.repository.InscriptionRepository;
import com.fst.elearning.repository.UtilisateurRepository;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CertificateService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    public byte[] generateCertificatePdf(Long apprenantId, Long coursId) throws IOException {
        Optional<Utilisateur> apprenant = utilisateurRepository.findById(apprenantId);
        Optional<Cours> cours = coursRepository.findById(coursId);

        if (apprenant.isEmpty() || cours.isEmpty()) {
            throw new IllegalArgumentException("Apprenant ou cours introuvable");
        }

        String htmlContent = generateCertificateHtml(apprenant.get(), cours.get());
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlContent, outputStream);
        
        return outputStream.toByteArray();
    }

    public String generateCertificateHtml(Utilisateur apprenant, Cours cours) {
        LocalDate completionDate = LocalDate.now();
        String formattedDate = completionDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale.FRENCH));

        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <title>Certificat de Complétion</title>" +
                "    <style>" +
                "        body {" +
                "            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;" +
                "            margin: 0;" +
                "            padding: 40px;" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                "            min-height: 100vh;" +
                "        }" +
                "        .certificate {" +
                "            max-width: 800px;" +
                "            margin: 0 auto;" +
                "            background: white;" +
                "            padding: 60px;" +
                "            border-radius: 20px;" +
                "            box-shadow: 0 20px 60px rgba(0,0,0,0.3);" +
                "            text-align: center;" +
                "        }" +
                "        .certificate-header {" +
                "            border-bottom: 3px solid #667eea;" +
                "            padding-bottom: 30px;" +
                "            margin-bottom: 40px;" +
                "        }" +
                "        .certificate-header h1 {" +
                "            color: #667eea;" +
                "            font-size: 48px;" +
                "            margin: 0;" +
                "            font-weight: 800;" +
                "        }" +
                "        .certificate-body {" +
                "            margin: 40px 0;" +
                "        }" +
                "        .certificate-body h2 {" +
                "            color: #333;" +
                "            font-size: 32px;" +
                "            margin: 0 0 20px 0;" +
                "        }" +
                "        .certificate-body p {" +
                "            color: #666;" +
                "            font-size: 18px;" +
                "            line-height: 1.8;" +
                "            margin: 10px 0;" +
                "        }" +
                "        .student-name {" +
                "            font-size: 36px;" +
                "            font-weight: 700;" +
                "            color: #764ba2;" +
                "            margin: 30px 0;" +
                "            padding: 20px;" +
                "            background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);" +
                "            border-radius: 10px;" +
                "        }" +
                "        .course-name {" +
                "            font-size: 28px;" +
                "            font-weight: 600;" +
                "            color: #667eea;" +
                "            margin: 20px 0;" +
                "        }" +
                "        .certificate-footer {" +
                "            margin-top: 60px;" +
                "            padding-top: 30px;" +
                "            border-top: 2px solid #eee;" +
                "        }" +
                "        .certificate-footer p {" +
                "            color: #888;" +
                "            font-size: 16px;" +
                "            margin: 5px 0;" +
                "        }" +
                "        .badge {" +
                "            display: inline-block;" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                "            color: white;" +
                "            padding: 10px 30px;" +
                "            border-radius: 50px;" +
                "            font-size: 14px;" +
                "            font-weight: 600;" +
                "            margin-bottom: 20px;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='certificate'>" +
                "        <div class='certificate-header'>" +
                "            <span class='badge'>CERTIFICAT DE COMPLÉTION</span>" +
                "            <h1>e-learn Fst</h1>" +
                "        </div>" +
                "        <div class='certificate-body'>" +
                "            <h2>Certificat de Réussite</h2>" +
                "            <p>Ce certificat atteste que</p>" +
                "            <div class='student-name'>" + apprenant.getNom() + "</div>" +
                "            <p>a complété avec succès le cours</p>" +
                "            <div class='course-name'>" + cours.getTitre() + "</div>" +
                "            <p>avec une progression de 100%</p>" +
                "        </div>" +
                "        <div class='certificate-footer'>" +
                "            <p><strong>Date de complétion:</strong> " + formattedDate + "</p>" +
                "            <p><strong>ID du cours:</strong> " + cours.getId() + "</p>" +
                "            <p><em>Ce certificat a été généré automatiquement par e-learn Fst</em></p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    public boolean canGenerateCertificate(Long apprenantId, Long coursId) {
        Optional<Inscription> inscription = inscriptionRepository.findByApprenantId(apprenantId).stream()
                .filter(ins -> ins.getCours() != null && ins.getCours().getId().equals(coursId))
                .findFirst();

        return inscription.isPresent() && 
               inscription.get().getStatut() == com.fst.elearning.entity.Statut.TERMINE;
    }
}
