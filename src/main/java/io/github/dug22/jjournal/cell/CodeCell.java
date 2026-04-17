package io.github.dug22.jjournal.cell;

import io.github.dug22.jjournal.utils.ClassPathsUtils;
import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class CodeCell extends Cell {

    private static CodeCell activeCell = null;
    private static final JShell jShell;
    private final JTextPane outputArea;
    private final JScrollPane scrollOutput;
    private boolean isHidden = false;
    private boolean streamActive = false;
    private String lastValue = "";

    static {
        OutputStream proxyOutputStream = new OutputStream() {
            private final StringBuilder lineBuffer = new StringBuilder();

            @Override
            public void write(int b) {
                if (b == 8 || b == 13) return;
                if (b == '\n') {
                    processLine(lineBuffer.toString().trim());
                    lineBuffer.setLength(0);
                } else {
                    lineBuffer.append((char) b);
                }
            }

            @Override
            public void write(byte[] b, int off, int len) {
                String text = new String(b, off, len, StandardCharsets.UTF_8);
                for (char c : text.toCharArray()) {
                    write(c);
                }
            }

            private void processLine(String line) {
                if (activeCell == null || line.isEmpty()) return;

                if (line.startsWith("RENDER_IMAGE:")) {
                    String imageRenderContent = line.substring("RENDER_IMAGE:".length()).trim();
                    String[] imageRenderContentParts = imageRenderContent.split(" ", 3);
                    int width = Integer.parseInt(imageRenderContentParts[1]);
                    int height = Integer.parseInt(imageRenderContentParts[2]);
                    String path = imageRenderContentParts[0].trim();
                    activeCell.renderImage(path, width, height);
                } else {
                    activeCell.streamActive = true;
                    SwingUtilities.invokeLater(() -> activeCell.appendToOutput(line + "\n"));
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
        this.outputArea = new JTextPane();
        this.outputArea.setEditable(false);
        this.outputArea.setBackground(new Color(61, 61, 61));
        this.outputArea.setForeground(Color.WHITE);
        this.outputArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        this.scrollOutput = new JScrollPane(outputArea);

        setResultComponent(scrollOutput);

        JButton runBtn = new JButton("▶ Run");
        runBtn.addActionListener(e -> executeCode());
        actionPanel.add(runBtn, 0);
        addHideButton();
        new CodeSuggestions().loadSuggestions(jShell, this.textArea);
    }

    private void appendToOutput(String text) {
        try {
            Document doc = outputArea.getDocument();
            doc.insertString(doc.getLength(), text, null);
            outputArea.setCaretPosition(doc.getLength());
        } catch (BadLocationException ignore) {
        }
    }

    private void renderImage(String filePath, int width, int height) {
        SwingUtilities.invokeLater(() -> {
            Image icon = null;
            try {
                icon = ImageIO.read(new File(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            icon = icon.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            outputArea.insertIcon(new ImageIcon(icon));
            outputArea.revalidate();
            outputArea.repaint();
        });
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
        lastValue = "";
        String remainingCode = getText();

        while (remainingCode != null && !remainingCode.trim().isEmpty()) {
            SourceCodeAnalysis.CompletionInfo info = jShell.sourceCodeAnalysis().analyzeCompletion(remainingCode);
            streamActive = false;
            List<SnippetEvent> events = jShell.eval(info.source());

            for (SnippetEvent e : events) {
                handleSnippetEvent(e);
            }
            remainingCode = info.remaining();
        }

        if (!lastValue.isEmpty() && !streamActive) {
            appendToOutput(lastValue + "\n");
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
            appendToOutput(diagnostics + "\n");
        }
    }
}