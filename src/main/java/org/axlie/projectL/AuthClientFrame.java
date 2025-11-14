package org.axlie.projectL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class AuthClientFrame extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    public AuthClientFrame() {
        setTitle("Super Launcher Login");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel background = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/minecraft.png"))));
        background.setLayout(new OverlayLayout(background));
        setContentPane(background);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setMaximumSize(new Dimension(330, 400));
        formPanel.setOpaque(true);
        formPanel.setBackground(new Color(0, 0, 0, 120)); // полупрозрачная тёмная панель
        formPanel.setBorder(null);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 15, 10, 15);
        c.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);


        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 0;
        formPanel.add(usernameLabel, c);

        usernameField = new JTextField();
        usernameField.setFont(fieldFont);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        usernameField.setOpaque(true);
        usernameField.setBackground(new Color(5, 5, 5, 70));
        usernameField.setForeground(Color.BLACK);
        setPlaceholder(usernameField, "Enter username");
        c.gridy = 1;
        formPanel.add(usernameField, c);


        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.WHITE);
        c.gridy = 2;
        formPanel.add(passwordLabel, c);

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        passwordField.setOpaque(true);
        passwordField.setBackground(new Color(0, 0, 0, 70));
        passwordField.setForeground(Color.BLACK);
        setPlaceholder(passwordField, "Enter password");
        c.gridy = 3;
        formPanel.add(passwordField, c);


        JButton loginButton = new JButton("Login");
        styleButton(loginButton);

        JButton registerButton = new JButton("Register");
        styleButton(registerButton);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        c.gridy = 4;
        formPanel.add(buttonPanel, c);


        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(labelFont);
        statusLabel.setForeground(Color.WHITE);
        c.gridy = 5;
        formPanel.add(statusLabel, c);

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(formPanel, BorderLayout.EAST);

        background.add(rightWrapper);


        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(this::handleRegister);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(93, 27, 191));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void setPlaceholder(JTextField field, String text) {
        field.setText(text);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(text)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(text);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private String sendPost(String urlStr, String username, String password) {
        try {
            String fullUrl = urlStr + "?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                    + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);

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

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        String response = sendPost("http://localhost:8080/api/login", username, password);

        if (response.startsWith("eyJ")) {
            statusLabel.setText("Login successful!");
        } else {
            statusLabel.setText("Login Failed");
        }
    }

    private void handleRegister(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String response = sendPost("http://localhost:8080/api/registration", username, password);

        statusLabel.setText(response);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AuthClientFrame().setVisible(true));
    }
}
