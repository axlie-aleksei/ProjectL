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

import net.lingala.zip4j.ZipFile;

public class LaunchMine extends JFrame {

    private final JButton actButton;
    private final JProgressBar progBar;

    public LaunchMine() {
        setTitle("Axlie Project L");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        actButton = new JButton("Launch");
        progBar = new JProgressBar(0, 100);
        progBar.setStringPainted(true);
        progBar.setVisible(true);

        JPanel center = new JPanel(new BorderLayout());
        center.add(progBar, BorderLayout.WEST);
        center.add(actButton);
        add(center, BorderLayout.EAST);
        String destination = "C:\\downloads";
        Path path = Paths.get(destination + "\\.minecraft");
        if (Files.exists(path)) {
            actButton.addActionListener(e -> {
                String path1 = "C:\\downloads\\.minecraft\\launchers\\forge\\start_forge_1.16.5.bat\\";
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

    }

    public void buttonDo() {
        actButton.setEnabled(false);
        progBar.setVisible(true);

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws IOException {
rootPane
                URL url = new URL("http://localhost:8080/docs/download/5");
                InputStream in = url.openStream();
                URLConnection conn = url.openConnection();

                String source = "C:\\downloads\\.minecraft.zip\\";
                String destination = "C:\\downloads";
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

                Path zipFilePath = Paths.get(source);
                if (Files.exists(zipFilePath)) {
                    ZipFile zipFile = new ZipFile(source);
                    zipFile.extractAll(destination);
                    zipFile.removeFile(source);

                    Files.delete(zipFilePath);

                } else {
                    System.err.println("Файл не найден по пути: " + source);
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
                    String path = "C:\\downloads\\.minecraft\\launchers\\forge\\start_forge_1.16.5.bat\\";
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

