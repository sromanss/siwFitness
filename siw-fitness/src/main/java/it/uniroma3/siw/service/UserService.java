package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
    
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User findOrCreateOAuthUser(String email, String name) {
        // Prima cerca se esiste già un utente con questa email
        User existingUser = userRepository.findByEmail(email);
        
        if (existingUser != null) {
            return existingUser;
        }
        
        // Se non esiste, crea un nuovo utente
        User newUser = new User();
        newUser.setEmail(email);
        
        // Estrai nome e cognome dalla stringa name
        String[] nameParts = name.split(" ", 2);
        newUser.setNome(nameParts[0]);
        if (nameParts.length > 1) {
            newUser.setCognome(nameParts[1]);
        } else {
            newUser.setCognome(""); // Cognome vuoto se non fornito
        }
        
        return userRepository.save(newUser);
    }


}
