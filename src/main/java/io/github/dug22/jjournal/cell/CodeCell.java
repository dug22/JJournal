package io.github.dug22.jjournal.cell;

import io.github.dug22.jjournal.utils.ClassPathsUtils;
import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class CodeCell extends Cell {

    private static CodeCell activeCell = null;
    private static final JShell jShell;
    private final JTextArea outputArea;
    private final JScrollPane scrollOutput;
    private boolean isHidden = false;
    private String lastValue = "";
    private JLabel predictiveLabel;
    private JPopupMenu predictivePopup;

    static {
        OutputStream proxyOutputStream = new OutputStream() {
            @Override
            public void write(int b) {
                if (activeCell != null) {
                    String text = String.valueOf((char) b);
                    SwingUtilities.invokeLater(() -> {
                        activeCell.outputArea.append(text);
                        activeCell.outputArea.setCaretPosition(activeCell.outputArea.getDocument().getLength());
                    });
                }
            }

            @Override
            public void write(byte[] b, int off, int len) {
                if (activeCell != null) {
                    String text = new String(b, off, len, StandardCharsets.UTF_8);
                    SwingUtilities.invokeLater(() -> {
                        activeCell.outputArea.append(text);
                        activeCell.outputArea.setCaretPosition(activeCell.outputArea.getDocument().getLength());
                    });
                }
            }
        };

        PrintStream ps = new PrintStream(proxyOutputStream, true, StandardCharsets.UTF_8);
        jShell = JShell.builder().out(ps).err(ps).build();
        List<String> classPaths = ClassPathsUtils.getClassPaths();
        if (classPaths != null && !classPaths.isEmpty()) {
            for (String classPath : classPaths) {
                jShell.addToClasspath(classPath);
            }
        }
    }

    public CodeCell(Container parent) {
        super(parent);
        this.outputArea = new JTextArea(5, 20);
        this.outputArea.setEditable(false);
        this.outputArea.setBackground(new Color(61, 61, 61));
        this.outputArea.setForeground(Color.WHITE);

        this.scrollOutput = new JScrollPane(outputArea);
        add(scrollOutput, BorderLayout.SOUTH);

        JButton runBtn = new JButton("▶ Run");
        runBtn.addActionListener(e -> executeCode());
        actionPanel.add(runBtn, 0);
        addHideButton();
        predict();
    }

    private void addHideButton() {
        JButton hideBtn = new JButton("◉");
        hideBtn.addActionListener(e -> {
            isHidden = !isHidden;
            scrollOutput.setVisible(!isHidden);
            hideBtn.setText(isHidden ? "⊘" : "◉");
            hideBtn.setToolTipText(isHidden ? "Show Hidden Output" : "Hide Output");
            revalidate();
            repaint();
        });
        actionPanel.add(hideBtn, 1);
    }

    private void executeCode() {
        activeCell = this;
        outputArea.setText("");
        String remainingCode = getText();
        while (remainingCode != null && !remainingCode.trim().isEmpty()) {
            SourceCodeAnalysis.CompletionInfo info = jShell.sourceCodeAnalysis().analyzeCompletion(remainingCode);
            List<SnippetEvent> events = jShell.eval(info.source());

            for (SnippetEvent e : events) {
                handleSnippetEvent(e);
            }
            remainingCode = info.remaining();
        }
        if (!lastValue.isEmpty()) {
            outputArea.append("=> " + lastValue + "\n");
        }
    }

    private void handleSnippetEvent(SnippetEvent e) {
        if (e.status() == Snippet.Status.VALID) {
            if (e.value() != null && !e.value().isEmpty()) {
                lastValue = e.value();
            }
        } else {
            String diagnostics = jShell.diagnostics(e.snippet())
                    .map(diag -> "Error: " + diag.getMessage(null))
                    .collect(Collectors.joining("\n"));
            outputArea.append(diagnostics + "\n");
        }
    }

    private void predict() {
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (predictivePopup != null && e.getKeyCode() != KeyEvent.VK_ALT) {
                    predictivePopup.setVisible(false);
                    predictivePopup = null;
                }

                if (e.getKeyCode() == KeyEvent.VK_ALT) {
                    e.consume();
                    List<SourceCodeAnalysis.Suggestion> suggestions = jShell.sourceCodeAnalysis().completionSuggestions(textArea.getText(), textArea.getCaretPosition(), new int[1]);
                    if (!suggestions.isEmpty()) {
                        String topSuggestion = suggestions.getFirst().continuation();
                        predictiveLabel = new JLabel(topSuggestion);
                        predictiveLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                        predictivePopup = new JPopupMenu();
                        predictivePopup.setBackground(new Color(30, 30, 30));
                        predictivePopup.add(predictiveLabel);
                        predictivePopup.setFocusable(false);
                        predictiveLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                String currentText = textArea.getText();
                                String suggestion = predictiveLabel.getText();
                                String toAppend = suggestion;
                                int length = suggestion.length();
                                while (length > 0){
                                    if (currentText.endsWith(suggestion.substring(0, length))) {
                                        toAppend = suggestion.substring(length);
                                        break;
                                    }
                                    length--;
                                }
                                int caretPos = textArea.getCaretPosition();
                                textArea.insert(toAppend, caretPos);
                                predictivePopup.setVisible(false);
                                predictivePopup = null;
                                textArea.requestFocusInWindow();
                            }
                        });

                        try {
                            Rectangle suggestionBox = textArea.modelToView2D(textArea.getCaretPosition()).getBounds();
                            predictivePopup.show(textArea, (int) suggestionBox.getX(), (int) suggestionBox.getY() + (int) suggestionBox.getHeight());
                            textArea.requestFocusInWindow();
                        } catch (BadLocationException ignore) {

                        }
                    }
                }
            }
        });
    }

    public JTextArea getOutputArea() {
        return outputArea;
    }
}