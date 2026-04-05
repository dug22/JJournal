package io.github.dug22.jjournal.utils;

import io.github.dug22.jjournal.file.FileHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClassPathsUtils {

    public static List<String> classPaths = new ArrayList<>();
    private static final Path initFilePath = FileHandler.getFile("init").getFilePath();
    private static final Path classPathsFilePath = FileHandler.getFile("class-paths").getFilePath();

    public static void startClassPathTask() throws Exception {

        if (Files.readString(initFilePath).contains("init=true")) {
            if(classPathsFilePath.toFile().length() != 0) {
                classPaths = Files.readAllLines(classPathsFilePath);
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Before using JJournal for the first time, you will be prompted to provide a list of class paths you wish to use.");
            System.out.println("Once entered, these class paths will be saved in the following text file: " + classPathsFilePath);
            System.out.println("""
                    When entering your JAR file paths, use the following format:
                    
                    \\\\path\\\\to\\\\jar1.jar
                    \\\\path\\\\to\\\\jar2.jar
                    """
            );
            System.out.println("Type ‘done’ when you have finished entering class paths, or if you prefer to use JJournal without any dependencies." +
                               "\n" +
                               "If you need to update your class paths later, simply edit " + classPathsFilePath + "!" +
                               "\nThis prompt appears only once, during the initial launch.");
            String input;
            while (true) {
                input = scanner.nextLine();
                if (input.equalsIgnoreCase("done")) {
                    Files.writeString(initFilePath, "init=true");
                    break;
                }

                classPaths.add(input);
            }

            Files.write(classPathsFilePath, classPaths);
            System.out.println("Great! Starting JJournal right now!");
        }
    }

    public static List<String> getClassPaths() {
        return classPaths;
    }
}
