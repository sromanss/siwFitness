package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "allenamenti")
public class Allenamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotBlank(message = "Il nome è obbligatorio")
    @Size(min = 1, max = 100, message = "Il nome deve essere tra 1 e 100 caratteri")
    private String nome;
    
    @NotBlank(message = "Il tipo di sport è obbligatorio")
    private String tipoSport;
    
    @NotNull(message = "La data è obbligatoria")
    @PastOrPresent(message = "La data non può essere nel futuro")
    private LocalDate data;
    
    @NotNull(message = "La durata è obbligatoria")
    private LocalTime durata;
    
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String descrizione;
    
    @ManyToOne
    private User utente;
        
    // Costruttori
    public Allenamento() {}
    
    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getTipoSport() { return tipoSport; }
    public void setTipoSport(String tipoSport) { this.tipoSport = tipoSport; }
    
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    
    public LocalTime getDurata() { return durata; }
    public void setDurata(LocalTime durata) { this.durata = durata; }
    
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    
    public User getUtente() { return utente; }
    public void setUtente(User utente) { this.utente = utente; }
    
    // equals e hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Allenamento that = (Allenamento) obj;
        return nome != null && nome.equals(that.nome) && 
               data != null && data.equals(that.data) &&
               utente != null && utente.equals(that.utente);
    }
    
    @Override
    public int hashCode() {
        int result = nome != null ? nome.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (utente != null ? utente.hashCode() : 0);
        return result;
    }
}
