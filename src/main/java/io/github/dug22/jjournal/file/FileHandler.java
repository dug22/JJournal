package io.github.dug22.jjournal.file;

import io.github.dug22.jjournal.file.impl.ClassPathsFile;
import io.github.dug22.jjournal.file.impl.InitFile;

import java.util.HashMap;
import java.util.Map;

public class FileHandler {

    private static final Map<String, AbstractFile> fileMap = new HashMap<>();

    static {
        fileMap.put("class-paths", new ClassPathsFile());
        fileMap.put("init", new InitFile());
    }

    public static AbstractFile getFile(String key) {
        return fileMap.get(key);
    }
}
