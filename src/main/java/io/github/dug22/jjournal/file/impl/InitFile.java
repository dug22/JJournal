package io.github.dug22.jjournal.file.impl;

import io.github.dug22.jjournal.file.AbstractFile;
import io.github.dug22.jjournal.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InitFile extends AbstractFile {

    @Override
    public String fileName() {
        return "init.txt";
    }

    @Override
    public void createFile() {

        File file = new File(FileUtils.getJJournalFolder() + File.separator + fileName());
        try {
            if(!file.exists()) {
                Files.createFile(Path.of(file.getPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Path getFilePath() {
        return Path.of(FileUtils.getJJournalFolder(), "init.txt");
    }
}
