package io.github.dug22.jjournal.file;

import java.nio.file.Path;

public abstract class AbstractFile {

    public abstract String fileName();

    public abstract void createFile();

    public abstract Path getFilePath();
}
