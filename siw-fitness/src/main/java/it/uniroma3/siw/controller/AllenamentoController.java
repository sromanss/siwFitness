package it.uniroma3.siw.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;
import it.uniroma3.siw.model.*;

@Controller
public class AllenamentoController {
    
    @Autowired
    private AllenamentoService allenamentoService;
    
    @Autowired
    private CredentialsService credentialsService;
    
    // Visualizza tutti gli allenamenti
    @GetMapping("/allenamenti")
    public String getAllenamenti(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Trova l'utente autenticato
        Credentials credentials = credentialsService.findByUsername(username);
        if (credentials != null && credentials.getUser() != null) {
            // Mostra solo gli allenamenti dell'utente autenticato
            model.addAttribute("allenamenti", allenamentoService.findByUtente(credentials.getUser()));
        } else {
            model.addAttribute("allenamenti", Collections.emptyList());
        }
        
        addAuthenticationStatus(model);
        return "allenamenti";
    }

    
    // Visualizza un singolo allenamento
    @GetMapping("/allenamento/{id}")
    public String getAllenamento(@PathVariable("id") Long id, Model model) {
        Allenamento allenamento = allenamentoService.findById(id);
        if (allenamento != null) {
            model.addAttribute("allenamento", allenamento);
            addAuthenticationStatus(model); 
            return "allenamento";
        }
        return "redirect:/allenamenti";
    }


    
    // Form per nuovo allenamento
    @GetMapping("/formNewAllenamento")
    public String formNewAllenamento(Model model) {
        model.addAttribute("allenamento", new Allenamento());
        addAuthenticationStatus(model);
        return "formNewAllenamento";
    }
    
    // Salva nuovo allenamento
    @PostMapping("/allenamento")
    public String newAllenamento(@Valid @ModelAttribute("allenamento") Allenamento allenamento, 
                                BindingResult bindingResult, Model model) {
        
        if (bindingResult.hasErrors()) {
            addAuthenticationStatus(model);
            return "formNewAllenamento";
        }
        
        // Ottieni l'utente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Trova l'utente nel database
        Credentials credentials = credentialsService.findByUsername(username);
        if (credentials != null && credentials.getUser() != null) {
            allenamento.setUtente(credentials.getUser());
        }
        
        allenamentoService.save(allenamento);
        return "redirect:/allenamenti";
    }


    
    // Form per modificare allenamento esistente
    @GetMapping("/allenamento/{id}/edit")
    public String editAllenamento(@PathVariable("id") Long id, Model model) {
        Allenamento allenamento = allenamentoService.findById(id);
        if (allenamento != null) {
            model.addAttribute("allenamento", allenamento);
            addAuthenticationStatus(model);
            return "formEditAllenamento";
        }
        return "redirect:/allenamenti";
    }



    // Aggiorna allenamento esistente
    @PostMapping("/allenamento/{id}")
    public String updateAllenamento(@PathVariable("id") Long id, 
                                   @ModelAttribute("allenamento") Allenamento allenamento) {
        allenamento.setId(id);
        allenamentoService.save(allenamento);
        return "redirect:/allenamento/" + id;
    }

    // Cancella allenamento
    @PostMapping("/allenamento/{id}/delete")
    public String deleteAllenamento(@PathVariable("id") Long id) {
        allenamentoService.deleteById(id);
        return "redirect:/allenamenti";
    }
    @GetMapping("/allenamenti/filtro")
    public String getAllenamentiByTipo(@RequestParam(value = "tipoSport", required = false) String tipoSport, Model model) {
        
        if (tipoSport != null && !tipoSport.isEmpty()) {
            // Filtra per tipo di sport
            model.addAttribute("allenamenti", allenamentoService.findByTipoSport(tipoSport));
            model.addAttribute("filtroAttivo", tipoSport);
        } else {
            // Se non c'è filtro, mostra tutti
            model.addAttribute("allenamenti", allenamentoService.findAll());
        }
        
        // Aggiungi lo stato di autenticazione
        addAuthenticationStatus(model);
        
        return "allenamenti";
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
