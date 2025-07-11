package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.AllenamentoConsigliato;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.service.AllenamentoConsigliatoService;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;

@Controller
public class RecensioneController {
    
    @Autowired
    private RecensioneService recensioneService;
    
    @Autowired
    private AllenamentoConsigliatoService allenamentoConsigliatoService;
    
    @Autowired
    private CredentialsService credentialsService;
    
    @GetMapping("/allenamentoConsigliato/{id}/recensioni")
    public String showRecensioni(@PathVariable("id") Long id, Model model) {
        AllenamentoConsigliato allenamento = allenamentoConsigliatoService.findById(id);
        if (allenamento != null) {
            model.addAttribute("allenamento", allenamento);
            model.addAttribute("recensioni", recensioneService.findByAllenamentoConsigliato(allenamento));
            
            // Verifica se l'utente è autenticato e se ha già recensito
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                String username = authentication.getName();
                User utente = credentialsService.findByUsername(username).getUser();
                boolean hasReviewed = recensioneService.hasUserReviewed(utente, allenamento);
                model.addAttribute("hasReviewed", hasReviewed);
                
                if (hasReviewed) {
                    Recensione userReview = recensioneService.findByUtenteAndAllenamentoConsigliato(utente, allenamento);
                    model.addAttribute("userReview", userReview);
                }
            }
            
            addAuthenticationStatus(model);
            return "recensioni";
        }
        return "redirect:/";
    }
    
    @GetMapping("/allenamentoConsigliato/{id}/nuovaRecensione")
    public String formNuovaRecensione(@PathVariable("id") Long id, Model model) {
        AllenamentoConsigliato allenamento = allenamentoConsigliatoService.findById(id);
        if (allenamento != null) {
            model.addAttribute("allenamento", allenamento);
            model.addAttribute("recensione", new Recensione());
            addAuthenticationStatus(model);
            return "formNuovaRecensione";
        }
        return "redirect:/";
    }
    
    @PostMapping("/allenamentoConsigliato/{id}/recensione")
    public String saveRecensione(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("recensione") Recensione recensione,
                                BindingResult bindingResult, Model model) {
        
        AllenamentoConsigliato allenamento = allenamentoConsigliatoService.findById(id);
        if (allenamento == null) {
            return "redirect:/";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("allenamento", allenamento);
            addAuthenticationStatus(model);
            return "formNuovaRecensione";
        }
        
        // Ottieni l'utente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User utente = credentialsService.findByUsername(username).getUser();
        
        // Verifica se l'utente ha già recensito
        if (recensioneService.hasUserReviewed(utente, allenamento)) {
            model.addAttribute("errorMessage", "Hai già recensito questo allenamento");
            model.addAttribute("allenamento", allenamento);
            addAuthenticationStatus(model);
            return "formNuovaRecensione";
        }
        
        recensione.setUtente(utente);
        recensione.setAllenamentoConsigliato(allenamento);
        recensioneService.save(recensione);
        
        return "redirect:/allenamentoConsigliato/" + id + "/recensioni";
    }
    
    @GetMapping("/recensione/{id}/edit")
    public String editRecensione(@PathVariable("id") Long id, Model model) {
        Recensione recensione = recensioneService.findById(id);
        if (recensione != null) {
            // Verifica che l'utente sia il proprietario della recensione
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User utente = credentialsService.findByUsername(username).getUser();
            
            if (recensione.getUtente().equals(utente)) {
                model.addAttribute("recensione", recensione);
                model.addAttribute("allenamento", recensione.getAllenamentoConsigliato());
                addAuthenticationStatus(model);
                return "formEditRecensione";
            }
        }
        return "redirect:/";
    }
    
    @PostMapping("/recensione/{id}")
    public String updateRecensione(@PathVariable("id") Long id,
                                  @Valid @ModelAttribute("recensione") Recensione recensione,
                                  BindingResult bindingResult, Model model) {
        
        Recensione existingRecensione = recensioneService.findById(id);
        if (existingRecensione == null) {
            return "redirect:/";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("allenamento", existingRecensione.getAllenamentoConsigliato());
            addAuthenticationStatus(model);
            return "formEditRecensione";
        }
        
        // Mantieni i dati originali
        recensione.setId(id);
        recensione.setUtente(existingRecensione.getUtente());
        recensione.setAllenamentoConsigliato(existingRecensione.getAllenamentoConsigliato());
        recensione.setDataCreazione(existingRecensione.getDataCreazione());
        
        recensioneService.save(recensione);
        
        return "redirect:/allenamentoConsigliato/" + existingRecensione.getAllenamentoConsigliato().getId() + "/recensioni";
    }
    
    @PostMapping("/recensione/{id}/delete")
    public String deleteRecensione(@PathVariable("id") Long id) {
        Recensione recensione = recensioneService.findById(id);
        if (recensione != null) {
            Long allenamentoId = recensione.getAllenamentoConsigliato().getId();
            recensioneService.deleteById(id);
            return "redirect:/allenamentoConsigliato/" + allenamentoId + "/recensioni";
        }
        return "redirect:/";
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
