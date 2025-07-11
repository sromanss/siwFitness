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

import it.uniroma3.siw.model.AllenamentoConsigliato;
import it.uniroma3.siw.service.AllenamentoConsigliatoService;
import jakarta.validation.Valid;

@Controller
public class AdminController {
    
    @Autowired
    private AllenamentoConsigliatoService allenamentoConsigliatoService;
    
    @GetMapping("/adminHome")
    public String adminHome(Model model) {
        model.addAttribute("allenamenti", allenamentoConsigliatoService.findAll());
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
                       BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            addAuthenticationStatus(model);
            return "formNewAllenamentoConsigliato";
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
                                              @ModelAttribute("allenamento") AllenamentoConsigliato allenamento) {
        allenamento.setId(id);
        allenamentoConsigliatoService.save(allenamento);
        return "redirect:/adminHome";
    }
    
    @PostMapping("/adminHome/{id}/delete")
    public String deleteAllenamentoConsigliato(@PathVariable("id") Long id) {
        allenamentoConsigliatoService.deleteById(id);
        return "redirect:/adminHome";
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
