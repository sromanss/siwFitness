package it.uniroma3.siw.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.AllenamentoConsigliato;

public interface AllenamentoConsigliatoRepository extends CrudRepository<AllenamentoConsigliato, Long> {
    public List<AllenamentoConsigliato> findByTipoSport(String tipoSport);
    public List<AllenamentoConsigliato> findByLivelloDifficolta(String livelloDifficolta);
}
