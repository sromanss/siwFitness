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
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
    
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User findOrCreateOAuthUser(String email, String name) {
    	if (email == null || name == null) {
            throw new IllegalArgumentException("Email e nome non possono essere null");
        }
    	
        // Prima cerca se esiste già un utente con questa email
        User existingUser = userRepository.findByEmail(email);
        
        if (existingUser != null) {
            return existingUser;
        }
        
        // Se non esiste, crea un nuovo utente
     // Crea nuovo utente
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setNome(name);
        // Imposta altri campi necessari
        
        return userRepository.save(newUser);
    }


}
