package org.axlie.projectL;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;

public class MainPane extends JPanel {


        private JFileChooser fileChooser;
        private String resultPath = null;

        public MainPane() {

            setLayout(new BorderLayout());

            // --- FILE CHOOSER ---
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setApproveButtonText("delete");

            // Удаляем стандартные кнопки
            removeButtons(fileChooser);

            // Слушаем список файлов
            JList list = findFirstChildren(fileChooser, JList.class);
            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    File file = (File) list.getSelectedValue();
                    if (file != null) {
                        resultPath = file.getAbsolutePath();
                    }
                }
            });

            add(fileChooser, BorderLayout.CENTER);

            // --- КНОПКИ ВНИЗУ ---
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton select = new JButton("Select");
            JButton cancel = new JButton("Cancel");

            bottom.add(select);
            bottom.add(cancel);

            add(bottom, BorderLayout.SOUTH);

            // Логика кнопки Select
            select.addActionListener(e -> {
                Window w = SwingUtilities.getWindowAncestor(MainPane.this);
                w.dispose(); // закрываем окно
            });

            // Логика кнопки Cancel
            cancel.addActionListener(e -> {
                resultPath = null;
                Window w = SwingUtilities.getWindowAncestor(MainPane.this);
                w.dispose();
            });
        }

        // --- Метод, который показывает окно и ВОЗВРАЩАЕТ path ---
        public String showDialog() {

            JDialog dialog = new JDialog((Frame) null, "Select file", true);
            dialog.setContentPane(this);
            dialog.setSize(800, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            return resultPath;
        }

        // Удаление кнопок JFileChooser
        protected void removeButtons(Container container) {
            for (Component child : container.getComponents()) {
                if (child instanceof JButton btn) {
                    if (btn.getText() != null &&
                            (btn.getText().equals(fileChooser.getApproveButtonText()) ||
                                    btn.getText().equals("Cancel"))) {

                        container.remove(child);
                    }
                } else if (child instanceof Container cont) {
                    removeButtons(cont);
                }
            }
        }

        // Рекурсивный поиск JList внутри JFileChooser
        public <T extends Component> T findFirstChildren(JComponent comp, Class<T> clazz) {
            for (Component c : comp.getComponents()) {
                if (clazz.isInstance(c)) return clazz.cast(c);
                if (c instanceof JComponent sub) {
                    T child = findFirstChildren(sub, clazz);
                    if (child != null) return child;
                }
            }
            return null;
        }
    }