package com.fst.elearning.controller;

import com.fst.elearning.entity.Avis;
import com.fst.elearning.service.AvisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/avis")
public class AvisController {
    @Autowired
    private AvisService avisService;

    @PostMapping
    public ResponseEntity<?> createAvis(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        try {
            Long apprenantId = getUserIdFromUsername(authentication.getName());
            Long coursId = Long.valueOf(payload.get("coursId").toString());
            int note = Integer.parseInt(payload.get("note").toString());
            String commentaire = payload.get("commentaire") != null ? payload.get("commentaire").toString() : "";

            Avis avis = avisService.createAvis(apprenantId, coursId, note, commentaire);
            return ResponseEntity.ok(avis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{avisId}")
    public ResponseEntity<?> updateAvis(
            @PathVariable Long avisId,
            @RequestBody Map<String, Object> payload) {
        try {
            int note = Integer.parseInt(payload.get("note").toString());
            String commentaire = payload.get("commentaire") != null ? payload.get("commentaire").toString() : "";

            Avis avis = avisService.updateAvis(avisId, note, commentaire);
            return ResponseEntity.ok(avis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cours/{coursId}")
    public ResponseEntity<List<Avis>> getAvisByCours(@PathVariable Long coursId) {
        List<Avis> avis = avisService.getAvisByCours(coursId);
        return ResponseEntity.ok(avis);
    }

    @GetMapping("/cours/{coursId}/stats")
    public ResponseEntity<?> getAvisStats(@PathVariable Long coursId) {
        double averageNote = avisService.getAverageNote(coursId);
        long totalAvis = avisService.getTotalAvis(coursId);
        return ResponseEntity.ok(Map.of(
            "averageNote", averageNote,
            "totalAvis", totalAvis
        ));
    }

    @GetMapping("/my-avis/{coursId}")
    public ResponseEntity<?> getMyAvis(
            @PathVariable Long coursId,
            Authentication authentication) {
        Long apprenantId = getUserIdFromUsername(authentication.getName());
        return avisService.getAvisByApprenantAndCours(apprenantId, coursId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{avisId}")
    public ResponseEntity<?> deleteAvis(@PathVariable Long avisId) {
        avisService.deleteAvis(avisId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromUsername(String username) {
        // This should be implemented based on your user lookup logic
        // For now, returning a placeholder - you'll need to implement proper user lookup
        return 1L;
    }
}
