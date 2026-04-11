package io.github.dug22.jjournal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.dug22.jjournal.cell.Cell;
import io.github.dug22.jjournal.cell.CellData;
import io.github.dug22.jjournal.cell.CodeCell;
import io.github.dug22.jjournal.cell.NoteCell;
import io.github.dug22.jjournal.utils.ClassPathsUtils;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Window extends JFrame {

    private final JPanel container;
    public static List<Cell> cellList = new ArrayList<>();
    private static final Color background = new Color(30, 30, 30);
    private static final Color surface = new Color(45, 45, 45);
    private static final Color accent = new Color(60, 60, 60);
    private static final Color highlight = new Color(75, 110, 175);
    private static final Color foreground = new Color(220, 220, 220);

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Panel.background", background);
            UIManager.put("Window.background", background);
            UIManager.put("RootPane.background", background);
            UIManager.put("Label.foreground", foreground);
            UIManager.put("TextField.background", accent);
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("TextField.caretForeground", Color.WHITE);
            UIManager.put("TextArea.background", surface);
            UIManager.put("TextArea.foreground", foreground);
            UIManager.put("TextArea.caretForeground", Color.WHITE);
            UIManager.put("TextPane.background", surface);
            UIManager.put("TextPane.foreground", foreground);
            UIManager.put("TextPane.caretForeground", Color.WHITE);
            UIManager.put("PasswordField.background", accent);
            UIManager.put("PasswordField.foreground", Color.WHITE);
            UIManager.put("Button.background", accent);
            UIManager.put("Button.foreground", foreground);
            UIManager.put("Button.select", highlight);
            UIManager.put("Button.focus", highlight);
            UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 10, 5, 10));
            UIManager.put("ToggleButton.background", accent);
            UIManager.put("ToggleButton.foreground", foreground);
            UIManager.put("ToolBar.background", surface);
            UIManager.put("ToolBar.border", BorderFactory.createEmptyBorder());
            UIManager.put("ScrollPane.background", background);
            UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
            UIManager.put("ScrollBar.background", background);
            UIManager.put("ScrollBar.thumb", accent);
            UIManager.put("ScrollBar.track", background);
            UIManager.put("ScrollBar.width", 8);
            UIManager.put("List.background", surface);
            UIManager.put("List.foreground", foreground);
            UIManager.put("List.selectionBackground", highlight);
            UIManager.put("List.selectionForeground", Color.WHITE);
            UIManager.put("Table.background", surface);
            UIManager.put("Table.foreground", foreground);
            UIManager.put("Table.selectionBackground", highlight);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("Table.gridColor", accent);
            UIManager.put("Tree.background", surface);
            UIManager.put("Tree.foreground", foreground);
            UIManager.put("Tree.selectionBackground", highlight);
            UIManager.put("Tree.selectionForeground", Color.WHITE);
            UIManager.put("ComboBox.background", accent);
            UIManager.put("ComboBox.foreground", foreground);
            UIManager.put("ComboBox.selectionBackground", highlight);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            UIManager.put("MenuBar.background", surface);
            UIManager.put("MenuBar.foreground", foreground);
            UIManager.put("Menu.background", surface);
            UIManager.put("Menu.foreground", foreground);
            UIManager.put("MenuItem.background", surface);
            UIManager.put("MenuItem.foreground", foreground);
            UIManager.put("MenuItem.selectionBackground", highlight);
            UIManager.put("MenuItem.selectionForeground", Color.WHITE);
            UIManager.put("TabbedPane.background", background);
            UIManager.put("TabbedPane.foreground", foreground);
            UIManager.put("TabbedPane.selected", surface);
            UIManager.put("TitledBorder.titleColor", foreground);
            UIManager.put("FileChooser.background", background);
            UIManager.put("FileChooser.foreground", foreground);
            UIManager.put("FileChooser.listViewBackground", surface);
            UIManager.put("FileChooser.listViewBorder", BorderFactory.createEmptyBorder());
            UIManager.put("FileChooser.readOnly", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Window() {
        setTitle("JJournal");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/icon-3.png"));
        Image scaledImage = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        setIconImage(scaledImage);
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(new EmptyBorder(10, 15, 10, 15));
        toolbar.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JLabel logoLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        JLabel titleLabel = new JLabel("JJournal");
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 13));
        toolbar.add(logoLabel);
        toolbar.add(titleLabel);
        JButton addCodeButton = new JButton("Add Code Cell");
        JButton addNoteButton = new JButton("Add Note Cell");
        JButton saveButton = new JButton("Save Journal");
        JButton loadButton = new JButton("Load Journal");
        JButton[] buttons = new JButton[]{addCodeButton, addNoteButton, saveButton, loadButton};
        String[] buttonEmojis = new String[]{"\uD83D\uDCBB", "\uD83D\uDCDD", "\uD83D\uDCBE", "\uD83D\uDCC2"};
        for (int i = 0; i < buttons.length; i++) {
            designButton(buttons[i], buttonEmojis[i]);
            toolbar.add(buttons[i]);
        }

        List<String> classPathJarsList = ClassPathsUtils.getClassPaths().stream().map(p -> Path.of(p).getFileName().toString()).toList();
        String[] classPathJars = classPathJarsList.toArray(new String[0]);
        JComboBox<String> libraryList = new JComboBox<>() {
            @Override
            public void setSelectedItem(Object o) {
            }
        };
        libraryList.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return new BasicArrowButton(
                        BasicArrowButton.SOUTH,
                        background,
                        background,
                        Color.WHITE,
                        highlight
                );
            }
        });
        libraryList.setFont(new Font("Serif", Font.PLAIN, 13));
        libraryList.addItem("📚 Libraries...");
        for (String jar : classPathJars) {
            libraryList.addItem(jar);
        }
        toolbar.add(libraryList);
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        addCodeButton.addActionListener(_ -> addCell(new CodeCell(container)));
        addNoteButton.addActionListener(_ -> addCell(new NoteCell(container)));
        saveButton.addActionListener(_ -> saveJournal());
        loadButton.addActionListener(_ -> loadJournal());
        add(toolbar, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setWheelScrollingEnabled(true);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addCell(Cell cell) {
        cellList.add(cell);
        container.add(cell);
        container.revalidate();
        container.repaint();
    }

    private void saveJournal() {
        List<CellData> dataToSave = new ArrayList<>();
        for (Cell cell : cellList) {
            String type = (cell instanceof CodeCell) ? "CODE" : "NOTE";
            String content = cell.getText();
            dataToSave.add(new CellData(type, content));
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Writer writer = new FileWriter(fileChooser.getSelectedFile())) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(dataToSave, writer);
                JOptionPane.showMessageDialog(this, "<html><font color='white'>You successfully saved your journal's contents</font></html>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadJournal() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Reader reader = new FileReader(fileChooser.getSelectedFile())) {
                Gson gson = new Gson();
                java.lang.reflect.Type listType = new TypeToken<ArrayList<CellData>>() {
                }.getType();
                List<CellData> loadedData = gson.fromJson(reader, listType);
                container.removeAll();
                cellList.clear();
                for (CellData data : loadedData) {
                    Cell newCell;
                    if (data.getCellType().equals("CODE")) {
                        newCell = new CodeCell(container);
                    } else {
                        newCell = new NoteCell(container);
                    }
                    newCell.setText(data.getCellContent());
                    addCell(newCell);
                }

                container.revalidate();
                container.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void designButton(JButton button, String emoji) {
        button.setText("<html><center><font size='5'>" + emoji + "</font><br>" + button.getText() + "</center></html>");
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Serif", Font.PLAIN, 13));
    }
}