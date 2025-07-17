package it.uniroma3.siw.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import it.uniroma3.siw.service.AuthenticationService;
import jakarta.validation.Valid;
import it.uniroma3.siw.model.*;

@Controller
public class AllenamentoController {

    @Autowired
    private AllenamentoService allenamentoService;

    @Autowired
    private AuthenticationService authHelper;

    @GetMapping("/allenamenti")
    public String getAllenamenti(@RequestParam(required = false) String keyword, 
                                @RequestParam(required = false) String sortBy,
                                Model model) {
        User currentUser = authHelper.getCurrentUser();

        if (currentUser != null) {
            List<Allenamento> allenamenti;
            
            // Usa il nuovo metodo che gestisce sia ricerca che ordinamento
            allenamenti = allenamentoService.findAllWithFilters(currentUser, keyword, sortBy);
            
            model.addAttribute("allenamenti", allenamenti);
            model.addAttribute("keyword", keyword);
            model.addAttribute("currentSort", sortBy);
            
            // Aggiunge le statistiche
            model.addAttribute("totaleAllenamenti", allenamentoService.countByUtente(currentUser));
            model.addAttribute("durataTotale", allenamentoService.sumDurataByUtente(currentUser));
        } else {
            model.addAttribute("allenamenti", Collections.emptyList());
            model.addAttribute("totaleAllenamenti", 0L);
            model.addAttribute("durataTotale", 0);
        }

        authHelper.addAuthenticationInfo(model);
        return "allenamenti";
    }


    @GetMapping("/allenamento/{id}")
    public String getAllenamento(@PathVariable("id") Long id, Model model) {
        Allenamento allenamento = allenamentoService.findById(id);
        if (allenamento != null) {
            model.addAttribute("allenamento", allenamento);
            authHelper.addAuthenticationInfo(model);
            return "allenamento";
        }

        return "redirect:/allenamenti";
    }

    @GetMapping("/formNewAllenamento")
    public String formNewAllenamento(Model model) {
        model.addAttribute("allenamento", new Allenamento());
        authHelper.addAuthenticationInfo(model);
        return "formNewAllenamento";
    }

    @PostMapping("/allenamento")
    public String newAllenamento(@Valid @ModelAttribute("allenamento") Allenamento allenamento,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            authHelper.addAuthenticationInfo(model);
            return "formNewAllenamento";
        }

        User currentUser = authHelper.getCurrentUser();
        
        if (currentUser != null) {
            allenamento.setUtente(currentUser);
            allenamentoService.save(allenamento);
        } else {
            model.addAttribute("error", "Errore nell'identificazione dell'utente");
            authHelper.addAuthenticationInfo(model);
            return "formNewAllenamento";
        }

        return "redirect:/allenamenti";
    }

    @GetMapping("/allenamento/{id}/edit")
    public String editAllenamento(@PathVariable("id") Long id, Model model) {
        Allenamento allenamento = allenamentoService.findById(id);
        if (allenamento != null) {
            model.addAttribute("allenamento", allenamento);
            authHelper.addAuthenticationInfo(model);
            return "formEditAllenamento";
        }

        return "redirect:/allenamenti";
    }

    @PostMapping("/allenamento/{id}")
    public String updateAllenamento(@PathVariable("id") Long id,
                                   @ModelAttribute("allenamento") Allenamento allenamento,
                                   Model model) {
        // Recupera l'utente corrente
        User currentUser = authHelper.getCurrentUser();
        
        if (currentUser != null) {
            // Imposta l'ID e l'utente
            allenamento.setId(id);
            allenamento.setUtente(currentUser);  // ✅ SOLUZIONE: riassegna l'utente
            
            allenamentoService.save(allenamento);
            return "redirect:/allenamento/" + id;
        } else {
            model.addAttribute("error", "Errore nell'identificazione dell'utente");
            authHelper.addAuthenticationInfo(model);
            return "formEditAllenamento";
        }
    }


    @PostMapping("/allenamento/{id}/delete")
    public String deleteAllenamento(@PathVariable("id") Long id) {
        allenamentoService.deleteById(id);
        return "redirect:/allenamenti";
    }

    
}
