package io.github.dug22.jjournal;

import io.github.dug22.jjournal.utils.ClassPathsUtils;
import io.github.dug22.jjournal.utils.FileUtils;

import javax.swing.*;

public class JJournalMain {

    public static void main(String[] args) throws Exception {
        FileUtils.createDirectoriesAndFiles();
        ClassPathsUtils.startClassPathTask();
        SwingUtilities.invokeLater(() -> new Window().setVisible(true));
    }
}
