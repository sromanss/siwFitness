package it.uniroma3.siw.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.AllenamentoConsigliato;

public interface AllenamentoConsigliatoRepository extends CrudRepository<AllenamentoConsigliato, Long> {
    
    // Metodi esistenti
    public List<AllenamentoConsigliato> findByTipoSport(String tipoSport);
    public List<AllenamentoConsigliato> findByLivelloDifficolta(String livelloDifficolta);
    
    // Nuovo metodo per ricerca per nome o tipo sport
    public List<AllenamentoConsigliato> findByNomeContainingIgnoreCaseOrTipoSportContainingIgnoreCase(
            String nome, String tipoSport);
}
