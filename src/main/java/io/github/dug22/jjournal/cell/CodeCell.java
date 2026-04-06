package io.github.dug22.jjournal.cell;

import io.github.dug22.jjournal.utils.ClassPathsUtils;
import jdk.jshell.*;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CodeCell extends Cell {

    private static JTextArea outputArea = null;
    private final JScrollPane scrollOutput;
    private static final JShell jShell;
    private boolean isHidden = false;

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
        jShell = JShell.builder().out(ps).err(ps).build();
        List<String> classPaths = ClassPathsUtils.getClassPaths();
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
        outputArea = new JTextArea(5, 20);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(61, 61, 61));
        scrollOutput = new JScrollPane(outputArea);
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
            if(isHidden){
                hideBtn.setText("⊘");
                hideBtn.setToolTipText("Show Hidden Output");
            } else {
                hideBtn.setText("◉");
                hideBtn.setToolTipText("Hide Output");
            }
            revalidate();
            repaint();
        });
        actionPanel.add(hideBtn, 1);
    }


    private void executeCode() {
        outputArea.setText("");
        String lastlineResult = "";
        String remainingCode = getText();
        while (!remainingCode.isEmpty()) {
            SourceCodeAnalysis.CompletionInfo info = jShell.sourceCodeAnalysis().analyzeCompletion(remainingCode);
            List<SnippetEvent> events = jShell.eval(info.source());
            for (SnippetEvent e : events) {
                lastlineResult = getEventResult(e);
            }
            remainingCode = info.remaining();
        }
        outputArea.append(lastlineResult);
    }

    private String getEventResult(SnippetEvent e) {
        StringBuilder sb = new StringBuilder();
        if (e.status() == Snippet.Status.VALID) {
            sb.append("=> ").append(e.value()).append("\n");
        } else {
            List<Diag> diagnositics = jShell.diagnostics(e.snippet()).toList();
            for (Diag diag : diagnositics) {
                sb.append("Error: ").append(diag.getMessage(null)).append("\n");
            }
        }
        return sb.toString();
    }

    public JTextArea getOutputArea() {
        return outputArea;
    }
}

