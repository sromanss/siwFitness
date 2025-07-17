package it.uniroma3.siw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private CredentialsService credentialsService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Verifica se l'admin esiste già
        if (credentialsService.findByUsername("admin") == null) {
            // Crea le credenziali admin SENZA associare l'utente
            Credentials adminCredentials = new Credentials();
            adminCredentials.setUsername("admin");
            adminCredentials.setPassword("admin"); 
            adminCredentials.setRole(Credentials.ADMIN_ROLE);

            // Crea l'utente admin
            User adminUser = new User();
            adminUser.setNome("Admin");
            adminUser.setCognome("Sistema");
            adminUser.setEmail("admin@fitness.com");
            adminUser.setRole(User.Role.ADMIN); // Imposta il ruolo ADMIN

            // Associa l'utente alle credenziali PRIMA del salvataggio
            adminCredentials.setUser(adminUser);

            // Salva solo le credenziali (l'utente verrà salvato automaticamente per cascade)
            credentialsService.save(adminCredentials);

            System.out.println("✅ Utente amministratore creato con successo!");
            System.out.println("Username: admin");
            System.out.println("Password: admin");
        } else {
            System.out.println("ℹ️ Utente amministratore già esistente.");
        }
        
        // Crea utente di test per debugging
        if (credentialsService.findByUsername("testuser") == null) {
            Credentials testCredentials = new Credentials();
            testCredentials.setUsername("testuser");
            testCredentials.setPassword("test123");
            testCredentials.setRole(Credentials.DEFAULT_ROLE);

            User testUser = new User();
            testUser.setNome("Test");
            testUser.setCognome("User");
            testUser.setEmail("test@fitness.com");
            testUser.setRole(User.Role.DEFAULT);

            testCredentials.setUser(testUser);
            credentialsService.save(testCredentials);

            System.out.println("✅ Utente di test creato!");
            System.out.println("Username: testuser");
            System.out.println("Password: test123");
        }
    }
}
