package io.github.dug22.jjournal.cell;

import java.awt.*;

public class NoteCell extends Cell {

    public NoteCell(Container parent) {
        super(parent);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }
}
