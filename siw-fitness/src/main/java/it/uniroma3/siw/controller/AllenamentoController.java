package it.uniroma3.siw.controller;

import java.util.Collections;

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
    public String getAllenamenti(Model model) {
        User currentUser = authHelper.getCurrentUser();

        if (currentUser != null) {
            model.addAttribute("allenamenti", allenamentoService.findByUtente(currentUser));
        } else {
            model.addAttribute("allenamenti", Collections.emptyList());
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
                                   @ModelAttribute("allenamento") Allenamento allenamento) {
        allenamento.setId(id);
        allenamentoService.save(allenamento);
        return "redirect:/allenamento/" + id;
    }

    @PostMapping("/allenamento/{id}/delete")
    public String deleteAllenamento(@PathVariable("id") Long id) {
        allenamentoService.deleteById(id);
        return "redirect:/allenamenti";
    }

    @GetMapping("/allenamenti/filtro")
    public String getAllenamentiByTipo(@RequestParam(value = "tipoSport", required = false) String tipoSport, Model model) {
        if (tipoSport != null && !tipoSport.isEmpty()) {
            model.addAttribute("allenamenti", allenamentoService.findByTipoSport(tipoSport));
            model.addAttribute("filtroAttivo", tipoSport);
        } else {
            model.addAttribute("allenamenti", allenamentoService.findAll());
        }

        authHelper.addAuthenticationInfo(model);
        return "allenamenti";
    }
}
