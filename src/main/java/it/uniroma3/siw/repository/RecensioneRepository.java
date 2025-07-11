package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.AllenamentoConsigliato;

public interface RecensioneRepository extends CrudRepository<Recensione, Long> {
    
    // Trova tutte le recensioni di un allenamento consigliato
    List<Recensione> findByAllenamentoConsigliato(AllenamentoConsigliato allenamentoConsigliato);
    
    // Trova tutte le recensioni di un utente
    List<Recensione> findByUtente(User utente);
    
    // Trova una recensione specifica di un utente per un allenamento
    Optional<Recensione> findByUtenteAndAllenamentoConsigliato(User utente, AllenamentoConsigliato allenamentoConsigliato);
    
    // Verifica se un utente ha già recensito un allenamento
    boolean existsByUtenteAndAllenamentoConsigliato(User utente, AllenamentoConsigliato allenamentoConsigliato);
}
