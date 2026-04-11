package io.github.dug22.jjournal.cell;

import jdk.jshell.JShell;
import jdk.jshell.SourceCodeAnalysis;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class CodeSuggestions {

    private JPopupMenu suggestionsPopup;
    private final int maxVisibleSuggestions = 8;

    public void loadSuggestions(JShell shell, JTextArea textArea) {
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                int appropriateKeyCode = KeyEvent.VK_ALT;
                if (suggestionsPopup != null && keyCode != appropriateKeyCode) {
                    suggestionsPopup.setVisible(false);
                    suggestionsPopup = null;
                }

                if (keyCode == appropriateKeyCode) {
                    e.consume();
                    String textAreaText = textArea.getText();
                    int caretPosition = textArea.getCaretPosition();
                    List<SourceCodeAnalysis.Suggestion> suggestions = shell.sourceCodeAnalysis().completionSuggestions(
                            textAreaText,
                            caretPosition,
                            new int[1]
                    );

                    if (!suggestions.isEmpty()) {
                        DefaultListModel<String> suggestionModel = new DefaultListModel<>();
                        for (SourceCodeAnalysis.Suggestion s : suggestions) {
                            suggestionModel.addElement(s.continuation());
                        }
                        JList<String> suggestionList = new JList<>(suggestionModel);
                        suggestionList.setVisibleRowCount(Math.min(suggestionModel.size(), maxVisibleSuggestions));
                        suggestionsPopup = new JPopupMenu();
                        suggestionsPopup.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
                        suggestionsPopup.add(new JScrollPane(suggestionList));
                        suggestionsPopup.setFocusable(false);
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
                                    suggestionsPopup.setVisible(false);
                                    suggestionsPopup = null;
                                    textArea.requestFocusInWindow();
                                }
                            }
                        });

                        try {
                            Rectangle2D rect = textArea.modelToView2D(textArea.getCaretPosition());
                            if (rect != null) {
                                suggestionsPopup.show(textArea, (int) rect.getX(), (int) rect.getY() + (int) rect.getHeight());
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