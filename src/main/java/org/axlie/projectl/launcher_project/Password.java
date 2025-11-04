package org.axlie.projectl.launcher_project;

import jakarta.persistence.*;

@Entity
public class Password {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String Password;

    @OneToOne
    @JoinColumn(name = "username_id")
    private Username username;



    public Username getUsername() {
        return username;
    }

    public void setUsername(Username username) {
        this.username = username;
    }

    public Password(String password) {
        Password = password;
    }

    public Password() {}

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
