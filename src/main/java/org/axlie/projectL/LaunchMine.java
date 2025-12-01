package org.axlie.projectL;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

public class LaunchMine extends JFrame {

    private final JButton settings;
    private final JButton actButton;
    private final JProgressBar progBar;
    String destination = System.getenv("APPDATA");

    public LaunchMine() {
        Preferences prefs = Preferences.userRoot().node("AxlieProjectL");
        destination = prefs.get("minecraftPath", System.getenv("APPDATA"));
        setTitle("Axlie Project L");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        settings = new JButton("Download Path Change");
        actButton = new JButton("Launch");
        progBar = new JProgressBar(0, 100);
        progBar.setStringPainted(true);
        progBar.setVisible(true);

        JPanel center = new JPanel(new BorderLayout());
        center.add(progBar, BorderLayout.WEST);
        center.add(actButton);
        center.add(settings, BorderLayout.EAST);
        add(center, BorderLayout.EAST);
        Path path = Paths.get(destination + "\\.minecraft");
        if (Files.exists(path)) {
            actButton.addActionListener(e -> {
                String path1 = destination + "\\.minecraft\\launchers\\start_forge_1.16.5.bat\\";
                ProcessBuilder pb = new ProcessBuilder(path1);

                try {
                    pb.start();
                    System.exit(0);

                } catch (IOException a) {
                    a.printStackTrace();
                }
            });
        } else {
            actButton.setText("download");
            actButton.addActionListener(e -> buttonDo());
        }
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
                actButton.addActionListener(e -> {
                    buttonDo();
                });
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
                } catch (IOException e) {
                }
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
                        "http://localhost:8080/docs/download/6",
                        "http://localhost:8080/docs/download/5",
                        "http://localhost:8080/docs/download/4",
                        "http://localhost:8080/docs/download/3"
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
                            Files.deleteIfExists(output);
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

                actButton.addActionListener(e -> {
                    String path = destination + "\\.minecraft\\launchers\\start_forge_1.16.5.bat\\";
                    System.out.println(path);
                    ProcessBuilder pb = new ProcessBuilder(path);

                    try {
                        pb.start();
                        System.exit(0);

                    } catch (IOException a) {
                        a.printStackTrace();
                    }
                });
            }

        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LaunchMine().setVisible(true));
    }

}

