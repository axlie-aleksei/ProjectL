package org.axlie.projectl.launcher_project;


import jakarta.persistence.*;


@Entity
public class Username {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    //one to one is odin user have one password, mapped by ukazivaet chto vladelec svjazi in entity password
    //cascade ucazivaet chto vse operations for user primenjautsja k password
    @OneToOne(mappedBy = "username", cascade = CascadeType.ALL)
    private Password password;

    public Username(String username) {
        this.username = username;
    }

    public Username() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
