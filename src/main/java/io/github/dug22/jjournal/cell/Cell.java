package io.github.dug22.jjournal.cell;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public abstract class Cell extends JPanel {

    protected JTextArea textArea;
    protected JPanel actionPanel;
    protected Container parentContainer;


    public Cell(Container parent) {
        this.parentContainer = parent;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        setMaximumSize(new Dimension(Short.MAX_VALUE, 250));
        this.textArea = new JTextArea(5, 50);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane,  BorderLayout.CENTER);
        actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        add(actionPanel, BorderLayout.NORTH);
        addDeleteButton();
    }

    private void addDeleteButton() {
        JButton deleteBtn = new JButton("\uD835\uDC17");
        deleteBtn.setToolTipText("Delete Cell");
        deleteBtn.addActionListener(e -> {
            parentContainer.remove(this);
            parentContainer.revalidate();
            parentContainer.repaint();
        });
        actionPanel.add(deleteBtn);
    }
    public String getText() {
        return textArea.getText();
    }

    public void setText(String text){
        this.textArea.setText(text);
    }
}
