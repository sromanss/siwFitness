package it.uniroma3.siw.model;

import jakarta.persistence.*;

@Entity
@Table (name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String cognome;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    // Costruttori
    public User() {}
    
    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // equals e hashCode basati su email
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return email != null && email.equals(user.email);
    }
    
    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}
