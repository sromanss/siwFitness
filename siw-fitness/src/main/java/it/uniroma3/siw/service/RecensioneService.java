package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.AllenamentoConsigliato;
import it.uniroma3.siw.repository.RecensioneRepository;
import java.util.List;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    public Recensione save(Recensione recensione) {
        return recensioneRepository.save(recensione);
    }

    public Recensione findById(Long id) {
        return recensioneRepository.findById(id).orElse(null);
    }

    public List<Recensione> findByAllenamentoConsigliato(AllenamentoConsigliato allenamentoConsigliato) {
        return recensioneRepository.findByAllenamentoConsigliato(allenamentoConsigliato);
    }

    public List<Recensione> findByUtente(User utente) {
        return recensioneRepository.findByUtente(utente);
    }

    public Recensione findByUtenteAndAllenamentoConsigliato(User utente, AllenamentoConsigliato allenamentoConsigliato) {
        return recensioneRepository.findByUtenteAndAllenamentoConsigliato(utente, allenamentoConsigliato).orElse(null);
    }

    public boolean hasUserReviewed(User utente, AllenamentoConsigliato allenamentoConsigliato) {
        return recensioneRepository.existsByUtenteAndAllenamentoConsigliato(utente, allenamentoConsigliato);
    }

    public void deleteById(Long id) {
        recensioneRepository.deleteById(id);
    }
}
