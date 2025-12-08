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
    //method for register
    public String registration(String username, String password) {
        if (usernameRepository.findAll().stream().anyMatch(user -> user.getUsername().equals(username))) {
            return "Username is already in use";
        }

        Username user = new Username(username);
        user.setUsername(username);
        usernameRepository.save(user);
        //heshiruem password
        String hashedPassword = encoder.encode(password);
        //create new entity password
        Password pass = new Password();
        pass.setUsername(user);
        pass.setPassword(hashedPassword);
        passwordRepository.save(pass);

        return "User registered successfully";
    }
    //login method
    public String login(String username, String password, boolean rememberMe) {
        Username user = usernameRepository.findAll()
                //otkrivaem potok filtruem cherez sravnenie username naxodim pervoe sovpadenie
                .stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        //esli user null vozrashaem json status error
        if (user == null) {
            return "{ \"status\": \"error\", \"message\": \"User not found\" }";
        }

        Password pass = passwordRepository.findAll()
                //otkrivaem potok filtruem passwor na sovpadenie user naxodim pervoe i sravnivaem
                .stream()
                .filter(p -> p.getUsername().equals(user))
                .findFirst()
                .orElse(null);

        if (pass == null) {
            return "{ \"status\": \"error\", \"message\": \"Password not found\" }";
        }
        //esli passwor is incorrect status error
        if (!encoder.matches(password, pass.getPassword())) {
            return "{ \"status\": \"error\", \"message\": \"Wrong password\" }";
        }
        //generiruem token and return json
        String token = jwtService.generateToken(username, rememberMe);

        return "{ \"status\": \"success\", \"message\": \"Successfully logged in\", \"token\": \"" + token + "\" }";
    }

    }


