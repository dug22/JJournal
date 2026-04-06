package io.github.dug22.jjournal.cell;

import io.github.dug22.jjournal.utils.ClassPathsUtils;
import jdk.jshell.*;

import javax.swing.*;
import java.awt.*;
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
    }

    private void handleSnippetEvent(SnippetEvent e) {
        if (e.status() == Snippet.Status.VALID) {
            if (e.value() != null && !e.value().isEmpty()) {
                outputArea.append("=> " + e.value() + "\n");
            }
        } else {
            String diagnostics = jShell.diagnostics(e.snippet())
                    .map(diag -> "Error: " + diag.getMessage(null))
                    .collect(Collectors.joining("\n"));
            outputArea.append(diagnostics + "\n");
        }
    }

    public JTextArea getOutputArea() {
        return outputArea;
    }
}