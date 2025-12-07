package org.axlie.projectL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;
import net.lingala.zip4j.ZipFile;

public class LaunchMine extends JFrame {

    // Цветовые константы для стилизации
    private static final Color BG_DARK = new Color(20, 20, 30, 230);
    private static final Color ACCENT_BLUE = new Color(70, 130, 180);
    private static final Color ACCENT_GREEN = new Color(60, 179, 113);

    private CustomStyledButton settings;
    private CustomStyledButton actButton;
    private final JProgressBar progBar;
    String destination;
    private long modLen;
    private long assetLen;

    // Класс для кастомных стилизованных кнопок
    private static class CustomStyledButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private Color currentColor;

        public CustomStyledButton(String text, Color base, Color hover) {
            super(text);
            this.baseColor = base;
            this.hoverColor = hover;
            this.currentColor = base;
            

            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 16));
            setPreferredSize(new Dimension(150, 40));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    currentColor = hoverColor;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    currentColor = baseColor;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isEnabled()) {
                g2.setColor(currentColor);
            } else {
                g2.setColor(new Color(100, 100, 100)); // Цвет для неактивной кнопки
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Закругленные углы

            super.paintComponent(g);
            g2.dispose();
        }
    }

    // Утилита — обновление/замена actButton в UI
    private void updateActionButtonState(String text, ActionListener action) {
        Container parent = (actButton != null) ? actButton.getParent() : null;

        // Создаём новый actButton по тексту (цвета по смыслу)
        CustomStyledButton newButton;
        if ("Launch".equalsIgnoreCase(text)) {
            newButton = new CustomStyledButton(text, ACCENT_GREEN, ACCENT_GREEN.brighter());
        } else {
            newButton = new CustomStyledButton(text, ACCENT_BLUE, ACCENT_BLUE.brighter());
        }
        newButton.addActionListener(action);
        newButton.setEnabled(true);

        // Если старый был, заменим в родителе
        if (parent != null) {
            // Найдём индекс старого и заменим
            parent.remove(actButton);
            parent.add(newButton);
            parent.revalidate();
            parent.repaint();
        }

        actButton = newButton;
    }

    public LaunchMine() throws IOException {
        Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
        destination = prefs.get("minecraftPath", System.getenv("APPDATA"));

        setTitle("PixelGate");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            Image iconImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png"));
            setIconImage(iconImage);
        } finally {
            // ничего
        }

        // Главная панель с фоновым изображением (используем BorderLayout чтобы прижать formPanel в SOUTH)
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            Image bg = new ImageIcon(getClass().getResource("/GIF.gif")).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        setContentPane(mainPanel);

        // Полупрозрачная закруглённая панель, которая будет прижата в самый низ (SOUTH)
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK); // Полупрозрачный темный фон
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        formPanel.setOpaque(false);
        // Прижать форму в самый низ: делаем фиксированную высоту панели (например 160px)
        formPanel.setPreferredSize(new Dimension(850, 160));

        // Внутренние компоненты formPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        progBar = new JProgressBar(0, 100);
        progBar.setStringPainted(true);
        progBar.setVisible(true);
        progBar.setPreferredSize(new Dimension(250, 30));
        progBar.setFont(new Font("Arial", Font.BOLD, 14));

        // Кнопки
        settings = new CustomStyledButton("Path Change", ACCENT_BLUE, ACCENT_BLUE.brighter());

        // Логика определения начального состояния кнопки
        Path path = Paths.get(destination + "\\.minecraft");
        if (Files.exists(path)) {
            actButton = new CustomStyledButton("Launch", ACCENT_GREEN, ACCENT_GREEN.brighter());
            actButton.addActionListener(e -> checkAndLunch());
        } else {
            actButton = new CustomStyledButton("Download", ACCENT_BLUE, ACCENT_BLUE.brighter());
            actButton.addActionListener(e -> buttonDo());
        }

        // Разместим прогресс-бар сверху (в форме)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(progBar, gbc);

        // Создаём отдельную панель для кнопок (по центру), она будет внизу внутри formPanel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.add(settings);
        buttonPanel.add(actButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        formPanel.add(buttonPanel, gbc);

        // Добавляем formPanel в самый низ mainPanel
        mainPanel.add(formPanel, BorderLayout.SOUTH);

        settings.addActionListener(e -> pathServis());
    }

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

                // корректно заменим кнопку
                updateActionButtonState("Download", e -> buttonDo());
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
                List<String> urls = List.of(
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
                        String[] parts = disposition.split("filename=");
                        if (parts.length > 1) {
                            fileName = parts[1].replace("\"", "").trim();
                        }
                    }

                    if (fileName.isEmpty()) {
                        // fallback
                        fileName = "downloaded_" + fileCount + ".zip";
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
                // заменим кнопку на Launch
                updateActionButtonState("Launch", e -> checkAndLunch());
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new LaunchMine().setVisible(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}


