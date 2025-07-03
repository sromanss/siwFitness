package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CredentialsService credentialsService;
    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                              @ModelAttribute("credentials") Credentials credentials,
                              BindingResult bindingResult, Model model) {
        
        // Verifica se l'username esiste già
        if (credentialsService.findByUsername(credentials.getUsername()) != null) {
            model.addAttribute("errorMessage", "Username già esistente");
            return "register";
        }
        
        // Salva l'utente e le credenziali
        user = userService.save(user);
        credentials.setUser(user);
        credentialsService.save(credentials);
        
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    
    @GetMapping("/success")
    public String defaultAfterLogin(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        
        // Gestione utenti OAuth
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String email = oauthToken.getPrincipal().getAttribute("email");
            String name = oauthToken.getPrincipal().getAttribute("name");
            
            // Crea o trova l'utente OAuth
            User user = userService.findOrCreateOAuthUser(email, name);
            
            return "redirect:/";
        } 
        
        // Gestione utenti tradizionali
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        
        return isAdmin ? "redirect:/adminHome" : "redirect:/";
    }




    

}
