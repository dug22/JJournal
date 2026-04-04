package io.github.dug22.jjournal;

public class CellData {

    private String cellType;
    private String cellContent;

    public CellData(String cellType, String cellContent){
        this.cellType = cellType;
        this.cellContent = cellContent;
    }

    public String getCellType() {
        return cellType;
    }

    public String getCellContent() {
        return cellContent;
    }
}
