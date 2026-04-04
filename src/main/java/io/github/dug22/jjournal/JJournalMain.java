package io.github.dug22.jjournal;

import javax.swing.*;

public class JJournalMain {

    public static void main(String[] args) {
        ClassPaths.startClassPathTask();
        SwingUtilities.invokeLater(() -> new Window().setVisible(true));
    }
}
