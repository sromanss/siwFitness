package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recensioni")
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer voto;

    @NotBlank
    @Size(min = 1, max = 500)
    private String testo;

    @Column(nullable = false)
    private LocalDateTime dataCreazione;
    
    private LocalDateTime dataModifica;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utente_id", nullable = false)
    private User utente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allenamento_id", nullable = false)
    private AllenamentoConsigliato allenamentoConsigliato;

    // Costruttori
    public Recensione() {
        this.dataCreazione = LocalDateTime.now();
    }

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataModifica() { return dataModifica; }
    public void setDataModifica(LocalDateTime dataModifica) { this.dataModifica = dataModifica; }

    public User getUtente() { return utente; }
    public void setUtente(User utente) { this.utente = utente; }

    public AllenamentoConsigliato getAllenamentoConsigliato() { return allenamentoConsigliato; }
    public void setAllenamentoConsigliato(AllenamentoConsigliato allenamentoConsigliato) { this.allenamentoConsigliato = allenamentoConsigliato; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recensione that = (Recensione) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
