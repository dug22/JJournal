package io.github.dug22.jjournal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.dug22.jjournal.cell.Cell;
import io.github.dug22.jjournal.cell.CellData;
import io.github.dug22.jjournal.cell.CodeCell;
import io.github.dug22.jjournal.cell.NoteCell;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Window extends JFrame {

    private final JPanel container;
    public static List<Cell> cellList = new ArrayList<>();

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            Color background = new Color(30, 30, 30);
            Color surface = new Color(45, 45, 45);
            Color accent = new Color(60, 60, 60);
            Color highlight = new Color(75, 110, 175);
            Color foreground = new Color(220, 220, 220);
            UIManager.put("Panel.background", background);
            UIManager.put("Label.foreground", foreground);
            UIManager.put("Window.background", background);
            UIManager.put("TextField.background", accent);
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("TextField.caretForeground", Color.WHITE);
            UIManager.put("TextArea.background", surface);
            UIManager.put("TextArea.foreground", foreground);
            UIManager.put("TextArea.caretForeground", Color.WHITE);
            UIManager.put("TextPane.background", surface);
            UIManager.put("TextPane.foreground", foreground);
            UIManager.put("TextPane.caretForeground", Color.WHITE);
            UIManager.put("Button.background", accent);
            UIManager.put("Button.foreground", foreground);
            UIManager.put("ToolBar.background", surface);
            UIManager.put("ToolBar.border", BorderFactory.createEmptyBorder());
            UIManager.put("Button.select", highlight);
            UIManager.put("ScrollPane.background", background);
            UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
            UIManager.put("ScrollBar.background", background);
            UIManager.put("ScrollBar.width", 0);
            UIManager.put("ScrollBar.trackWidth", 0);
            UIManager.put("ScrollBar.thumb", background);
            UIManager.put("ScrollBar.track", background);
            UIManager.put("FileChooser.background", background);
            UIManager.put("FileChooser.foreground", foreground);

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
        int count = 0;
        for(JButton button : buttons){
            designButton(button,  buttonEmojis[count]);
            toolbar.add(button);
            count++;
        }
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
        button.setFont(new Font("Serif", Font.PLAIN,13));
    }
}