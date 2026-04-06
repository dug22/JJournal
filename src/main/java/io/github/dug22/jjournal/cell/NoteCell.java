package io.github.dug22.jjournal.cell;

import javax.swing.*;
import java.awt.*;

public class NoteCell extends Cell {

    private final JTextArea displayPane;
    private final JScrollPane editorScrollPane;
    private final JScrollPane displayScrollPane;
    private final JButton editBtn;
    private boolean isEditing = true;

    public NoteCell(Container parent) {
        super(parent);
        this.editorScrollPane = (JScrollPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        displayPane = new JTextArea();
        displayPane.setLineWrap(true);
        displayPane.setWrapStyleWord(true);
        displayPane.setEditable(false);
        displayScrollPane = new JScrollPane(displayPane);
        displayScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        displayScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        displayPane.setOpaque(true);
        editBtn = new JButton(isEditing ? "Stop Editing" : "Start Editing");
        editBtn.addActionListener(_ -> toggleMode());
        actionPanel.add(editBtn, 0);
    }

    private void toggleMode() {
        if (isEditing) {
            remove(editorScrollPane);
            displayPane.setText(textArea.getText());
            add(displayScrollPane);
        } else {
            remove(displayScrollPane);
            System.out.println(textArea.getText().length());
            editorScrollPane.setPreferredSize(null);
            add(editorScrollPane, BorderLayout.CENTER);
        }

        isEditing = !isEditing;
        editBtn.setText(isEditing ? "Stop Editing" : "Start Editing");

        revalidate();
        repaint();
    }
}