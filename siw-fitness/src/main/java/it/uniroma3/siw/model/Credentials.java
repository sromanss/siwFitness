package it.uniroma3.siw.model;

import jakarta.persistence.*;

@Entity
public class Credentials {
    
    public static final String DEFAULT_ROLE = "DEFAULT";
    public static final String ADMIN_ROLE = "ADMIN";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role;
    
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
    
    // Costruttori
    public Credentials() {
        this.role = DEFAULT_ROLE;
    }
    
    public Credentials(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }
    
    // Getter e Setter completi...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
