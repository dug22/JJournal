package io.github.dug22.jjournal.cell;

import io.github.dug22.jjournal.utils.MarkdownToHtml;

import javax.swing.*;
import java.awt.*;

public class NoteCell extends Cell {

    private final JTextPane displayPane;
    private final JScrollPane editorScrollPane;
    private final JScrollPane displayScrollPane;
    private final JButton editBtn;
    private boolean isEditing = true;

    public NoteCell(Container parent) {
        super(parent);
        this.editorScrollPane = (JScrollPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        displayPane = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }
        };
        displayPane.setContentType("text/html");
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
            displayPane.setText(MarkdownToHtml.render(textArea.getText()));
            displayScrollPane.setPreferredSize(new Dimension(parentContainer.getWidth() - 50, 250));
            add(displayScrollPane, BorderLayout.CENTER);
        } else {
            remove(displayScrollPane);
            editorScrollPane.setPreferredSize(null);
            add(editorScrollPane, BorderLayout.CENTER);
        }

        isEditing = !isEditing;
        editBtn.setText(isEditing ? "Stop Editing" : "Start Editing");

        revalidate();
        repaint();
    }
}