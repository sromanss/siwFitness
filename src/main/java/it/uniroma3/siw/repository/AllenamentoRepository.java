package it.uniroma3.siw.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.User;

public interface AllenamentoRepository extends CrudRepository<Allenamento, Long> {
    public List<Allenamento> findByUtente(User utente);
    public List<Allenamento> findByTipoSport(String tipoSport);
    public List<Allenamento> findByUtenteAndTipoSport(User utente, String tipoSport);
}
