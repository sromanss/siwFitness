package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.AllenamentoConsigliato;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.service.AllenamentoConsigliatoService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.service.ImageService;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private AllenamentoConsigliatoService allenamentoConsigliatoService;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private RecensioneService recensioneService;

    // ========== GESTIONE ALLENAMENTI ==========

    @GetMapping("/adminHome")
    public String adminHome(Model model) {
        model.addAttribute("allenamenti", allenamentoConsigliatoService.findAll());
        
        // Aggiungi statistiche recensioni
        List<Recensione> tutteRecensioni = recensioneService.findAll();
        model.addAttribute("totalRecensioni", tutteRecensioni.size());
        
        addAuthenticationStatus(model);
        return "adminHome";
    }

    @GetMapping("/adminHome/new")
    public String formNew(Model model) {
        model.addAttribute("allenamento", new AllenamentoConsigliato());
        addAuthenticationStatus(model);
        return "formNewAllenamentoConsigliato";
    }

    @PostMapping("/adminHome")
    public String save(@Valid @ModelAttribute("allenamento") AllenamentoConsigliato allenamento,
                      BindingResult bindingResult,
                      @RequestParam("immagineFile") MultipartFile file,
                      Model model) {
        
        if (bindingResult.hasErrors()) {
            addAuthenticationStatus(model);
            return "formNewAllenamentoConsigliato";
        }

        // Gestione upload immagine
        if (!file.isEmpty()) {
            if (!imageService.isValidImageFile(file)) {
                model.addAttribute("errorImmagine", "Formato file non supportato. Usa JPG, JPEG o PNG.");
                addAuthenticationStatus(model);
                return "formNewAllenamentoConsigliato";
            }
            
            try {
                String fileName = imageService.saveImage(file);
                allenamento.setNomeImmagine(fileName);
                allenamento.setPathImmagine("/images/" + fileName);
            } catch (IOException e) {
                model.addAttribute("errorImmagine", "Errore nel salvataggio dell'immagine.");
                addAuthenticationStatus(model);
                return "formNewAllenamentoConsigliato";
            }
        }

        allenamentoConsigliatoService.save(allenamento);
        return "redirect:/adminHome";
    }

    @GetMapping("/adminHome/{id}/edit")
    public String editAllenamentoConsigliato(@PathVariable("id") Long id, Model model) {
        AllenamentoConsigliato allenamento = allenamentoConsigliatoService.findById(id);
        if (allenamento != null) {
            model.addAttribute("allenamento", allenamento);
            addAuthenticationStatus(model);
            return "formEditAllenamentoConsigliato";
        }
        return "redirect:/adminHome";
    }

    @PostMapping("/adminHome/{id}")
    public String updateAllenamentoConsigliato(@PathVariable("id") Long id,
                                             @ModelAttribute("allenamento") AllenamentoConsigliato allenamento,
                                             @RequestParam(value = "immagineFile", required = false) MultipartFile file,
                                             @RequestParam(value = "rimuoviImmagine", required = false) boolean rimuoviImmagine,
                                             Model model) {
        
        AllenamentoConsigliato esistente = allenamentoConsigliatoService.findById(id);
        if (esistente == null) {
            return "redirect:/adminHome";
        }
        
        // GESTIONE ELIMINAZIONE IMMAGINE
        if (rimuoviImmagine) {
            // Elimina l'immagine dal filesystem se esiste
            if (esistente.getNomeImmagine() != null) {
                imageService.deleteImage(esistente.getNomeImmagine());
            }
            // Rimuovi i riferimenti all'immagine dall'entità
            allenamento.setNomeImmagine(null);
            allenamento.setPathImmagine(null);
        }
        // GESTIONE UPLOAD NUOVA IMMAGINE
        else if (file != null && !file.isEmpty()) {
            if (!imageService.isValidImageFile(file)) {
                model.addAttribute("errorImmagine", "Formato file non supportato. Usa JPG, JPEG o PNG.");
                model.addAttribute("allenamento", allenamento);
                addAuthenticationStatus(model);
                return "formEditAllenamentoConsigliato";
            }
            
            try {
                // Elimina vecchia immagine se esiste
                if (esistente.getNomeImmagine() != null) {
                    imageService.deleteImage(esistente.getNomeImmagine());
                }
                
                String fileName = imageService.saveImage(file);
                allenamento.setNomeImmagine(fileName);
                allenamento.setPathImmagine("/images/" + fileName);
            } catch (IOException e) {
                model.addAttribute("errorImmagine", "Errore nel salvataggio dell'immagine.");
                model.addAttribute("allenamento", allenamento);
                addAuthenticationStatus(model);
                return "formEditAllenamentoConsigliato";
            }
        } 
        // MANTIENI IMMAGINE ESISTENTE
        else {
            // Mantieni l'immagine esistente se non viene caricata una nuova e non viene eliminata
            allenamento.setNomeImmagine(esistente.getNomeImmagine());
            allenamento.setPathImmagine(esistente.getPathImmagine());
        }
        
        allenamento.setId(id);
        allenamentoConsigliatoService.save(allenamento);
        return "redirect:/adminHome";
    }

    @PostMapping("/adminHome/{id}/delete")
    public String deleteAllenamentoConsigliato(@PathVariable("id") Long id) {
        AllenamentoConsigliato allenamento = allenamentoConsigliatoService.findById(id);
        if (allenamento != null && allenamento.getNomeImmagine() != null) {
            // Elimina anche l'immagine associata
            imageService.deleteImage(allenamento.getNomeImmagine());
        }
        allenamentoConsigliatoService.deleteById(id);
        return "redirect:/adminHome";
    }

    // ========== GESTIONE RECENSIONI (ADMIN) ==========

    @GetMapping("/adminHome/recensioni")
    public String adminRecensioni(Model model) {
        if (!isCurrentUserAdmin()) {
            return "redirect:/";
        }
        
        model.addAttribute("recensioni", recensioneService.findAll());
        addAuthenticationStatus(model);
        return "adminRecensioni";
    }
    
    @PostMapping("/adminHome/recensione/{id}/delete")
    public String deleteRecensioneAsAdmin(@PathVariable("id") Long id) {
        if (!isCurrentUserAdmin()) {
            return "redirect:/";
        }
        
        Recensione recensione = recensioneService.findById(id);
        if (recensione != null) {
            Long allenamentoId = recensione.getAllenamentoConsigliato().getId();
            
            if (recensioneService.deleteRecensioneAsAdmin(id)) {
                // MODIFICA: Redirect alla pagina normale delle recensioni
                return "redirect:/allenamentoConsigliato/" + allenamentoId + "/recensioni";
            }
        }
        return "redirect:/adminHome";
    }


    // ========== METODI HELPER ==========

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               authentication.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
    }

    private void addAuthenticationStatus(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("isAdmin", false);
        }
    }
}
