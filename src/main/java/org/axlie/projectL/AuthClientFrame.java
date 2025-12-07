package org.axlie.projectL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class AuthClientFrame extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    public AuthClientFrame() {
        setTitle("PixelGate");
        setSize(850, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        try {
            Image iconImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png"));
            setIconImage(iconImage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JPanel mainPanel = new JPanel() {
            Image bg = new ImageIcon(getClass().getResource("/GIF.gif")).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(30, 30, 30, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHEAST;

        Font titleFont = new Font("Minecraft Rus", Font.BOLD, 20);
        Font mainFont = new Font("Minecraft Rus", Font.PLAIN, 13);

        JLabel title = new JLabel("Login / Register", SwingConstants.LEFT);
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        formPanel.add(title, c);
        c.gridwidth = 1;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(mainFont);
        c.gridx = 0;
        c.gridy = 1;
        formPanel.add(usernameLabel, c);

        usernameField = new JTextField();
        styleField(usernameField, mainFont);
        usernameField.setOpaque(true);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        formPanel.add(usernameField, c);
        c.gridwidth = 1;

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(mainFont);
        c.gridx = 0;
        c.gridy = 3;
        formPanel.add(passwordLabel, c);

        passwordField = new JPasswordField();
        styleField(passwordField, mainFont);
        passwordField.setOpaque(true);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        formPanel.add(passwordField, c);
        c.gridwidth = 1;

        JButton showBtn = new JButton();
        showBtn.setPreferredSize(new Dimension(30, 22));
        showBtn.setFocusPainted(false);
        showBtn.setContentAreaFilled(false);
        showBtn.setBorder(null);
        showBtn.setForeground(Color.WHITE);

        ImageIcon eyeOpen = new ImageIcon(getClass().getResource("/eye_open.png"));
        ImageIcon eyeClosed = new ImageIcon(getClass().getResource("/eye_closed.png"));
        showBtn.setIcon(eyeClosed);

        showBtn.addActionListener(ev -> {
            if (passwordField.getEchoChar() == '•') {
                passwordField.setEchoChar((char) 0);
                showBtn.setIcon(eyeOpen);
            } else {
                passwordField.setEchoChar('•');
                showBtn.setIcon(eyeClosed);
            }
        });

        c.gridx = 2;
        c.gridy = 4;
        formPanel.add(showBtn, c);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonsPanel.setOpaque(false);

        JButton loginBtn = new OvalButton("Login", mainFont, new Color(0, 122, 255), new Color(0, 79, 246));
        loginBtn.setPreferredSize(new Dimension(110, 38));
        loginBtn.addActionListener(this::handleLogin);

        JButton registerBtn = new OvalButton("Register", mainFont, new Color(0, 200, 83), new Color(0, 150, 56));
        registerBtn.setPreferredSize(new Dimension(110, 38));
        registerBtn.addActionListener(this::handleRegister);

        buttonsPanel.add(loginBtn);
        buttonsPanel.add(registerBtn);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        formPanel.add(buttonsPanel, c);
        c.gridwidth = 1;

        statusLabel = new JLabel(" ", SwingConstants.LEFT);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(mainFont);
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        formPanel.add(statusLabel, c);

        mainPanel.add(formPanel, BorderLayout.EAST);
    }

    private void styleField(JTextField field, Font font) {
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(0, 0, 0));
        field.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        field.setFont(font);
        if (field instanceof JPasswordField) ((JPasswordField) field).setEchoChar('•');
    }

    static class OvalButton extends JButton {
        private final Color normalColor;
        private final Color hoverColor;

        public OvalButton(String text, Font font, Color normalColor, Color hoverColor) {
            super(text);
            setFont(font);
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(hoverColor);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(normalColor);
                }
            });

            setBackground(normalColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void paintBorder(Graphics g) {}
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

    private void handleRegister(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        statusLabel.setText(sendPost("http://localhost:8080/api/registration", username, password));
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        statusLabel.setText(sendPost("http://localhost:8080/api/login", username, password));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AuthClientFrame().setVisible(true));
    }
}



