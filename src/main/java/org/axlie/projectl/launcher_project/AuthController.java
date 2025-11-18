package org.axlie.projectl.launcher_project;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/registration")
    public String registration(@RequestParam String username, @RequestParam String password) {
        if (password.length() <= 5) {
            return "password cannot be less than 6 characters";
        } else if (!username.matches("^[a-zA-Z0-9]+$")) {
            return "You are cannot enter this symbols(!@#$%^&*()_+-<>?|,./[]=";
        }else if (!password.matches("^[^\\p{IsCyrillic}\s]+$")){
            return "ebanat?";
        }else
            return authService.registration(username, password);
        }


    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return authService.login(username, password);
    }

}
