package org.axlie.projectL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
        background.setLayout(new BorderLayout());
        setContentPane(background);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(330, 400));
        formPanel.setOpaque(true);
        formPanel.setBackground(new Color(0, 0, 0, 120));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 15, 10, 15);
        c.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 0;
        formPanel.add(usernameLabel, c);

        usernameField = new JTextField();
        usernameField.setFont(fieldFont);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        usernameField.setBackground(new Color(20, 20, 20));
        usernameField.setForeground(Color.WHITE);
        c.gridy = 1;
        formPanel.add(usernameField, c);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.WHITE);
        c.gridy = 2;
        formPanel.add(passwordLabel, c);


        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setOpaque(false);

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        passwordField.setBackground(new Color(20, 20, 20));
        passwordField.setForeground(Color.WHITE);
        passwordField.setEchoChar('•');

        JButton eyeButton = new JButton();
        eyeButton.setBorder(null);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setFocusPainted(false);
        eyeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeButton.setPreferredSize(new Dimension(40, 30));

        ImageIcon eyeOpen = new ImageIcon(getClass().getResource("/eye_open.png"));
        ImageIcon eyeClosed = new ImageIcon(getClass().getResource("/eye_closed.png"));
        eyeButton.setIcon(eyeClosed);

        eyeButton.addActionListener(ev -> {
            if (passwordField.getEchoChar() == 0) {
                passwordField.setEchoChar('•');
                eyeButton.setIcon(eyeClosed);
            } else {
                passwordField.setEchoChar((char)0);
                eyeButton.setIcon(eyeOpen);
            }
        });

        passPanel.add(passwordField, BorderLayout.CENTER);
        passPanel.add(eyeButton, BorderLayout.EAST);

        c.gridy = 3;
        formPanel.add(passPanel, c);


        JButton loginButton = new JButton("Login");
        styleButton(loginButton);

        JButton registerButton = new JButton("Register");
        styleButton(registerButton);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        c.gridy = 4;
        c.insets = new Insets(20, 15, 10, 15);
        formPanel.add(buttonPanel, c);


        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(labelFont);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setPreferredSize(new Dimension(200, 30));

        c.gridy = 5;
        c.insets = new Insets(5, 15, 20, 15);
        formPanel.add(statusLabel, c);

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(formPanel, BorderLayout.CENTER);

        background.add(rightWrapper, BorderLayout.EAST);

        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(this::handleRegister);
    }


    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);

        Color normal = new Color(93, 27, 191);
        Color hover = new Color(123, 57, 221);

        btn.setPreferredSize(new Dimension(120, 40));
        btn.setBackground(normal);

        btn.addMouseListener(new MouseAdapter() {
            Timer timer;
            float progress = 0f;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new Timer(15, ev -> {
                    progress += 0.08f;
                    if (progress > 1f) progress = 1f;
                    btn.setBackground(interpolate(normal, hover, progress));
                    btn.repaint();
                    if (progress >= 1f) timer.stop();
                });
                timer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new Timer(15, ev -> {
                    progress -= 0.08f;
                    if (progress < 0f) progress = 0f;
                    btn.setBackground(interpolate(normal, hover, progress));
                    btn.repaint();
                    if (progress <= 0f) timer.stop();
                });
                timer.start();
            }
        });

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = btn.getWidth();
                int h = btn.getHeight();
                int arc = h;

                g2.setColor(btn.getBackground());
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

                super.paint(g2, c);
                g2.dispose();
            }
        });
    }

    private Color interpolate(Color c1, Color c2, float t) {
        t = Math.min(1f, Math.max(0f, t));
        int r = (int)(c1.getRed() + (c2.getRed() - c1.getRed()) * t);
        int g = (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
        int b = (int)(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
        return new Color(r, g, b);
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
            return "Connection error";
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
