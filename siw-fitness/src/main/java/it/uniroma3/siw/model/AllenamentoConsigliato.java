package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "allenamenti_consigliati")
public class AllenamentoConsigliato {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String tipoSport;
    
    @Column(nullable = false)
    private LocalTime durata;
    
    @Column(length = 1000)
    private String descrizione;
    
    @Column(nullable = false)
    private String livelloDifficolta;
    
    private String nomeImmagine; // Nome del file immagine salvato
    private String pathImmagine; // Path completo dell'immagine
    
    @OneToMany(mappedBy = "allenamentoConsigliato", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recensione> recensioni = new ArrayList<>();
    
    // Metodi helper per gestire le recensioni
    public List<Recensione> getRecensioni() { 
        return recensioni; 
    }

    public String getNomeImmagine() {
        return nomeImmagine;
    }
    
    public void setNomeImmagine(String nomeImmagine) {
        this.nomeImmagine = nomeImmagine;
    }
    
    public String getPathImmagine() {
        return pathImmagine;
    }
    
    public void setPathImmagine(String pathImmagine) {
        this.pathImmagine = pathImmagine;
    }
    
    // Metodo helper per rimuovere l'immagine
    public void rimuoviImmagine() {
        this.nomeImmagine = null;
        this.pathImmagine = null;
    }
    
    // Metodo per verificare se ha un'immagine
    public boolean hasImmagine() {
        return this.pathImmagine != null && !this.pathImmagine.trim().isEmpty();
    }

    public void setRecensioni(List<Recensione> recensioni) { 
        this.recensioni = recensioni; 
    }

    public void addRecensione(Recensione recensione) {
        recensioni.add(recensione);
        recensione.setAllenamentoConsigliato(this);
    }

    public void removeRecensione(Recensione recensione) {
        recensioni.remove(recensione);
        recensione.setAllenamentoConsigliato(null);
    }

    // Metodo per calcolare la media dei voti
    public Double getMediaVoti() {
        if (recensioni.isEmpty()) {
            return 0.0;
        }
        return recensioni.stream()
                .mapToInt(Recensione::getVoto)
                .average()
                .orElse(0.0);
    }

    // Metodo per contare le recensioni
    public int getNumeroRecensioni() {
        return recensioni.size();
    }

    // Costruttori
    public AllenamentoConsigliato() {}
    
    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getTipoSport() { return tipoSport; }
    public void setTipoSport(String tipoSport) { this.tipoSport = tipoSport; }
    
    public LocalTime getDurata() { return durata; }
    public void setDurata(LocalTime durata) { this.durata = durata; }
    
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    
    public String getLivelloDifficolta() { return livelloDifficolta; }
    public void setLivelloDifficolta(String livelloDifficolta) { this.livelloDifficolta = livelloDifficolta; }

    // equals e hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AllenamentoConsigliato that = (AllenamentoConsigliato) obj;
        return nome != null && nome.equals(that.nome) && 
               tipoSport != null && tipoSport.equals(that.tipoSport);
    }
    
    @Override
    public int hashCode() {
        int result = nome != null ? nome.hashCode() : 0;
        result = 31 * result + (tipoSport != null ? tipoSport.hashCode() : 0);
        return result;
    }
}
