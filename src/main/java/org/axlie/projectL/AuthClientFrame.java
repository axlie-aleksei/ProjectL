package org.axlie.projectL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class AuthClientFrame extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;


    public AuthClientFrame() {
        setTitle("Super Launcher Login");
        setSize(350,220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5,1,10,5));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        statusLabel = new JLabel(" ", SwingConstants.CENTER);

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(registerButton);
        add(statusLabel);

        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(this::handleRegister);
    }

    private String sendPost(String urlStr, String username, String password){
        try {
            String fullUrl = urlStr + "?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) + "&password="
                    + URLEncoder.encode(password, StandardCharsets.UTF_8);

            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLogin(ActionEvent e){
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        String response = sendPost("http://localhost:8080/api/login", username, password);

        if (response.startsWith("eyJ")){
            statusLabel.setText(response);
        }else{
            statusLabel.setText("Login Failed");
        }
    }

    private void handleRegister(ActionEvent e){
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String response = sendPost("http://localhost:8080/api/registration", username, password);

        statusLabel.setText(response);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {new AuthClientFrame().setVisible(true);});
    }

}



