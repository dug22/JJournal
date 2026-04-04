package io.github.dug22.jjournal;

import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CodeCell extends Cell {
    private static JTextArea outputArea = null;

    private static final JShell jShell;

    static {
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) {
                String text = String.valueOf((char) b);
                SwingUtilities.invokeLater(() -> {
                    outputArea.append(text);
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                });
            }

            @Override
            public void write(byte[] b, int off, int len) {
                String text = new String(b, off, len, StandardCharsets.UTF_8);
                SwingUtilities.invokeLater(() -> {
                    outputArea.append(text);
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                });
            }
        };

        PrintStream ps = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
        jShell = JShell.builder().out(ps).err(ps).remoteVMOptions("-Dfile.encoding=UTF-8").build();
        List<String> classPaths = ClassPaths.getClassPaths();
        if (!classPaths.isEmpty()) {
            for (String classPath : classPaths) {
                jShell.addToClasspath(classPath);
            }
        } else {
            System.out.println();
        }
    }

    public CodeCell(Container parent) {
        super(parent);
        //setBackground(new Color(240, 240, 240));
        outputArea = new JTextArea(5, 20);
        //textArea.setBackground(new Color(230, 230, 230));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(61, 61, 61));
        JScrollPane scrollOutput = new JScrollPane(outputArea);
        add(scrollOutput, BorderLayout.SOUTH);
        JButton runBtn = new JButton("▶ Run");
        runBtn.addActionListener(e -> executeCode());
        actionPanel.add(runBtn, 0);
    }

    private void executeCode() {
        outputArea.setText("");
        String remainingCode = getText();
        while (!remainingCode.isEmpty()) {
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
                outputArea.append("==> " + e.value() + "\n");
            }
        } else {
            jShell.diagnostics(e.snippet()).forEach(diag ->
                    outputArea.append("Error: " + diag.getMessage(null) + "\n")
            );
        }
    }

    public JTextArea getOutputArea() {
        return outputArea;
    }
}

