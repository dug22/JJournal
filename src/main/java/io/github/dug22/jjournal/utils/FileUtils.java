package io.github.dug22.jjournal.utils;

import io.github.dug22.jjournal.file.FileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {


    public static void createDirectoriesAndFiles(){
        try {
            Files.createDirectories(Path.of(getJJournalFolder()));
            Files.createDirectories(Path.of(getJJournalFolder(), "journals"));
            FileHandler.getFile("class-paths").createFile();
            FileHandler.getFile("init").createFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getJJournalFolder(){
       return System.getProperty("user.home") + File.separator + "JJournal";
    }
}
