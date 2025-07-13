package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.AllenamentoConsigliato;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.service.AllenamentoConsigliatoService;
import it.uniroma3.siw.service.AuthenticationService;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;
    
    @Autowired
    private AllenamentoConsigliatoService allenamentoConsigliatoService;
    
    @Autowired
    private AuthenticationService authHelper;

    // ACCESSO PUBBLICO - Non richiede autenticazione
    @GetMapping("/allenamentoConsigliato/{id}/recensioni")
    public String showRecensioni(@PathVariable("id") Long id, Model model) {
        AllenamentoConsigliato allenamento = allenamentoConsigliatoService.findById(id);
        if (allenamento == null) {
            return "redirect:/";
        }
        
        model.addAttribute("allenamento", allenamento);
        model.addAttribute("recensioni", recensioneService.findByAllenamentoConsigliato(allenamento));
        
        // Gestione utente autenticato (opzionale)
        User currentUser = authHelper.getCurrentUser();
        if (currentUser != null) {
            boolean hasReviewed = recensioneService.hasUserReviewed(currentUser, allenamento);
            model.addAttribute("hasReviewed", hasReviewed);
            
            if (hasReviewed) {
                Recensione userReview = recensioneService.findByUtenteAndAllenamentoConsigliato(currentUser, allenamento);
                model.addAttribute("userReview", userReview);
            }
        } else {
            model.addAttribute("hasReviewed", false);
        }
        
        authHelper.addAuthenticationInfo(model);
        return "recensioni";
    }

    @PostMapping("/allenamentoConsigliato/{id}/recensione")
    public String saveRecensione(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("recensione") Recensione recensione,
                                 BindingResult bindingResult, Model model) {
        
        User currentUser = authHelper.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        AllenamentoConsigliato allenamento = allenamentoConsigliatoService.findById(id);
        if (allenamento == null) {
            return "redirect:/";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("allenamento", allenamento);
            authHelper.addAuthenticationInfo(model);
            return "formNewRecensione";
        }
        
        if (recensioneService.hasUserReviewed(currentUser, allenamento)) {
            model.addAttribute("errorMessage", "Hai già recensito questo allenamento.");
            model.addAttribute("allenamento", allenamento);
            authHelper.addAuthenticationInfo(model);
            return "formNewRecensione";
        }
        
        // CORREZIONE HIBERNATE: Non impostare ID per nuova entità
        Recensione nuovaRecensione = new Recensione();
        nuovaRecensione.setVoto(recensione.getVoto());
        nuovaRecensione.setTesto(recensione.getTesto());
        nuovaRecensione.setUtente(currentUser);
        nuovaRecensione.setAllenamentoConsigliato(allenamento);
        nuovaRecensione.setDataCreazione(LocalDateTime.now());
        
        recensioneService.save(nuovaRecensione);
        
        return "redirect:/allenamentoConsigliato/" + id + "/recensioni";
    }

    @GetMapping("/allenamentoConsigliato/{id}/nuovaRecensione")
    public String formNuovaRecensione(@PathVariable("id") Long id, Model model) {
        User currentUser = authHelper.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        AllenamentoConsigliato allenamento = allenamentoConsigliatoService.findById(id);
        if (allenamento == null) {
            return "redirect:/";
        }
        
        // Controlla se ha già recensito
        if (recensioneService.hasUserReviewed(currentUser, allenamento)) {
            return "redirect:/allenamentoConsigliato/" + id + "/recensioni";
        }
        
        model.addAttribute("allenamento", allenamento);
        model.addAttribute("recensione", new Recensione());
        authHelper.addAuthenticationInfo(model);
        
        return "formNewRecensione";
    }

    @GetMapping("/recensione/{id}")
    public String showRecensione(@PathVariable("id") Long id, Model model) {
        Recensione recensione = recensioneService.findById(id);
        if (recensione == null) {
            return "redirect:/";
        }
        
        model.addAttribute("recensione", recensione);
        model.addAttribute("allenamento", recensione.getAllenamentoConsigliato());
        
        // Controlla se è la propria recensione
        User currentUser = authHelper.getCurrentUser();
        boolean isOwnReview = (currentUser != null && recensione.getUtente().equals(currentUser));
        model.addAttribute("isOwnReview", isOwnReview);
        
        // Carica altre recensioni per lo stesso allenamento
        List<Recensione> altreRecensioni = recensioneService.findByAllenamentoConsigliato(recensione.getAllenamentoConsigliato())
                .stream()
                .filter(r -> !r.getId().equals(id))
                .limit(3)
                .collect(Collectors.toList());
        
        model.addAttribute("altreRecensioni", altreRecensioni);
        authHelper.addAuthenticationInfo(model);
        
        return "dettaglioRecensione";
    }

    @GetMapping("/recensione/{id}/edit")
    public String editRecensione(@PathVariable("id") Long id, Model model) {
        User currentUser = authHelper.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Recensione recensione = recensioneService.findById(id);
        if (recensione == null || !recensione.getUtente().equals(currentUser)) {
            return "redirect:/";
        }
        
        model.addAttribute("recensione", recensione);
        model.addAttribute("allenamento", recensione.getAllenamentoConsigliato());
        authHelper.addAuthenticationInfo(model);
        
        return "formEditRecensione";
    }

    @PostMapping("/recensione/{id}")
    public String updateRecensione(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("recensione") Recensione recensione,
                                   BindingResult bindingResult, Model model) {
        
        User currentUser = authHelper.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Recensione existingRecensione = recensioneService.findById(id);
        if (existingRecensione == null || !existingRecensione.getUtente().equals(currentUser)) {
            return "redirect:/";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("allenamento", existingRecensione.getAllenamentoConsigliato());
            authHelper.addAuthenticationInfo(model);
            return "formEditRecensione";
        }
        
        // CORREZIONE HIBERNATE: Modifica entità esistente senza ricreare
        existingRecensione.setVoto(recensione.getVoto());
        existingRecensione.setTesto(recensione.getTesto());
        existingRecensione.setDataModifica(LocalDateTime.now());
        
        recensioneService.save(existingRecensione);
        
        return "redirect:/allenamentoConsigliato/" + existingRecensione.getAllenamentoConsigliato().getId() + "/recensioni";
    }

    @PostMapping("/recensione/{id}/delete")
    public String deleteRecensione(@PathVariable("id") Long id) {
        User currentUser = authHelper.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Recensione recensione = recensioneService.findById(id);
        if (recensione != null && recensione.getUtente().equals(currentUser)) {
            Long allenamentoId = recensione.getAllenamentoConsigliato().getId();
            recensioneService.deleteById(id);
            return "redirect:/allenamentoConsigliato/" + allenamentoId + "/recensioni";
        }
        
        return "redirect:/";
    }
}
