package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.AllenamentoConsigliato;

public interface RecensioneRepository extends CrudRepository<Recensione, Long> {
    List<Recensione> findByAllenamentoConsigliato(AllenamentoConsigliato allenamentoConsigliato);
    List<Recensione> findByUtente(User utente);
    Optional<Recensione> findByUtenteAndAllenamentoConsigliato(User utente, AllenamentoConsigliato allenamentoConsigliato);
    boolean existsByUtenteAndAllenamentoConsigliato(User utente, AllenamentoConsigliato allenamentoConsigliato);
}
