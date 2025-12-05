package org.axlie.projectl.launcher_project;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordRepository passwordRepository;
    private final UsernameRepository usernameRepository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;

    public AuthService(PasswordRepository passwordRepository, UsernameRepository usernameRepository, JwtService jwtService) {
        this.passwordRepository = passwordRepository;
        this.usernameRepository = usernameRepository;
        this.jwtService = jwtService;
    }

    public String registration(String username, String password) {
        if (usernameRepository.findAll().stream().anyMatch(user -> user.getUsername().equals(username))) {
            return "Username is already in use";
        }

        Username user = new Username(username);
        user.setUsername(username);
        usernameRepository.save(user);

        String hashedPassword = encoder.encode(password);

        Password pass = new Password();
        pass.setUsername(user);
        pass.setPassword(hashedPassword);
        passwordRepository.save(pass);

        return "User registered successfully";
    }

    public String login(String username, String password) {
        Username user = usernameRepository.findAll()
                .stream()
                .filter(userr -> userr.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return "usernotfound"; //user not found
        }
        Password pass = passwordRepository.findAll()
                .stream()
                .filter(paass -> paass.getUsername().equals(user))
                .findFirst()
                .orElse(null);
        if (pass == null) {
            return "password not null"; //pass not found
        }

        if (!encoder.matches(password, pass.getPassword())) {
            return "wrong password";
        }

        if (encoder.matches(password, pass.getPassword())) {
            jwtService.generateToken(username);
        }

        return "succesfully logged in";

    }

}
