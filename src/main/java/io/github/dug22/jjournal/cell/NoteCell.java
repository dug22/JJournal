package io.github.dug22.jjournal.cell;

import io.github.dug22.jjournal.utils.MarkdownToHtml;
import javax.swing.*;
import java.awt.*;

public class NoteCell extends Cell {
    private final JTextPane displayPane;
    private final JButton editBtn;
    private boolean isPreviewMode = false;
    private int lastDividerLocation = 150;

    public NoteCell(Container parent) {
        super(parent);

        displayPane = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }
        };
        displayPane.setContentType("text/html");
        displayPane.setEditable(false);
        JScrollPane displayScrollPane = new JScrollPane(displayPane);
        displayScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setResultComponent(displayScrollPane);
        editBtn = new JButton("Preview");
        editBtn.addActionListener(_ -> toggleMode());
        actionPanel.add(editBtn, 0);
    }

    private void toggleMode() {
        isPreviewMode = !isPreviewMode;
        if (isPreviewMode) {
            displayPane.setText(MarkdownToHtml.render(textArea.getText()));
            lastDividerLocation = splitPane.getDividerLocation();
            splitPane.setDividerLocation(0);
            splitPane.setEnabled(false);
            editBtn.setText("Edit");
        } else {
            splitPane.setEnabled(true);
            splitPane.setDividerLocation(lastDividerLocation);
            editBtn.setText("Preview");
        }

        revalidate();
        repaint();
    }
}