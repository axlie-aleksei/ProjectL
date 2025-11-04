package org.axlie.projectl.launcher_project;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    PasswordRepository passwordRepository;

    UsernameRepository usernameRepository;

    public AuthService(PasswordRepository passwordRepository, UsernameRepository usernameRepository) {
        this.passwordRepository = passwordRepository;
        this.usernameRepository = usernameRepository;
    }

    public String registration(String username, String password) {
        if (usernameRepository.findAll().stream().anyMatch(user -> user.getUsername().equals(username))) {
            return "Username is already in use";
        }

        Username user = new Username(username);
        user.setUsername(username);
        usernameRepository.save(user);

        Password pass = new Password();
        pass.setUsername(user);
        pass.setPassword(password);
        passwordRepository.save(pass);

        return "User registered successfully";
    }

    public boolean login(String username, String password) {
        Username user = usernameRepository.findAll()
                .stream()
                .filter(userr -> userr.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return false; //user not found
        }
        Password pass = passwordRepository.findAll()
                .stream()
                .filter(paass -> paass.getUsername().equals(user))
                .findFirst()
                .orElse(null);
        if (pass == null) {
            return false; //pass not found
        }

        return pass.getPassword().equals(password);
    }

}
