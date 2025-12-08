package org.axlie.projectL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.lingala.zip4j.ZipFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.List;
import java.util.prefs.Preferences;

public class Main extends JFrame {
    //farme system
    //cardlayout hronit neskolko frameov
    private CardLayout layout;
    private JPanel rootPanel;
    // auth ui
    private final AuthService authService = new AuthService();
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    JCheckBox rememberMeCheck;

    //change coment tima
    // Цветовые константы для стилизации
    private static final Color BG_DARK = new Color(20, 20, 30, 230);
    private static final Color ACCENT_BLUE = new Color(70, 130, 180);
    private static final Color ACCENT_RED = new Color(250, 3, 32);
    //launch frame frame and buttons
    private JPanel launcherPanel;
    private CustomButton settings;
    private CustomButton actButton;
    private JProgressBar progBar;


    // tima coment
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
    Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
    private String destination;
    private long modLen;
    private long assetLen;
    String token = prefs.get("authToken", null);

    //constructor for frames
    public Main() {
        setTitle("Pixel Gate");
        setSize(850, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //set ico
        try {
            Image iconImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resourceRoot/icon.png"));
            setIconImage(iconImage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //path memory
        Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
        destination = prefs.get("minecraftPath", System.getenv("APPDATA"));

        // frame sozdanie
        layout = new CardLayout();
        rootPanel = new JPanel(layout);
        add(rootPanel);

        // add frames login and launch
        rootPanel.add(createLoginScreen(), "login");
        rootPanel.add(createLauncherScreen(), "launcher");

        //token memory
        String token = prefs.get("authToken", null);
        //vizivaem method for check our token
        if (token != null && validateToken(token)) {
            layout.show(rootPanel, "launcher");
        } else {
            //vizivaem method log out dlja smeni frame and clear memory ot nashego tokena
            handleLogout();
        }

    }

    //frame login
    private JPanel createLoginScreen() {
        JPanel bgPanel = new JPanel() {
            Image bg = new ImageIcon(getClass().getResource("/resourceRoot/GIF.gif")).getImage();

            //tima comment
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(new BorderLayout());
        //tima comment
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

        ImageIcon eyeOpen = new ImageIcon(getClass().getResource("/resourceRoot/eye_open.png"));
        ImageIcon eyeClosed = new ImageIcon(getClass().getResource("/resourceRoot/eye_closed.png"));
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
        //call method handle login
        loginBtn.addActionListener(this::handleLogin);

        JButton registerBtn = new OvalButton("Register", mainFont, new Color(0, 200, 83), new Color(0, 150, 56));
        registerBtn.setPreferredSize(new Dimension(110, 38));
        //call method register
        registerBtn.addActionListener(this::handleRegister);
        rememberMeCheck = new JCheckBox("remember me");
        rememberMeCheck.setForeground(Color.WHITE);
        c.gridx = 1;
        c.gridy = 5;
        c.gridwidth = 2;

        formPanel.add(rememberMeCheck, c);
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

    //frame launcher
    private JPanel createLauncherScreen() {
        //bordearlayout is basic fomating layout commands EAST WEST and other
        launcherPanel = new JPanel(new BorderLayout()) {
            Image bg = new ImageIcon(getClass().getResource("/resourceRoot/GIF.gif")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Нижняя полупрозрачная панель
        JPanel bottomPanel = new JPanel(new BorderLayout()) {
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
        bottomPanel.setPreferredSize(new Dimension(850, 120)); // уменьшили высоту
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0)); // сдвиг прогресса вниз

        // progress bar
        progBar = new JProgressBar(0, 100);
        progBar.setStringPainted(true);
        //tima comment
        progBar.setFont(new Font("Arial", Font.BOLD, 14));
        progBar.setPreferredSize(new Dimension(600, 30)); // длиннее прогресс-бар
        //same
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        //same
        settings = new CustomButton("", ACCENT_BLUE, ACCENT_BLUE.brighter());
        settings.setIcon(new ImageIcon(getClass().getResource("/resourceRoot/settings.png")));
        settings.setText(null);
        settings.setBorder(null);
        settings.setContentAreaFilled(false);
        settings.setPreferredSize(new Dimension(40, 40));
        //vizivaem pathservis method
        settings.addActionListener(e -> pathServis());
        //zadaem path to minecraft and do check for est li folder minecraft and if est set button play and call method for launch mine else download
        Path mc = Paths.get(destination, ".minecraft");
        if (Files.exists(mc)) {
            actButton = new CustomButton("Play", ACCENT_RED, ACCENT_RED.brighter());
            actButton.addActionListener(e -> checkAndLunch());
        } else {
            actButton = new CustomButton("Download", ACCENT_BLUE, ACCENT_BLUE.brighter());
            actButton.addActionListener(e -> buttonDo());
        }
        //tima comment
        buttonPanel.add(settings);
        buttonPanel.add(actButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(progBar, BorderLayout.EAST); // прогресс-бар справа

        launcherPanel.add(bottomPanel, BorderLayout.SOUTH);
        //add logout button to log out from account
        CustomButton logoutButton = new CustomButton("Logout", new Color(200, 50, 50), new Color(255, 80, 80));
        logoutButton.addActionListener(e -> handleLogout());
        buttonPanel.add(logoutButton);

        return launcherPanel;
    }

    //login frame methods (mb i raplace it to login service class)
    //for this method tima comment
    private void styleField(JTextField field, Font font) {
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(0, 0, 0));
        field.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        field.setFont(font);
        if (field instanceof JPasswordField) ((JPasswordField) field).setEchoChar('•');
    }

    //same
    private static class OvalButton extends JButton {
        private final Color normalColor;
        private final Color hoverColor;

        //same
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

        //same
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            super.paintComponent(g2);
            g2.dispose();
        }

        //tima
        @Override
        public void paintBorder(Graphics g) {
        }
    }

    //this method do post  method for validation the token


    //methor for validathing token by sravnenie otveta s servera
    private boolean validateToken(String token) {
        String response = authService.sendToken("http://localhost:8080/api/validate", token);
        return "success".equals(response);
    }

    //method dlja regisration
    private void handleRegister(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        statusLabel.setText(authService.sendPostReg("http://localhost:8080/api/registration", username, password));
    }

    //method dlja login
    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        //ispolzuetsa dva vida tokena 12 hours and 7 days for auto log in and if remeber me is selected we get 7 days token
        boolean rememberMe = rememberMeCheck.isSelected();
        String response = authService.sendPost("http://localhost:8080/api/login", username, password, rememberMe);
        //na servere response v vide json object dlja poluchenia tokena otdelno
        JsonObject json = new Gson().fromJson(response, JsonObject.class);
        //proverka na to chto status is succes
        if (json.get("status").getAsString().equals("success")) {
            //vitaskivaem iz json token and putting it in config storage
            String token = json.get("token").getAsString();
            Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
            prefs.put("authToken", token);
            prefs.put("savedUsername", username);
            statusLabel.setText("succesfully logged in");
            layout.show(rootPanel, "launcher");
        } else {
            //tk na servere net razdelenia dlja error statusa libo ne veren username libo password
            if (json.get("status").getAsString().equals("error")) {
                statusLabel.setText("wrong username or password");
            }
        }
    }

    //logout method
    private void handleLogout() {
        // token delete
        Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
        prefs.remove("authToken");
        prefs.remove("savedUsername");
        //pocazivaem login frame
        layout.show(rootPanel, "login");

        // clear fields
        usernameField.setText("");
        passwordField.setText("");
        rememberMeCheck.setSelected(false);

        // messege for user
        statusLabel.setText("Logged out successfully");
    }
    //method for check size of asset and mod folders and launch minecraft
    private void checkAndLunch() {
        Path mc = Paths.get(destination, ".minecraft");
        if (Files.exists(mc)) {
            try {
                //files walk return the stream<path> it contains all files and derictories in it
                modLen = Files.walk(Paths.get(destination, ".minecraft", "mods"))
                        //filtruem na obichnie faili
                        .filter(Files::isRegularFile)
                        //maptolong preobrazuet vse path to long(size) lamda p is kazdiy path
                        .mapToLong(p -> {
                            try {
                                //return size in bytes
                                return Files.size(p);
                            } catch (IOException e) {
                                return 0;
                            }
                        }).sum();
                //same as mod len
                assetLen = Files.walk(Paths.get(destination, ".minecraft", "assets"))
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try {
                                return Files.size(p);
                            } catch (IOException e) {
                                return 0;
                            }
                        }).sum();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //proverka na sovpadenie size of folders
            if (modLen == 24815086 && assetLen == 334221916) {
                //path to bat file aka launch mine command(waiting danja to replace it by code command)
                String bat = destination + "\\.minecraft\\launchers\\start_forge_1.16.5.bat";
                try {
                    //processbuilder is class of java that can start vneshnie programs v dannom sluchae .bat
                    //.start zapuskaet bat
                    new ProcessBuilder(bat).start();
                    //zacrivaem launcher chtobi ne conflictovalo s FML early loading proccess
                    System.exit(0);
                } catch (IOException a) {
                    a.printStackTrace();
                }
            } else {
                //esli et modifikacii predlogaem pereustonovit minecraft or polzovatel dolzen v ruchnuy udalit modifikacii(otdelnuy papku s savami ne sdelal:) po etomu udaljautsa)
                JOptionPane.showMessageDialog(this, "Delete custom mods or assets from .minecraft.");
                int result = JOptionPane.showConfirmDialog(null,
                        "do you want to reinstall mine craft? (your saves will be lost)",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                //esli v paneli vibrana knopka yes vizivaem method download minecraft
                if (result == JOptionPane.YES_OPTION) {
                    buttonDo();
                } else {
                    JOptionPane.showMessageDialog(this, "clear your modifications");
                }
            }
        }
    }
    //method for choose path where minecraft budet ustanovlen(panel s viborom bila vzata so stackoverflow)
    private void pathServis() {
        //poluchaem path gde ustanovlen minecraft i do check ustanovlen li tam minecraft esli da to sprashivaem hochet li user pereustanovit minecraft
        Path path = Paths.get(destination);
        if (Files.exists(path)) {
            int result = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to change the Minecraft installation location? (Your saves will be lost.)",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                String newPath = new MainPane().showDialog();
                if (newPath == null || newPath.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "path not selected");
                    return;
                }
                //stariy path zapisivaem v otdelniy string i perezapisivaem main destination of minecraft and config container
                String oldPath = destination;
                destination = newPath;
                File oldMine = new File(oldPath + "\\.minecraft");
                Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
                prefs.put("minecraftPath", newPath);
                //udaljaem stariy mincraft
                if (oldMine.exists()) {
                    delFolder(oldMine);
                }
                actButton.setText("Download");
                actButton.addActionListener(e -> buttonDo());
                JOptionPane.showMessageDialog(this, "path changed now you can download it again");
            }
        }
    }
    //vot tut pipjau
    private void buttonDo() {
        actButton.setEnabled(false);
        progBar.setVisible(true);
        //swing worker is class in swing that razdeljaet proccesi chtobi ne zamorazivat frame i izbezat conflictov integer for show procces of vipolnenija
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws IOException {
                //hotel skachivat vse modi no v sledstvie peredumal i ne ubral list
                List<String> urls = List.of("http://localhost:8080/docs/download/1");
                // zadajom derictoriju kuda budet ustanovlen minecraft
                Path outputDir = Paths.get(destination);
                Files.createDirectories(outputDir);
                //ostatok ot lista
                int allFiles = urls.size();
                int fileCount = 0;
                //same
                for (String link : urls) {
                    fileCount++;
                    //url download
                    URL url = new URL(link);
                    //open coonection to url
                    URLConnection conn = url.openConnection();
                    //content disposition is http heder kotoriy otpravljaet server dlja geting name of file
                    String disposition = conn.getHeaderField("Content-Disposition");
                    String fileName = "";
                    //zadaem nazvanie file
                    if (disposition != null) {
                        fileName = disposition.split("filename=")[1].replace("\"", "").trim();
                    }

                    Path output = outputDir.resolve(fileName);
                    //inputstream poluchaet stream of data
                    //buffer dlja uscorenija processa
                    //outputstream opens file to write and output to file is path where we save file
                    try (InputStream in = new BufferedInputStream(conn.getInputStream());
                         OutputStream out = new FileOutputStream(output.toFile())) {
                        //buffer is buffer for reading read is kolichestvo bitov total is how much is dwnloaded length is polniy size of file
                        byte[] buffer = new byte[4096];
                        int read;
                        long total = 0;
                        long length = conn.getContentLengthLong();
                        // in read reads bytes v buffer out write write from buffer to out stream
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                            //schetchic
                            total += read;
                            //proverka dlja uvelichenija prochentov oin progress bar
                            if (length > 0) {
                                int percent = (int) (((fileCount - 1 + (double) total / length) / allFiles) * 100);
                                publish(percent);
                            }
                        }
                        //chastichno ostatok ot lista || proverjaem file na nalichie .zip i unzipping it
                        if (fileName.toLowerCase().endsWith(".zip")) {
                            ZipFile zipFile = new ZipFile(output.toFile());
                            zipFile.extractAll(destination);
                            zipFile.close();
                        }
                    }
                    //upaljaem zip after unzipping
                    Path mineZip = Paths.get(destination + "\\.minecraft.zip");
                    if (fileName.toLowerCase().endsWith(".zip")) {
                        Files.deleteIfExists(mineZip);
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                //kazdiy vizov publish() adds value to ochered v dannom sluchae berjom last value of progress and update progressbar without lag bc its Event Dispatch Thread()EDT
                progBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            //perenaznachaem text, deistvie i delaem activnoi button
            protected void done() {
                progBar.setValue(100);
                actButton.setText("Play");
                actButton.setEnabled(true);

                actButton.addActionListener(e -> checkAndLunch());
            }
        };
        //sozdaem fonoviy potok
        worker.execute();
    }
    //method dlja udalenija folder(used for udalenija pri pereustanovke)
    public static void delFolder(File folder) {
        if (!folder.exists()) return;
        //method listfiles returns massive File[] kotoriy soderzit vse v folder
        File[] files = folder.listFiles();
        //proverka na to chto folder sushestvuet dalshe perebiraem kazdiy object
        if (files != null) for (File file : files) {
            //proverka na to javljaetsa li object folderom i udaljaem file
            if (file.isDirectory()) delFolder(file);
            else file.delete();
        }
        folder.delete();
    }
    //zapuskaem frame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}

