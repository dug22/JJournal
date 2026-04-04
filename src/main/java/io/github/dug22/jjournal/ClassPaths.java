package io.github.dug22.jjournal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClassPaths {

    public static List<String> classPaths;

    public static void startClassPathTask() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Before using JJournal we require that you provide a file of your all Class Paths");
        System.out.println("If you do not have a text file of your class paths create one!");
        System.out.println("The format of your file should look like this:\n \\\\path\\\\to\\\\jar1.jar\n \\\\path\\\\to\\\\jar2.jar");
        System.out.println("Type done if you wish to continue without using any third party libraries");
        String input;
        input = scanner.nextLine();
        while (true) {
            if(input.equalsIgnoreCase("done")){
                classPaths = new ArrayList<>();
                break;
            }
            try {
                classPaths = Files.readAllLines(Paths.get(input));
                break;
            } catch (IOException e) {
                System.out.println("That file does not exist! Try again!");
            }
        }

        System.out.println("Great! Starting JJournal right now!");
    }

    public static List<String> getClassPaths() {
        return classPaths;
    }
}
