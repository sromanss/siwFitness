package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.model.AllenamentoConsigliato;
import it.uniroma3.siw.service.AllenamentoConsigliatoService;

@Controller
public class MainController {
    
    @Autowired
    private AllenamentoConsigliatoService allenamentoConsigliatoService;
    
    @GetMapping("/")
    public String index(Model model) {
        // Mostra gli allenamenti consigliati (lista vuota se non ce ne sono)
        Iterable<AllenamentoConsigliato> allenamenti = allenamentoConsigliatoService.findAll();
        model.addAttribute("allenamenti", allenamenti);
        
        // Verifica se l'utente è autenticato
        addAuthenticationStatus(model);
        
        return "index";
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
