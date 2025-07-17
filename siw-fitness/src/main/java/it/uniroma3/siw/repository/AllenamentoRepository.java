package it.uniroma3.siw.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.User;

public interface AllenamentoRepository extends CrudRepository<Allenamento, Long> {
    
    public List<Allenamento> findByUtente(User utente);
    public List<Allenamento> findByTipoSport(String tipoSport);
    public List<Allenamento> findByUtenteAndTipoSport(User utente, String tipoSport);
    
    // Metodo per ricerca per keyword (nome o tipo sport)
    public List<Allenamento> findByUtenteAndNomeContainingIgnoreCaseOrUtenteAndTipoSportContainingIgnoreCase(
        User utente1, String nome, User utente2, String tipoSport);
    
    // Metodo per contare allenamenti per utente
    public long countByUtente(User utente);
    
    // Metodo corretto per sommare durata - estrae minuti dal campo TIME
    @Query("SELECT COALESCE(SUM(EXTRACT(HOUR FROM a.durata) * 60 + EXTRACT(MINUTE FROM a.durata)), 0) " +
           "FROM Allenamento a WHERE a.utente = :utente")
    public Integer sumDurataByUtente(@Param("utente") User utente);
}
