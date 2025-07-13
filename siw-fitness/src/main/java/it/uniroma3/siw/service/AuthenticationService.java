package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;

@Component
public class AuthenticationService {

    @Autowired
    private CredentialsService credentialsService;
    
    @Autowired
    private UserService userService;

    /**
     * Ottiene l'utente autenticato corrente (OAuth + Tradizionale)
     * @return User se autenticato, null altrimenti
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        
        // Gestione OAuth (Google/GitHub)
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String email = oauthToken.getPrincipal().getAttribute("email");
            
            // Gestione specifica per GitHub se email è null
            if (email == null && "github".equals(oauthToken.getAuthorizedClientRegistrationId())) {
                String login = oauthToken.getPrincipal().getAttribute("login");
                email = login + "@github.local";
            }
            
            return userService.findByEmail(email);
        }
        
        // Gestione autenticazione tradizionale
        String username = authentication.getName();
        Credentials credentials = credentialsService.findByUsername(username);
        
        if (credentials != null && credentials.getUser() != null) {
            return credentials.getUser();
        }
        
        return null;
    }

    /**
     * Controlla se l'utente è autenticato
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * Controlla se l'utente è admin
     */
    public boolean isAdmin() {
        User user = getCurrentUser();
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    /**
     * Ottiene il nome utente corrente
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        
        return authentication.getName();
    }

    /**
     * Popola il model con le informazioni di autenticazione
     */
    public void addAuthenticationInfo(Model model) {
        User user = getCurrentUser();
        
        if (user != null) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("isAdmin", user.getRole() == User.Role.ADMIN);
            model.addAttribute("currentUser", user);
        } else {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("isAdmin", false);
            model.addAttribute("currentUser", null);
        }
    }
}
