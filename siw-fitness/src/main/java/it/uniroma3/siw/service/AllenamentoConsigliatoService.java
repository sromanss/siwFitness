package it.uniroma3.siw.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.AllenamentoConsigliato;
import it.uniroma3.siw.repository.AllenamentoConsigliatoRepository;

@Service
public class AllenamentoConsigliatoService {
    
    @Autowired
    private AllenamentoConsigliatoRepository allenamentoConsigliatoRepository;
    
    public AllenamentoConsigliato save(AllenamentoConsigliato allenamento) {
        return allenamentoConsigliatoRepository.save(allenamento);
    }
    
    public AllenamentoConsigliato findById(Long id) {
        return allenamentoConsigliatoRepository.findById(id).orElse(null);
    }
    
    public Iterable<AllenamentoConsigliato> findAll() {
        return allenamentoConsigliatoRepository.findAll();
    }
    
    public List<AllenamentoConsigliato> findAllWithFilters(String search, String sortBy) {
        List<AllenamentoConsigliato> allenamenti;
        
        // Ricerca per nome o tipo sport
        if (search != null && !search.trim().isEmpty()) {
            allenamenti = allenamentoConsigliatoRepository.findByNomeContainingIgnoreCaseOrTipoSportContainingIgnoreCase(
                search.trim(), search.trim());
        } else {
            allenamenti = StreamSupport.stream(allenamentoConsigliatoRepository.findAll().spliterator(), false)
                                     .collect(Collectors.toList());
        }
        
        // Ordinamento corretto
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy) {
                case "durata":
                    allenamenti.sort(Comparator.comparing(a -> a.getDurata() != null ? a.getDurata() : java.time.LocalTime.MIN));
                    break;
                case "numeroRecensioni":
                    // Gestione corretta per tipo primitivo o wrapper
                    allenamenti.sort(Comparator.comparingInt(a -> {
                        try {
                            return a.getNumeroRecensioni(); // Se è int primitivo
                        } catch (Exception e) {
                            return 0; // Valore di default
                        }
                    }));
                    Collections.reverse(allenamenti);
                    break;
                case "mediaVoti":
                    // Gestione corretta per tipo primitivo o wrapper
                    allenamenti.sort(Comparator.comparing(a -> {
                        try {
                            return a.getMediaVoti(); // Se è double primitivo
                        } catch (Exception e) {
                            return 0.0; // Valore di default
                        }
                    }));
                    Collections.reverse(allenamenti);
                    break;
                default:
                    allenamenti.sort(Comparator.comparing(AllenamentoConsigliato::getId).reversed());
            }
        } else {
            allenamenti.sort(Comparator.comparing(AllenamentoConsigliato::getId).reversed());
        }
        
        return allenamenti;
    }
    
    // Metodo per aggiornare l'allenamento gestendo l'eliminazione dell'immagine
    public AllenamentoConsigliato updateAllenamento(AllenamentoConsigliato allenamento, boolean rimuoviImmagine) {
        if (rimuoviImmagine) {
            allenamento.rimuoviImmagine();
        }
        return allenamentoConsigliatoRepository.save(allenamento);
    }
    
    public void deleteById(Long id) {
        allenamentoConsigliatoRepository.deleteById(id);
    }
}
