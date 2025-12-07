package org.axlie.projectL;

import net.lingala.zip4j.ZipFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

public class Main extends JFrame {
    //farme system
    private CardLayout layout;
    private JPanel rootPanel;
    // auth ui
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    // luncher ui

    // Цветовые константы для стилизации
    private static final Color BG_DARK = new Color(20, 20, 30, 230);
    private static final Color ACCENT_BLUE = new Color(70, 130, 180);
    private static final Color ACCENT_GREEN = new Color(60, 179, 113);

    private JPanel launcherPanel;
    private CustomButton settings;
    private CustomButton actButton;
    private JProgressBar progBar;

    // Класс для кастомных стилизованных кнопок
    private static class CustomButton extends JButton {

        private Color base;
        private Color hover;
        private Color current;

        public CustomButton(String text, Color base, Color hover) {
            super(text);
            this.base = base;
            this.hover = hover;
            this.current = base;

            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 16));
            setPreferredSize(new Dimension(150, 40));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    current = hover;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    current = base;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(current);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            g2.dispose();
            super.paintComponent(g);
        }
    }
        // data
        private String destination;
        private long modLen;
        private long assetLen;

        public Main() {
            setTitle("Pixel Gate");
            setSize(850, 500);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            try {
                Image iconImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png"));
                setIconImage(iconImage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //path memory
            Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
            destination = prefs.get("minecraftPath", System.getenv("APPDATA"));

            // frame
            layout = new CardLayout();
            rootPanel = new JPanel(layout);
            add(rootPanel);

            // add frames
            rootPanel.add(createLoginScreen(), "login");
            rootPanel.add(createLauncherScreen(), "launcher");

            layout.show(rootPanel, "login");
        }

        private JPanel createLoginScreen() {
            JPanel bgPanel = new JPanel() {
                Image bg = new ImageIcon(getClass().getResource("/GIF.gif")).getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            bgPanel.setLayout(new BorderLayout());

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

            JButton loginBtn = new AuthClientFrame.OvalButton("Login", mainFont, new Color(0, 122, 255), new Color(0, 79, 246));
            loginBtn.setPreferredSize(new Dimension(110, 38));
            loginBtn.addActionListener(this::handleLogin);

            JButton registerBtn = new AuthClientFrame.OvalButton("Register", mainFont, new Color(0, 200, 83), new Color(0, 150, 56));
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

            bgPanel.add(formPanel, BorderLayout.EAST);
            return bgPanel;
        }

        private JPanel createLauncherScreen() {
            // launcherPanel с BorderLayout
            launcherPanel = new JPanel(new BorderLayout()) {
                Image bg = new ImageIcon(getClass().getResource("/GIF.gif")).getImage();
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            };

            // Нижняя полупрозрачная панель
            JPanel bottomPanel = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BG_DARK);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            bottomPanel.setOpaque(false);
            bottomPanel.setPreferredSize(new Dimension(850, 160));

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(10, 20, 10, 20);
            c.fill = GridBagConstraints.HORIZONTAL;

            // Progress bar
            progBar = new JProgressBar(0, 100);
            progBar.setStringPainted(true);
            progBar.setFont(new Font("Arial", Font.BOLD, 14));
            c.gridx = 0;
            c.gridy = 10;
            c.gridwidth = 2;
            bottomPanel.add(progBar, c);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
            buttonPanel.setOpaque(false);

            settings = new CustomButton("Path Change", ACCENT_BLUE, ACCENT_BLUE.brighter());
            settings.addActionListener(e -> pathServis());

            Path mc = Paths.get(destination, ".minecraft");
            if (Files.exists(mc)) {
                actButton = new CustomButton("Launch", ACCENT_GREEN, ACCENT_GREEN.brighter());
                actButton.addActionListener(e -> checkAndLunch());
            } else {
                actButton = new CustomButton("Download", ACCENT_BLUE, ACCENT_BLUE.brighter());
                actButton.addActionListener(e -> buttonDo());
            }

            buttonPanel.add(settings);
            buttonPanel.add(actButton);

            c.gridy = 1;
            c.gridwidth = 2;
            bottomPanel.add(buttonPanel, c);

            launcherPanel.add(bottomPanel, BorderLayout.SOUTH);

            return launcherPanel;
        }

        //login methods
        private void styleField(JTextField field, Font font) {
            field.setForeground(Color.WHITE);
            field.setBackground(new Color(0, 0, 0));
            field.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            field.setFont(font);
            if (field instanceof JPasswordField) ((JPasswordField) field).setEchoChar('•');
        }

        private static class OvalButton extends JButton {
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
            public void paintBorder(Graphics g) {
            }
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
        String response = sendPost("http://localhost:8080/api/login", username, password);

        statusLabel.setText(response);
        System.out.println(response);

        // Проверка успешного логина
        if (response != null && response.toLowerCase().contains("succesfully logged in")) {

            // Переключение на лаунчер
            SwingUtilities.invokeLater(() -> {
                layout.show(rootPanel, "launcher");
                rootPanel.revalidate();
                rootPanel.repaint();
            });
        }
    }

        //launcher methods

    // Переопределение updateActionButtonState для работы с CustomStyledButton
    private void updateActionButtonState(String text, ActionListener action) {
        // Удаляем старый actButton
        Container parent = actButton.getParent();
        if (parent != null) {
            parent.remove(actButton);
        }

        // Создаем новый actButton с соответствующей стилизацией
        if (text.equals("Launch")) {
            actButton = new CustomButton(text, ACCENT_GREEN, ACCENT_GREEN.brighter());
        } else {
            actButton = new CustomButton(text, ACCENT_BLUE, ACCENT_BLUE.brighter());
        }

        actButton.addActionListener(action);
        actButton.setEnabled(true);

        // Добавляем новый actButton обратно на родительскую панель
        if (parent != null) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 1;
            gbc.gridy = 1;
            parent.add(actButton, gbc);
            parent.revalidate();
            parent.repaint();
        }
    }

    // ... (Методы delFolder,checkAndLunch, pathServis, buttonDo и main остаются без изменений)
    // Эти методы не содержат логики дизайна, поэтому их можно оставить как в предыдущем варианте.

    public static void delFolder(File folder) {
        if (!folder.exists()) return;

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    delFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    private void checkAndLunch(){
        if (Files.exists(Paths.get(destination + "\\.minecraft"))) {
            try {
                this.modLen = Files.walk(Paths.get(destination, ".minecraft", "mods"))
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try { return Files.size(p); }
                            catch (IOException e) { return 0; }
                        }).sum();

                this.assetLen = Files.walk(Paths.get(destination, ".minecraft", "assets"))
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try { return Files.size(p); }
                            catch (IOException e) { return 0; }
                        }).sum();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (modLen == 24815086 && assetLen == 334221916){
                String bat = destination + "\\.minecraft\\launchers\\start_forge_1.16.5.bat";

                try {
                    new ProcessBuilder(bat).start();
                    System.exit(0);

                } catch (IOException a) {
                    a.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this,
                        "Delete custom mods or assets from .minecraft.");
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "do you want to reinstall mine craft? (your saves will be lost)",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION){
                    buttonDo();
                }else {
                    JOptionPane.showMessageDialog(this, "clear your modifications");
                }
            }
        }
    }

    public void pathServis() {
        Path path = Paths.get(destination);
        if (Files.exists(path)) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to change the Minecraft installation location? (Your saves will be lost.)",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                MainPane pane = new MainPane();
                String newPath = pane.showDialog();

                if (newPath == null || newPath.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "path not selected");
                    return;
                }

                String oldPath = destination;
                destination = newPath;
                File oldMine = new File(oldPath + "\\.minecraft");

                Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
                prefs.put("minecraftPath", newPath);

                if (oldMine.exists()) {
                    delFolder(oldMine);
                }

                actButton.setText("Download");
                actButton.addActionListener(e -> buttonDo());
                JOptionPane.showMessageDialog(this, "path changed now you can download it again");
            }
        } else {
            MainPane pane = new MainPane();
            String newPath = pane.showDialog();

            if (newPath == null || newPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "path not selected");
                return;
            }

            String oldPath = destination;
            destination = newPath;
            Path oldMine = Paths.get(oldPath + "\\.minecraft");

            Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
            prefs.put("minecraftPath", newPath);

            if (Files.exists(oldMine)) {
                try {
                    Files.delete(oldMine);
                } catch (IOException e) {}
            }
        }
    }

    public void buttonDo() {
        actButton.setEnabled(false);
        progBar.setVisible(true);

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws IOException {
                java.util.List<String> urls = java.util.List.of(
                        "http://localhost:8080/docs/download/1"
                );

                Path outputDir = Paths.get(destination);
                Files.createDirectories(outputDir);

                int allFiles = urls.size();
                int fileCount = 0;

                for (String link : urls) {
                    fileCount++;

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    String disposition = conn.getHeaderField("Content-Disposition");
                    String fileName = "";

                    if (disposition != null) {
                        fileName = disposition.split("filename=")[1].replace("\"", "").trim();
                    }

                    Path output = outputDir.resolve(fileName);

                    try (InputStream in = new BufferedInputStream(conn.getInputStream());
                         OutputStream out = new FileOutputStream(output.toFile())) {

                        byte[] buffer = new byte[4096];
                        int read;
                        long total = 0;
                        long length = conn.getContentLengthLong();

                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                            total += read;

                            if (length > 0) {
                                int percent = (int) (((fileCount - 1 + (double) total / length) / allFiles) * 100);
                                publish(percent);
                            }
                        }
                        if (fileName.toLowerCase().endsWith(".zip")) {
                            ZipFile zipFile = new ZipFile(output.toFile());
                            zipFile.extractAll(destination);
                            zipFile.close();
                        }
                    }
                    Path mineZip = Paths.get(destination + "\\.minecraft.zip");
                    if (fileName.toLowerCase().endsWith(".zip")) {
                        Files.deleteIfExists(mineZip);
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                progBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                progBar.setValue(100);
                actButton.setText("Launch");
                actButton.setEnabled(true);

                actButton.addActionListener(e -> checkAndLunch());
            }
        };
        worker.execute();
    }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new Main().setVisible(true));
        }
    }

