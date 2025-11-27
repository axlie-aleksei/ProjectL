package org.axlie.projectL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

public class LaunchMine extends JFrame {

    private final JButton actButton;
    private final JProgressBar progBar;

    public LaunchMine() {
        setTitle("Axlie Project L");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel() {
            Image bg = new ImageIcon(getClass().getResource("/minecraft.png")).getImage();
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        progBar = new JProgressBar(0, 100);
        progBar.setStringPainted(true);
        progBar.setVisible(false);
        progBar.setPreferredSize(new Dimension(300, 20));
        bottomPanel.add(progBar);

        actButton = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource("/DOWNLOAD.png"))));
        actButton.setPreferredSize(new Dimension(200, 40));
        actButton.setFocusPainted(false);
        actButton.setContentAreaFilled(false);
        actButton.setBorder(null);
        actButton.addActionListener(e -> buttonDo());
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(actButton);

        formPanel.add(bottomPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
    }

    public void buttonDo() {
        actButton.setEnabled(false);
        progBar.setVisible(true);

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                URL url = new URL("http://localhost:8080/docs/download/4");
                InputStream in = url.openStream();
                URLConnection conn = url.openConnection();

                String disposition = conn.getHeaderField("Content-Disposition");
                String fileName = "downloaded_file.bin";
                if (disposition != null) {
                    fileName = disposition.split("filename=")[1].replace("\"", "").trim();
                }

                Path outputDir = Paths.get("C:\\downloads");
                Files.createDirectories(outputDir);

                Path output = outputDir.resolve(fileName);

                try (BufferedInputStream bis = new BufferedInputStream(in);
                     OutputStream out = new FileOutputStream(output.toFile())) {

                    byte[] buffer = new byte[4096];
                    int read;
                    long total = 0;

                    long length = url.openConnection().getContentLength();
                    while ((read = bis.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        total += read;

                        if (length > 0) {
                            int percent = (int) ((total * 100) / length);
                            publish(percent);
                        }
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
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LaunchMine().setVisible(true));
    }
}



