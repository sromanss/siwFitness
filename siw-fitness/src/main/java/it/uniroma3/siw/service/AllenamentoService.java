package it.uniroma3.siw.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.AllenamentoRepository;

@Service
public class AllenamentoService {
    
    @Autowired
    private AllenamentoRepository allenamentoRepository;
    
    public Allenamento save(Allenamento allenamento) {
        return allenamentoRepository.save(allenamento);
    }
    
    public Allenamento findById(Long id) {
        return allenamentoRepository.findById(id).orElse(null);
    }
    
    public List<Allenamento> findByUtente(User utente) {
        return allenamentoRepository.findByUtente(utente);
    }
    
    public List<Allenamento> findByTipoSport(String tipoSport) {
        return allenamentoRepository.findByTipoSport(tipoSport);
    }
    
    public List<Allenamento> findByUtenteAndTipoSport(User utente, String tipoSport) {
        return allenamentoRepository.findByUtenteAndTipoSport(utente, tipoSport);
    }
    
    public void deleteById(Long id) {
        allenamentoRepository.deleteById(id);
    }
    
    public Iterable<Allenamento> findAll() {
        return allenamentoRepository.findAll();
    }

}
