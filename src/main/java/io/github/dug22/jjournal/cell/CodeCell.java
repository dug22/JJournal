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
import java.awt.geom.Rectangle2D;
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
    private JPopupMenu predictivePopup;
    private final int maxSuggestions = 8;

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
        this.outputArea.setFont(new Font("Serif", Font.PLAIN, 12));
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
            outputArea.append(lastValue.trim() + "\n");
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
                    List<SourceCodeAnalysis.Suggestion> suggestions = jShell.sourceCodeAnalysis().completionSuggestions(
                            textArea.getText(),
                            textArea.getCaretPosition(),
                            new int[1]
                    );

                    if (!suggestions.isEmpty()) {
                        DefaultListModel<String> suggestionModel = new DefaultListModel<>();
                        for (SourceCodeAnalysis.Suggestion s : suggestions) {
                            suggestionModel.addElement(s.continuation());
                        }
                        JList<String> suggestionList = new JList<>(suggestionModel);

                        suggestionList.setVisibleRowCount(Math.min(suggestionModel.size(), maxSuggestions));
                        predictivePopup = new JPopupMenu();
                        predictivePopup.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
                        predictivePopup.add(new JScrollPane(suggestionList));
                        predictivePopup.setFocusable(false);
                        suggestionList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent me) {
                                String selectedSuggestion = suggestionList.getSelectedValue();
                                if (selectedSuggestion != null) {
                                    String currentText = textArea.getText().substring(0, textArea.getCaretPosition());
                                    String toAppend = selectedSuggestion;
                                    int length = selectedSuggestion.length();
                                    while (length > 0) {
                                        if (currentText.endsWith(selectedSuggestion.substring(0, length))) {
                                            toAppend = selectedSuggestion.substring(length);
                                            break;
                                        }
                                        length--;
                                    }
                                    textArea.insert(toAppend, textArea.getCaretPosition());
                                    predictivePopup.setVisible(false);
                                    predictivePopup = null;
                                    textArea.requestFocusInWindow();
                                }
                            }
                        });

                        try {
                            Rectangle2D rect = textArea.modelToView2D(textArea.getCaretPosition());
                            if (rect != null) {
                                predictivePopup.show(textArea, (int) rect.getX(), (int) rect.getY() + (int) rect.getHeight());
                            }
                        } catch (BadLocationException ignore) {
                        }

                        textArea.requestFocusInWindow();
                    }
                }
            }
        });
    }
}