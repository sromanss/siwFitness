package it.uniroma3.siw.service;

import java.util.Collections;
import java.util.Comparator;
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
    
    // Nuovo metodo per ricerca per keyword
    public List<Allenamento> searchByKeyword(User utente, String keyword) {
        return allenamentoRepository.findByUtenteAndNomeContainingIgnoreCaseOrUtenteAndTipoSportContainingIgnoreCase(
            utente, keyword, utente, keyword);
    }
    
    // Nuovo metodo per contare allenamenti per utente
    public long countByUtente(User utente) {
        return allenamentoRepository.countByUtente(utente);
    }
    
    // Nuovo metodo per sommare durata per utente
    public Integer sumDurataByUtente(User utente) {
        Integer durata = allenamentoRepository.sumDurataByUtente(utente);
        return durata != null ? durata : 0;
    }
    
    public void deleteById(Long id) {
        allenamentoRepository.deleteById(id);
    }
    
    public Iterable<Allenamento> findAll() {
        return allenamentoRepository.findAll();
    }
 // Aggiungi questo nuovo metodo
    public List<Allenamento> findAllWithFilters(User utente, String keyword, String sortBy) {
        List<Allenamento> allenamenti;
        
        // Ricerca per keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            allenamenti = allenamentoRepository.findByUtenteAndNomeContainingIgnoreCaseOrUtenteAndTipoSportContainingIgnoreCase(
                utente, keyword.trim(), utente, keyword.trim());
        } else {
            allenamenti = allenamentoRepository.findByUtente(utente);
        }
        
        // Ordinamento
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy) {
                case "durata":
                    allenamenti.sort(Comparator.comparing(a -> a.getDurata() != null ? a.getDurata() : java.time.LocalTime.MIN));
                    break;
                case "nome":
                    allenamenti.sort(Comparator.comparing(Allenamento::getNome));
                    break;
                case "data":
                    allenamenti.sort(Comparator.comparing(a -> a.getData() != null ? a.getData() : java.time.LocalDate.MIN));
                    Collections.reverse(allenamenti); // Più recenti prima
                    break;
                case "tipoSport":
                    allenamenti.sort(Comparator.comparing(Allenamento::getTipoSport));
                    break;
                default:
                    allenamenti.sort(Comparator.comparing(Allenamento::getId).reversed());
            }
        } else {
            allenamenti.sort(Comparator.comparing(Allenamento::getId).reversed());
        }
        
        return allenamenti;
    }
}
