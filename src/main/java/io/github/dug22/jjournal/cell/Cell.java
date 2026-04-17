package io.github.dug22.jjournal.cell;

import io.github.dug22.jjournal.Window;
import javax.swing.*;
import java.awt.*;
import java.util.Collections;

public abstract class Cell extends JPanel {

    protected JTextArea textArea;
    protected JPanel actionPanel;
    protected Container parentContainer;
    protected JSplitPane splitPane;
    protected JScrollPane inputScrollPane;

    public Cell(Container parent) {
        this.parentContainer = parent;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));

        setMaximumSize(new Dimension(Short.MAX_VALUE, 1000));
        setPreferredSize(new Dimension(parent.getWidth() > 0 ? parent.getWidth() - 20 : 600, 350));
        this.textArea = new JTextArea(5, 50);
        this.textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        this.inputScrollPane = new JScrollPane(textArea);
        JPanel placeholder = new JPanel();
        placeholder.setBackground(new Color(45, 45, 45));
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScrollPane, placeholder);
        this.splitPane.setDividerLocation(150);
        this.splitPane.setContinuousLayout(true);
        this.splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
        actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        add(actionPanel, BorderLayout.NORTH);
        addDeleteButton();
        addMoveUpButton();
        addMoveDownButton();
    }

    protected void setResultComponent(JComponent component) {
        splitPane.setBottomComponent(component);
        revalidate();
        repaint();
    }

    private void addDeleteButton() {
        JButton deleteBtn = new JButton("\uD835\uDC17");
        deleteBtn.setToolTipText("Delete Cell");
        deleteBtn.addActionListener(e -> {
            Window.cellList.remove(this);
            parentContainer.remove(this);
            parentContainer.revalidate();
            parentContainer.repaint();
        });
        actionPanel.add(deleteBtn);
    }

    private void addMoveUpButton(){
        JButton moveUpBtn = new JButton("▲");
        moveUpBtn.addActionListener(_ -> moveCellUp());
        actionPanel.add(moveUpBtn);
    }

    private void addMoveDownButton(){
        JButton moveDownBtn = new JButton("▼");
        moveDownBtn.addActionListener(_ -> moveCellDown());
        actionPanel.add(moveDownBtn);
    }

    private void moveCellUp(){
        int index = Window.cellList.indexOf(this);
        if(index > 0) {
            Collections.swap(Window.cellList, index, index - 1);
            parentContainer.setComponentZOrder(this, (index - 1));
            parentContainer.revalidate();
            parentContainer.repaint();
        }
    }

    private void moveCellDown() {
        int index = Window.cellList.indexOf(this);
        if (index != -1 && index < Window.cellList.size() - 1) {
            Collections.swap(Window.cellList, index, index + 1);
            parentContainer.setComponentZOrder(this, (index + 1));
            parentContainer.revalidate();
            parentContainer.repaint();
        }
    }

    public String getText() { return textArea.getText(); }
    public void setText(String text) { this.textArea.setText(text); }
}