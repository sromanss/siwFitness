package it.uniroma3.siw.service;

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

    
    public void deleteById(Long id) {
        allenamentoConsigliatoRepository.deleteById(id);
    }
}
