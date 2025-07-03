package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
public class Recensione {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotBlank(message = "Il testo della recensione è obbligatorio")
    @Size(max = 500, message = "La recensione non può superare i 500 caratteri")
    private String testo;
    
    @NotNull(message = "Il voto è obbligatorio")
    @Min(value = 1, message = "Il voto minimo è 1")
    @Max(value = 5, message = "Il voto massimo è 5")
    private Integer voto;
    
    @NotNull
    private LocalDateTime dataCreazione;
    
    @ManyToOne
    @JoinColumn(name = "utente_id")
    private User utente;
    
    @ManyToOne
    @JoinColumn(name = "allenamento_consigliato_id")
    private AllenamentoConsigliato allenamentoConsigliato;
    
    // Costruttori
    public Recensione() {
        this.dataCreazione = LocalDateTime.now();
    }
    
    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }
    
    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }
    
    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }
    
    public User getUtente() { return utente; }
    public void setUtente(User utente) { this.utente = utente; }
    
    public AllenamentoConsigliato getAllenamentoConsigliato() { return allenamentoConsigliato; }
    public void setAllenamentoConsigliato(AllenamentoConsigliato allenamentoConsigliato) { 
        this.allenamentoConsigliato = allenamentoConsigliato; 
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recensione that = (Recensione) obj;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
