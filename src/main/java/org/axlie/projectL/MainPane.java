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

            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setApproveButtonText("delete");

            // delete old buttons
            removeButtons(fileChooser);

            // file list
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

            // bottom buttons(select cancel)
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton select = new JButton("Select");
            JButton cancel = new JButton("Cancel");

            bottom.add(select);
            bottom.add(cancel);

            add(bottom, BorderLayout.SOUTH);

            // Select button
            select.addActionListener(e -> {
                Window w = SwingUtilities.getWindowAncestor(MainPane.this);
                w.dispose(); // close
            });

            // cancel button
            cancel.addActionListener(e -> {
                resultPath = null;
                Window w = SwingUtilities.getWindowAncestor(MainPane.this);
                w.dispose();
            });
        }

        //show frame and return path
        public String showDialog() {

            JDialog dialog = new JDialog((Frame) null, "Select file", true);
            dialog.setContentPane(this);
            dialog.setSize(800, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            return resultPath;
        }

        //buttons delete
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

        // recursive search T = Inside the comp component, find the first child element of type T
        public <T extends Component> T findFirstChildren(JComponent comp, Class<T> clazz) {
            for (Component c : comp.getComponents()) {
                //Is this component the correct type?
                if (clazz.isInstance(c)) return clazz.cast(c);
                //If the component is a JComponent, then we search inside it recursively
                //Если компонент является JComponent, то мы ищем внутри него рекурсивно.
                if (c instanceof JComponent sub) {
                    T child = findFirstChildren(sub, clazz);
                    if (child != null) return child;
                }
            }
            return null;
        }
    }