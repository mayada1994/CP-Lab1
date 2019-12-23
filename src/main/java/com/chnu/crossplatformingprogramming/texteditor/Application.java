package main.java.com.chnu.crossplatformingprogramming.texteditor;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Application extends JFrame {
    private String pathToFile = "";

    Application() throws HeadlessException {
        super("TextEditor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(960, 500));
        setResizable(false);

        setLayout(new BorderLayout());

        JTextArea jTextArea = new JTextArea(25, 72);

        mainContainer(jTextArea);

        setJMenuBar(menuBar(jTextArea));

        WindowListener frameClose = new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if (confirmSaveBeforeAction(jTextArea)) {
                    System.exit(0);
                }
            }
        };
        addWindowListener(frameClose);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel textWindow(JPanel jPanel, JTextArea jTextArea) {
        jPanel.add(entryField(jTextArea));
        JScrollPane jsp = new JScrollPane(jTextArea);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jPanel.add(jsp);
        return jPanel;
    }

    private JTextArea entryField(JTextArea jTextArea) {
        jTextArea.setFont(jTextArea.getFont().deriveFont(Font.PLAIN, 14f));
        jTextArea.setLineWrap(true);
        return jTextArea;
    }

    private void mainContainer(JTextArea jTextArea) {
        Container mainContainer = getContentPane();
        JPanel jPanel = new JPanel();
        mainContainer.add(textWindow(jPanel, jTextArea));
    }

    private void saveContentInFile(JTextArea jTextArea) {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showDialog(null, "Save");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            pathToFile = jfc.getSelectedFile().getPath();
            File file = new File(pathToFile);
            try (FileWriter writer = new FileWriter(file.getAbsolutePath())) {
                writer.write(entryField(jTextArea).getText());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean confirmSaveBeforeAction(JTextArea jTextArea) {
        String strMsg = "Do you want to save the changes?";
        if (!jTextArea.getText().equals("")) {
            int x = JOptionPane.showConfirmDialog(this, strMsg, this.getTitle(),
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (x == JOptionPane.CANCEL_OPTION) {
                return true;
            } else if (x == JOptionPane.YES_OPTION) {
                saveContentInFile(jTextArea);
                return false;
            }
        }
        return false;
    }

    private JMenuBar menuBar(JTextArea jTextArea) {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        jMenuBar.add(fileMenu);

        JMenuItem newFileItem = fileMenu.add("New");
        newFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (confirmSaveBeforeAction(jTextArea)) {
                    jTextArea.setText("");
                }
            }
        });
        newFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));

        JMenuItem openItem = fileMenu.add("Open...");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (confirmSaveBeforeAction(jTextArea)) {

                    JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    int returnValue = jfc.showDialog(null, "Open");
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File file = new File(jfc.getSelectedFile().getPath());

                        StringBuilder stringBuilder = new StringBuilder();

                        try (FileReader reader = new FileReader(file.getAbsolutePath());
                             Scanner scan = new Scanner(reader)) {
                            while (scan.hasNextLine()) {
                                stringBuilder.append(scan.nextLine()).append("\n");
                            }

                            String finalContent;

                            if (stringBuilder.toString().startsWith("i-")) {
                                setFont(jTextArea, Font.ITALIC);
                                finalContent = stringBuilder.toString().substring(2);
                            } else if (stringBuilder.toString().startsWith("b-")) {
                                setFont(jTextArea, Font.BOLD);
                                finalContent = stringBuilder.toString().substring(2);
                            } else {
                                setFont(jTextArea, Font.PLAIN);
                                finalContent = stringBuilder.toString();
                            }

                            jTextArea.setText(finalContent);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        JMenuItem saveItem = fileMenu.add("Save");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!pathToFile.equals("")) {
                    File file = new File(pathToFile);
                    try (FileWriter writer = new FileWriter(file.getAbsolutePath())) {
                        writer.write(entryField(jTextArea).getText());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    saveContentInFile(jTextArea);
                }
            }
        });
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        JMenuItem saveAsItem = fileMenu.add("Save as...");
        saveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveContentInFile(jTextArea);
            }
        });
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        JMenuItem closeItem = fileMenu.add("Exit");
        closeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));

        JMenu formatMenu = new JMenu("Format");
        jMenuBar.add(formatMenu);

        JMenuItem fontPlainItem = formatMenu.add("Plain font");
        fontPlainItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFont(jTextArea, Font.PLAIN);
            }
        });

        JMenuItem fontBoldItem = formatMenu.add("Bold font");
        fontBoldItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFont(jTextArea, Font.BOLD);
            }
        });

        JMenuItem fontItalicItem = formatMenu.add("Italic font");
        fontItalicItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFont(jTextArea, Font.ITALIC);
            }
        });

        return jMenuBar;
    }

    private void setFont(JTextArea jTextArea, int style) {
        jTextArea.setFont(jTextArea.getFont().deriveFont(style, 14f));
    }
}