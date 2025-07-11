package it.uniroma3.siw.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import it.uniroma3.siw.model.*;

@Controller
public class AllenamentoController {
    
    @Autowired
    private AllenamentoService allenamentoService;
    
    @Autowired
    private CredentialsService credentialsService;
    
    @Autowired
    private UserService userService;

    
    @GetMapping("/allenamenti")
    public String getAllenamenti(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = getCurrentUser(authentication);
        
        if (currentUser != null) {
            // Mostra solo gli allenamenti dell'utente autenticato
            model.addAttribute("allenamenti", allenamentoService.findByUtente(currentUser));
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
    
    @PostMapping("/allenamento")
    public String newAllenamento(@Valid @ModelAttribute("allenamento") Allenamento allenamento,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            addAuthenticationStatus(model);
            return "formNewAllenamento";
        }

        // Ottieni l'utente corrente (funziona per entrambi i tipi di autenticazione)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = getCurrentUser(authentication);
        
        if (currentUser != null) {
            allenamento.setUtente(currentUser);
            allenamentoService.save(allenamento);
        } else {
            // Gestione errore se non si riesce a trovare l'utente
            model.addAttribute("error", "Errore nell'identificazione dell'utente");
            addAuthenticationStatus(model);
            return "formNewAllenamento";
        }

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
    
    private User getCurrentUser(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            // Utente OAuth (Google/GitHub)
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String email = oauthToken.getPrincipal().getAttribute("email");
            
            // Gestione specifica per GitHub se email è null
            if (email == null && "github".equals(oauthToken.getAuthorizedClientRegistrationId())) {
                String login = oauthToken.getPrincipal().getAttribute("login");
                email = login + "@github.local";
            }
            
            return userService.findByEmail(email);
            
        } else {
            // Utente tradizionale
            String username = authentication.getName();
            Credentials credentials = credentialsService.findByUsername(username);
            return credentials != null ? credentials.getUser() : null;
        }
    }


    


}
