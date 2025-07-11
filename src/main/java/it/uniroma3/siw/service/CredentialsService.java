package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {
    
    @Autowired
    private CredentialsRepository credentialsRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Credentials save(Credentials credentials) {
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        return credentialsRepository.save(credentials);
    }
    
    public Credentials findByUsername(String username) {
        return credentialsRepository.findByUsername(username).orElse(null);
    }
    
    public Credentials getCredentials(Long id) {
        return credentialsRepository.findById(id).orElse(null);
    }
}
